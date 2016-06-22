package de.uniks.networkparser.test;

import java.beans.PropertyChangeEvent;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.And;
import de.uniks.networkparser.logic.Between;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.logic.CompareTo;
import de.uniks.networkparser.logic.Deep;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.logic.IdFilterElements;
import de.uniks.networkparser.logic.IfCondition;
import de.uniks.networkparser.logic.InstanceOf;
import de.uniks.networkparser.logic.Not;
import de.uniks.networkparser.logic.Or;
import de.uniks.networkparser.logic.SimpleEvent;
import de.uniks.networkparser.logic.UpdateCondition;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.University;

public class ConditionTest implements UpdateListener {
	@Test
	public void testCondition () {
		IfCondition ifCondition = new IfCondition();
		ifCondition.withExpression(new Between().withRange(0, 32));
		Person albert = new Person().withName("Albert");
		ifCondition.withTrue(new CompareTo().withValue(albert));
		And and = new And();
		Not not = new Not().with(new Equals().withValue("Albert"));
		and.add(not);
		and.add(new Or().add(new BooleanCondition().withValue(false)));
		ifCondition.withFalse(and);

		Assert.assertFalse(ifCondition.update(new PropertyChangeEvent(this, null, null, 23)));
		Assert.assertFalse(ifCondition.update(new PropertyChangeEvent(this, null, null, 42)));
		Assert.assertFalse(ifCondition.update(new PropertyChangeEvent(this, null, null, 23.0)));
		Assert.assertFalse(ifCondition.update(new PropertyChangeEvent(this, null, null, 42.0)));
	}

	@Test
	public void testLogicCondition() {
		IdFilterElements filter = new IdFilterElements(Person.class);
		Person albert = new Person().withName("Albert");

		University uni = new University().withName("Albert");
		PropertyChangeEvent pce = new PropertyChangeEvent(this, "child", null, albert);
		Assert.assertTrue(filter.update(pce));
		Assert.assertFalse(filter.update(pce));
		pce = new PropertyChangeEvent(this, "child", null, new Date());
		Assert.assertFalse(filter.update(pce));

		pce = new PropertyChangeEvent(this, "child", null, uni);
		Assert.assertFalse(filter.update(pce));
		filter = new IdFilterElements(InstanceOf.value(uni.getClass()));
		Assert.assertTrue(filter.update(pce));
	}

	@Test
	public void testLogicSimpleMapEvent() {
		IdMap map = new IdMap();
		SimpleEvent filter = new SimpleEvent(IdMap.NEW, map, "child");
		Assert.assertNotNull(filter.getSource());
		Assert.assertFalse(filter.isUpdateEvent());
		Assert.assertTrue(filter.isNewEvent());
		filter.with(IdMap.UPDATE);
		Assert.assertTrue(filter.isUpdateEvent());
	}

	@Test
	public void testLogicSimpleCollectionEvent() {
		SimpleSet<Object> items = new SimpleSet<Object>();
		items.withListener(this);
		items.add(new Person().withName("Albert"));
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent event = (SimpleEvent) value;
		Assert.assertNull(event.getBeforeElement());
		return false;
	}

	@Test
	public void testLogic() {
		And and = new And();
		and.add(BooleanCondition.value(true));
		Or or = new Or();
		and.add(or);
		and.setValue(and, And.CHILD, Deep.value(42), IdMap.NEW);
		or.add(InstanceOf.value(Person.class));
		BooleanCondition falseCondition = new BooleanCondition();
		falseCondition.setValue(falseCondition, BooleanCondition.VALUE, false, IdMap.NEW);
		Not not = new Not();
		not.setValue(not, Not.ITEM, falseCondition, IdMap.NEW);
		
		and.add(not);
		IfCondition ifCon = new IfCondition();
		ifCon.withTrue(BooleanCondition.value(true));
		ifCon.withFalse(BooleanCondition.value(false));
		ifCon.withExpression(new Not().with(new Between().withRange(0, 42)));
		or.setValue(or, Or.CHILD, ifCon, IdMap.NEW);
		
		
		Assert.assertFalse(and.update(new PropertyChangeEvent(23, null, null, 23)));

		IdMap map=new IdMap();
		map.with(new And());
		map.with(new Or());
		map.with(new InstanceOf());
		map.with(new BooleanCondition());
		map.with(new IfCondition());
		map.with(new Between());
		map.with(new Not());
		
		not.update(null);
		
		Assert.assertFalse(new IfCondition().update(null));

		JsonObject jsonObject = map.toJsonObject(and);
		
		And newAnd = (And) map.decode(jsonObject);
		
		Assert.assertNotNull(newAnd);
		
		Between between = new Between();
		between.withRange(0, 23);
		between.setValue(between, Between.FROM, 1, IdMap.NEW);
		between.setValue(between, Between.FROM, 1.0, IdMap.NEW);
		between.setValue(between, Between.TO, 23, IdMap.NEW);
		between.setValue(between, Between.TO, 42.0, IdMap.NEW);
		
//		InstanceOf	48154	76%	1521	58%	12	32	9	49	0	14	0	1
//		UpdateCondition	722	76%	33	50%	2	5	0	6	0	2	0	1
//		Deep	2055	73%	26	75%	2	13	3	17	0	9	0	1
//		CompareTo	4882	63%	119	45%	9	20	8	31	0	10	0	1
//		Equals
	}
	@Test
	public void testBetween() {
		Between between = new Between();
		between.withRange(0, 23);
		Assert.assertFalse(between.update(new PropertyChangeEvent(this, "root", null, new University())));
	}

	@Test
	public void testInstanceOf() {
		University uni = new University();
		InstanceOf condition = new InstanceOf();
		condition.setValue(condition, InstanceOf.PROPERTY, "root", IdMap.NEW);
		condition.setValue(condition, InstanceOf.VALUE, uni, IdMap.NEW);
		
		Assert.assertTrue(new InstanceOf().update(new PropertyChangeEvent(this, null, null, uni)));
		
		condition = InstanceOf.value(uni, InstanceOf.VALUE);
		Assert.assertNotNull(condition);
		
		Assert.assertFalse(condition.update(new PropertyChangeEvent(this, InstanceOf.VALUE, null, uni)));
		Assert.assertFalse(condition.update(this));
				
		condition = new InstanceOf();
		Assert.assertTrue(condition.update(new PropertyChangeEvent(this, InstanceOf.VALUE, null, uni)));
		condition.withValue(uni);
		Assert.assertFalse(condition.update(new PropertyChangeEvent(this, InstanceOf.VALUE, null, uni)));
		Assert.assertTrue(condition.update(new PropertyChangeEvent(this, InstanceOf.VALUE, null, new University())));
	}
	
	@Test
	public void testUpdateCondition() {
		UpdateCondition condition = new UpdateCondition();
		IdMap map=new IdMap();
		University uni = new University();
		map.put("root", uni);
		Assert.assertTrue(condition.update(new SimpleEvent("new", map,"VALUE")));
		
		Assert.assertFalse(condition.update(new SimpleEvent("new", map,"VALUE", null, uni)));
	}
}
