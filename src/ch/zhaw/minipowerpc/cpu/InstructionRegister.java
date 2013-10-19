package ch.zhaw.minipowerpc.cpu;

import java.util.LinkedList;

public class InstructionRegister {
	private final int historySize = 5;
	private LinkedList<Instruction> instructions = new LinkedList<Instruction>();

	public void load(Instruction instruction) {
		this.instructions.add(instruction);

		if (instructions.size() == historySize+1) {
			instructions.removeFirst();
		}
	}

	public Instruction getInstruction() {
		return instructions.getLast();
	}

	public LinkedList<Instruction> getHistory() {
		return instructions;
	}
}
