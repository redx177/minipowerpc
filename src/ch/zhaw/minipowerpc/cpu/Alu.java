package ch.zhaw.minipowerpc.cpu;

import ch.zhaw.minipowerpc.Binary;

public class Alu {
	private Register accu;
	private boolean carry;

	public Alu(Register accu) {
		this.accu = accu;
	}

	public void setCarry(boolean value) {
		carry = value;
	}

	public void Add(Binary summand) {
		int summand1 = accu.get().toInt();
		int summand2 = summand.toInt();
		accu.set(new Binary(summand1+summand2));
	}

	public boolean getCarry() {
		return carry;
	}

	public Register getAccu() {
		return accu;
	}
}
