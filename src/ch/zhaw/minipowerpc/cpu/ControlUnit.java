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

		return perform();
	}

	private boolean perform() throws InvalidInstructionException, StorageException {
		Instruction instruction = instructionRegister.getInstruction();
		Binary machineCode = instruction.getMachineCode();
		String machineCodeBinary = machineCode.toBin();

		String tempMachineCodeBinary = machineCodeBinary.substring(0, 4);
		// CLR, ADD, AND, OR
		if (tempMachineCodeBinary.equals("0000")) {
			String middle = machineCodeBinary.substring(6, 9);

			if (middle.equals("101")) {
				performClr();
				return true;
			}

			if (middle.equals("111")) {
				performAdd();
				return true;
			}

			if (middle.equals("100")) {
				performAnd();
				return true;
			}

			if (middle.equals("110")) {
				performOr();
				return true;
			}
		}

		if (tempMachineCodeBinary.equals("0001")) {
			String middle = machineCodeBinary.substring(6, 8);

			if (middle.equals("10")) {
				performBz();
				return true;
			}

			if (middle.equals("01")) {
				performBnz();
				return true;
			}

			if (middle.equals("11")) {
				performBc();
				return true;
			}

			if (middle.equals("00")) {
				performB();
				return true;
			}
		}

		tempMachineCodeBinary = machineCodeBinary.substring(0, 8);
		if (tempMachineCodeBinary.equals("00000001")) {
			performInc();
			return true;
		}

		if (tempMachineCodeBinary.equals("00000100")) {
			performDec();
			return true;
		}

		if (tempMachineCodeBinary.equals("00000101")) {
			alu.Sra();
			instructionCounter.increment();
			return true;
		}

		if (tempMachineCodeBinary.equals("00001000")) {
			alu.Sla();
			instructionCounter.increment();
			return true;
		}

		if (tempMachineCodeBinary.equals("00001001")) {
			alu.Srl();
			instructionCounter.increment();
			return true;
		}

		if (tempMachineCodeBinary.equals("00001100")) {
			alu.Sll();
			instructionCounter.increment();
			return true;
		}

		if (machineCodeBinary.substring(0, 1).equals("1")) {
			performAddd();
			return true;
		}

		tempMachineCodeBinary = machineCodeBinary.substring(0, 3);
		if (tempMachineCodeBinary.equals("010")) {
			performLwdd();
			return true;
		}

		if (tempMachineCodeBinary.equals("011")) {
			performSwdd();
			return true;
		}

		if (machineCodeBinary.substring(0, 9).equals("000000001")) {
			alu.Not();
			instructionCounter.increment();
			return true;
		}

		if (machineCodeBinary.substring(0, 5).equals("00110")) {
			performBzd();
			return true;
		}

		if (machineCodeBinary.substring(0, 5).equals("00101")) {
			performBnzd();
			return true;
		}

		if (machineCodeBinary.substring(0, 5).equals("00111")) {
			performBcd();
			return true;
		}

		if (machineCodeBinary.substring(0, 5).equals("00100")) {
			performBd();
			return true;
		}

		if (machineCodeBinary.equals("0000000000000000")) {
			return false;
		}

		throw new InvalidInstructionException(
				String.format("Unrecognized instruction. MachineCode: [%s] Mnemonic: [%s] Address: [%s]",
						machineCodeBinary,
						instruction.getMnemonic(),
						instruction.getAddress().toInt()));
	}

	private void performInc() {
		alu.Add(new Binary(1));
		instructionCounter.increment();
	}

	private void performDec() {
		alu.Add(new Binary(-1));
		instructionCounter.increment();
	}

	private void performClr() throws InvalidInstructionException {
		getCurrentRegister().set(new Binary("0"));
		alu.unsetCarry();
		instructionCounter.increment();
	}

	private void performAdd() throws InvalidInstructionException {
		alu.Add(getCurrentRegister().get());
		instructionCounter.increment();
	}

	private void performAddd() throws InvalidInstructionException {
		Instruction instruction = instructionRegister.getInstruction();
		String binary = instruction.getMachineCode().toBin().substring(1);
		Binary summand = new Binary((isPositive(binary) ? "0" : "1") + binary);
		alu.Add(summand);
		instructionCounter.increment();
	}

	private void performLwdd() throws InvalidInstructionException, StorageException {
		Register register = getCurrentRegister();
		Binary address = getCurrentAddress();
		Binary value = loadBinaryFromStorage(address);
		register.set(value);
		instructionCounter.increment();
	}

	private void performSwdd() throws InvalidInstructionException, StorageException {
		Register register = getCurrentRegister();
		Binary address = getCurrentAddress();
		storage.set(address, register.get());
		instructionCounter.increment();
	}

	private void performAnd() throws InvalidInstructionException {
		alu.And(getCurrentRegister().get());
		instructionCounter.increment();
	}

	private void performOr() throws InvalidInstructionException {
		alu.Or(getCurrentRegister().get());
		instructionCounter.increment();
	}

	private void performBz() throws InvalidInstructionException {
		if (alu.getAccu().get().toInt() == 0) {
			instructionCounter.jumpTo(getCurrentRegister().get());
		} else {
			instructionCounter.increment();
		}
	}

	private void performBnz() throws InvalidInstructionException {
		if (alu.getAccu().get().toInt() != 0) {
			instructionCounter.jumpTo(getCurrentRegister().get());
		} else {
			instructionCounter.increment();
		}
	}

	private void performBc() throws InvalidInstructionException {
		if (alu.getCarry() == 1) {
			instructionCounter.jumpTo(getCurrentRegister().get());
		} else {
			instructionCounter.increment();
		}
	}

	private void performB() throws InvalidInstructionException {
		instructionCounter.jumpTo(getCurrentRegister().get());
	}

	private void performBzd() {
		if (alu.getAccu().get().toInt() == 0) {
			instructionCounter.jumpTo(getCurrentAddress());
		} else {
			instructionCounter.increment();
		}
	}

	private void performBnzd() throws InvalidInstructionException {
		if (alu.getAccu().get().toInt() != 0) {
			instructionCounter.jumpTo(getCurrentAddress());
		} else {
			instructionCounter.increment();
		}
	}

	private void performBcd() throws InvalidInstructionException {
		if (alu.getCarry() == 1) {
			instructionCounter.jumpTo(getCurrentAddress());
		} else {
			instructionCounter.increment();
		}
	}

	private void performBd() throws InvalidInstructionException {
		instructionCounter.jumpTo(getCurrentAddress());
	}

	private Binary getCurrentAddress() {
		Instruction instruction = instructionRegister.getInstruction();
		return new Binary(instruction.getMachineCode().toBin().substring(6));
	}

	private Register getCurrentRegister() throws InvalidInstructionException {
		Instruction instruction = instructionRegister.getInstruction();
		String registerName = instruction.getMachineCode().toBin().substring(4, 6);
		if (registerName.equals("00")) return accu;
		if (registerName.equals("01")) return register1;
		if (registerName.equals("10")) return register2;
		if (registerName.equals("11")) return register3;

		throw new InvalidInstructionException(
				String.format("Register %s is invalid @address %d",
						registerName, instruction.getAddress()));
	}

	private boolean isPositive(String binary) {
		if (binary.length() < 15) return true;
		return binary.substring(0, 1).equals("0");
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
