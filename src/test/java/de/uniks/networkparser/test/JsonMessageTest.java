package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.House;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.util.AppleCreator;
import de.uniks.networkparser.test.model.util.AppleTreeCreator;
import de.uniks.networkparser.test.model.util.GroupAccountCreator;
import de.uniks.networkparser.test.model.util.HouseCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;

public class JsonMessageTest implements ObjectCondition {
	private SimpleList<String> messages;
	private int pos =0;

	@Override
	public boolean update(Object event) {
		if(event instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent simpleEvent = (SimpleEvent) event;
		if(simpleEvent.isNewEvent()){
			Assert.assertEquals("Message "+pos+":", messages.get(pos++), simpleEvent.getEntity().toString());
		}
		return true;
	}

	@Test
	public void testModell(){
		messages=new SimpleList<String>();
		this.pos = 0;
		IdMap map= new IdMap();
		map.withTimeStamp(1);
		map.with(new GroupAccountCreator());
		map.with(new PersonCreator());
		map.with(this);

		GroupAccount account= new GroupAccount();

		messages.with("{\"class\":\"de.uniks.networkparser.test.model.GroupAccount\",\"id\":\"G1\"}");
		messages.with("{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"P2\",\"prop\":{\"name\":\"Tobi\"}}");
		messages.with("{\"class\":\"de.uniks.networkparser.test.model.GroupAccount\",\"id\":\"G1\",\"upd\":{\"persons\":{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"P2\",\"prop\":{\"name\":\"Tobi\"}}}}");
		messages.with("{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"P2\",\"upd\":{\"parent\":{\"class\":\"de.uniks.networkparser.test.model.GroupAccount\",\"id\":\"G1\"}}}");
		messages.with("{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"P3\",\"prop\":{\"parent\":{\"class\":\"de.uniks.networkparser.test.model.GroupAccount\",\"id\":\"G1\"}}}");
		messages.with("{\"class\":\"de.uniks.networkparser.test.model.GroupAccount\",\"id\":\"G1\",\"upd\":{\"persons\":{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"P3\",\"prop\":{\"parent\":{\"class\":\"de.uniks.networkparser.test.model.GroupAccount\",\"id\":\"G1\"}}}}}");
		messages.with("{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"P3\",\"upd\":{\"name\":\"Albert\"}}");

		map.toJsonObject(account);

		Person tobi = new Person().withName("Tobi");
		account.withUnidirectionalPersons(tobi);
		tobi.setUnidirectionalParent(account);

		account.createPersons().withName("Albert");
//		account.createPersons().withName("Tobi");
	}

	@Test
	public void testModellWithUpdateSet(){
		messages=new SimpleList<String>();
		this.pos = 0;

		AppleTree tree = new AppleTree();
		tree.setName("Bananenbaum");
		IdMap map= new IdMap();
		map.withTimeStamp(1);
		map.with(new AppleTreeCreator());
		map.with(new AppleCreator());
		map.put("root", tree);
		messages.with("{\"class\":\"de.uniks.networkparser.test.model.Apple\",\"id\":\"A1\"}");
		messages.with("{\"class\":\"de.uniks.networkparser.test.model.AppleTree\",\"id\":\"root\",\"upd\":{\"has\":{\"class\":\"de.uniks.networkparser.test.model.Apple\",\"id\":\"A1\"}}}");
		map.with(this);
		tree.createApple();
		Assert.assertEquals(messages.size(), pos);

	}
	@Test
	public void testSimple() {
		House house=new House();
		house.setFloor(4);
		house.setName("University");
		IdMap map=new IdMap().with(new HouseCreator());
		map.withTimeStamp(1);
		messages = new SimpleList<String>();
		map.with(new ObjectCondition() {
			@Override
			public boolean update(Object evt) {
				if(evt instanceof SimpleEvent == false) {
					return false;
				}
				SimpleEvent simpleEvent = (SimpleEvent) evt;
				String msg = simpleEvent.getEntity().toString();
				messages.add(msg);
				return false;
			}
		});

		JsonObject json = map.toJsonObject(house);
		String string=json.toString();

		IdMap decodeMap=new IdMap().with(new HouseCreator());
		House newHouse = (House) decodeMap.decode(string);

		// Old Model
		Assert.assertEquals(4, newHouse.getFloor());
		Assert.assertEquals("University", newHouse.getName());

		// Update old Model
		house.setFloor(42);

		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.House\",\"id\":\"H1\",\"prop\":{\"name\":\"University\",\"floor\":4}}", messages.get(0));
		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.House\",\"id\":\"H1\",\"rem\":{\"floor\":4},\"upd\":{\"floor\":42}}", messages.get(1));

		decodeMap.decode(messages.get(1));

		Assert.assertEquals(42, newHouse.getFloor());
	}

}
