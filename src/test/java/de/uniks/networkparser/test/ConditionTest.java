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

		Assert.assertFalse(ifCondition.update(new PropertyChangeEvent(23, null, null, null)));
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
		and.add(Deep.value(42));
		or.add(InstanceOf.value(Person.class));
		IfCondition ifCon = new IfCondition();
		ifCon.withTrue(BooleanCondition.value(true));
		ifCon.withFalse(BooleanCondition.value(false));
		ifCon.withExpression(new Not().with(new Between().withRange(0, 42)));
		or.add(ifCon);
		
		Assert.assertFalse(and.update(new PropertyChangeEvent(23, null, null, 23)));
		
		IdMap map=new IdMap();
		map.with(new And());
		map.with(new Or());
		map.with(new InstanceOf());
		map.with(new BooleanCondition());
		map.with(new IfCondition());
		map.with(new Between());
		map.with(new Not());
		
		JsonObject jsonObject = map.toJsonObject(and);
		System.out.println(jsonObject);
	}
//	Or	34114	77%	77	50%	6	16	8	29	1	9	0	1
//	UpdateCondition	722	76%	33	50%	2	5	0	6	0	2	0	1
//	BooleanCondition	1443	75%	22	50%	2	11	3	15	0	9	0	1
//	Deep	2055	73%	26	75%	2	13	3	17	0	9	0	1
//	And	2866	70%	55	50%	4	13	6	20	0	8	0	1
//	InstanceOf	68134	66%	1917	47%	16	32	14	49	0	14	0	1
//	IfCondition	4687	65%	108	44%	9	21	12	35	0	12	0	1
//	Between	5194	65%	128	40%	10	21	8	30	0	11	0	1
//	Not	2240	65%	62	25%	5	13	4	15	1	9	0	1
//	CompareTo	5080	62%	128	40%	10	20	9	31	0	10	0	1
//	Equals	95100	51%
}
