package ch.zhaw.minipowerpc.cpu;

import ch.zhaw.minipowerpc.storage.IStorable;

public class Instruction implements IStorable {
	private final int address;
	private final String mnemonic;
	private final String machineCode;
	private final String comment;

	public Instruction(int address, String mnemonic, String machineCode, String comment) {
		this.address = address;
		this.mnemonic = mnemonic;
		this.machineCode = machineCode;
		this.comment = comment;
	}

	public int getAddress() {
		return address;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public String getComment() {
		return comment;
	}
}
