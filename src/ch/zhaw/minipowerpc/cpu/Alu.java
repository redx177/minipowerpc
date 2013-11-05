package ch.zhaw.minipowerpc.cpu;

import ch.zhaw.minipowerpc.Binary;

public class Alu {
	private Register accu;
	private int carry;

	public Alu(Register accu) {
		this.accu = accu;
	}

	public void unsetCarry() {
		carry = 0;
	}

	public void Add(Binary summand) {
		Binary summand1 = accu.get();
		int summand1Int = summand1.toInt();
		String summand1Bin = summand1.toBin();
		int summand2Int = summand.toInt();

		// Result
		int total = summand1Int + summand2Int;

		// Carry/Overflow
		if (total > 32767 || total < -32768) {
			carry = 1;
		}

		accu.set(new Binary(total));
	}

	public int getCarry() {
		return carry;
	}

	public Register getAccu() {
		return accu;
	}

	public void Sra() {
		String binary = accu.get().toBin();
		int index = binary.length() - 1;
		carry = binary.substring(index).equals("1") ? 1 : 0;

		accu.set(new Binary(String.format("%s0%s", binary.substring(0, 1), binary.substring(1, index))));
	}

	public void Sla() {
		String binary = accu.get().toBin();
		carry = binary.substring(1, 2).equals("1") ? 1 : 0;

		accu.set(new Binary(String.format("%s%s0", binary.substring(0, 1), binary.substring(2))));
	}

	public void Srl() {
		String binary = accu.get().toBin();
		int index = binary.length() - 1;
		carry = binary.substring(index).equals("1") ? 1 : 0;

		accu.set(new Binary("0" + binary.substring(0, index)));
	}

	public void Sll() {
		String binary = accu.get().toBin();
		carry = binary.substring(0, 1).equals("1") ? 1 : 0;

		accu.set(new Binary(binary.substring(1) + "0"));
	}

	public void And(Binary binary) {
		accu.set(new Binary(accu.get().toInt() & binary.toInt()));
	}

	public void Or(Binary binary) {
		accu.set(new Binary(accu.get().toInt() | binary.toInt()));
	}

	public void Not() {
		accu.set(new Binary(~accu.get().toInt()));
	}
}
