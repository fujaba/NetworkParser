package de.uniks.networkparser.ext.io;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;

public class FileBuffer extends Buffer {
	private BufferedReader reader;
	private File file;
	private CharacterBuffer lookAHead = new CharacterBuffer();
	private int length;
	private char currentChar;

	public FileBuffer withFile(String fileName) throws FileNotFoundException {
		withFile(new File(fileName));
		return this;
	}

	public FileBuffer withFile(File file) throws FileNotFoundException {
		this.file = file;
		this.length = (int) this.file.length();
		FileInputStream fis = new FileInputStream(this.file);
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		this.reader = new BufferedReader(isr, 1024*1024);
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
	public String toString() {
		char[] values = new char[remaining()];
		int len = lookAHead.length();
		if(len>0) {
			for(int i = 0;i<len;i++) {
				values[i] = lookAHead.charAt(i);
			}
		}
		try {
			int max = values.length - len;
			int read = this.reader.read(values, len, max);
			if(read<max) {
				this.length = (max -read);
			}
			this.position = this.length();
		} catch (IOException e) {
		}
		return new String(values);
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
	public char nextClean(boolean currentValid) {
		char current = super.nextClean(currentValid);
		this.currentChar = current;
		return current;
	}

	@Override
	public char getCurrentChar() {
		if(currentChar != 0) {
			return currentChar;
		}
		char value = getChar();
		return value;
	}

	public byte getByte() {
		return (byte)getChar();
	}
}
