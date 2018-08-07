package de.uniks.networkparser.test.generator;

import static de.uniks.networkparser.graph.Cardinality.MANY;
import static de.uniks.networkparser.graph.DataType.STRING;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.DataTypeSet;
import de.uniks.networkparser.graph.util.AssociationSet;

public class TestAssociation {

	@Test
	public void testUniDirectionalAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_a");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		person.withUniDirectional(room, "room", Cardinality.ONE);
		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testUniDirectionalAssociations() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_b");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		person.withUniDirectional(room, "room", Cardinality.ONE);
		room.withUniDirectional(person, "persons", Cardinality.MANY);
		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");
	}

	@Test
	public void testMultipleUniDirectionalAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_c");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		person.withUniDirectional(room, "room", Cardinality.ONE);
		person.withUniDirectional(person, "prevPerson", Cardinality.ONE);
		person.withUniDirectional(person, "nextPerson", Cardinality.ONE);

		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");
	}

	@Test
	public void testOneToOneAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_d");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		person.withBidirectional(room, "room", Cardinality.ONE, "person", Cardinality.ONE);
		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testOneToManyAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_e");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		room.withBidirectional(person, "persons", Cardinality.MANY, "room", Cardinality.ONE);
		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testManyToOneAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_f");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		person.withBidirectional(room, "room", Cardinality.ONE, "persons", Cardinality.MANY);
		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");

	}
	@Test
	public void testMultipleOneToOneAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_g");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		Clazz teacher = model.createClazz("Teacher");

		person.withBidirectional(room, "room", Cardinality.ONE, "person", Cardinality.ONE);
		person.withBidirectional(teacher, "teacher", Cardinality.ONE, "person", Cardinality.ONE);
		room.withBidirectional(teacher, "teacher", Cardinality.ONE, "room", Cardinality.ONE);
		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testMultipleOneToManyAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_h");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		Clazz teacher = model.createClazz("Teacher");

		person.withBidirectional(room, "rooms", Cardinality.MANY, "person", Cardinality.ONE);
		person.withBidirectional(teacher, "teachers", Cardinality.MANY, "person", Cardinality.ONE);
		room.withBidirectional(teacher, "teachers", Cardinality.MANY, "room", Cardinality.ONE);
		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testMultipleManyToOneAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_i");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		Clazz teacher = model.createClazz("Teacher");

		person.withBidirectional(room, "room", Cardinality.ONE, "persons", Cardinality.MANY);
		person.withBidirectional(teacher, "teacher", Cardinality.ONE, "persons", Cardinality.MANY);
		room.withBidirectional(teacher, "teacher", Cardinality.ONE, "rooms", Cardinality.MANY);
		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testMixedAssociations() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_j");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		person.withUniDirectional(room, "rooms", Cardinality.MANY);
		person.withBidirectional(room, "room", Cardinality.ONE, "persons", Cardinality.MANY);
		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");
	}

	@Test
	public void testManyToMany() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_k");
//		Clazz lecture = model.createClazz("Lecture");
//		Clazz student = model.createClazz("Student");
//
//		student.withBidirectional(lecture, "attended", Cardinality.MANY, "has", Cardinality.MANY);

	    Clazz task = model.createClazz("Task").withAttribute("name", STRING);
	    task.withBidirectional(task, "subTasks", MANY, "parentTasks", MANY);

		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");
	}
	@Test
	public void testOneToManyInterace() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_l");
		Clazz lecture = model.createClazz("Lecture");
		Clazz student = model.createClazz("Student");
		Clazz uni = model.createClazz("University");
		uni.enableInterface();
		lecture.enableInterface();

		student.withBidirectional(lecture, "attended", Cardinality.MANY, "has", Cardinality.MANY);
		student.withBidirectional(uni, "studs", Cardinality.ONE, "students", Cardinality.MANY);


		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");
	}
	
	@Test
	public void testDuppleTest() {
		// class model
		ClassModel model = new ClassModel("de.uniks.universityofmadness.model");

		// classes
		Clazz character = model.createClazz("Character");
		Clazz hero = model.createClazz("Hero");
		Clazz board = model.createClazz("Board");
		Clazz room = model.createClazz("Room");

		// superclasses
		hero.withSuperClazz(character);

		
		board.createBidirectional(character, "characters", Cardinality.MANY, "board", Cardinality.ONE);
		board.createBidirectional(room, "rooms", Cardinality.MANY, "board", Cardinality.ONE);

		room.createBidirectional(character, "characters", Cardinality.MANY, "room", Cardinality.ONE);

//		System.out.println(model.getAssociations().size());

	}
	
	@Test
	public void testDuppleTestFull() {
		// class model
		ClassModel model = new ClassModel("de.uniks.universityofmadness.model");

		// classes
		Clazz gameSession = model.createClazz("GameSession");
		Clazz gameSessionController = model.createClazz("GameSessionController");
		Clazz player = model.createClazz("Player");
		Clazz character = model.createClazz("Character");
		Clazz hero = model.createClazz("Hero");
		Clazz item = model.createClazz("Item");
		Clazz weapon = model.createClazz("Weapon");
		Clazz board = model.createClazz("Board");
		Clazz room = model.createClazz("Room");
		Clazz door = model.createClazz("Door");
		Clazz hint = model.createClazz("Hint");
		Clazz enemy = model.createClazz("Enemy");
		Clazz adjutant = model.createClazz("Adjutant");

		// superclasses
		hero.withSuperClazz(character);
		enemy.withSuperClazz(character);
		adjutant.withSuperClazz(character);
		weapon.withSuperClazz(item);

		// links
		player.createBidirectional(hero, "hero", Cardinality.ONE, "player", Cardinality.ONE);
		character.createBidirectional(item, "items", Cardinality.MANY, "owner", Cardinality.ONE);
		
		gameSession.createUniDirectional(player, "currentPlayer", Cardinality.ONE);
		gameSession.createBidirectional(board, "board", Cardinality.ONE, "gameSession", Cardinality.ONE);
		gameSession.createBidirectional(player, "players", Cardinality.MANY, "gameSession", Cardinality.ONE);
		gameSessionController.createBidirectional(gameSession, "gameSession", Cardinality.ONE, "gameSessionController",
				Cardinality.ONE);
		
		board.createBidirectional(room, "rooms", Cardinality.MANY, "board", Cardinality.ONE);
		board.createBidirectional(character, "characters", Cardinality.MANY, "board", Cardinality.ONE);

		room.createBidirectional(item, "items", Cardinality.MANY, "room", Cardinality.ONE);
		room.createBidirectional(door, "doors", Cardinality.MANY, "rooms", Cardinality.MANY);
		room.createBidirectional(hint, "hints", Cardinality.MANY, "room", Cardinality.ONE);
		Association createBidirectional = room.createBidirectional(character, "characters", Cardinality.MANY, "room", Cardinality.ONE);
		// TODO: attributes

		gameSessionController.createAttribute("tmxPath", DataType.STRING);
		board.createAttribute("tileSets", DataTypeSet.create(DataType.STRING));
		
		room.createAttribute("name", DataType.STRING);
		item.createAttribute("name", DataType.STRING);
		character.createAttribute("name", DataType.STRING);
		player.createAttribute("name", DataType.STRING);
		hint.createAttribute("name", DataType.STRING);
		
		room.createAttribute("visible", DataType.BOOLEAN);
		room.createAttribute("state", DataType.STRING);
		
		player.createAttribute("health", DataType.INT);
		player.createAttribute("madness", DataType.INT);
		player.createAttribute("actionsleft", DataType.INT);
		AssociationSet associations = model.getAssociations();
		
		System.out.println(associations.size());
	}
}