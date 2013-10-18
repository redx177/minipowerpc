package ch.zhaw.minipowerpc.storage;

import ch.zhaw.minipowerpc.cpu.Instruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage {
	Map<Integer, IStorable> storage = new HashMap<Integer, IStorable>();

	public Storage(List<Instruction> instructions) {
		for(Instruction instruction : instructions) {
			storage.put(instruction.getAddress(), instruction);
		}
	}

	public IStorable get(int address) {
		return storage.get(100);
	}
}
