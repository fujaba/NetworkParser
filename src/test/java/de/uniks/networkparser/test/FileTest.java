package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.ext.generic.SimpleParser;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.UniversityCreator;
import de.uniks.networkparser.xml.XMLEntity;

public class FileTest {
	@Test
	public void fileWriterXM() throws IOException{
		XMLEntity xmlEntity = new XMLEntity().withValue("<chatmsg folder=\"C:\\temp\\\\\" />");
		assertEquals("C:\\temp\\", xmlEntity.get("folder"));
		File file = new File("build/test.txt");
		file.getParentFile().mkdirs();
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(xmlEntity.toString());
		fileWriter.close();
	}

	@Test
	public void fileReader() throws IOException{
		FileBuffer buffer = new FileBuffer();
		buffer.withFile(new File("src/test/resources/de/uniks/networkparser/test/sample.xml"));
		XMLEntity root = new XMLEntity().withValue(buffer);
		Assert.assertEquals(18, root.sizeChildren());
	}
	
	@Test
	public void fileReaderChanges() throws IOException{
		BaseItem buffer = FileBuffer.readBaseFile("src/test/resources/de/uniks/networkparser/test/change.json");
		Assert.assertTrue(buffer instanceof EntityList);
		EntityList list = (EntityList)buffer;
		Assert.assertEquals(3, list.size());
	}
	
	@Test
	public void fileReaderModel() {
		BaseItem modelJson = FileBuffer.readBaseFile("src/test/resources/de/uniks/networkparser/test/model.json");
		University uni = (University) UniversityCreator.createIdMap("read").decode(modelJson);
		Assert.assertEquals(2, uni.getStudents().size());
	}
	@Test
	public void fileReaderModelSimple() {
		University uni = SimpleParser.fromFile("src/test/resources/de/uniks/networkparser/test/model.json");
		Assert.assertEquals(2, uni.getStudents().size());
	}
}
