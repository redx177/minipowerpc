package ch.zhaw.minipowerpc;

import ch.zhaw.minipowerpc.storage.IStorable;

public class Binary implements IStorable {
	private String binary;
	private int integer;

	public Binary(String binary) {
		setBinary(binary);
		setInteger();
	}

	public Binary(int integer) {
		this(Integer.toBinaryString(integer));
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
		if (value.length() > 16) {
			value = value.substring(value.length()-16);
		}
		binary = String.format("%16s", value).replace(' ', isPositive(value) ? '0' : '1');
	}

	private void setInteger() {
		integer = (short)Integer.parseInt(binary, 2);
	}

	private boolean isPositive(String value) {
		if (value.length() <= 16) return true;
		return value.substring(0, 1).equals("0");
	}
}
