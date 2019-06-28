package de.uniks.networkparser.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.EntityCreator;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.ExcelBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.ExcelParser;
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
	
	@Test
	public void testCSV() {
		URL resource = ExcelTest.class.getResource("zisterne.csv");
		
		CharacterBuffer readFile = FileBuffer.readFile(resource.getFile());
		ExcelParser excelParser = new ExcelParser();
		SimpleList<Object> readCSV = excelParser.readCSV(readFile, EntityCreator.createJson(true));
		Assert.assertEquals(3, readCSV.size());
	}
}
