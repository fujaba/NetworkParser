package de.uniks.networkparser.test;

import java.math.BigInteger;

import org.junit.Test;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.interfaces.UpdateListenerRead;
import de.uniks.networkparser.interfaces.UpdateListenerSend;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.logic.InstanceOf;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.util.PersonCreator;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;

public class JsonModellTest implements UpdateListenerRead, UpdateListenerSend {

	private JsonIdMap secondMap;

	@Test
	public void testSet(){
		GroupAccount account= new GroupAccount();
		account.createPersons().withName("Albert");
		account.createPersons().withName("Tobi");
		
		
		JsonIdMap map= new JsonIdMap();
		map.withCreator(new PersonCreator());
//		System.out.println(map.toJsonArray(account.getPersons(), new Filter().withPropertyRegard(InstanceOf.value(Person.class, Person.PROPERTY_PARENT))).toString(2));
		System.out.println(map.toJsonArray(account.getPersons(), Filter.regard(InstanceOf.value(Person.class, Person.PROPERTY_PARENT))).toString(2));
	}
	
	
	@Test
	public void testModell(){
		JsonIdMap map= new JsonIdMap();
		map.withUpdateListenerRead(this);
		map.withUpdateListenerSend(this);
		map.withCreator(new SortedMsgCreator());
		SortedMsg first= new SortedMsg();
		first.setNumber(1);
		
		SortedMsg second= new SortedMsg();
		second.setNumber(2);
		first.setChild(second);
		
		
		String sample="Hallo Welt";
		
	    byte[] dataByte = sample.getBytes();
	    System.out.println("Compression Demo");
	    System.out.println("Actual Size of String : " + dataByte.length);
	    
		// test string
		String text = "Hello world!";
		System.out.println("" + text+ "(" +text.length()+ ")");

		// convert to big integer
		BigInteger number = new BigInteger(text.getBytes());
		
		// convert back
		String textBack = new String(number.toByteArray());
		
		this.secondMap= new JsonIdMap();
		secondMap.withUpdateListenerRead(this);
		secondMap.withUpdateListenerSend(this);
		secondMap.withCreator(new SortedMsgCreator());

		JsonObject jsonObject=map.toJsonObject(first);
		System.out.println(jsonObject.toString(2));
		secondMap.getUpdateListener().execute(jsonObject);
		
		SortedMsg third= new SortedMsg();
		third.setNumber(4);
		second.setChild(third);
		// DEEP 0
//		System.out.println(map.toJsonObject(first, new JsonFilter(0)).toString());
		// DEEP 1
//		System.out.println(map.toJsonObject(first, new JsonFilter(1)).toString());
		// DEEP 2
//		System.out.println(map.toJsonObject(first, new JsonFilter(2)).toString());
		third.updateNumber(2);
		third.setNumber(5);
		
//		System.out.println(map.size());
		second.setChild(null);
	}

	@Test
	public void testModellReplicator(){
		System.out.println("9".compareTo("10")); //8
		System.out.println("1".compareTo("2"));  //-1
		System.out.println("10".compareTo("9"));  //-8
		System.out.println(new Integer(1).compareTo(2)); //-1
		System.out.println(new Integer(9).compareTo(10));//-1
		System.out.println(new Integer(10).compareTo(9)); // 1
	}

	@Override
	public boolean sendUpdateMsg(Object target, String property, Object oldObj, Object newObject,
			JsonObject jsonObject) {
		System.out.println("Send: " +jsonObject);
		secondMap.getUpdateListener().execute(jsonObject);
		return true;
	}

	@Override
	public boolean readMessages(String key, Object element, Object value,
			JsonObject props, String type) {
		System.out.println("ReceiveOBJ: Typ:" +key+ "value:" +value);
		return false;
	}
}
