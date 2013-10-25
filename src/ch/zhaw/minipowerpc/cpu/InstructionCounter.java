package ch.zhaw.minipowerpc.cpu;

import ch.zhaw.minipowerpc.Binary;

public class InstructionCounter {
	private Binary address;
	public InstructionCounter() {
		address = new Binary(100);
	}

	public Binary get() {
		return address;
	}

	public void increment() {
		Binary newAddress = new Binary(address.toInt() + 2);
		address = newAddress;
	}

	public void jumpTo(Binary address) {
		this.address = address;
	}
}
