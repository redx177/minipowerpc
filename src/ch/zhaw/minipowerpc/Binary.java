package ch.zhaw.minipowerpc;

import ch.zhaw.minipowerpc.storage.IStorable;

public class Binary implements IStorable {
	private String binary;
	private final int integer;

	public Binary(String binary) {
		setBinary(binary);
		integer = (short)Integer.parseInt(binary, 2);
	}

	public Binary(int integer) {
		this.integer = integer;
		String binary = Integer.toBinaryString(integer);
		if (binary.length() > 16) {
			binary = binary.substring(binary.length()-16);
		}
		setBinary(binary);
	}

	public String toBin() {
		return binary;
	}

	public String toBinShort() {
		return binary.substring(binary.indexOf("1"));
	}

	public int toInt() {
		return integer;
	}

	private void setBinary(String value) {
		binary = String.format("%16s", value).replace(' ', isPositive(value) ? '0' : '1');
	}

	private boolean isPositive(String value) {
		if (value.length() <= 16) return true;
		return value.substring(0, 1).equals("0");
	}
}
