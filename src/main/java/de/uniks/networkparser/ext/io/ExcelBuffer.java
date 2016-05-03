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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.parser.excel.ExcelParser;
import de.uniks.networkparser.parser.excel.ExcelSheet;
import de.uniks.networkparser.parser.excel.ExcelWorkBook;

public class ExcelBuffer {
	public ExcelSheet parse(File file) {
		ExcelSheet data = null;
		ZipFile zipEntry = null;
		try {
			CharacterBuffer sharedStrings = null, sheetData = null;
			zipEntry=new ZipFile(file);
			InputStream inputStream;
			ZipEntry entry = zipEntry.getEntry("xl/sharedStrings.xml");
			if(entry != null) {
				inputStream = zipEntry.getInputStream(entry);
				sharedStrings = readContext(inputStream);
				inputStream.close();
			}
			entry = zipEntry.getEntry("xl/worksheets/sheet1.xml");
			if(entry != null) {
				inputStream = zipEntry.getInputStream(entry);
				sheetData = readContext(inputStream);
				inputStream.close();
			}
			zipEntry.close();
			zipEntry = null;
			if(sheetData == null) {
				sheetData = new CharacterBuffer();
			}
			data = new ExcelParser().parseSheet(sharedStrings, sheetData);
		} catch (IOException e) {
		} finally {
			if(zipEntry != null) {
				try {
					zipEntry.close();
				} catch (IOException e) {
				}
			}
		}
		return data;
	}

	private CharacterBuffer readContext(InputStream is) {
		final char[] buffer = new char[1024];
		CharacterBuffer out = new CharacterBuffer();
		try {
			Reader in = new InputStreamReader(is, "UTF-8");
			for (;;) {
				int rsz = in.read(buffer, 0, buffer.length);
				if (rsz < 0)
					break;
				out.with(buffer, 0, rsz);
			}
		} catch (IOException e) {
		}
		return out;
	}
	public boolean encode(File file, ExcelWorkBook workbook) {
		boolean result=false;
		ZipOutputStream zos = null;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			zos = new ZipOutputStream(fos);
			ExcelParser excelParser = new ExcelParser();
			SimpleKeyValueList<String, String> content = excelParser.createExcelContent(workbook);
			for(Iterator<Entry<String, String>> iterator = content.entrySet().iterator();iterator.hasNext();){
				Entry<String, String> entry = iterator.next();
				ZipEntry zipEntry = new ZipEntry(entry.getKey());
				zos.putNextEntry(zipEntry);
				byte[] values = entry.getValue().getBytes("UTF-8");
				zos.write(values, 0, values.length);
				zos.closeEntry();
			}
			zos.close();
			result = true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if(zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
				}
			}
		}
		return result;
	}

	public void addToZipFile(String fileName, String content, ZipOutputStream zos) throws FileNotFoundException, IOException {
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);
		byte[] bytes = content.getBytes("UTF-8");
		zos.write(bytes, 0, bytes.length);
		zos.closeEntry();
	}
}
