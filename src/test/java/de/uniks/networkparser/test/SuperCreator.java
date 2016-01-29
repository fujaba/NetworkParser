package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;
import de.uniks.networkparser.gui.javafx.GenericGrammar;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.Fruit;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.Tree;
import de.uniks.networkparser.test.model.util.FruitCreator;
import de.uniks.networkparser.test.model.util.GenericFruit;
import de.uniks.networkparser.test.model.util.PersonCreator;
import de.uniks.networkparser.test.model.util.TreeCreator;

public class SuperCreator {
	@Test
	public void testGenicApple() {
		JsonIdMap map=new JsonIdMap();
		map.with(new FruitCreator());
		map.with(new GenericGrammar());
		Apple apple = new Apple();
		apple.withX(23).withY(42);

		String data = map.encode(apple).toString(2);

		JsonIdMap decodeMap=new JsonIdMap();
		decodeMap.with(new FruitCreator());
		decodeMap.with(new GenericGrammar());
		Fruit newData = (Fruit) decodeMap.decode(data);

		Assert.assertNotNull(newData);
		Assert.assertTrue(newData instanceof GenericFruit);

		JsonIdMap decodeAppleMap=new JsonIdMap();
		decodeAppleMap.with(new FruitCreator());
		decodeAppleMap.with(new GenericGrammar());
		Fruit newApple = (Fruit) decodeAppleMap.decode(new Apple(), JsonObject.create(data));
		Assert.assertNotNull(newApple);
		Assert.assertTrue(newApple instanceof Apple);
	}
	@Test
	public void testGenicAppleTree() {
		JsonIdMap map=new JsonIdMap();
		map.with(new TreeCreator());
		map.with(new GenericGrammar());

		AppleTree appletree = new AppleTree();
		appletree.setName("Grace");

		String data = map.encode(appletree).toString(2);

		JsonIdMap decodeMap=new JsonIdMap();
		decodeMap.with(new GenericGrammar());
		decodeMap.with(new TreeCreator());
		Tree newData = (Tree) decodeMap.decode(data);
		Assert.assertNotNull(newData);
		Assert.assertTrue(newData instanceof AppleTree);

	}

	@Test
	public void testGenicAppleTreePlusOwner() {
		JsonIdMap map=new JsonIdMap();
		map.with(new TreeCreator());
		map.with(new PersonCreator());
		map.with(new GenericGrammar());

		AppleTree appletree = new AppleTree();
		appletree.setName("Grace");
		Person owner= new Person();
		owner.withName("Albert");
		appletree.setPerson(owner);
		String data = map.encode(appletree).toString(2);

		JsonIdMap decodeMap=new JsonIdMap();
		decodeMap.with(new GenericGrammar());
		decodeMap.with(new TreeCreator());
		decodeMap.with(new PersonCreator());
		Tree newData = (Tree) decodeMap.decode(data);
		Assert.assertNotNull(newData);
		Assert.assertTrue(newData instanceof AppleTree);
	}

}
