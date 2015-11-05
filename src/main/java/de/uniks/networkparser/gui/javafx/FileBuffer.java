package de.uniks.networkparser.gui.javafx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.uniks.networkparser.String.CharList;
import de.uniks.networkparser.interfaces.Buffer;

public class FileBuffer extends Buffer{
	private BufferedReader reader;
	private File file;
	private CharList lookAHead = new CharList();
	private int length;
	private char currentChar;

	public FileBuffer withFile(String fileName) throws FileNotFoundException {
		withFile(new File(fileName));
		return this;
	}
	
	
	public FileBuffer withFile(File file) throws FileNotFoundException {
		this.file = file;
		this.length = (int) this.file.length();
		this.reader = new BufferedReader(new FileReader(this.file), 1024*1024);
		this.position = 0;
		return this;
	}
	
	@Override
	public int length() {
		return length;
	}

	@Override
	public char getChar() {
		char value = 0;
		if(lookAHead.length() > 0) {
			value = lookAHead.charAt(0);
			if(lookAHead.length() == 1) {
				lookAHead.clear();
			}else {
				lookAHead.addStart(1);
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
		int len = lookAHead.length();
		if(len>0) {
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
	public FileBuffer withLookAHead(CharSequence lookahead) {
		this.lookAHead.set(lookahead);
		this.currentChar = lookahead.charAt(0);
		this.lookAHead.addStart(1);
		this.position -= this.lookAHead.length();
		return this;
	}
	
	@Override
	public FileBuffer withLookAHead(char current) {
		this.lookAHead.set(this.currentChar);
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

