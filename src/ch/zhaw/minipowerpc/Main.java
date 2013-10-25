package ch.zhaw.minipowerpc;

import ch.zhaw.minipowerpc.compiler.MnemonicsCompiler;
import ch.zhaw.minipowerpc.compiler.MnemonicsCompilerException;
import ch.zhaw.minipowerpc.cpu.*;
import ch.zhaw.minipowerpc.storage.Storage;
import ch.zhaw.minipowerpc.storage.StorageException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main {

	//private static final String fileName = "D:\\Git\\minipowerpc\\examples\\CompilerTest.slang";
	private static final String fileName = "D:\\Git\\minipowerpc\\examples\\MathExample.slang";
	//private static final String fileName = "D:\\Git\\minipowerpc\\examples\\Serie3.slang";
	private static final int instructionPredictionCount = 10;
	private static final int storageDisplayCount = 15;
	private static final int storageOffset = 500;
	private static final char defaultMode = 'f';
	private static final int waitTimeInMilliseconds = 500;

	public static void main(String[] args) {
		Storage storage = new Storage();

		System.out.println("----------------------------");
		System.out.println("Mini-Power-PC by Simon Lang.");
		System.out.println("----------------------------");

		char mode = getMode();

		initializeStorage(storage, args);

		try {
			compile(storage);
		} catch (MnemonicsCompilerException e) {
			System.out.printf("Unable to parse code. Error: " + e.getMessage());
			return;
		}

		try {
			run(storage, mode);
		} catch (InvalidInstructionException e) {
			System.out.printf("Invalid instruction. Error: " + e.getMessage());
			return;
		} catch (StorageException e) {
			System.out.printf("Storage exception. Error: " + e.getMessage());
			return;
		}
	}

	private static char getMode() {
		System.out.printf("Select mode [f]ast, [s]low, s[t]ep: ");
		char mode;
		try {
			mode = (char) System.in.read();
		} catch (IOException e) {
			return defaultMode;
		}

		if (mode == 'f' || mode == 's' || mode == 't') {
			return mode;
		}
		return defaultMode;
	}

	private static void run(Storage storage, char mode) throws InvalidInstructionException, StorageException {
		ControlUnit controlUnit = new ControlUnit(storage);

		int cycleCount = 0;
		while (controlUnit.nextCycle()) {

			InstructionRegister instructionRegister = controlUnit.getInstructionRegister();
			Instruction instruction = instructionRegister.getInstruction();
			Binary address = instruction.getAddress();
			LinkedList<Instruction> history = instructionRegister.getHistory();

			Alu alu = controlUnit.getAlu();
			Binary accu = alu.getAccu().get();
			Binary reg1 = controlUnit.getRegister1().get();
			Binary reg2 = controlUnit.getRegister2().get();
			Binary reg3 = controlUnit.getRegister3().get();

			if (mode != 'f') {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
				printHeader(controlUnit, instruction, address, alu, accu, reg1, reg2, reg3, cycleCount);
				printInstructionsAndStorage(storage, controlUnit.getInstructionCounter().get(), history);
			}

			cycleCount++;

			waitIfRequired(mode);
		}
		if (mode == 'f') {

			InstructionRegister instructionRegister = controlUnit.getInstructionRegister();
			Instruction instruction = instructionRegister.getInstruction();
			Binary address = instruction.getAddress();
			LinkedList<Instruction> history = instructionRegister.getHistory();

			Alu alu = controlUnit.getAlu();
			Binary accu = alu.getAccu().get();
			Binary reg1 = controlUnit.getRegister1().get();
			Binary reg2 = controlUnit.getRegister2().get();
			Binary reg3 = controlUnit.getRegister3().get();

			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
			printHeader(controlUnit, instructionRegister.getInstruction(), address, alu, accu, reg1, reg2, reg3, cycleCount);
			printInstructionsAndStorage(storage, controlUnit.getInstructionCounter().get(), history);
		}
	}

	private static void waitIfRequired(char mode) {
		if (mode == 't') {
			try {
				System.in.read();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (mode == 's') {
			try {
				Thread.sleep(waitTimeInMilliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void printInstructionsAndStorage(Storage storage, Binary nextInstructionAddress,
	                                                LinkedList<Instruction> history) {

		System.out.printf("Instructions: %38s Storage:%n", " ");

		int c = history.size();
		int i;
		int empty = 0;
		List<String> instructionList = new ArrayList<String>();
		for (i = 0; i < 5; i++) {
			int address;
			String mnemonic;
			String machineCode;
			if (5 - c <= i) {
				Instruction instruction = history.get(i - empty);
				address = instruction.getAddress().toInt();
				mnemonic = instruction.getMnemonic();
				machineCode = instruction.getMachineCode().toBin();
			} else {
				address = 0;
				mnemonic = "";
				machineCode = "";
				empty++;
			}
			instructionList.add(String.format(" %d %3d %s %s", i - 5, address, padRight(mnemonic, 15), machineCode));
		}

		instructionList.add("----------------------------------------");
		for (i = 1; i < instructionPredictionCount + 1; i++) {
			int address;
			String mnemonic;
			String machineCode;
			int tempAddress = nextInstructionAddress.toInt() - 2 + i * 2;
			try {
				Instruction instruction = (Instruction) storage.get(tempAddress);
				address = instruction.getAddress().toInt();
				mnemonic = instruction.getMnemonic();
				machineCode = instruction.getMachineCode().toBin();
			} catch (StorageException e) {
				address = tempAddress;
				mnemonic = "";
				machineCode = "";
			}
			instructionList.add(String.format(" %2d %3d %s %s", i - 1, address, padRight(mnemonic, 15), machineCode));
		}

		List<String> storageList = new ArrayList<String>();
		for (i = 0; i < storageDisplayCount; i++) {
			int address = storageOffset + 2 * i;
			Binary value;
			try {
				value = (Binary) storage.get(address);
			} catch (StorageException e) {
				value = new Binary(0);
			}
			storageList.add(String.format("%d %5d (%s)", address, value.toInt(), value.toBin()));
		}
		storageList.add("");

		for (i = 0; i < 16; i++) {
			System.out.printf(" %s %10s %s%n", padRight(instructionList.get(i), 40), " ", storageList.get(i));
		}
	}

	private static void printHeader(ControlUnit controlUnit, Instruction instruction, Binary address,
	                                Alu alu, Binary accu, Binary reg1, Binary reg2, Binary reg3, int cycleCount) {
		System.out.printf("╔════════════════════╤═══════════════════╤═════════════════╗%n");
		System.out.printf("║ Befehlsregister: %16s│ Akku: %6d %s │ Carry: %d %18s ║%n",
				" ",
				accu.toInt(), padRight("(" + accu.toBin() + ")", 18),
				alu.getCarry(),
				" ");
		System.out.printf("║  - Address: %d %s│ REG1: %6d %s │ Befehlszähler: %d %8s ║%n",
				address.toInt(), padRight("(" + address.toBin() + ")", 17),
				reg1.toInt(), padRight("(" + reg1.toBin() + ")", 18),
				controlUnit.getInstructionCounter().get().toInt(),
				" ");
		System.out.printf("║  - MachineCode: %s │ REG2: %6d %s │ Durchgeführte Befehle: %3d  ║%n",
				instruction.getMachineCode().toBin(),
				reg2.toInt(), padRight("(" + reg2.toBin() + ")", 18),
				cycleCount);
		System.out.printf("║  - Mnemonic: %s│ REG3: %6d %s │ %27s ║%n",
				padRight(instruction.getMnemonic(), 20),
				reg3.toInt(), padRight("(" + reg3.toBin() + ")", 18),
				" ");
		System.out.printf("╚════════════════════╧═══════════════════╧═════════════════╝%n");
	}

	private static String padRight(String s, int length) {
		StringBuilder sb = new StringBuilder(s);
		for (int i = 0; i < length - s.length(); i++) {
			sb.append(" ");
		}

		return sb.toString();
	}

	private static void compile(Storage storage) throws MnemonicsCompilerException {
		MnemonicsCompiler mnemonicsCompiler = new MnemonicsCompiler(storage);
		mnemonicsCompiler.compile(loadFile(fileName));

		/*
	    for (Instruction instruction : mnemonicsCompiler.getInstructions()) {
		    System.out.printf("%d %s   =>   %s%n", instruction.getAddress().toInt(), padRight(instruction.getMnemonic(), 15), instruction.getMachineCode().toBin());
	    }
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}

	private static void initializeStorage(Storage storage, String[] args) {
		for (String arg : args) {
			Binary address = new Binary(Integer.parseInt(arg.substring(0, 3)));
			Binary value = new Binary(Integer.parseInt(arg.substring(4)));
			storage.set(address, value);
		}
	}

	private static ArrayList<String> loadFile(String fileName) {
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(fileName));

			while ((sCurrentLine = br.readLine()) != null) {
				lines.add(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return lines;
	}
}
