package de.uniks.networkparser.test;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.MavenXML;

public class testMaven {

	@Test
	public void  testMavenTest() {
		MavenXML xml = new MavenXML();
		xml.buildMaven("build/libs", "de.uniks", "maven");
	}
}
