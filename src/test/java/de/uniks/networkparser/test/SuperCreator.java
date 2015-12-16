package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.gui.javafx.GenericGrammar;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.Fruit;
import de.uniks.networkparser.test.model.Tree;
import de.uniks.networkparser.test.model.util.FruitCreator;
import de.uniks.networkparser.test.model.util.GenericFruit;
import de.uniks.networkparser.test.model.util.TreeCreator;

public class SuperCreator {
	@Test
	public void testGenicApple() {
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new FruitCreator());
		
		Apple apple = new Apple();
		apple.withX(23).withY(42);
		
		String data = map.encode(apple).toString(2);
		
		JsonIdMap decodeMap=new JsonIdMap();
		decodeMap.withCreator(new FruitCreator());
		Fruit newData = (Fruit) decodeMap.decode(data);
		
		Assert.assertNotNull(newData);
		Assert.assertTrue(newData instanceof GenericFruit);

		JsonIdMap decodeAppleMap=new JsonIdMap();
		decodeAppleMap.withCreator(new FruitCreator());
		Fruit newApple = (Fruit) decodeAppleMap.decode(new Apple(), JsonObject.create(data));
		Assert.assertNotNull(newApple);
		Assert.assertTrue(newApple instanceof Apple);
	}
	@Test
	public void testGenicAppleTree() {
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new TreeCreator());
		
		AppleTree appletree = new AppleTree();
		appletree.setName("Grace");
		
		String data = map.encode(appletree).toString(2);
		
		JsonIdMap decodeMap=new JsonIdMap();
		decodeMap.withGrammar(new GenericGrammar());
		decodeMap.withCreator(new TreeCreator());
		Tree newData = (Tree) decodeMap.decode(data);
		Assert.assertNotNull(newData);
		Assert.assertTrue(newData instanceof AppleTree);
		
	}

}
