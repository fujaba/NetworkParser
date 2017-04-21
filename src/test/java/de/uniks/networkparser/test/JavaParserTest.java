package de.uniks.networkparser.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.parser.JavaParser;
import de.uniks.networkparser.parser.SymTabEntry;

public class JavaParserTest {

	@Test
	public void testJavaParser() throws IOException{
		byte[] fullContext = Files.readAllBytes(new File("src/main/java/de/uniks/networkparser/parser/JavaParser.java").toPath());
		String context = new String(fullContext);
		JavaParser parser = new JavaParser();
		SymTabEntry parse = parser.parse(context);
		Assert.assertNotNull(parse);
	}
}
