package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.xml.ArtifactFile;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLTokener;

public class JarValidatorTest {

//	@Test
//	public void test() throws IOException {
//
//		SimpleSet<ArtifactFile> analysePom = new JarValidator().analysePom(new File("C:\\\\Arbeit\\\\release\\\\iguana-ldm.jar"), true);
//		Files.write(Path.of("C:\\Arbeit\\release\\poms.xml"), analysePom.toString(", \n").getBytes());
//		System.out.println(analysePom);
//	}
	
	@Test
	public void pomWriter() {
		FileBuffer buffer =new FileBuffer().withFile("C:\\Arbeit\\release\\pom.xml");
		XMLEntity xmlEntity = new XMLEntity().withValue(buffer);
		
		ArtifactFile reference = new ArtifactFile().withValue(xmlEntity);
		
		XMLTokener tokener = new XMLTokener();
		assertNotNull(reference);
		assertNotNull(tokener);
	}
}
