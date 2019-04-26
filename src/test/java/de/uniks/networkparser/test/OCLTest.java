package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.logic.OCLParser;
import de.uniks.networkparser.test.model.House;
import de.uniks.networkparser.test.model.util.HouseCreator;

public class OCLTest {

	@Test
	public void testOCL() {
		String item = "context house inv: self.floor > 0";
		IdMap map = new IdMap().withCreator(new HouseCreator());
		OCLParser parser = OCLParser.create(item, map);
		House house = new House();
		Assert.assertFalse(parser.update(house));
		house.setFloor(1);
		Assert.assertTrue(parser.update(house));
	}
}
