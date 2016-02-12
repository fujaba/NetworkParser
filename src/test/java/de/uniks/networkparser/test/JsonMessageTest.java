package de.uniks.networkparser.test;

import java.beans.PropertyChangeEvent;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.SimpleMapEvent;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.util.GroupAccountCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;

public class JsonMessageTest implements UpdateListener {
	@Test
	public void testModell(){
		JsonIdMap map= new JsonIdMap();
		map.with(new GroupAccountCreator());
		map.with(new PersonCreator());
		map.with(this);

		GroupAccount account= new GroupAccount();

		messages.with("{\"id\":\"J1.G1\",\"class\":\"de.uniks.networkparser.test.model.GroupAccount\",\"upd\":{\"persons\":{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"J1.P2\",\"prop\":{\"name\":\"Tobi\"}}}}",
			"{\"id\":\"J1.P2\",\"class\":\"de.uniks.networkparser.test.model.Person\",\"upd\":{\"parent\":{\"id\":\"J1.G1\",\"class\":\"de.uniks.networkparser.test.model.GroupAccount\"}}}",
			"{\"id\":\"J1.G1\",\"class\":\"de.uniks.networkparser.test.model.GroupAccount\",\"upd\":{\"persons\":{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"J1.P3\",\"prop\":{\"parent\":{\"id\":\"J1.G1\",\"class\":\"de.uniks.networkparser.test.model.GroupAccount\"}}}}}",
			"{\"id\":\"J1.P3\",\"class\":\"de.uniks.networkparser.test.model.Person\",\"upd\":{\"name\":\"Albert\"}}"
		);

		map.encode(account);

		Person tobi = new Person().withName("Tobi");
		account.withUnidirectionalPersons(tobi);
		tobi.setUnidirectionalParent(account);

		account.createPersons().withName("Albert");
//		account.createPersons().withName("Tobi");
	}
	private SimpleList<String> messages=new SimpleList<String>();
	private int pos =0;

	@Override
	public boolean update(PropertyChangeEvent event) {
		SimpleMapEvent simpleEvent = (SimpleMapEvent) event;
		Assert.assertEquals("Message "+pos+":", messages.get(pos++), simpleEvent.getEntity().toString());
		return false;
	}

}
