package de.uniks.networkparser.gui.javafx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.uniks.networkparser.interfaces.Buffer;

public class FileBuffer extends Buffer{
	private FileReader reader;
	private File file;
	private String lookAHead;
	private char currentChar;

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
		if(lookAHead != null) {
			value = lookAHead.charAt(0);
			if(lookAHead.length() == 1) {
				lookAHead = null;
			}else {
				lookAHead = lookAHead.substring(1);
			}
			this.position++;
			return value;
		}
		try {
			value = (char) this.reader.read();
			this.currentChar = value;
			position++;
		} catch (IOException e) {
		}
		return value;
	}
	
	@Override
	public String toText() {
		char[] values = new char[remaining()];
		int len = 0;
		if(lookAHead != null) {
			len = lookAHead.length();
			for(int i = 0;i<len;i++) {
				values[i] = lookAHead.charAt(i);
			}
		}
		try {
			this.reader.read(values, len, values.length - len);
			this.position = this.length();
		} catch (IOException e) {
		}
		return new String(values);
	}

	@Override
	public byte[] toArray() {
		return toText().getBytes();
	}


	@Override
	public FileBuffer withLookAHead(String lookahead) {
		this.lookAHead = lookahead;
		this.currentChar = lookahead.charAt(0);
		lookahead = lookahead.substring(1);
		this.position -= lookahead.length();
		return this;
	}
	
	@Override
	public FileBuffer withLookAHead(char current) {
		this.lookAHead = ""+this.currentChar;
		this.currentChar = current;
		this.position--;
		return this;
	}

	@Override
	public char getCurrentChar() {
		if(currentChar != 0) {
			return currentChar;
		}
		char value = getChar();
		return value;
	}
}

