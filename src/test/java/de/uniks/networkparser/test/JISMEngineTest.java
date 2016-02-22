package de.uniks.networkparser.test;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.xml.XMLTokener;
import de.uniks.networkparser.xml.util.XMLEntityCreator;

public class JISMEngineTest extends IOClasses{

	@Test
	public void testJISMEngine(){
		StringBuffer stringBuffer = readFile("template.html");

		IdMap decoder= new IdMap();
		String data = stringBuffer.toString();
		XMLEntityCreator factory = new XMLEntityCreator();
//FIXME		Object decode = decoder.decode(new XMLTokener().withBuffer(data), factory);
//		assertNotNull(decode);
	}
}
