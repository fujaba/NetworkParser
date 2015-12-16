package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.util.AppleCreator;
import de.uniks.networkparser.test.model.util.AppleTreeCreator;

public class FilterAtomar {

	@Test
	public void testFilter() {
		AppleTree tree=new AppleTree();
		
		JsonIdMap map = new JsonIdMap();
		map.with(new AppleTreeCreator());
		map.with(new AppleCreator());
		
		map.encode(tree);
		
		map.with(new UpdateListener() {
			@Override
			public boolean update(String typ, BaseItem source, Object target, String property, Object oldValue,
					Object newValue) {
				JsonObject json=(JsonObject) source;
//				System.out.println(json.toString(2));
				return false;
			}
		});
		Apple apple = new Apple();
		apple.withPassword("23");
		apple.withX(23);
		apple.withY(42);
		tree.addToHas(apple);
	}
}
