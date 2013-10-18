package ch.zhaw.minipowerpc.cpu;

public class Register<T> {
	private T value;

	public Register(T value) {
		this.value = value;
	}

	public Register() {
	}

	public T get() {
		return value;
	}
}
