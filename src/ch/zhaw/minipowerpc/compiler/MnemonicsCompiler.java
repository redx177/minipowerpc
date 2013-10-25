package ch.zhaw.minipowerpc.compiler;

import ch.zhaw.minipowerpc.Binary;
import ch.zhaw.minipowerpc.cpu.Instruction;
import ch.zhaw.minipowerpc.storage.Storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MnemonicsCompiler {

	private Storage storage;

	public MnemonicsCompiler(Storage storage) {
		this.storage = storage;
	}

	public void compile(ArrayList<String> mnemonicLines) throws MnemonicsCompilerException {
		int line = 1;
		for (String code : mnemonicLines) {

			Pattern p = Pattern.compile("^(\\d{3})\\s+(\\w{1,4})(\\s+(.*?))?(\\s*;.*)?$");
			Matcher m = p.matcher(code);

			if (!m.find()) {
				throw new MnemonicsCompilerException(String.format("%s", "Unable to parse line " + line));
			}
			String addressString = m.group(1);
			String operationCode = m.group(2);
			String options = m.group(4);
			String comment = m.group(5);

			Binary address = validateAddress(addressString, line);
			String machineCodeString = validateOperationCode(operationCode, options, line);
			Binary machineCode = new Binary(machineCodeString);

			options = options != null && !options.equals("") ? " " + options : "";
			Instruction instruction = new Instruction(address, operationCode + options, machineCode, comment);
			storage.set(instruction.getAddress(), instruction);
			line++;
		}
	}

	private String validateOperationCode(String operationCode, String options, int line) throws MnemonicsCompilerException {
		if (operationCode.equals("CLR")) {
			String register = validateOptionAsRegister(options, line);
			return String.format("0000%s1010000000", register);
		}
		if (operationCode.equals("ADD")) {
			String register = validateOptionAsRegister(options, line);
			return String.format("0000%s1110000000", register);
		}
		if (operationCode.equals("ADDD")) {
			String number = validateOptionAsNumber(options, line);
			return String.format("1%s", number);
		}
		if (operationCode.equals("INC")) {
			validateOptionsAreEmpty(options, line);
			return "0000000100000000";
		}
		if (operationCode.equals("DEC")) {
			validateOptionsAreEmpty(options, line);
			return "0000010000000000";
		}
		if (operationCode.equals("LWDD")) {
			List<String> optionList = getRegisterAndAddressFromOptions(options, line);
			String register = validateOptionAsRegister(optionList.get(0), line);
			String address = validateOptionAsAddress(optionList.get(1), line);
			return String.format("0100%s%s", register, address);
		}
		if (operationCode.equals("SWDD")) {
			List<String> optionList = getRegisterAndAddressFromOptions(options, line);
			String register = validateOptionAsRegister(optionList.get(0), line);
			String address = validateOptionAsAddress(optionList.get(1), line);
			return String.format("0110%s%s", register, address);
		}
		if (operationCode.equals("SRA")) {
			validateOptionsAreEmpty(options, line);
			return "0000010100000000";
		}
		if (operationCode.equals("SLA")) {
			validateOptionsAreEmpty(options, line);
			return "0000100000000000";
		}
		if (operationCode.equals("SRL")) {
			validateOptionsAreEmpty(options, line);
			return "0000100100000000";
		}
		if (operationCode.equals("SLL")) {
			validateOptionsAreEmpty(options, line);
			return "0000110000000000";
		}
		if (operationCode.equals("AND")) {
			String register = validateOptionAsRegister(options, line);
			return String.format("0000%s1000000000", register);
		}
		if (operationCode.equals("OR")) {
			String register = validateOptionAsRegister(options, line);
			return String.format("0000%s1100000000", register);
		}
		if (operationCode.equals("NOT")) {
			validateOptionsAreEmpty(options, line);
			return "0000000010000000";
		}
		if (operationCode.equals("BZ")) {
			String register = validateOptionAsRegister(options, line);
			return String.format("0001%s1000000000", register);
		}
		if (operationCode.equals("BNZ")) {
			String register = validateOptionAsRegister(options, line);
			return String.format("0001%s0100000000", register);
		}
		if (operationCode.equals("BC")) {
			String register = validateOptionAsRegister(options, line);
			return String.format("0001%s1100000000", register);
		}
		if (operationCode.equals("B")) {
			String register = validateOptionAsRegister(options, line);
			return String.format("0001%s0000000000", register);
		}
		if (operationCode.equals("BZD")) {
			String number = validateOptionAsAddress(options, line);
			return String.format("001100%s", number);
		}
		if (operationCode.equals("BNZD")) {
			String number = validateOptionAsAddress(options, line);
			return String.format("001010%s", number);
		}
		if (operationCode.equals("BCD")) {
			String number = validateOptionAsAddress(options, line);
			return String.format("001110%s", number);
		}
		if (operationCode.equals("BD")) {
			String number = validateOptionAsAddress(options, line);
			return String.format("001000%s", number);
		}
		if (operationCode.equals("END")) {
			validateOptionsAreEmpty(options, line);
			return "0000000000000000";
		}

		throw new MnemonicsCompilerException(String.format("Unknown operationCode [%s] @line %d", operationCode, line));
	}

	private List<String> getRegisterAndAddressFromOptions(String options, int line) throws MnemonicsCompilerException {
		String[] optionList = options.split(",");
		if (optionList.length != 2) {
			throw new MnemonicsCompilerException(String.format("Wrong options received. Excpected register and address @line %s", line));
		}
		return Arrays.asList(optionList);
	}

	private void validateOptionsAreEmpty(String options, int line) throws MnemonicsCompilerException {
		if (options == null || options.equals("")) return;
		throw new MnemonicsCompilerException(String.format("No options allowed @line %d", line));
	}

	private String validateOptionAsAddress(String option, int line) throws MnemonicsCompilerException {

		int address = StringNumberToInt(option, line);

		if (address < 100 || address > 999) {
			throw new MnemonicsCompilerException(String.format("Address [%d] is out of range. Valid values are 99 < x < 1023", address));
		}

		return intToBinaryString(address, 10);
	}

	private int StringNumberToInt(String option, int line) throws MnemonicsCompilerException {
		if (!option.substring(0, 1).equals("#")) {
			throw new MnemonicsCompilerException(String.format("Number [%s] is invalid. Needs to start with a # @line %d", option, line));
		}
		option = option.substring(1);

		return StringToInt(option, line);
	}

	private String validateOptionAsNumber(String option, int line) throws MnemonicsCompilerException {
		int number = StringNumberToInt(option, line);

		if (number > 32767) {
			throw new MnemonicsCompilerException(String.format("Number [%d] is out of range. Valid values are x < 32767", number));
		}

		if (number >= 0) {
			return intToBinaryString(number, 15);
		}

		return TwosComplement(number, 15);
	}

	private String TwosComplement(int number, int length) {
		String binaryString = intToBinaryString(number, length);
		return binaryString;
	}

	private String validateOptionAsRegister(String option, int line) throws MnemonicsCompilerException {
		Pattern p = Pattern.compile("^(r|R)([0-3])$");
		Matcher m = p.matcher(option);

		if (!m.find()) {
			throw new MnemonicsCompilerException(String.format("%s", String.format("Invalid register specified in option [%s] @line %d", option, line)));
		}

		String register = m.group(2);
		return intToBinaryString(Integer.parseInt(register), 2);
	}

	private String intToBinaryString(int number, int length) {
		// To binary.
		String s = Integer.toBinaryString(number);
		// Pad with zeros if number is to short.
		s = String.format("%" + length + "s", s).replace(' ', '0');
		// If number was to long, trim it.
		return s.substring(s.length()-length);
	}

	private Binary validateAddress(String addressString, int line) throws MnemonicsCompilerException {
		int address = StringToInt(addressString, line);

		if (address < 100 || address > 498) {
			throw new MnemonicsCompilerException(String.format("Address [%d] is out of range. Valid values are 99 < x < 499). @line %d", address, line));
		}
		if (address % 2 == 1) {
			throw new MnemonicsCompilerException(String.format("Address [%d] is invalid. Only even addresses allowed on 2 byte system. @line %d", address, line));
		}

		return new Binary(address);
	}

	private int StringToInt(String addressString, int line) throws MnemonicsCompilerException {
		int address;
		try {
			address = Integer.parseInt(addressString);
		} catch (NumberFormatException nfe) {
			throw new MnemonicsCompilerException(String.format(String.format("Invalid number [%s]. @line %d", addressString, line)));
		}
		return address;
	}
}
