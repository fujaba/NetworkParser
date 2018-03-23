package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.gui.TileMap;

public class TestMap {

	@Test
	public void testMap() {
		CharacterBuffer buffer = FileBuffer.readFile("res/assets/room1.tmx");
		
		TileMap tile = TileMap.create(buffer.toString());
		tile.withPath("src/test/resources/de/uniks/networkparser/test/");
	}
}
