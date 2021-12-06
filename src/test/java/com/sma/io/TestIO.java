package com.sma.io;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;

public class TestIO {

	@Test
	public void testGen() {
		ClassModel model = new ClassModel("de.sma.io");
		Clazz event = model.createClazz("IOEvent").enableInterface();
		Clazz type = model.createClazz("EventType").enableEnumeration("ADD", "VALUE", "REMOVE");
		
		event.withAttribute("type", type.toDataType());
		event.withAttribute("value", DataType.OBJECT);

		
		model.generate("src-sma");
//		model.dumpHTML("SMA-IO", true);
		
	}
}
