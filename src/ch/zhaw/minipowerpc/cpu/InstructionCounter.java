package ch.zhaw.minipowerpc.cpu;

import ch.zhaw.minipowerpc.Binary;

public class InstructionCounter {
	private Register register;
	public InstructionCounter() {
		register = new Register(new Binary(100));
	}

	public Binary get() {
		return register.get();
	}

	public void increment() {
		Binary newAddress = new Binary(register.get().toInt() + 2);
		register.set(newAddress);
	}

	public void jumpTo(Binary address) {
		register.set(address);
	}
}
