package de.uniks.networkparser.test;

import static de.uniks.networkparser.graph.Association.MANY;
import static de.uniks.networkparser.graph.Association.ONE;

import org.junit.Test;

import de.uniks.ludo.model.Ludo;
import de.uniks.ludo.model.util.LudoSet;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.story.Cucumber;
import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.ext.story.StoryStepJUnit;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.ObjectInstance;
import de.uniks.networkparser.graph.ObjectModel;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.xml.HTMLEntity;

public class LudoTest {
	@Test
	public void ludoTest () {
//		step1();

		step2();
//		stepErrorGeneralization();
//		stepError();
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
	
	public void stepErrorGeneralization() {
		// Step 2
		ClassModel model = new ClassModel("de.uniks.ludo.model");
		Clazz fieldClass = model.createClazz("Field");
		fieldClass.withAssoc(fieldClass, "next", ONE, "prev", ONE);

		Clazz homeClass = model.createClazz("Home");
		fieldClass.withKidClazzes(homeClass);
		model.generate("src/test/java");
	}
	
	@Test
	public void testStory() {
		Story story = new Story();
		story.withName("Play his/her first token");
		
		ObjectModel model = new ObjectModel();
		ObjectInstance alice = model.createObject("alice", "Player");
		ObjectInstance bob = model.createObject("bob", "Player");
		ObjectInstance t1 = model.createObject("t1", "Token");
		ObjectInstance f8 = model.createObject("f8", "Field");
		ObjectInstance game = model.createObject("game", "Ludo");
		
		ObjectInstance die = model.createObject("die", "Die");
		ObjectInstance t6 = model.createObject("t6", "Token");
		ObjectInstance f5 = model.createObject("f5", "Field");
		
		alice.withLink(t1);
		t1.withLink(f8);
		alice.withLink(game);
		bob.withLink(game);
		die.withLink(game);
		bob.withLink(t6);
		t6.withLink(f5);
		
		story.addDiagram(model);
		story.addStep("She moves her token t3 to her start field.");
//		Add Code for Move Meeple
		
		//story.addCode();
//		story.addDiagram(endSituationModel);

		story.dumpHTML("ludo.html");
	}
	
	public void stepError() {
		// Step 2
		ClassModel model = new ClassModel("de.uniks.ludo.model");

		Clazz ludoClass = model.createClazz("Ludo");
		ludoClass.createMethod("init", Parameter.create("Player..."));
		Clazz playerClass = model.createClazz("Player")
				.withAttribute("color", DataType.STRING)
				.withAttribute("name", DataType.STRING);

		playerClass.createBidirectional(ludoClass, "currentGame", ONE, "currentPlayer", ONE);

		model.generate("src/test/java");
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
		ludoClass.createMethod("init", Parameter.create("Player..."));
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
		
		model.generate("src/test/java");
	}
	
	
	@Test
	public void testReflection() {
		StoryStepJUnit story = new StoryStepJUnit();
		story.withPackageName("de.uniks.ludo.model");
		story.executeBlackBoxTest("src/test/java/");
	}
	
	public void testPersistierung() {
		Ludo game = new Ludo();
		IdMap map = LudoSet.createIdMap("42");
		map.toJsonObject(game);
	}
	
	public void step3() {
		Ludo game = new Ludo();
		
		IdMap map = LudoSet.createIdMap("42");
		Space space=new Space();
		space.withModel(map, game);
	}
	
	public boolean stepLogic() {
//		public boolean moveOut( Home currentHome ) {
//		Home home = new Home();
//		PatternCondition pattern = PatternCondition.createPatternPair(home);
//		HomeSet homePO = (HomeSet) pattern.getRoot();
//		PlayerSet playerPO = homePO.getPlayer();
//		StartSet startPO = playerPO.getStart();
//		PatternCondition.setValue(startPO, Start.PROPERTY_MEEPLE, home.getMeeple(), startPO.getMeeple().size() == 0);
		
//		public boolean moveMeeple( Field currentField )
//		Field currentField=new Field();
//		   int pips = getGame().getDie().getNumber();
//		   if(pips<1 || currentField.getMeeple()==null || currentField.getMeeple().getPlayer()!=getGame().getCurrentPlayer()){
//			   return false;
//		   }
//		   Field target=currentField;
//		   for(;pips>0;pips--){
//			   if(target==null){
//				   return false;
//			   }
//			   target = target.getNext();
//		   }
//		   if(target.getMeeple()==null){
//			   currentField.getMeeple().setField(target);
//			   return true;
//		   }else if(target.getMeeple().getPlayer() != this)
//		   {
//			   //throw out
//				Meeple opponentMeeple = target.getMeeple();
//				for(Home homeField : opponentMeeple.getPlayer().getHome())
//				{
//				   if (homeField.getMeeple() == null) {
//					   homeField.setMeeple(opponentMeeple);
//					   break;
//				   }
//				 }
//				 currentField.getMeeple().setField(target);
//				 return true;
//		   }
//		   return false;
//		}
		
		
		return true;
	}
}


