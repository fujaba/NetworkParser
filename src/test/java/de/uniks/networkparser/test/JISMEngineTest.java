package de.uniks.networkparser.test;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import de.uniks.networkparser.xml.XMLIdMap;
import de.uniks.networkparser.xml.XMLTokener;
import de.uniks.networkparser.xml.util.XMLEntityCreator;

public class JISMEngineTest extends IOClasses{

	@Test
	public void testJISMEngine(){
		StringBuffer stringBuffer = readFile("template.html");

		XMLIdMap decoder= new XMLIdMap();
		String data = stringBuffer.toString();
		XMLEntityCreator factory = new XMLEntityCreator();
		Object decode = decoder.decode(new XMLTokener().withBuffer(data), factory);
		assertNotNull(decode);
	}
}
