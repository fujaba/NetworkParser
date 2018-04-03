package de.uniks.networkparser.test;

import java.util.List;

import org.junit.Test;

import de.uniks.networkparser.graph.Pattern;
import de.uniks.networkparser.test.ferrymansproblem.Bank;
import de.uniks.networkparser.test.ferrymansproblem.Boat;
import de.uniks.networkparser.test.ferrymansproblem.Cargo;
import de.uniks.networkparser.test.ferrymansproblem.River;
import de.uniks.networkparser.test.model.ludo.Field;
import de.uniks.networkparser.test.model.ludo.Label;
import de.uniks.networkparser.test.model.ludo.Ludo;

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

		List<Object> allMatches = new Pattern(river).has(River.PROPERTY_BANKS).has(Bank.PROPERTY_CARGOS).allMatches();
		Pattern pattern = new Pattern(river).has(River.PROPERTY_BANKS).has(Bank.PROPERTY_CARGOS);
		for (Pattern matchPattern : pattern) {
			System.out.println(pattern);
		}

		System.out.println(allMatches);
		for (Object found : allMatches) {
			Cargo cargo = (Cargo) found;
			System.out.println(cargo.getName());
		}
	}

	@Test
	public void testPattern() {
		Ludo ludo = new Ludo();
		Field field = ludo.createFields().withName("1");
		field.createPawns().withColor("1-1");
		field.createPawns().withColor("1-2");
		field.createPawns().withColor("1-3");

		field = ludo.createFields().withName("2");
		field.createPawns().withColor("2-1");
		field.createPawns().withColor("2-2");
		field.createPawns().withColor("2-3");
		field.createPawns().withColor("2-4");

		field = ludo.createFields().withName("3");
		field.createPawns().withColor("3-1");
		field.createPawns().withColor("3-2");
		field.createPawns().withColor("3-3");
		field.createPawns().withColor("3-4");

		
		Pattern ludoPO = new Pattern(ludo);
		Pattern fieldPO = ludoPO.has(Ludo.PROPERTY_FIELDS);
		Pattern pawnPO = fieldPO.has(Field.PROPERTY_PAWNS);
//		PawnPO pawnPO = fieldPO.createPawnsPO();

//		ludoPO.getCurrentMatch();
		
		ludoPO.hasNext();
//
//		while (ludoPO.isHasMatch()) {
//			System.out.println(
//					"LUDO: " + fieldPO.getCurrentMatch().getName() + " -- " + pawnPO.getCurrentMatch().getColor());
//			ludoPO.nextMatch();
//		}
		System.out.println("FINISH");
	}

	@Test
	public void testPatternCombi() {
		Ludo ludo = new Ludo();
		Field field = ludo.createFields().withName("1");
		Label labelA = field.createLabel().withName("A");
		Label labelB = field.createLabel().withName("B");
		Label labelC = field.createLabel().withName("C");
		field.createPawns().withColor("1-1");
		field.createPawns().withColor("1-2");
		field.createPawns().withColor("1-3");

		field = ludo.createFields().withName("2");
		field.createPawns().withColor("2-1");
		field.createPawns().withColor("2-2");
		field.createPawns().withColor("2-3");
		field.createPawns().withColor("2-4");
		field.withLabel(labelA);
		field.withLabel(labelB);
		field.withLabel(labelC);

		field = ludo.createFields().withName("3");
		field.withLabel(labelA);
		field.withLabel(labelB);
		field.withLabel(labelC);

//		LudoPO ludoPO = new LudoPO(ludo);
//		FieldPO fieldPO = ludoPO.createFieldsPO();
//		LabelPO labelPO = fieldPO.createLabelPO();
//		PawnPO pawnPO = fieldPO.createPawnsPO();
//
//		while (ludoPO.isHasMatch()) {
//			System.out.println("LUDO: " + fieldPO.getCurrentMatch().getName() + " -- "
//					+ labelPO.getCurrentMatch().getName() + " -- " + pawnPO.getCurrentMatch().getColor());
//			ludoPO.nextMatch();
//		}
	}

	@Test
	public void testPattern2() {
		Ludo ludo = new Ludo();
		Field field = ludo.createFields().withName("1");
		Label labelA = field.createLabel().withName("A");
		Label labelB = field.createLabel().withName("B");
		Label labelC = field.createLabel().withName("C");

		field = ludo.createFields().withName("2");
		field.withLabel(labelA);
		field.withLabel(labelB);
		field.withLabel(labelC);

		field = ludo.createFields().withName("3");
		field.withLabel(labelA);
		field.withLabel(labelB);
		field.withLabel(labelC);

//		LudoPO ludoPO = new LudoPO(ludo);
//		FieldPO fieldPO = ludoPO.createFieldsPO();
//		LabelPO labelPO = fieldPO.createLabelPO();
//
//		while (ludoPO.isHasMatch()) {
//			System.out.println(
//					"LUDO: " + fieldPO.getCurrentMatch().getName() + " -- " + labelPO.getCurrentMatch().getName());
//			ludoPO.nextMatch();
//		}
	}

}
