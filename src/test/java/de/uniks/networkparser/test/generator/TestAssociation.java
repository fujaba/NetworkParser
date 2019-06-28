package de.uniks.networkparser.test.generator;

import static de.uniks.networkparser.graph.DataType.STRING;

import org.junit.Assert;
import org.junit.Test;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationSet;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.DataTypeSet;

public class TestAssociation {

	@Test
	public void testUniDirectionalAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_a");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		person.withUniDirectional(room, "room", Association.ONE);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testUniDirectionalAssociations() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_b");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		person.withUniDirectional(room, "room", Association.ONE);
		room.withUniDirectional(person, "persons", Association.MANY);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");
	}

	@Test
	public void testMultipleUniDirectionalAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_c");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		person.withUniDirectional(room, "room", Association.ONE);
		person.withUniDirectional(person, "prevPerson", Association.ONE);
		person.withUniDirectional(person, "nextPerson", Association.ONE);

		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");
	}

	@Test
	public void testOneToOneAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_d");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		person.withBidirectional(room, "room", Association.ONE, "person", Association.ONE);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testOneToManyAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_e");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		room.withBidirectional(person, "persons", Association.MANY, "room", Association.ONE);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testManyToOneAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_f");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		person.withBidirectional(room, "room", Association.ONE, "persons", Association.MANY);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}
	@Test
	public void testMultipleOneToOneAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_g");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		Clazz teacher = model.createClazz("Teacher");

		person.withBidirectional(room, "room", Association.ONE, "person", Association.ONE);
		person.withBidirectional(teacher, "teacher", Association.ONE, "person", Association.ONE);
		room.withBidirectional(teacher, "teacher", Association.ONE, "room", Association.ONE);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testMultipleOneToManyAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_h");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		Clazz teacher = model.createClazz("Teacher");

		person.withBidirectional(room, "rooms", Association.MANY, "person", Association.ONE);
		person.withBidirectional(teacher, "teachers", Association.MANY, "person", Association.ONE);
		room.withBidirectional(teacher, "teachers", Association.MANY, "room", Association.ONE);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testMultipleManyToOneAssociation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_i");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		Clazz teacher = model.createClazz("Teacher");

		person.withBidirectional(room, "room", Association.ONE, "persons", Association.MANY);
		person.withBidirectional(teacher, "teacher", Association.ONE, "persons", Association.MANY);
		room.withBidirectional(teacher, "teacher", Association.ONE, "rooms", Association.MANY);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testMixedAssociations() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_j");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		person.withUniDirectional(room, "rooms", Association.MANY);
		person.withBidirectional(room, "room", Association.ONE, "persons", Association.MANY);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");
	}

	@Test
	public void testManyToMany() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.association_k");
//		Clazz lecture = model.createClazz("Lecture");
//		Clazz student = model.createClazz("Student");
//
//		student.withBidirectional(lecture, "attended", Association.MANY, "has", Association.MANY);

	    Clazz task = model.createClazz("Task").withAttribute("name", STRING);
	    task.withBidirectional(task, "subTasks", Association.MANY, "parentTasks", Association.MANY);

		model.getGenerator().removeAndGenerate("java");
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

		student.withBidirectional(lecture, "attended", Association.MANY, "has", Association.MANY);
		student.withBidirectional(uni, "studs", Association.ONE, "students", Association.MANY);


		model.getGenerator().removeAndGenerate("java");
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

		
		board.createBidirectional(character, "characters", Association.MANY, "board", Association.ONE);
		board.createBidirectional(room, "rooms", Association.MANY, "board", Association.ONE);

		room.createBidirectional(character, "characters", Association.MANY, "room", Association.ONE);
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
		player.createBidirectional(hero, "hero", Association.ONE, "player", Association.ONE);
		character.createBidirectional(item, "items", Association.MANY, "owner", Association.ONE);
		
		gameSession.createUniDirectional(player, "currentPlayer", Association.ONE);
		gameSession.createBidirectional(board, "board", Association.ONE, "gameSession", Association.ONE);
		gameSession.createBidirectional(player, "players", Association.MANY, "gameSession", Association.ONE);
		gameSessionController.createBidirectional(gameSession, "gameSession", Association.ONE, "gameSessionController",
				Association.ONE);
		
		board.createBidirectional(room, "rooms", Association.MANY, "board", Association.ONE);
		board.createBidirectional(character, "characters", Association.MANY, "board", Association.ONE);

		room.createBidirectional(item, "items", Association.MANY, "room", Association.ONE);
		room.createBidirectional(door, "doors", Association.MANY, "rooms", Association.MANY);
		room.createBidirectional(hint, "hints", Association.MANY, "room", Association.ONE);
//		Association createBidirectional = room.createBidirectional(character, "characters", Association.MANY, "room", Association.ONE);
		room.createBidirectional(character, "characters", Association.MANY, "room", Association.ONE);
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
		Assert.assertEquals(15, associations.size());
	}
}