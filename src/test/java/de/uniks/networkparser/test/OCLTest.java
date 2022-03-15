package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
		assertFalse(parser.update(house));
		house.setFloor(1);
		assertTrue(parser.update(house));
	}
}
