package de.uniks.networkparser.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import de.uniks.networkparser.ext.io.ExcelBuffer;
import de.uniks.networkparser.parser.excel.ExcelSheet;
import de.uniks.networkparser.parser.excel.ExcelWorkBook;

public class ExcelTest extends IOClasses{
	@Test
	public void testCreate() {
		String path = getAbsolutePath("test.xlsx");
		String output = "build/test2.xlsx";
		File file = new File(path);
		if(file.exists()) {
			ExcelBuffer buffer = new ExcelBuffer();
			ExcelSheet content = buffer.parse(file);
			assertNotNull(content);
			ExcelWorkBook workBook = new ExcelWorkBook();
			workBook.add(content);
			new File("build").mkdir();
			buffer.encode(new File(output), workBook);
		}
	}
}
