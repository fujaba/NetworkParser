package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModelBuilder;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;

public class LiveRiskTest {

	@Test
	public void genModel() {
        ClassModelBuilder mb = new ClassModelBuilder("de.uniks.pm.ws1819.model");

        // Classes
        Clazz game = mb.buildClass("Game");
        Clazz plattform = mb.buildClass("Platform");
        Clazz player = mb.buildClass("Player");
        Clazz unit = mb.buildClass("Unit");

        // Attributes
        mb.createAttribute("name", DataType.STRING, game);

        plattform.createAttribute("capacity", DataType.INT);
        plattform.createAttribute("xPos", DataType.DOUBLE);
        plattform.createAttribute("yPos", DataType.DOUBLE);

        player.createAttribute("name", DataType.STRING);
        player.createAttribute("color", DataType.STRING);

        // Assoc
        game.withAssoc(player, "players", Association.MANY, "game", Association.ONE);
        game.withAssoc(player, "currentPlayer", Association.ONE, "currentGame", Association.ONE);
        game.withAssoc(player, "winner", Association.ONE, "gameWon", Association.ONE);
        game.withAssoc(plattform, "platforms", Association.MANY, "game", Association.ONE);
        game.withAssoc(plattform, "selectedPlatform", Association.ONE, "selectedBy", Association.ONE);

        plattform.withAssoc(plattform, "neighbors", Association.MANY, "neighbors", Association.MANY);
        plattform.withAssoc(unit, "units", Association.MANY, "platform", Association.ONE);

        player.withAssoc(unit, "units", Association.MANY, "player", Association.ONE);
        player.withAssoc(plattform, "platforms", Association.MANY, "player", Association.ONE);
        
//        ClassModel model = mb.build(ClassModelBuilder.NOGEN);
//        ClassModel model = mb.build("gen");
	}
}
