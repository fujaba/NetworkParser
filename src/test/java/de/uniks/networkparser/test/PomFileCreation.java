package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.Manifest;
import de.uniks.networkparser.logic.VersionCondition;
import de.uniks.networkparser.xml.ArtifactFile;

public class PomFileCreation {
	@Test
	public void NPMTest() {
		VersionCondition first = new VersionCondition().withVersion("1.0.0");
		VersionCondition second = new VersionCondition().withVersion("2.0.0");
		int retval = first.compareTo(second);
		if(retval > 0) {
//			System.out.println("d1 is greater than d2");
		} else if(retval < 0) {
//			System.out.println("d1 is less than d2");
		} else {
//			System.out.println("d1 is equal to d2");
		}
	}
	
	@Test
	public void testPom(){
		ArtifactFile pom=new ArtifactFile();
		pom.withModelVersion("4.0.0");
		pom.withGroupId("de.uniks");
		pom.withArtifactId("NetworkParser");
		pom.withVersion("4.2.244-SNAPSHOT");
		pom.withDependency(new ArtifactFile().withArtifact("junit", "junit", "4.+").withScope("test"));
		String ref = "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"+
		"  <modelVersion>4.0.0</modelVersion>\r\n"+
		"  <groupId>de.uniks</groupId>\r\n"+
		"  <artifactId>NetworkParser</artifactId>\r\n"+
		"  <version>4.2.244-SNAPSHOT</version>\r\n"+
		"  <dependencies>\r\n"+
		"    <dependency>\r\n"+
		"      <groupId>junit</groupId>\r\n"+
		"      <artifactId>junit</artifactId>\r\n"+
		"      <version>4.+</version>\r\n"+
		"      <scope>test</scope>\r\n"+
		"    </dependency>\r\n"+
		"  </dependencies>\r\n"+
		"</project>";
		assertEquals(ref, pom.toString(2));
	}

	@Test
	public void testManifest(){
		CharacterBuffer sb=new CharacterBuffer();
		sb.withLine("Manifest-Version: 1.0");
		sb.withLine("Specification-Title: Networkparser");
		sb.withLine("Author: Stefan Lindel)");

		Manifest manifest = Manifest.create(sb);
		assertEquals(3, manifest.size());

	}
}
