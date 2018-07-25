package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.TileMap;
import de.uniks.networkparser.xml.XMLEntity;

public class TMXLoader {
	@Test
	public void testTMXLoader() {
		StringBuffer readFile = DocEnvironment.readFile("simpleMap.tmx");
		TileMap tile = TileMap.create(readFile.toString());
		tile.withPath("src/test/resources/de/uniks/networkparser/test/");
	

//		tile.getImage()

		// Write TileMap
		IdMap map=new IdMap();
		map.with(new TileMap());
		map.withFlag(IdMap.FLAG_NONE);
		XMLEntity xmlEntity = map.toXMLEntity(tile);
		Assert.assertNotNull(xmlEntity.toString(2));
		
		String mapStr = "<map><prop version=\"1.0\" orientation=\"orthogonal\" renderorder=\"right-down\" width=\"10\" height=\"10\" tilewidth=\"32\" tileheight=\"32\"><tileset firstgid=\"1\" tilewidth=\"32\" tileheight=\"32\" tilecount=\"667\" columns=\"23\"><image source=\"sprite.png\" width=\"736\" height=\"928\"/></tileset><layer name=\"Background\" width=\"10\" height=\"10\">" + 
				"<data encoding=\"csv\">24,25,25,25,25,25,25,25,26,0,\r\n47,48,48,48,48,48,48,48,49,0,\r\n47,48,48,48,48,48,48,48,48,0,\r\n47,48,48,48,48,95,118,48,0,48,\r\n47,48,48,48,48,118,0,0,0,0,\r\n47,48,48,48,78,78,78,55,77,77,\r\n47,48,93,48,0,0,0,0,0,0,\r\n47,48,48,48,0,0,0,0,0,0,\r\n47,48,48,0,0,0,0,0,0,0,\r\n47,48,48,48,0,0,0,0,0,0\r\n</data></layer></prop></map>";
		Assert.assertEquals(mapStr, xmlEntity.toString());
	}
}
