package de.uniks.networkparser.gui.javafx;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.ExcelCell;
import de.uniks.networkparser.parser.ExcelParser;

public class ExcelReader {
	public SimpleList<SimpleList<ExcelCell>> parse(File file) {
		SimpleList<SimpleList<ExcelCell>> data = null;
		try {
			CharacterBuffer sharedStrings = null, sheetData = null;
			ZipFile zipEntry=new ZipFile(file);
			InputStream inputStream;
			ZipEntry entry = zipEntry.getEntry("xl/sharedStrings.xml");
			if(entry != null) {
				inputStream = zipEntry.getInputStream(entry);
				sharedStrings = readContext(inputStream);
			}
			entry = zipEntry.getEntry("xl/worksheets/sheet1.xml");
			if(entry != null) {
				inputStream = zipEntry.getInputStream(entry);
				sheetData = readContext(inputStream);
			}
			zipEntry.close();
			data = new ExcelParser().parse(sheetData, sharedStrings);
		} catch (Exception e) {
		}
		return data;
	}

	private CharacterBuffer readContext(InputStream is) {
		final char[] buffer = new char[1024];
		CharacterBuffer out = new CharacterBuffer();
		try (Reader in = new InputStreamReader(is, "UTF-8")) {
			for (;;) {
				int rsz = in.read(buffer, 0, buffer.length);
				if (rsz < 0)
					break;
				out.with(buffer, 0, rsz);
			}
		} catch (Exception e) {
		}
		return out;
	}
}
