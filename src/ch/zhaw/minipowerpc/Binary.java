package ch.zhaw.minipowerpc;

import ch.zhaw.minipowerpc.storage.IStorable;

public class Binary implements IStorable {
	private String binary;
	private final int integer;

	public Binary(String binary) {
		this.binary = binary;
		integer = Integer.parseInt(binary, 2);
	}

	public Binary(int integer) {
		this.integer = integer;
		binary = Integer.toBinaryString(integer);
	}

	public String toBin() {
		return binary;
	}

	public int toInt() {
		return integer;
	}
}
