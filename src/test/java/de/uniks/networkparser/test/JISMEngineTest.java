package de.uniks.networkparser.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.uniks.networkparser.IdMap;

public class JISMEngineTest extends IOClasses{

	@Test
	public void testJISMEngine(){
		StringBuffer stringBuffer = readFile("template.html");

		IdMap decoder= new IdMap();
		String data = stringBuffer.toString();
//		XMLEntityCreator factory = new XMLEntityCreator();
		Object decode = decoder.decode(data);
		assertNotNull(decode);
	}
}
