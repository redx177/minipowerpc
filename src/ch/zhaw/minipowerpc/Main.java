package ch.zhaw.minipowerpc;

import ch.zhaw.minipowerpc.compiler.MnemonicsCompiler;
import ch.zhaw.minipowerpc.compiler.MnemonicsCompilerException;
import ch.zhaw.minipowerpc.cpu.ControlUnit;
import ch.zhaw.minipowerpc.cpu.Instruction;
import ch.zhaw.minipowerpc.cpu.InvalidInstructionException;
import ch.zhaw.minipowerpc.storage.Storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
	    ArrayList<Instruction> instructions = null;
	    try {
		    instructions = new MnemonicsCompiler().compile(loadFile("D:\\Git\\minipowerpc\\examples\\MathExample.slang"));
	    } catch (MnemonicsCompilerException e) {
		    System.out.printf("Unable to parse code. Error: "+e.getMessage());
		    return;
	    }

	    /*
	    for (Instruction instruction : instructions) {
		    System.out.println(instruction.getMnemonic());
		    System.out.println(instruction.getMachineCode());
		    System.out.println("123456789ABCDEF0");
		    System.out.println("--------------------");
	    }
	    */

	    Storage storage = new Storage(instructions);
	    try {
		    ControlUnit controlUnit = new ControlUnit(storage);
	    } catch (InvalidInstructionException e) {
		    System.out.printf("Invalid instruction caught. Error: "+e.getMessage());
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
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return lines;
	}
}
