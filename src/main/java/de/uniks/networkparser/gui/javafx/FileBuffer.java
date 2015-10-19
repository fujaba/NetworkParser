package de.uniks.networkparser.gui.javafx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.uniks.networkparser.interfaces.Buffer;

public class FileBuffer extends Buffer{
	private FileReader reader;
	private File file;

	public FileBuffer withFile(String fileName) throws FileNotFoundException {
		withFile(new File(fileName));
		return this;
	}
	
	
	public FileBuffer withFile(File file) throws FileNotFoundException {
		this.file = file;
		this.reader = new FileReader(this.file);
		this.position = 0;
		return this;
	}
	
	@Override
	public int length() {
		return (int) this.file.length();
	}

	@Override
	public char getChar() {
		char value = 0;
		try {
			value = (char) this.reader.read();
			position++;
		} catch (IOException e) {
		}
		return value;
	}
	
	@Override
	public String toText() {
		char[] values = new char[remaining()];
		try {
			this.reader.read(values);
			this.position = this.length();
		} catch (IOException e) {
		}
		return new String(values);
	}

	@Override
	public byte[] toArray() {
		char[] values = new char[remaining()];
		try {
			this.reader.read(values);
			this.position = this.length();
		} catch (IOException e) {
		}
		return new String(values).getBytes();
	}


	@Override
	public Buffer withLookAHead(String lookahead) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public char getCurrentChar() {
		// TODO Auto-generated method stub
		return 0;
	}

}
