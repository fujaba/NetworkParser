package de.uniks.networkparser.test;

import java.beans.PropertyChangeEvent;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.logic.SimpleMapEvent;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.util.AppleCreator;
import de.uniks.networkparser.test.model.util.AppleTreeCreator;

public class FilterAtomar {
	BaseItem data;
	@Test
	public void testFilter() {
		AppleTree tree=new AppleTree();

		IdMap map = new IdMap();
		map.with(new AppleTreeCreator());
		map.with(new AppleCreator());

		UpdateListener listener = new UpdateListener() {
			@Override
			public boolean update(Object evt) {
				PropertyChangeEvent event = (PropertyChangeEvent) evt;
				return (Apple.PROPERTY_PASSWORD.equals(event.getPropertyName()) == false);
			}
		};
//		map.with(listener);
		map.toJsonObject(tree);
		map.getUpdateExecuter().withAtomarFilter(listener);
		map.with(new UpdateListener() {
			@Override
			public boolean update(Object event) {
				SimpleMapEvent simpleEvent = (SimpleMapEvent) event;
				data = simpleEvent.getEntity();
				return false;
			}
		});
		Apple apple = new Apple();
		apple.withPassword("23");
		apple.withX(23);
		apple.withY(42);
		tree.addToHas(apple);

		Assert.assertNotNull(data);
		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.AppleTree\",\"id\":\"J1.A1\",\"upd\":{\"has\":{\"class\":\"de.uniks.networkparser.test.model.Apple\",\"id\":\"J1.A2\",\"prop\":{\"x\":23,\"y\":42}}}}", data.toString());
	}
}
