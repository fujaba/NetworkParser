package de.uniks.networkparser.ext.io;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

	public FileBuffer withFile(String fileName) {
		withFile(new File(fileName));
		return this;
	}

	public FileBuffer withFile(File file) {
		this.file = file;
		this.length = (int) this.file.length();
		try {
			FileInputStream fis = new FileInputStream(this.file);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			this.reader = new BufferedReader(isr, 1024*1024);
		}catch (Exception e) {
		}
		this.position = 0;
		return this;
	}

	@Override
	public int length() {
		return length;
	}
	
	public boolean exist() {
		if(this.file == null) {
			return false;
		}
		return this.file.exists();
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
	
	public void close() {
		try {
			this.reader.close();
		} catch (IOException e) {
		}
	}
	
	public static final boolean writeFile(String fileName, CharSequence data, boolean appendData) {
		FileBuffer buffer = new FileBuffer();
		buffer.withFile(fileName);
		if(buffer.exist() == false) {
			if(buffer.createFile()) {
				return false;
			}
		}
		return buffer.write(data, appendData);
	}
	public static final boolean writeFile(String fileName, CharSequence data) {
		return writeFile(fileName, data, false);
	}

	private boolean createFile() {
		if(this.file == null) {
			return false;
		}
		return file.getParentFile().mkdirs();
	}
	
	public boolean write(CharSequence data, boolean append) {
		if(this.file == null) {
			return false;
		}
		try {
			FileOutputStream os = new FileOutputStream(this.file, append);
			os.write(data.toString().getBytes());
			os.flush();
			os.close();
			return true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return false;
	}
}
