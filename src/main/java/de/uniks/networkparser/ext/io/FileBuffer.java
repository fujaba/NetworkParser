package de.uniks.networkparser.ext.io;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.xml.XMLEntity;

/**
 * Buffer for FileContext.
 *
 * @author Stefan Lindel
 */
public class FileBuffer extends Buffer {
	
	/** The Constant BUFFER. */
	public static final int BUFFER = 4096;
	private BufferedReader reader;
	private File file;
	private CharacterBuffer lookAHead = new CharacterBuffer();
	private int length;
	private char currentChar;
	
	/** The none. */
	public static byte NONE = 0;
	
	/** The append. */
	public static byte APPEND = 1;
	
	/** The override. */
	public static byte OVERRIDE = 2;

	/**
	 * With file.
	 *
	 * @param fileName the file name
	 * @return the file buffer
	 */
	public FileBuffer withFile(String fileName) {
		if (fileName != null) {
			withFile(new File(fileName));
		}
		return this;
	}

	/**
	 * With file.
	 *
	 * @param file the file
	 * @param cache the cache
	 * @return the file buffer
	 */
	public FileBuffer withFile(File file, int cache) {
		this.file = file;
		if (file == null) {
			return this;
		}
		this.length = (int) this.file.length();
		try {
			FileInputStream fis = asStream();
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName(BaseItem.ENCODING));
			this.reader = new BufferedReader(isr, cache);
		} catch (Exception e) {
		}
		this.position = 0;
		return this;
	}
	
	/**
	 * As stream.
	 *
	 * @return the file input stream
	 * @throws FileNotFoundException the file not found exception
	 */
	public FileInputStream asStream() throws FileNotFoundException {
		return new FileInputStream(this.file);
	}

	/**
	 * As file.
	 *
	 * @return the file
	 */
	public File asFile() {
		return this.file;
	}

	/**
	 * With file.
	 *
	 * @param file the file
	 * @return the file buffer
	 */
	public FileBuffer withFile(File file) {
		return withFile(file, 1024 * 1024);
	}

	/**
	 * Length.
	 *
	 * @return the int
	 */
	@Override
	public int length() {
		return length;
	}

	/**
	 * Exists.
	 *
	 * @return true, if successful
	 */
	public boolean exists() {
		if (this.file == null) {
			return false;
		}
		return this.file.exists();
	}

	/**
	 * Gets the char.
	 *
	 * @return the char
	 */
	@Override
	public char getChar() {
		char value = 0;
		if (this.reader == null) {
			return value;
		}
		if (lookAHead.length() > 0) {
			value = lookAHead.charAt(0);
			if (lookAHead.length() == 1) {
				lookAHead.clear();
			} else {
				lookAHead.trimStart(1);
			}
			this.position++;
			this.currentChar = value;
			return value;
		}
		try {
			int charInt = this.reader.read();
			if (charInt < 0) {
				charInt = 0;
			}
			value = (char) charInt;
			this.currentChar = value;
			position++;
		} catch (IOException e) {
		}
		return value;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		char[] values = new char[remaining()];
		int len = lookAHead.length();
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				values[i] = lookAHead.charAt(i);
			}
		}
		try {
			if (values.length == 0) {
				return "";
			}
			int max = values.length - len;
			int read = this.reader.read(values, len, max);
			if (read < max) {
				this.length = (max - read);
			}
			this.lookAHead.clear();
			this.lookAHead.with(values, 0, len + read);
		} catch (IOException e) {
		}
		return new String(values);
	}

	/**
	 * With look A head.
	 *
	 * @param lookahead the lookahead
	 * @return the file buffer
	 */
	@Override
	public FileBuffer withLookAHead(CharSequence lookahead) {
		this.lookAHead.set(lookahead);
		if (lookahead == null || lookahead.length() < 1) {
			this.currentChar = 0;
		} else {
			this.currentChar = lookahead.charAt(0);
		}
		this.lookAHead.trimStart(1);
		this.position -= this.lookAHead.length();
		return this;
	}

	/**
	 * With look A head.
	 *
	 * @param current the current
	 * @return the file buffer
	 */
	@Override
	public FileBuffer withLookAHead(char current) {
		this.lookAHead.set(this.currentChar);
		this.currentChar = current;
		this.position--;
		return this;
	}

	/**
	 * Next clean.
	 *
	 * @param currentValid the current valid
	 * @return the char
	 */
	@Override
	public char nextClean() {
		currentChar = super.nextClean();
		return currentChar;
	}
    /**
     * Next clean.
     *
     * @param currentValid the current valid
     * @return the char
     */
    @Override
    public char nextCleanSkip() {
        currentChar = super.nextCleanSkip();
        return currentChar;
    }

	/**
	 * Gets the current char.
	 *
	 * @return the current char
	 */
	@Override
	public char getCurrentChar() {
		if (currentChar != 0) {
			return currentChar;
		}
		char value = getChar();
		return value;
	}

	/**
	 * Gets the byte.
	 *
	 * @return the byte
	 */
	public byte getByte() {
		return (byte) getChar();
	}

	/**
	 * Close.
	 */
	public void close() {
		try {
			if (this.reader != null) {
				this.reader.close();
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Write file.
	 *
	 * @param fileName the file name
	 * @param data the data
	 * @param flag the flag
	 * @return the int
	 */
	public static final int writeFile(String fileName, CharSequence data, byte flag) {
		if (data != null) {
			return writeFile(fileName, data.toString().getBytes(), flag);
		}
		return -1;
	}
	
	   /**
     * Write file.
     *
     * @param fileName the file name
     * @param data the data
     * @return the int
     */
    public static final int writeFileUTF8(String fileName, String data) {
        if (data != null) {
            String charset = StringUtil.charset(data, StandardCharsets.ISO_8859_1.name(),BaseItem.ENCODING);
            byte[] ptext = data.getBytes(Charset.forName(charset)); 
            String value = new String(ptext, StandardCharsets.UTF_8); 
            return writeFile(fileName, value);
        }
        return -1;
    }

	/**
	 * Copy file.
	 *
	 * @param sourceFile the source file
	 * @param targetfileName the targetfile name
	 * @return the int
	 */
	public static final int copyFile(String sourceFile, String targetfileName) {
		if (sourceFile != null) {
			ByteBuffer readFile = readBinaryFile(sourceFile);
			if (readFile == null) {
				System.err.println(sourceFile + " not found");
				return -1;
			}
			return writeFile(targetfileName, readFile.array());
		}
		return -1;
	}

	/**
	 * Write file.
	 *
	 * @param fileName the file name
	 * @param data the data
	 * @param flag the flag
	 * @return the int
	 */
	public static final int writeFile(String fileName, byte[] data, byte flag) {
		if (fileName == null || fileName.length() < 1) {
			return -1;
		}
		if (data == null || data.length < 1 || (data.length == 1 && data[0] == 0)) {
			return -1;
		}
		if (flag < 0 || flag > OVERRIDE) {
			return -1;
		}
		FileBuffer buffer = new FileBuffer();
		buffer.withFile(fileName);
		if (buffer.exists()) {
			if (flag == NONE) {
				return -1;
			}
		} else {
			if (!buffer.createFile()) {
				return -1;
			}
		}
		return buffer.write(flag, data);
	}

	/**
	 * Write file.
	 *
	 * @param fileName the file name
	 * @param data the data
	 * @return the int
	 */
	public static final int writeFile(String fileName, CharSequence data) {
		return writeFile(fileName, data, OVERRIDE);
	}

	/**
	 * Write reource file.
	 *
	 * @param fileName the file name
	 * @param path the path
	 * @return the int
	 */
	public static final int writeReourceFile(String fileName, String path) {
		if (path == null) {
			return -1;
		}
		return writeFile(fileName, FileBuffer.readBinaryResource(path).array(), OVERRIDE);
	}

	/**
	 * Write file.
	 *
	 * @param fileName the file name
	 * @param data the data
	 * @return the int
	 */
	public static final int writeFile(String fileName, byte[] data) {
		return writeFile(fileName, data, OVERRIDE);
	}

	/**
	 * Read resource.
	 *
	 * @param file the file
	 * @return the character buffer
	 */
	public CharacterBuffer readResource(String file) {
		if (file == null) {
			return null;
		}
		return readResource(IdMap.class.getResourceAsStream(file));
	}
	
	/**
	 * Read resource.
	 *
	 * @param file the file
	 * @param reference the reference
	 * @return the character buffer
	 */
	public static CharacterBuffer readResource(String file, Class<?> reference) {
        if (file == null ||  reference == null) {
            return null;
        }
        return readResource(reference.getResourceAsStream(file));
    }
	
	/**
	 * Read all.
	 *
	 * @return the character buffer
	 */
	public CharacterBuffer readAll() {
		if (file == null) {
			return null;
		}
		return readFile(file);
	}

	/**
	 * Read resource.
	 *
	 * @param is the is
	 * @return the character buffer
	 */
	public static CharacterBuffer readResource(InputStream is) {
		CharacterBuffer sb = new CharacterBuffer();
		if (is != null) {
			final byte[] buffer = new byte[BUFFER];
			try {
				int read;
				do {
					read = is.read(buffer);
					if (read > 0) {
						sb.with(new String(buffer, 0, read, BaseItem.ENCODING));
					}
				} while (read >= 0);
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

	/**
	 * Read binary resource.
	 *
	 * @param file the file
	 * @return the byte buffer
	 */
	public static ByteBuffer readBinaryResource(String file) {
		return readBinaryResource(file, IdMap.class);
	}
	
	/**
	 * Read binary resource.
	 *
	 * @param file the file
	 * @param reference the reference
	 * @return the byte buffer
	 */
	public static ByteBuffer readBinaryResource(String file, Class<?> reference) {
		if (file == null) {
			return null;
		}
		InputStream is = reference.getResourceAsStream(file);
		ByteBuffer sb = new ByteBuffer();
		if (is != null) {
			final byte[] buffer = new byte[BUFFER];
			int read;
			try {
				do {
					read = is.read(buffer, 0, buffer.length);
					if (read > 0) {
						sb.addBytes(buffer, read, false);
					}
				} while (read >= 0);
			} catch (IOException e) {
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		sb.withPosition(0);
		return sb;
	}

	/**
	 * Read file.
	 *
	 * @param file the file
	 * @return the character buffer
	 */
	public static final CharacterBuffer readFile(String file) {
		if (file == null) {
			return null;
		}
		return readFile(new File(file));
	}

	/**
	 * Read file.
	 *
	 * @param file the file
	 * @return the character buffer
	 */
	public static final CharacterBuffer readFile(File file) {
		CharacterBuffer sb = new CharacterBuffer();
		if (file == null) {
			return sb;
		}
		if (file.exists()) {
		    final byte[] buffer = new byte[BUFFER];
			int read;
			FileInputStream is = null;
			try {
				is = new FileInputStream(file);
				do {
					read = is.read(buffer, 0, buffer.length);
					if (read > 0) {
						sb.with(new String(buffer, 0, read, BaseItem.ENCODING));
					}
				} while (read >= 0);
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

	/**
	 * Read binary file.
	 *
	 * @param file the file
	 * @return the byte buffer
	 */
	public static final ByteBuffer readBinaryFile(String file) {
		if (file == null) {
			return null;
		}
		File content = new File(file);
		ByteBuffer sb = new ByteBuffer();
		if (content.exists()) {
			final byte[] buffer = new byte[BUFFER];
			int read;
			FileInputStream is = null;
			try {
				is = new FileInputStream(content);
				do {
					read = is.read(buffer, 0, buffer.length);
					if (read > 0) {
						sb.addBytes(buffer, read, false);
					}
				} while (read >= 0);
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

	/**
	 * Read base file.
	 *
	 * @param configFile the config file
	 * @return the base item
	 */
	public static BaseItem readBaseFile(String configFile) {
		return readBaseFile(configFile, null);
	}

	/**
	 * Read base file resource.
	 *
	 * @param file the file
	 * @param referenceClass the reference class
	 * @return the base item
	 */
	public static BaseItem readBaseFileResource(String file, Class<?> referenceClass) {
		if (referenceClass == null || file == null) {
			return null;
		}
		InputStream stream = referenceClass.getResourceAsStream(file);
		CharacterBuffer buffer = readResource(stream);
		return parsingBuffer(buffer, null);

	}

	/**
	 * Read base file.
	 *
	 * @param configFile the config file
	 * @param container the container
	 * @return the base item
	 */
	public static BaseItem readBaseFile(String configFile, BaseItem container) {
		/* load it */
		CharacterBuffer buffer = FileBuffer.readFile(configFile);
		return parsingBuffer(buffer, container);
	}

	private static BaseItem parsingBuffer(CharacterBuffer buffer, BaseItem container) {
		if (buffer != null && buffer.length() > 0) {
			char startCharacter = buffer.nextClean();
			if (startCharacter == '{') {
				JsonObject result;
				if (container instanceof JsonObject) {
					result = (JsonObject) container;
				} else {
					result = new JsonObject();
				}
				result.withValue(buffer);
				if (buffer.isEnd()) {
					return result;
				}
				/* buffer not at end */
				JsonArray array = new JsonArray();
				array.add(result);
				while (!buffer.isEndCharacter()) {
					result = new JsonObject();
					result.withValue(buffer);
					array.add(result);
				}
				return array;
			}
			if (startCharacter == '[') {
				JsonArray result;
				if (container instanceof JsonArray) {
					result = (JsonArray) container;
				} else {
					result = new JsonArray();
				}
				return result.withValue(buffer);
			}
			if (startCharacter == '<') {
				XMLEntity result;
				if (container instanceof XMLEntity) {
					result = (XMLEntity) container;
				} else {
					result = new XMLEntity();
				}
				return result.withValue(buffer);
			}
		}
		return container;
	}

	/**
	 * Delete file.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 */
	public static final boolean deleteFile(String fileName) {
		if (fileName == null) {
			return false;
		}
		File file;
		file = new File(fileName);

		if (file.exists()) {
			return file.delete();
		}
		return true;
	}

	/**
	 * Creates the file.
	 *
	 * @return true, if successful
	 */
	public boolean createFile() {
		return FileBuffer.createFile(this.file);
	}

	/**
	 * Creates the file.
	 *
	 * @param file the file
	 * @return true, if successful
	 */
	public static boolean createFile(File file) {
		if (file == null) {
			return false;
		}
		File parentFile = file.getParentFile();
		if (parentFile == null || parentFile.exists()) {
			return true;
		}
		if (!parentFile.mkdirs()) {
			return false;
		}
		try {
			return file.createNewFile();
		} catch (IOException e) {
		}
		return false;
	}
	
	/**
	 * Creates the folder.
	 *
	 * @param file the file
	 * @return true, if successful
	 */
	public static boolean createFolder(String file) {
		if (file == null) {
			return false;
		}
		File folder = new File(file);
		if(folder.exists()) {
			return true;
		}
		return folder.mkdirs();
	}

	/**
	 * Write.
	 *
	 * @param flag the flag
	 * @param data the data
	 * @return the int
	 */
	public int write(byte flag, CharSequence data) {
		if (data != null) {
			return write(flag, data.toString().getBytes());
		}
		return -1;
	}
	
	/**
	 * Write binary.
	 *
	 * @param data the data
	 * @return the int
	 */
	public int writeBinary(byte... data) {
		return write(APPEND, data);
	}
	
	/**
	 * Write.
	 *
	 * @param flag the flag
	 * @param data the data
	 * @return the int
	 */
	public int write(byte flag, byte... data) {
		if (this.file == null || data == null) {
			return -1;
		}
		if (data.length == 1 && data[0] == 0) {
			return -1;
		}
		try {
			boolean append = false;
			if (flag == APPEND) {
				append = true;
			}
			FileOutputStream os = new FileOutputStream(this.file, append);
			os.write(data);
			os.flush();
			os.close();
			return data.length;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return -1;
	}

	/**
	 * Println.
	 *
	 * @param string the string
	 * @return true, if successful
	 */
	public boolean println(CharSequence string) {
		this.write(APPEND, string);
		return newline();
	}

	/**
	 * Newline.
	 *
	 * @return true, if successful
	 */
	public boolean newline() {
		return this.write(APPEND, BaseItem.CRLF) > 0;
	}

	/**
	 * Skip.
	 *
	 * @param input the input
	 * @param numToSkip the num to skip
	 * @return the long
	 */
	public static long skip(InputStream input, long numToSkip) {
		if (input == null) {
			return -1;
		}
		long available = numToSkip;
		try {
			while (numToSkip > 0) {
				long skipped = input.skip(numToSkip);
				if (skipped == 0) {
					break;
				}
				numToSkip -= skipped;
			}
		} catch (IOException e) {
			return -1;
		}
		byte[] byteSkip = new byte[BUFFER];
		while (numToSkip > 0) {
			final int read = readFully(input, byteSkip, 0, (int) Math.min(numToSkip, BUFFER));
			if (read < 1) {
				break;
			}
			numToSkip -= read;
		}
		return available - numToSkip;
	}

	/**
	 * Read fully.
	 *
	 * @param input the input
	 * @param b the b
	 * @return the int
	 */
	public static int readFully(final InputStream input, final byte[] b) {
		if (b == null) {
			return -1;
		}
		return readFully(input, b, 0, b.length);
	}

	/**
	 * Copy.
	 *
	 * @param input the input
	 * @param output the output
	 * @return the long
	 */
	public static long copy(final InputStream input, final OutputStream output) {
		if (input == null || output == null) {
			return -1;
		}
		final byte[] buffer = new byte[BUFFER];
		int n = 0;
		long count = 0;
		try {
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
				count += n;
			}
		} catch (Exception e) {
			return -1;
		}
		return count;
	}

	/**
	 * Read fully.
	 *
	 * @param input the input
	 * @param b the b
	 * @param offset the offset
	 * @param len the len
	 * @return the int
	 */
	public static int readFully(final InputStream input, final byte[] b, final int offset, final int len) {
		if (b == null || input == null || len < 0 || offset < 0 || len + offset > b.length) {
			return -1;
		}
		int count = 0, x = 0;
		try {
			while (count != len) {
				x = input.read(b, offset + count, len - count);
				if (x == -1) {
					break;
				}
				count += x;
			}
		} catch (Exception e) {
			return -1;
		}
		return count;
	}
}
