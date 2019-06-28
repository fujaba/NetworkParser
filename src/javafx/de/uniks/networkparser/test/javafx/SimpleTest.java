package de.uniks.networkparser.test.javafx;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Item;
import de.uniks.networkparser.test.model.Person;

public class SimpleTest {
	@Test
	public void testModel() {
		GroupAccount groupAccount = new GroupAccount();
		Person albert = groupAccount.createPersons();
		albert.withItem(new Item().withValue(42));
		Assert.assertEquals(42, albert.getItem().getValueSum(), 0.0001);
	}
}
