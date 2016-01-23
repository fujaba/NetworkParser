package de.uniks.networkparser.test;

import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.bytes.ByteFilter;
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

//	@Test
	public void testSerialization() {
		AppleTree appleTree = new AppleTree();

		appleTree.withHas(new Apple("0", 123.32f, 239f));
		appleTree.withHas(new Apple("1", 5644f, 564f));
		appleTree.withHas(new Apple("2", 1680f, 50f));
		appleTree.withHas(new Apple("3", 54f, 654f));
		appleTree.withHas(new Apple("4", 654f, 333f));

		ByteIdMap map = new ByteIdMap();
		map.with(new AppleTreeCreator());
		map.with(new AppleCreator());
		ByteItem item = map.encode(appleTree);
		ByteBuffer bytes = item.getBytes(true);
		Assert.assertEquals(167, bytes.length());
		String string = item.toString();
		Assert.assertEquals(267, string.length());
		Assert.assertEquals(267, map.encode(appleTree, new ByteFilter()).toString().length());
	}
//	@Te st
	public void testSimpleApple() {
		Apple apple = new Apple("4", 1, 3);
		ByteIdMap map = new ByteIdMap();
		map.with(new AppleCreator());
		ByteItem item = map.encode(apple);
		ByteBuffer bytes = item.getBytes(true);
		Assert.assertEquals(61, bytes.length());
	}
	
//	@Te st
	public void testSimpleAppleTree() {
		AppleTree appleTree = new AppleTree();
		appleTree.withHas(new Apple("0", 123.32f, 239f));
		appleTree.withHas(new Apple("0", 123.32f, 239f));
		
//		appleTree.withHas(new Apple(1, 2, 3));
//		appleTree.withHas(new Apple(4, 5, 6));
//		appleTree.withHas(new Apple(7, 8, 9));
		ByteIdMap map = new ByteIdMap();
		map.with(new AppleCreator(), new AppleTreeCreator());
		ByteItem item = map.encode(appleTree);
		ByteBuffer bytes = item.getBytes(true);
		Assert.assertEquals(94, bytes.length());
	}
//	@Test
	public void testSimpleAppleTreePrimitive() {
		AppleTree appleTree = new AppleTree();
		appleTree.withHas(new Apple("2100000000", 123.32f, 239f));
		appleTree.withHas(new Apple("2100000000", 123.32f, 239f));
		ByteIdMap map = new ByteIdMap();
		map.with(new AppleCreator(), new AppleTreeCreator());
		ByteItem item = map.encode(appleTree);
		ByteBuffer bytes = item.getBytes(true);
		Assert.assertEquals(101, bytes.length());
	}
	
	@Test
	public void testSerializationTwoItems() {
		AppleTree appleTree = new AppleTree();

		appleTree.withHas(new Apple("1", 5644f, 564f));
		appleTree.withHas(new Apple("0", 123.32f, 239f));

		ByteIdMap map = new ByteIdMap();
		map.with(new AppleTreeCreator());
		map.with(new AppleCreator());
//		map.withCreator(new AppleTreeCreator(), new AppleCreator());
		ByteItem item = map.encode(appleTree);

		ByteBuffer bytes = item.getBytes(true);
//		outputStream(bytes.array(), System.out);
		Assert.assertEquals(100, bytes.length());
		String string = item.toString();
		Assert.assertEquals(128, string.length());
	}
	
	void outputStream(byte[] bytes, PrintStream stream){
		if(stream == null) {
			return;
		}
		
		boolean newline=false;
		for (int i=0;i<bytes.length;i++){
			if(bytes[i]<10){
				stream.print(" 00" +(byte)bytes[i]);
				newline=false;
			} else if(bytes[i]<100){
				stream.print(" 0" +(byte)bytes[i]);
				newline=false;
			} else {
				stream.print(" " +(byte)bytes[i]);
				newline=false;
			}
			if((i+1)%10==0){
				newline=true;
				stream.println("");
			}
		}
		if(!newline){
			stream.println("");
		}
	}
}
