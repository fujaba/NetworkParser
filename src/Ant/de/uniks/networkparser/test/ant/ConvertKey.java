package de.uniks.networkparser.test.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import de.uniks.networkparser.bytes.converter.ByteConverterHex;

public class ConvertKey {
	String input;
	String output;
	public void execute() throws IOException {
		if(input == null) {
			System.err.println("No Inputfile defined");
		}
		File inputFile = new File(input);
		if(output == null) {
			output = input+".hex";
		}
		String key = new String(Files.readAllBytes(Paths.get(inputFile.toURI())));
		ByteConverterHex converterHex = new ByteConverterHex();
		String hexValue = converterHex.toString(key.getBytes(), key.length());
		FileOutputStream out = new FileOutputStream(output);
		out.write(hexValue.getBytes());
		out.close();
	}

	public void setInput(String value) {
		this.input = value;
	}

	public String getInput() {
		return input;
	}

	public void setOutput(String value) {
		this.output = value;
	}

	public String getOutput() {
		return output;
	}

	public static void main(String[] args) throws IOException {

		ConvertKey converter = new ConvertKey();
		converter.setInput("secring.gpg");
		converter.execute();
	}
}
