package ch.zhaw.minipowerpc.cpu;

import ch.zhaw.minipowerpc.storage.IStorable;
import ch.zhaw.minipowerpc.storage.Storage;

public class ControlUnit {
	private Storage storage;
	private final Register<Integer> instructionCounter;
	private Instruction instructionRegister;
	private final Alu alu;
	private final Register<String> register1;
	private final Register<String> register2;
	private final Register<String> register3;

	public ControlUnit(Storage storage) throws InvalidInstructionException {
		this.storage = storage;
		this.instructionCounter = new Register<Integer>(100);
		this.alu = new Alu(new Register<String>());
		this.register1 = new Register<String>();
		this.register2 = new Register<String>();
		this.register3 = new Register<String>();
	}

	private void nextCycle() throws InvalidInstructionException {
		instructionRegister = loadFromStorage(instructionCounter.get());
	}

	private Instruction loadFromStorage(int address) throws InvalidInstructionException {
		IStorable storable = storage.get(address);
		if (storable instanceof Instruction) {
			return (Instruction)storable;
		}

		throw new InvalidInstructionException(address, storable.getClass().getName());
	}
}
