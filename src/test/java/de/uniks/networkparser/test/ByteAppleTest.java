package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.bytes.ByteBuffer;
import de.uniks.networkparser.bytes.ByteIdMap;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.util.AppleCreator;
import de.uniks.networkparser.test.model.util.AppleTreeCreator;

public class ByteAppleTest {

	// @Test
//	public void test() {
//		ClassModel model = new ClassModel("de.uniks");
//
//		Clazz appleTree = model.createClazz("AppleTree");
//
//		Clazz fruit = model.createClazz("Fruit")
//				.withAttribute("x", DataType.DOUBLE)
//				.withAttribute("y", DataType.DOUBLE);
//
//		Clazz apple = model.createClazz("Apple").withSuperClazz(fruit)
//				.withAttribute("value", DataType.INT);
//
//		appleTree.withAssoc(apple, "has", Card.MANY, "owner", Card.ONE);
//
//		model.generate("gen");
//	}

	@Test
	public void testSerialization() {
		AppleTree appleTree = new AppleTree();

		appleTree.withHas(new Apple(0, 123.32f, 239f));
		appleTree.withHas(new Apple(1, 5644f, 564f));
		appleTree.withHas(new Apple(2, 1680f, 50f));
		appleTree.withHas(new Apple(3, 54f, 654f));
		appleTree.withHas(new Apple(4, 654f, 333f));

		ByteIdMap map = new ByteIdMap();
		map.withCreator(new AppleTreeCreator());
		map.withCreator(new AppleCreator());
		ByteItem item = map.encode(appleTree);
		ByteBuffer bytes = item.getBytes(true);
		System.out.println(bytes.length());
		String string = item.toString();
//		System.out.println(string.length());
		System.out.println(string);
//		System.out.println(map.encode(appleTree, new ByteFilter()));
	}
	@Test
	public void testSimpleApple() {
		Apple apple = new Apple(4, 1, 3);
		ByteIdMap map = new ByteIdMap();
		map.withCreator(new AppleCreator());
		ByteItem item = map.encode(apple);
		ByteBuffer bytes = item.getBytes(true);
		System.out.println(bytes.length());
	}
	
	@Test
	public void testSimpleAppleTree() {
		AppleTree appleTree = new AppleTree();
		appleTree.withHas(new Apple(0, 123.32f, 239f));
		appleTree.withHas(new Apple(0, 123.32f, 239f));
		
//		appleTree.withHas(new Apple(1, 2, 3));
//		appleTree.withHas(new Apple(4, 5, 6));
//		appleTree.withHas(new Apple(7, 8, 9));
		ByteIdMap map = new ByteIdMap();
		map.withCreator(new AppleCreator(), new AppleTreeCreator());
		ByteItem item = map.encode(appleTree);
		ByteBuffer bytes = item.getBytes(true);
		System.out.println(bytes.length());
	}
	@Test
	public void testSimpleAppleTreePrimitive() {
		AppleTree appleTree = new AppleTree();
		appleTree.withHas(new Apple(2100000000, 123.32f, 239f));
		appleTree.withHas(new Apple(2100000000, 123.32f, 239f));
		ByteIdMap map = new ByteIdMap();
		map.withCreator(new AppleCreator(), new AppleTreeCreator());
		ByteItem item = map.encode(appleTree);
		ByteBuffer bytes = item.getBytes(true);
		System.out.println(bytes.length());
	}
	
	@Test
	public void testSerializationTwoItems() {
		AppleTree appleTree = new AppleTree();

		appleTree.withHas(new Apple(0, 123.32f, 239f));
		appleTree.withHas(new Apple(1, 5644f, 564f));

		ByteIdMap map = new ByteIdMap();
		map.withCreator(new AppleTreeCreator());
		map.withCreator(new AppleCreator());
//		map.withCreator(new AppleTreeCreator(), new AppleCreator());
		ByteItem item = map.encode(appleTree);

		ByteBuffer bytes = item.getBytes(true);
		System.out.println(bytes.length());
		String string = item.toString();
		System.out.println(string);
	}
}
