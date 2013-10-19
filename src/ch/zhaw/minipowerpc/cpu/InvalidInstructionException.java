package ch.zhaw.minipowerpc.cpu;

public class InvalidInstructionException extends Throwable {
	public InvalidInstructionException(String message) {
		super(message);
	}
}
