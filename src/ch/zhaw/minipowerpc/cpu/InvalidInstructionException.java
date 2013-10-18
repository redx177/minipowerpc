package ch.zhaw.minipowerpc.cpu;

public class InvalidInstructionException extends Throwable {
	private final int address;
	private final String type;

	public InvalidInstructionException(int address, String typeName) {
		this.address = address;
		this.type = typeName;
	}

	public String getMessage() {
		return String.format("Invalid instruction of type [%s] found at address %d", type, address);
	}
}
