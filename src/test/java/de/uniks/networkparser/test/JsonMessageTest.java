package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.SimpleEvent;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.util.AppleCreator;
import de.uniks.networkparser.test.model.util.AppleTreeCreator;
import de.uniks.networkparser.test.model.util.GroupAccountCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;

public class JsonMessageTest implements UpdateListener {
	private SimpleList<String> messages;
	private int pos =0;

	@Override
	public boolean update(Object event) {
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
		map.with(new GroupAccountCreator());
		map.with(new PersonCreator());
		map.with(this);

		GroupAccount account= new GroupAccount();

		messages.with("{\"class\":\"de.uniks.networkparser.test.model.GroupAccount\",\"id\":\"J1.G1\",\"upd\":{\"persons\":{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"J1.P2\",\"prop\":{\"name\":\"Tobi\"}}}}",
			"{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"J1.P2\",\"upd\":{\"parent\":{\"class\":\"de.uniks.networkparser.test.model.GroupAccount\",\"id\":\"J1.G1\"}}}",
			"{\"class\":\"de.uniks.networkparser.test.model.GroupAccount\",\"id\":\"J1.G1\",\"upd\":{\"persons\":{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"J1.P3\",\"prop\":{\"parent\":{\"class\":\"de.uniks.networkparser.test.model.GroupAccount\",\"id\":\"J1.G1\"}}}}}",
			"{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"J1.P3\",\"upd\":{\"name\":\"Albert\"}}"
		);

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
		map.with(new AppleTreeCreator());
		map.with(new AppleCreator());
		map.put("root", tree);
		messages.with("{\"class\":\"de.uniks.networkparser.test.model.AppleTree\",\"id\":\"root\",\"upd\":{\"has\":{\"class\":\"de.uniks.networkparser.test.model.Apple\",\"id\":\"J1.A1\"}}}");
		map.with(this);
		tree.createApple();
		
	}
	
}
