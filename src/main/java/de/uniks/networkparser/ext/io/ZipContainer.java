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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.ByteEntity;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.xml.XMLEntity;

/**
 * Container for ZIP.
 *
 * @author Stefan Lindel
 */
public class ZipContainer {
	private static final String XML = "xml";
	private static final String BINARY = "bin";
	private static final String JSON = "json";

	/**
	 * Gets the file name.
	 *
	 * @param data the data
	 * @return the file name
	 */
	public String getFileName(BaseItem data) {
		String extension = "txt";
		String name = "data";
		if (data != null) {
			name = data.getClass().getName();
			int pos = name.lastIndexOf(".");
			if (pos > 0) {
				name = name.substring(pos + 1);
			}
			if (data instanceof JsonObject || data instanceof JsonArray) {
				extension = JSON;
			}
			if (data instanceof XMLEntity) {
				extension = XML;
			}
			if (data instanceof ByteEntity) {
				extension = BINARY;
			}
		}
		return name + "." + extension;
	}

	/**
	 * Gets the new instance from file name.
	 *
	 * @param fileName the file name
	 * @return the new instance from file name
	 */
	public BaseItem getNewInstanceFromFileName(String fileName) {
		if (fileName != null) {
			int pos = fileName.lastIndexOf(".");
			String extension = "";
			if (pos >= 0) {
				extension = fileName.substring(pos + 1);
				fileName = fileName.substring(0, pos);
			}
			if (BINARY.equals(extension)) {
				return new ByteEntity();
			} else if (XML.equals(extension)) {
				return new XMLEntity();
			} else if (JSON.equals(extension)) {
				if ("JsonArray".equals(fileName)) {
					return new JsonArray();
				} else {
					return new JsonObject();
				}
			}
		}
		return null;
	}

	/**
	 * Encode.
	 *
	 * @param data the data
	 * @param stream the stream
	 * @param closeStream the close stream
	 * @return the zip output stream
	 */
	public ZipOutputStream encode(BaseItem data, OutputStream stream, boolean closeStream) {
		if (data != null) {
			ZipOutputStream zos;
			if (stream instanceof ZipOutputStream) {
				zos = (ZipOutputStream) stream;
			} else {
				zos = new ZipOutputStream(stream);
			}
			ZipEntry zipEntry = new ZipEntry(getFileName(data));
			try {
				byte[] bytes = data.toString().getBytes(BaseItem.ENCODING);
				zos.putNextEntry(zipEntry);
				zos.write(bytes, 0, bytes.length);
				zos.closeEntry();
				if (closeStream) {
					zos.close();
				}
			} catch (IOException e) {
			}
			return zos;
		}
		return null;
	}

	/**
	 * Decode.
	 *
	 * @param stream the stream
	 * @return the base item
	 */
	public BaseItem decode(InputStream stream) {
		if(stream == null) {
			return null;
		}
		ZipInputStream zis;
		if (stream instanceof ZipInputStream) {
			zis = (ZipInputStream) stream;
		} else {
			zis = new ZipInputStream(stream);
		}
		try {
			ZipEntry item = zis.getNextEntry();
			byte[] buffer = new byte[2048];
			while (item != null) {
				if (item.isDirectory() == false) {
					BaseItem element = getNewInstanceFromFileName(item.getName());
					if (element != null) {
						CharacterBuffer output = new CharacterBuffer();
						int len = 0;
						while ((len = zis.read(buffer)) > 0) {
							output.write(buffer, len);
						}
						if (element instanceof Entity) {
							((Entity) element).withValue(output);
						} else {
							element.add(output.toString());
						}
						return element;
					}
				}
				zis.closeEntry();
				item = zis.getNextEntry();
			}
		} catch (IOException e) {
		}
		return null;
	}
	
	/**
	 * Decoding.
	 *
	 * @param params the params
	 * @return true, if successful
	 */
	public static boolean decoding(String... params) {
		if(params == null || params.length <1) {
			return false;
		}
		String fileName = params[0];
		String pwd = null;
		if(params.length >1) {
			pwd = params[1]; 
		}
		
		String folder = "";
		if(params.length >2) {
			folder = params[2]; 
		}
		
		FileBuffer file = new FileBuffer().withFile(fileName);
		ZipInputStream zis=null;
		try {
			zis = new ZipInputStream(new ZipDecryptInputStream(file.asStream(), pwd));
			ZipEntry item = zis.getNextEntry();
			byte[] buffer = new byte[2048];
			CharacterBuffer output = new CharacterBuffer();
			int len = 0;
			while ((len = zis.read(buffer)) > 0) {
				output.write(buffer, len);
			}
			FileBuffer.writeFile(folder + item.getName(), output);
		} catch (Exception e) {
		} finally {
			if(zis != null) {
				try {
					zis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}
