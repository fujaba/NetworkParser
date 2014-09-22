package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import de.uniks.networkparser.xml.XMLEntity;

public class FileTest {
	@Test
	public void fileWriterXM() throws IOException{
		XMLEntity xmlEntity = new XMLEntity().withValue("<chatmsg folder=\"C:\\temp\\\\\" />");
		assertEquals("C:\\temp\\\\", xmlEntity.get("folder"));
		File file = new File("build/test.txt");
		file.getParentFile().mkdirs();
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(xmlEntity.toString());
		fileWriter.close();
	}
}
