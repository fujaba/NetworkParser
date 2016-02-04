package de.uniks.networkparser.test;

import java.io.File;

import org.junit.Test;

import de.uniks.networkparser.ext.io.ExcelBuffer;
import de.uniks.networkparser.parser.excel.ExcelSheet;
import de.uniks.networkparser.parser.excel.ExcelWorkBook;

public class ExcelTest {
	@Test
	public void testCreate() {
		File file = new File("test.xlsx");
		if(file.exists()) {
			ExcelBuffer buffer = new ExcelBuffer();
			ExcelSheet content = buffer.parse(file);
			ExcelWorkBook workBook = new ExcelWorkBook();
			workBook.add(content);
			buffer.encode(new File("test2.xlsx"), workBook);
		}
	}
}
