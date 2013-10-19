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
		this.value = value;
	}
}
