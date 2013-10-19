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

	private static final String fileName = "D:\\Git\\minipowerpc\\examples\\MathExample.slang";
	private static final int instructionPredictionCount = 10;
	private static final int storageDisplayCount = 15;
	private static final int storageOffset = 500;

	public static void main(String[] args) {
		Storage storage = new Storage();

		initializeStorage(storage, args);

		try {
			compile(storage);
		} catch (MnemonicsCompilerException e) {
			System.out.printf("Unable to parse code. Error: " + e.getMessage());
			return;
		}

		try {
			run(storage);
		} catch (InvalidInstructionException e) {
			System.out.printf("Invalid instruction. Error: " + e.getMessage());
			return;
		} catch (StorageException e) {
			System.out.printf("Storage exception. Error: " + e.getMessage());
			return;
		}
	}

	private static void run(Storage storage) throws InvalidInstructionException, StorageException {
		ControlUnit controlUnit = new ControlUnit(storage);


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


			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
			printHeader(controlUnit, instruction, address, alu, accu, reg1, reg2, reg3);
			printInstructionsAndStorage(storage, address, history);
			try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void printInstructionsAndStorage(Storage storage, Binary currentInstructionAddress,
	                                                LinkedList<Instruction> history) {

		System.out.printf("Instructions: %38s Storage:%n", " ");

		int c = history.size();
		int i = 1;
		int empty = 0;
		List<String> instructionList = new ArrayList<String>();
		for (i = 0; i < 5; i++) {
			String mnemonic;
			String machineCode;
			if (5-c <= i) {
				Instruction instruction = history.get(i - empty);
				mnemonic = instruction.getMnemonic();
				machineCode = instruction.getMachineCode().toBin();
			} else {
				mnemonic = "";
				machineCode = "";
				empty++;
			}
			instructionList.add(String.format(" %d %s %s", i - 5, padRight(mnemonic, 15), machineCode));
		}

		for (i = 1; i < instructionPredictionCount + 1; i++) {
			String mnemonic;
			String machineCode;
			try {
				Instruction instruction = (Instruction) storage.get(currentInstructionAddress.toInt() + i * 2);
				mnemonic = instruction.getMnemonic();
				machineCode = instruction.getMachineCode().toBin();
			} catch (StorageException e) {
				mnemonic = "";
				machineCode = "";
			}
			instructionList.add(String.format(" %2d %s %s", i-1, padRight(mnemonic, 15), machineCode));
		}

		List<String> storageList = new ArrayList<String>();
		for (i = 0; i < storageDisplayCount; i++) {
			int address = storageOffset + 2 * i;
			Binary value;
			try {
				value = (Binary)storage.get(address);
			} catch (StorageException e) {
				value = new Binary(0);
			}
			storageList.add(String.format("%d %5d (%s)", address, value.toInt(), value.toBin()));
		}

		for (i = 0; i < 15; i++) {
			System.out.printf(" %s %10s %s%n", padRight(instructionList.get(i), 40), " ", storageList.get(i));
		}
	}

	private static void printHeader(ControlUnit controlUnit, Instruction instruction, Binary address,
	                                Alu alu, Binary accu, Binary reg1, Binary reg2, Binary reg3) {
		System.out.printf("╔════════════════════╤═════════════╤═════════════╗%n");
		System.out.printf("║ Befehlsregister: %16s│ Akku: %5d %s│ Carry: %d %10s ║%n",
				" ",
				accu.toInt(), padRight("("+accu.toBin()+")",10),
				(alu.getCarry() ? 1 : 0),
				" ");

		System.out.printf("║  - Address: %d %s│ REG1: %5d %s│ Befehlszähler: %d  ║%n",
				address.toInt(), padRight("("+address.toBin()+")", 17),
				reg1.toInt(), padRight("("+reg1.toBin()+")", 10),
				controlUnit.getInstructionCounter().get().toInt());
		System.out.printf("║  - MachineCode: %s │ REG2: %5d %s│ %19s ║%n",
				instruction.getMachineCode().toBin(),
				reg2.toInt(), padRight("("+reg2.toBin()+")", 10),
				" ");
		System.out.printf("║  - Mnemonic: %s│ REG3: %5d %s│ %19s ║%n",
				padRight(instruction.getMnemonic(), 20),
				reg3.toInt(), padRight("("+reg3.toBin()+")", 10),
				" ");
		System.out.printf("╚════════════════════╧═════════════╧═════════════╝%n");
	}

	private static String padRight(String s, int length) {
		StringBuilder sb = new StringBuilder(s);
		for (int i=0; i < length -s.length(); i++) {
			sb.append(" ");
		}

		return sb.toString();
	}

	private static void compile(Storage storage) throws MnemonicsCompilerException {
		new MnemonicsCompiler(storage).compile(loadFile(fileName));

	    /*
	    for (Instruction instruction : instructions) {
		    System.out.println(instruction.getMnemonic());
		    System.out.println(instruction.getMachineCode());
		    System.out.println("123456789ABCDEF0");
		    System.out.println("--------------------");
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
