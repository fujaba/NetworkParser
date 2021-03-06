package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.ext.GitRevision;
import de.uniks.networkparser.ext.Gradle;

public class LudoProjectTester {

	@Test
	public void testMain() {
		String filename = "NetworkParser-4.7.1254-git.jar";
		Gradle gradle = new Gradle();
		String projectName = "C:/Arbeit/demo/LudoTest";
		String licence = "MIT";
		// java -jar NetworkParser-4.7.1254-git.jar init ludo MIT http://gitlab.com/StefanLindel/Ludo
		boolean success = gradle.initProject(filename, projectName, licence);
		System.out.println(success);
		String remoteURL = "http://gitlab.com/StefanLindel/Ludo";
		GitRevision gitRevision = new GitRevision();
		gitRevision.withPath(gradle.getProjectPath());
		gitRevision.init(remoteURL);
		gitRevision.withAuthentification("StefanLindel","rMaSiaylTUdxojnR3zxC");
		gitRevision.pull();
	}
}
