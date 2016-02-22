package de.uniks.networkparser.test;

import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.test.model.FIXMLMessage;
import de.uniks.networkparser.test.model.util.ApplicationMessageCreator;
import de.uniks.networkparser.test.model.util.FIXMLMessageCreator;
import de.uniks.networkparser.xml.XMLEntity;

public class XMLTCC2014Test extends IOClasses{
	@Test
	public void testPattern() {
		String XMLText = readFile("test3.xml").toString();

		IdMap map= new IdMap();
		map.with(new FIXMLMessageCreator());
		map.with(new ApplicationMessageCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.OrderCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.HandInstCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.InstrumentCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.SideCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.OrderQuantityCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.OrderTypeCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.CurrencyCreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.Rule80ACreator());
//		map.withCreator(new de.kassel.ttc2014.fixml.test3.creators.LimitOrderCreator());

		FIXMLMessage item = (FIXMLMessage) map.decode(XMLText);
		assertNotNull(item);
		assertNotNull(item.getApplicationmessage());
	}

	@Test
	public void testSimpleXMLEntity(){
		String str = readFile("test3.xml").toString();


		XMLEntity item= new XMLEntity();
		item.withValue(str);
		Assert.assertEquals(505, item.toString().length());
	}
}
