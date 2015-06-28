package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.event.PomFile;

public class PomFileCreation {
	@Test
	public void testPom(){
		PomFile pom=new PomFile();
		pom.withModelVersion("4.0.0");
		pom.withGroupId("de.uniks");
		pom.withArtifactId("NetworkParser");
		pom.withVersion("4.2.244-SNAPSHOT");
		pom.withDependency(new PomFile().withArtifact("junit", "junit", "4.+").withScope("test"));
		System.out.println(pom.toString(2));
	}
}
