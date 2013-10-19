package ch.zhaw.minipowerpc.storage;

import ch.zhaw.minipowerpc.Binary;
import ch.zhaw.minipowerpc.cpu.Instruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage {
	Map<Integer, IStorable> storage = new HashMap<Integer, IStorable>();

	public IStorable get(Binary address) throws StorageException {
		return get(address.toInt());
	}

	public IStorable get(int address) throws StorageException {
		IStorable storable = storage.get(address);
		if (storable == null) {
			throw new StorageException(String.format("Found NULL in storage @address %s", address));
		}
		return storable;
	}

	public void set(Binary address, IStorable storable) {
		storage.put(address.toInt(), storable);
	}
}
