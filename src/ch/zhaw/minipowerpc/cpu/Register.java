package ch.zhaw.minipowerpc.cpu;

import ch.zhaw.minipowerpc.Binary;

public class Register {
	private Binary value;

	public Register(Binary value) {
		set(value);
	}

	public Register() {
		set(new Binary(0));
	}

	public Binary get() {
		return value;
	}

	public void set(Binary value) {
		String s = String.format("%16s", value.toBin()).replace(' ', isPositive(value) ? '0' : '1');
		this.value = new Binary(s);
	}

	private boolean isPositive(Binary value) {
		if (value.toBin().length() <= 16) return true;
		return value.toBin().substring(0, 1).equals("0");
	}
}
