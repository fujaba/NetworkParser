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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.xml.XMLEntity;

public class FileBuffer extends Buffer {
	public static final int BUFFER=4096;
	private BufferedReader reader;
	private File file;
	private CharacterBuffer lookAHead = new CharacterBuffer();
	private int length;
	private char currentChar;

	public FileBuffer withFile(String fileName) {
		withFile(new File(fileName));
		return this;
	}

	public FileBuffer withFile(File file, int cache) {
		this.file = file;
		this.length = (int) this.file.length();
		try {
			FileInputStream fis = new FileInputStream(this.file);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName(BaseItem.ENCODING));
			this.reader = new BufferedReader(isr, cache);
		}catch (Exception e) {
		}
		this.position = 0;
		return this;	
	}
	
	public FileBuffer withFile(File file) {
		return withFile(file, 1024*1024);
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
			int charInt = this.reader.read();
			if(charInt<0) {
				charInt =0;
			}
			value = (char)charInt; 
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
			this.lookAHead.clear();
			this.lookAHead.with(values, 0, len+read);
//			this.position = this.length();
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
		if(fileName == null || fileName.length()<1) {
			return false;
		}
		FileBuffer buffer = new FileBuffer();
		buffer.withFile(fileName);
		if(buffer.exist() == false) {
			if(buffer.createFile() == false) {
				return false;
			}
		}
		return buffer.write(data, appendData);
	}
	public static final boolean writeFile(String fileName, CharSequence data) {
		return writeFile(fileName, data, false);
	}
	
	public static final CharacterBuffer readResource(String file) {
		InputStream is = IdMap.class.getResourceAsStream(file);
		CharacterBuffer sb = new CharacterBuffer();
		if (is != null) {
			final byte[] buffer = new byte[BUFFER];
			try {
				int read;
				do {
					read = is.read(buffer);
					sb.with(new String(buffer, 0, read, BaseItem.ENCODING));
				} while (read>=0);
			} catch (IOException e) {
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return sb;
	}
	
	public static final CharacterBuffer readFile(String file) {
		File content=new File(file);
		CharacterBuffer sb = new CharacterBuffer();
        if(content.exists()){
   			final byte[] buffer = new byte[BUFFER];
   			int read;
   			FileInputStream is = null;
   			try {
   				is = new FileInputStream(content);
				do {
					read = is.read(buffer, 0, buffer.length);
					if (read>0) {
						sb.with(new String(buffer, 0, read, BaseItem.ENCODING));
//FIXME						sb.with(buffer, 0, read);
					}
				} while (read>=0);
//    				int count;
//    				while (count>=0) {
//    					count = ios.read(buffer);
//    					sb.with(new String(buffer, 0, count, BaseItem.ENCODING));
//    				}
   			} catch (IOException e) {
   			} finally {
   				try {
   					is.close();
   				} catch (IOException e) {
   				}
    		}
        }
        return sb;
    }
	
	public static BaseItem readBaseFile(String configFile){
		return readBaseFile(configFile, null);
	}
	
	public static BaseItem readBaseFile(String configFile, BaseItem container){
		// load it
		CharacterBuffer buffer = FileBuffer.readFile(configFile);
		if (buffer != null&&buffer.length()>0)
		{
			if(buffer.charAt(0)=='{'){
				JsonObject result;
				if(container instanceof JsonObject ) {
					result = (JsonObject) container;
				}else {
					result = new JsonObject();
				}
				return result.withValue(buffer);
			}
			if(buffer.charAt(0)=='['){
				JsonArray result;
				if(container instanceof JsonArray ) {
					result = (JsonArray) container;
				}else {
					result = new JsonArray();
				}
				return result.withValue(buffer);
			}
			if(buffer.charAt(0)=='<'){
				XMLEntity result;
				if(container instanceof XMLEntity ) {
					result = (XMLEntity) container;
				}else {
					result = new XMLEntity();
				}
				return result.withValue(buffer);
			}
		}
		return container;
	}
	
	public static final boolean deleteFile(String fileName) {
		File file;
		file = new File(fileName);

		if (file.exists()) {
			return file.delete();
		}
		return true;
	}
	
	public boolean createFile() {
		if(this.file == null) {
			return false;
		}
		File parentFile = file.getParentFile();
		if(parentFile == null || parentFile.exists()) {
			return true;
		}
		if(parentFile.mkdirs() == false) {
			return false;
		}
		try {
			return file.createNewFile();
		} catch (IOException e) {
		}
		return false;
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

	public boolean println(CharSequence string) {
		this.write(string, true);
		return newline();
	}
	
	public boolean newline() {
		return this.write(BaseItem.CRLF, true);
	}
}
