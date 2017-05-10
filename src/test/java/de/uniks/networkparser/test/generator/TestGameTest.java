package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;

public class TestGameTest {
	@Test
	public void testManyToMany() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.game");
		Clazz game = model.createClazz("Game");
		Clazz player = model.createClazz("Player");
		game.withBidirectional(player, "won", Cardinality.ONE, "winGame", Cardinality.ONE);
		player.withBidirectional(game, "currentGame", Cardinality.ONE, "currentPlayer", Cardinality.ONE);

		model.dumpHTML("game");
	}
	@Test
	public void testSuperClazz() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.game");
		Clazz game = model.createClazz("Game");
		Clazz entity = model.createClazz("GameEntity");
		Clazz player = model.createClazz("Player");
		
		game.withSuperClazz(entity);
		player.withSuperClazz(entity);
		//		game.withBidirectional(player, "won", Cardinality.ONE, "winGame", Cardinality.ONE);
//		player.withBidirectional(game, "currentGame", Cardinality.ONE, "currentPlayer", Cardinality.ONE);

		model.dumpHTML("game");
	}
}
