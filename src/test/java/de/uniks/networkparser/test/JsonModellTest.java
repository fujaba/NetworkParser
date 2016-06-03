package de.uniks.networkparser.test;

import java.beans.PropertyChangeEvent;
import java.io.PrintStream;
import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.logic.Deep;
import de.uniks.networkparser.logic.InstanceOf;
import de.uniks.networkparser.logic.SimpleMapEvent;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.util.GroupAccountCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;

public class JsonModellTest implements UpdateListener {

	private IdMap secondMap;

	@Test
	public void testSet(){
		GroupAccount account= new GroupAccount();
		account.createPersons().withName("Albert");
		account.createPersons().withName("Tobi");

		IdMap map= new IdMap();
		map.with(new PersonCreator());
		map.with(new GroupAccountCreator());
		Assert.assertEquals(175, map.toJsonArray(account.getPersons(), Filter.regard(InstanceOf.value(Person.class, Person.PROPERTY_PARENT))).toString(2).length());
	}
	
	@Test
	public void testModell(){
		IdMap map= new IdMap();
		map.with(this);
		map.with(new SortedMsgCreator());
		SortedMsg first= new SortedMsg();
		first.setNumber(1);

		SortedMsg second= new SortedMsg();
		second.setNumber(2);
		first.setChild(second);

		String sample="Hallo Welt";

		byte[] dataByte = sample.getBytes();
		Assert.assertEquals("Actual Size of String", 10, dataByte.length);

		// test string
		String text = "Hello world!";
		Assert.assertEquals("" + text+ "(" +text.length()+ ")", 12, text.length());

		// convert to big integer
		BigInteger number = new BigInteger(text.getBytes());

		// convert back
		new String(number.toByteArray());

		this.secondMap= new IdMap();
		secondMap.with(this);
		secondMap.with(new SortedMsgCreator());

		JsonObject jsonObject=map.toJsonObject(first);
		Assert.assertEquals(385, jsonObject.toString(2).length());
		
		secondMap.decode(jsonObject);

		SortedMsg third= new SortedMsg();
		third.setNumber(4);
		second.setChild(third);
		// DEEP 0
		Assert.assertEquals(165, map.toJsonObject(first, Filter.regard(Deep.value(1))).toString().length());
		// DEEP 1
		Assert.assertEquals(340, map.toJsonObject(first, Filter.regard(Deep.value(2))).toString().length());
		// DEEP 2
		Assert.assertEquals(438, map.toJsonObject(first, Filter.regard(Deep.value(3))).toString().length());
		third.updateNumber(2);
		third.setNumber(5);

		Assert.assertEquals(3, map.size());
		second.setChild(null);
	}

	@Override
	public boolean update(Object evt) {
		SimpleMapEvent simpleEvent = (SimpleMapEvent) evt;

		if(IdMap.NEW.equals(simpleEvent.getType())) {
			JsonObject jsonObject = (JsonObject) simpleEvent.getEntity();
			printToStream("Send: " +jsonObject, null);
//			secondMap.decode(jsonObject);
			return true;
		}
		PropertyChangeEvent event = (PropertyChangeEvent) evt;
		printToStream("ReceiveOBJ: Typ:" +event.getPropertyName()+ "value:" +event.getNewValue(), null);
		return false;
	}

	void printToStream(String str, PrintStream stream) {
		if(stream != null) {
			stream.println(str);
		}
	}
}
