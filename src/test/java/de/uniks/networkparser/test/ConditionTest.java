package de.uniks.networkparser.test;

import java.beans.PropertyChangeEvent;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.logic.And;
import de.uniks.networkparser.logic.Between;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.logic.CompareTo;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.logic.IfCondition;
import de.uniks.networkparser.logic.Not;
import de.uniks.networkparser.logic.Or;
import de.uniks.networkparser.test.model.Person;

public class ConditionTest {

	@Test
	public void testCondition () {
		IfCondition ifCondition = new IfCondition();
		ifCondition.withExpression(new Between().withRange(0, 32));
		Person albert = new Person().withName("Albert");
		ifCondition.withTrue(new CompareTo().withValue(albert));
		And and = new And();
		Not not = new Not().withItem(new Equals().withValue("Albert"));
		and.add(not);
		and.add(new Or().add(new BooleanCondition().withValue(false)));
		ifCondition.withFalse(and);

		Assert.assertFalse(ifCondition.update(new PropertyChangeEvent(23, null, null, null)));
	}
}
