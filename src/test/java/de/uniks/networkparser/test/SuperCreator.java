package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.generic.GenericJsonGrammar;
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
		IdMap map=new IdMap();
		map.with(new FruitCreator());
		map.with(new GenericJsonGrammar());
		Apple apple = new Apple();
		apple.withX(23).withY(42);

		String data = map.toJsonObject(apple).toString(2);

		IdMap decodeMap=new IdMap();
		decodeMap.with(new FruitCreator());
		decodeMap.with(new GenericJsonGrammar());
		Fruit newData = (Fruit) decodeMap.decode(data);

		Assert.assertNotNull(newData);
		Assert.assertTrue(newData instanceof GenericFruit);

		IdMap decodeAppleMap=new IdMap();
		decodeAppleMap.with(new FruitCreator());
		decodeAppleMap.with(new GenericJsonGrammar());
		Fruit newApple = (Fruit) decodeAppleMap.decode(JsonObject.create(data), new Apple(), null);
		Assert.assertNotNull(newApple);
		Assert.assertTrue(newApple instanceof Apple);
	}
	@Test
	public void testGenicAppleTree() {
		IdMap map=new IdMap();
		map.with(new TreeCreator());
		map.with(new GenericJsonGrammar());

		AppleTree appletree = new AppleTree();
		appletree.setName("Grace");

		String data = map.toJsonObject(appletree).toString(2);

		IdMap decodeMap=new IdMap();
		decodeMap.with(new GenericJsonGrammar());
		decodeMap.with(new TreeCreator());
		Tree newData = (Tree) decodeMap.decode(data);
		Assert.assertNotNull(newData);
		Assert.assertTrue(newData instanceof AppleTree);

	}

	@Test
	public void testGenicAppleTreePlusOwner() {
		IdMap map=new IdMap();
		map.with(new TreeCreator());
		map.with(new PersonCreator());
		map.with(new GenericJsonGrammar());

		AppleTree appletree = new AppleTree();
		appletree.setName("Grace");
		Person owner= new Person();
		owner.withName("Albert");
		appletree.setPerson(owner);
		String data = map.toJsonObject(appletree).toString(2);

		IdMap decodeMap=new IdMap();
		decodeMap.with(new GenericJsonGrammar());
		decodeMap.with(new TreeCreator());
		decodeMap.with(new PersonCreator());
		Tree newData = (Tree) decodeMap.decode(data);
		Assert.assertNotNull(newData);
		Assert.assertTrue(newData instanceof AppleTree);
	}

}
