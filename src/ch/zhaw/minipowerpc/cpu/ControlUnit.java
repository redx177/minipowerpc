package ch.zhaw.minipowerpc.cpu;

import ch.zhaw.minipowerpc.Binary;
import ch.zhaw.minipowerpc.storage.IStorable;
import ch.zhaw.minipowerpc.storage.Storage;
import ch.zhaw.minipowerpc.storage.StorageException;

public class ControlUnit {
	private final Register accu;
	private Storage storage;
	private final InstructionCounter instructionCounter;
	private InstructionRegister instructionRegister;
	private final Alu alu;
	private final Register register1;
	private final Register register2;
	private final Register register3;

	public ControlUnit(Storage storage) throws InvalidInstructionException {
		this.storage = storage;
		instructionCounter = new InstructionCounter();
		instructionRegister = new InstructionRegister();
		accu = new Register();
		alu = new Alu(accu);
		register1 = new Register();
		register2 = new Register();
		register3 = new Register();
	}

	public boolean nextCycle() throws InvalidInstructionException, StorageException {
		Instruction instruction = loadInstructionFromStorage(instructionCounter.get());
		instructionRegister.load(instruction);

		boolean hasNext = perform();

		instructionCounter.increment();

		return hasNext;
	}

	private boolean perform() throws InvalidInstructionException, StorageException {
		Instruction instruction = instructionRegister.getInstruction();
		Binary machineCode = instruction.getMachineCode();

		// CLR, ADD, INC, DEC, SRA, SLA, SRL, SLL, AND, OR, NOT
		if (machineCode.toBin().substring(0, 4).equals("0000")) {
			String middle = machineCode.toBin().substring(6, 9);

			if (middle == "101") {
				performClr();
				return true;
			}

			if (middle == "111") {
				performAdd();
				return true;
			}
		}

		if (machineCode.toBin().substring(0, 3).equals("010")) {
			performLwdd();
			return true;
		}

		if (machineCode.equals("0000000000000000")) {
			return false;
		}

		throw new InvalidInstructionException(
				String.format("Unrecognized instruction. MachineCode: [%s] Mnemonic: [%s] Address: [%s]",
						machineCode.toBin(),
						instruction.getMnemonic(),
						instruction.getAddress().toInt()));
	}

	private void performClr() throws InvalidInstructionException {
		getCurrentRegister().set(new Binary("0000000000000000"));
		alu.setCarry(false);
	}

	private void performAdd() throws InvalidInstructionException {
		alu.Add(getCurrentRegister().get());
	}

	private void performLwdd() throws InvalidInstructionException, StorageException {
		Register register = getCurrentRegister();
		Instruction instruction = instructionRegister.getInstruction();
		Binary address = new Binary(instruction.getMachineCode().toBin().substring(6));
		Binary value = loadBinaryFromStorage(address);
		register.set(value);
	}

	private Register getCurrentRegister() throws InvalidInstructionException {
		Instruction instruction = instructionRegister.getInstruction();
		String registerName = instruction.getMachineCode().toBin().substring(5, 7);
		if (registerName.equals("00")) return accu;
		if (registerName.equals("01")) return register1;
		if (registerName.equals("10")) return register2;
		if (registerName.equals("11")) return register3;

		throw new InvalidInstructionException(
				String.format("Register %s is invalid @address %d",
						registerName, instruction.getAddress()));
	}

	private Instruction loadInstructionFromStorage(Binary address) throws InvalidInstructionException, StorageException {
		IStorable storable = storage.get(address);
		if (storable instanceof Instruction) {
			return (Instruction) storable;
		}

		throw new InvalidInstructionException(
				String.format("Invalid value found in storage @address %d. Expected Instruction, got %s",
						address, storable.getClass().getName()));
	}

	private Binary loadBinaryFromStorage(Binary address) throws InvalidInstructionException, StorageException {
		IStorable storable = storage.get(address);
		if (storable instanceof Binary) {
			return (Binary) storable;
		}

		throw new InvalidInstructionException(
				String.format("Invalid value found in storage @address %d. Expected Binary, got %s",
						address, storable.getClass().getName()));
	}

	public InstructionCounter getInstructionCounter() {
		return instructionCounter;
	}

	public InstructionRegister getInstructionRegister() {
		return instructionRegister;
	}

	public Alu getAlu() {
		return alu;
	}

	public Register getRegister1() {
		return register1;
	}

	public Register getRegister2() {
		return register2;
	}

	public Register getRegister3() {
		return register3;
	}
}
