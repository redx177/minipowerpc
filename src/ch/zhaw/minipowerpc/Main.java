package ch.zhaw.minipowerpc;

import ch.zhaw.minipowerpc.compiler.MnemonicsCompiler;
import ch.zhaw.minipowerpc.compiler.MnemonicsCompilerException;
import ch.zhaw.minipowerpc.cpu.*;
import ch.zhaw.minipowerpc.storage.IStorable;
import ch.zhaw.minipowerpc.storage.Storage;
import ch.zhaw.minipowerpc.storage.StorageException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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

			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
			InstructionRegister instructionRegister = controlUnit.getInstructionRegister();


			System.out.printf("╔════════════════════╤═════════╤════════════╗%n");

			Alu alu = controlUnit.getAlu();
			Binary accu = alu.getAccu().get();
			System.out.printf("║ Befehlsregister: %16s│ Akku: %d (%s)  │ Carry: %d %9s ║%n",
					" ",
					accu.toInt(), accu.toBin(),
					(alu.getCarry() ? 1 : 0),
					" ");

			Instruction instruction = instructionRegister.getInstruction();
			Binary address = instruction.getAddress();
			Binary reg1 = controlUnit.getRegister1().get();
			Binary reg2 = controlUnit.getRegister2().get();
			Binary reg3 = controlUnit.getRegister3().get();
			System.out.printf("║  - Address: %d (%10s) %4s│ REG1: %d (%s) %3s│ Befehlszähler: %d ║%n",
					address.toInt(), address.toBin(),
					" ",
					reg1.toInt(), reg1.toBin(),
					" ",
					controlUnit.getInstructionCounter().get().toInt());
			System.out.printf("║  - MachineCode: %s │ REG2: %d (%s) %3s│ %18s ║%n",
					instruction.getMachineCode().toBin(),
					reg2.toInt(), reg3.toBin(),
					" ",
					" ");
			System.out.printf("║  - Mnemonic: %s %7s│ REG3: %d (%s) %3s│ %18s ║%n",
					instruction.getMnemonic(),
					" ",
					reg2.toInt(), reg3.toBin(),
					" ",
					" ");
			System.out.printf("╚════════════════════╧═════════╧════════════╝%n");

			System.out.printf("Instructions: %18s Storage:%n", " ");

			LinkedList<Instruction> history = instructionRegister.getHistory();
			int c = history.size();
			int i = 1;
			int empty = 0;
			List<String> instructionList = new ArrayList<String>();
			for (i = 0; i < 5; i++) {
				String s;
				if (5-c <= i) {
					Instruction pastInstruction = history.get(i - empty);
					s = pastInstruction.getMnemonic();
				} else {
					s = "";
					empty++;
				}
				instructionList.add(String.format(" %d %s", i - 5, s));
			}

			for (i = 1; i < instructionPredictionCount + 1; i++) {
				String s;
				try {
					Instruction futureInstruction = (Instruction) storage.get(address.toInt() + i * 2);
					s = futureInstruction.getMnemonic();
				} catch (StorageException e) {
					s = "";
				}
				instructionList.add(String.format(" %2d %s", i-1, s));
			}

			List<String> storageList = new ArrayList<String>();
			for (i = 0; i < storageDisplayCount; i++) {
				int intAddress = storageOffset + 2 * i;
				Binary stored;
				try {
					stored = (Binary)storage.get(intAddress);
				} catch (StorageException e) {
					stored = new Binary(0);
				}
				storageList.add(String.format("%d %5d %s", intAddress, stored.toInt(), stored.toBin()));
			}

			for (i = 0; i < 15; i++) {
				System.out.printf(" %s            %s%n", padRight(instructionList.get(i)), storageList.get(i));
			}

			try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String padRight(String s) {
		StringBuilder sb = new StringBuilder(s);
		for (int i=0; i < 20-s.length(); i++) {
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
