package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.graph.Pattern;
import de.uniks.networkparser.test.model.ferryman.Bank;
import de.uniks.networkparser.test.model.ferryman.Boat;
import de.uniks.networkparser.test.model.ferryman.River;
import de.uniks.networkparser.test.model.util.RiverCreator;

public class PatternTest {
	@Test
	public void testFerryMan() {
		// StartSituation
		River river = new River();
		Boat boat = river.createBoat();
		Bank left = river.createBanks().withName("left").withBoat(boat);

		left.createCargos().withName("cabbage");
		left.createCargos().withName("goat");
		left.createCargos().withName("wolf");

		river.createBanks().withName("right");

//		List<Object> allMatches = new Pattern(river).has(River.PROPERTY_BANKS).has(Bank.PROPERTY_CARGOS).allMatches();
		Pattern pattern = new Pattern(RiverCreator.createIdMap("42"), river).has(River.PROPERTY_BANKS).has(Bank.PROPERTY_CARGOS);
//		pattern.withMatch(candidate)
		int i=1;
		for (Object matchPattern : pattern) {
			if(matchPattern != null) {
				i++;
			}
		}
		assertEquals(3, i);

//		for (Object found : allMatches) {
//			Cargo cargo = (Cargo) found;
//			System.out.println(cargo.getName());
//		}
	}
}
