package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.TileMap;

public class TMXLoader extends IOClasses{
	@Test
	public void testTMXLoader() {
		StringBuffer readFile = readFile("simpleMap.tmx");
		TileMap tile = TileMap.create(readFile.toString());
		tile.withPath("src/test/resources/de/uniks/networkparser/test/");
	

//		tile.getImage()

		// Write TileMap
		IdMap map=new IdMap();
		map.with(new TileMap());
		map.withFlag(IdMap.FLAG_NONE);
		Assert.assertNotNull(map.toXMLEntity(tile).toString(2));
	}
}
