package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;

public class TestGameTest {
	@Test
	public void testManyToMany() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.game");
		Clazz game = model.createClazz("Game");
		Clazz player = model.createClazz("Player");
		game.withBidirectional(player, "won", Association.ONE, "winGame", Association.ONE);
		player.withBidirectional(game, "currentGame", Association.ONE, "currentPlayer", Association.ONE);

		model.dumpHTML("game");
	}
	@Test
	public void testSuperClazz() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.game");
		Clazz game = model.createClazz("Game");
		Clazz entity = model.createClazz("GameEntity");
		Clazz player = model.createClazz("Player");

		game.withSuperClazz(entity);
		player.withSuperClazz(entity);
		//		game.withBidirectional(player, "won", Association.ONE, "winGame", Association.ONE);
//		player.withBidirectional(game, "currentGame", Association.ONE, "currentPlayer", Association.ONE);

		model.dumpHTML("game");
	}
}
