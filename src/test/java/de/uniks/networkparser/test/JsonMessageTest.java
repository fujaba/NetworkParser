package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.test.model.util.GroupAccountCreator;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.util.PersonCreator;

public class JsonMessageTest implements UpdateListener {
	@Test
	public void testModell(){
		JsonIdMap map= new JsonIdMap();
		map.withCreator(new GroupAccountCreator());
		map.withCreator(new PersonCreator());
		map.withUpdateListenerSend(this);

		GroupAccount account= new GroupAccount();
		
		map.encode(account);
		
		Person tobi = new Person().withName("Tobi");
		account.withUnidirectionalPersons(tobi);
		tobi.setUnidirectionalParent(account);
		
		account.createPersons().withName("Albert");
//		account.createPersons().withName("Tobi");
		
	}
	
	@Override
	public boolean update(String typ, BaseItem source, Object target, String property,
			Object oldValue, Object newValue) {
//		if(IdMap.SENDUPDATE.equals(typ)) {
//			JsonObject jsonObject = (JsonObject) source;
//			System.out.println("Send: " +jsonObject);
//			secondMap.getUpdateListener().execute(jsonObject);
//			return true;
//		}
		System.out.println(source);
//		System.out.println("ReceiveOBJ: Typ:" +property+ "value:" +newValue);
		return false;
	}
	
}
