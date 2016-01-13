package de.uniks.networkparser.test;

import java.beans.PropertyChangeEvent;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.gui.javafx.PropertyChangeEventWrapper;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.Item;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.util.ItemCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;

public class PropertyChangeEventTest {

	@Test
	public void testPropertyChange() {
		JsonIdMap map=new JsonIdMap();
		map.with(new PropertyChangeEventWrapper());
		map.with(new PersonCreator());
		map.with(new ItemCreator());
		
		Person person = new Person();
		Item item = new Item();
		PropertyChangeEvent propertyChange = new PropertyChangeEvent(person, "child", null, item);
		
		JsonObject encode = map.encode(propertyChange);

		//Decode
		JsonIdMap decodeMap=new JsonIdMap();
		decodeMap.with(new PropertyChangeEventWrapper());
		decodeMap.with(new PersonCreator());
		decodeMap.with(new ItemCreator());

		
		PropertyChangeEvent decode = (PropertyChangeEvent) decodeMap.decode(encode.toString());
		Assert.assertEquals(person.getClass(), decode.getSource().getClass());
		Assert.assertEquals("child", decode.getPropertyName());
	}
}
