package de.uniks.networkparser.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.ext.io.ExcelBuffer;
import de.uniks.networkparser.parser.ExcelRow;
import de.uniks.networkparser.parser.ExcelSheet;
import de.uniks.networkparser.parser.ExcelWorkBook;

public class ExcelTest {
	@Test
	public void testLoad() throws FileNotFoundException {
		String path = DocEnvironment.getAbsolutePath("UniKassel.xlsx");
		File myFile = new File(path);

		ExcelBuffer exBuf = new ExcelBuffer();
		ExcelSheet sheet = exBuf.parse(myFile);

		ExcelRow tagRow = sheet.get(1);
		Assert.assertEquals("FG Softwaretechnik", tagRow.get(0).getContent());
		Assert.assertEquals("Albert", tagRow.get(2).getContent());
	}

	@Test
	public void testCreate() {
		String path =  DocEnvironment.getAbsolutePath("test.xlsx");
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
