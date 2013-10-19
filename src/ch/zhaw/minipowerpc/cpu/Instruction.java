package ch.zhaw.minipowerpc.cpu;

import ch.zhaw.minipowerpc.Binary;
import ch.zhaw.minipowerpc.storage.IStorable;

public class Instruction implements IStorable {
	private final Binary address;
	private final String mnemonic;
	private final Binary machineCode;
	private final String comment;

	public Instruction(Binary address, String mnemonic, Binary machineCode, String comment) {
		this.address = address;
		this.mnemonic = mnemonic;
		this.machineCode = machineCode;
		this.comment = comment;
	}

	public Binary getAddress() {
		return address;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public Binary getMachineCode() {
		return machineCode;
	}

	public String getComment() {
		return comment;
	}
}
