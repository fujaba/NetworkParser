package de.uniks.networkparser.test.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.converter.ByteConverterHex;

public class ConvertKey {
	String input;
	String output;
	public void execute() {
		if(input == null) {
			System.err.println("No Inputfile defined");
			return;
		}
		File inputFile = new File(input);
		if(output == null) {
			output = input+".hex";
		}
		FileOutputStream out = null;
		try {
			String key = new String(Files.readAllBytes(Paths.get(inputFile.toURI())), Charset.forName("UTF-8"));
			ByteConverterHex converterHex = new ByteConverterHex();

			String hexValue = converterHex.toString(new ByteBuffer().with(key.getBytes(Charset.forName("UTF-8"))));
			out = new FileOutputStream(output);
			out.write(hexValue.getBytes(Charset.forName("UTF-8")));
		} catch (IOException e) {
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
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
