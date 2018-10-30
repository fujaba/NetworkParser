package de.uniks.networkparser.test;

import static de.uniks.networkparser.graph.Association.MANY;
import static de.uniks.networkparser.graph.Association.ONE;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.story.Cucumber;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.xml.HTMLEntity;

public class LudoTest {
	@Test
	public void ludoTest () {
		step1();

		step2();
	}

	public void step1() {
		//Step 1
		Cucumber scenario = Cucumber.createScenario("defining the start player");
	
		scenario.Definition("Karli is a Player");
		scenario.Definition("Seb is a Player");
		scenario.Definition("dice is a Dice");
		
		
		scenario.Given("Alice and Seb play ludo");
		scenario.Given("the players has tokens on startingArea");
		scenario.Given("Alice has dice with value 5");
		scenario.When("Bob has dice with 1");
		scenario.Then("Alice is currentplayer from ludo");
		scenario.analyse();
		
		GraphList model = scenario.getModel();
		HTMLEntity file = new HTMLEntity();
		file.withGraph(model);
	}
	
	public void step2() {
		// Step 2
		ClassModel model = new ClassModel("de.uniks.ludo.model");
		Clazz diceClass = model.createClazz("Dice").withAttribute("number", DataType.INT);
		Clazz fieldClass = model.createClazz("Field");
		fieldClass.withAssoc(fieldClass, "next", ONE, "prev", ONE);

		Clazz homeClass = model.createClazz("Home");
		Clazz lastFieldClass = model.createClazz("LastField");

		Clazz ludoClass = model.createClazz("Ludo");
		ludoClass.createMethod("init", new Parameter(DataType.create("Player...")));
		ludoClass.createBidirectional(diceClass, "dice", ONE, "game", ONE);
		ludoClass.createBidirectional(fieldClass, "field", MANY, "game", ONE);
		Clazz meepleClass = model.createClazz("Meeple");

		meepleClass.createBidirectional(fieldClass, "field", ONE, "meeple", ONE);

		Clazz playerClass = model.createClazz("Player")
				.withAttribute("color", DataType.STRING)
				.withAttribute("name", DataType.STRING);

		playerClass.createBidirectional(ludoClass, "currentGame", ONE, "currentPlayer", ONE);

		playerClass.createBidirectional(ludoClass, "game", ONE, "players", MANY);

		playerClass.createBidirectional(homeClass, "home", MANY, "player", ONE);

		playerClass.createBidirectional(meepleClass, "meeple", MANY, "player", ONE);

		playerClass.createBidirectional(playerClass, "next", ONE, "prev", ONE);

		ludoClass.createBidirectional(playerClass, "winner", ONE, "wonGame", ONE);

		Clazz startClass = model.createClazz("Start");

		startClass.createBidirectional(playerClass, "player", ONE, "start", ONE);

		Clazz targetClass = model.createClazz("Target");

		targetClass.createBidirectional(lastFieldClass, "lastField", ONE, "target", ONE);
		targetClass.createBidirectional(playerClass, "player", ONE, "target", MANY);

		fieldClass.withKidClazzes(homeClass, lastFieldClass, startClass, targetClass);
		
		HTMLEntity dumpHTML = model.dumpHTML("model", false);
		
		FileBuffer.writeFile("build/model.html", dumpHTML.toString());
	}
	
	public void step3() {
//		Space space=new Space();
//		space.withModel(map, ludo);
	}
}
