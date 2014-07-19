package de.uniks.networkparser.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.uniks.networkparser.test.model.creator.ApplicationMessageCreator;
import de.uniks.networkparser.test.model.creator.FIXMLMessageCreator;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLIdMap;

public class XMLTCC2014Test extends IOClasses{
	@Test
	public void testPattern() {
		String XMLText = readFile("src/test/resources/test3.xml").toString();
		
		XMLIdMap map=new XMLIdMap();
		map.withCreator(new FIXMLMessageCreator());
		map.withCreator(new ApplicationMessageCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.OrderCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.HandInstCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.InstrumentCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.SideCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.OrderQuantityCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.OrderTypeCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.CurrencyCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.Rule80ACreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.LimitOrderCreator());
		
		
		Object item = map.decode(XMLText);

		System.out.println(item);
		assertNotNull(item);
	}

	@Test
	public void testSimpleXMLEntity(){
		String str = readFile("src/test/resources/test3.xml").toString();
		
		
		XMLEntity item=new XMLEntity();
		item.withValue(str);
		
		System.out.println(item.toString());
	}
}
