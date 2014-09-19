package de.uniks.networkparser.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.uniks.networkparser.xml.XMLSimpleIdMap;
import de.uniks.networkparser.xml.XMLTokener;
import de.uniks.networkparser.xml.util.XMLStyledEntityCreator;

public class JISMEngineTest extends IOClasses{
	
	@Test
	public void testJISMEngine(){
		StringBuffer stringBuffer = readFile("test/template.html");
		
		
		XMLSimpleIdMap decoder= new XMLSimpleIdMap();
		String data = stringBuffer.toString();
		XMLStyledEntityCreator factory = new XMLStyledEntityCreator();
		Object decode = decoder.decode(new XMLTokener().withText(data), factory);
		System.out.println(decode);
		assertNotNull(decode);
	}

}
