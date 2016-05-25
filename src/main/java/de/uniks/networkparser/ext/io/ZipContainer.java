package de.uniks.networkparser.ext.io;

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

public class ZipContainer {
	private static final String XML = "xml";
	private static final String BINARY = "bin";
	private static final String JSON = "json";

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
	
	public BaseItem getNewInstanceFromFileName(String fileName) {
		if (fileName != null) {
			int pos = fileName.lastIndexOf(".");
			String extension ="";
			if(pos>=0) {
				extension = fileName.substring(pos + 1);
				fileName = fileName.substring(0, pos);
			}
			if(BINARY.equals(extension)) {
				return new ByteEntity();
			} else if (XML.equals(extension)) {
				return new XMLEntity();
			} else if (JSON.equals(extension)) {
				if("JsonArray".equals(fileName)) {
					return new JsonArray();
				} else {
					return new JsonObject();
				}
			}
		}
		return null;
	}

	public ZipOutputStream encode(BaseItem data, OutputStream stream, boolean closeStream) {
		if(data != null) {
			ZipOutputStream zos;
			if(stream instanceof ZipOutputStream) {
				zos = (ZipOutputStream) stream;
			} else {
				zos = new ZipOutputStream(stream);
			}
			ZipEntry zipEntry = new ZipEntry(getFileName(data));
			try {
				byte[] bytes = data.toString().getBytes("UTF-8");
				zos.putNextEntry(zipEntry);
				zos.write(bytes, 0, bytes.length);
				zos.closeEntry();
				if(closeStream) {
					zos.close();
				}
			} catch (IOException e) {
			}
			return zos;
		}
		return null;
	}
	
	public BaseItem decode(InputStream stream) {
		ZipInputStream zis;
		if(stream instanceof ZipInputStream) {
			zis = (ZipInputStream) stream;
		} else {
			zis = new ZipInputStream(stream);
		}
		try {
			ZipEntry item = zis.getNextEntry();
			byte[] buffer = new byte[2048];
			while(item != null) {
				if(item.isDirectory() == false) {
					BaseItem element = getNewInstanceFromFileName(item.getName());
					if(element != null) {
						CharacterBuffer output=new CharacterBuffer();
						int len = 0;
		                while ((len = zis.read(buffer)) > 0) {
		                    output.write(buffer, len);
		                }
		                if(element instanceof Entity) {
		                	((Entity)element).withValue(output);
		                } else {
		                	element.with(output.toString());
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
}
	
//	private BaseItem data;
//	
//	public ZipContainer withData(BaseItem data) {
//		this.data = data;
//		return this;
//	}
//	
//	
//	public BaseItem getData() {
//		return data;
//	}
