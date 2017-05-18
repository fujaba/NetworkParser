package de.uniks.networkparser.logic;

import java.util.Set;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.ConditionSet;

public class And implements ParserCondition, SendableEntityCreator {
	public static final String CHILD = "childs";
	public static final String TAG="and";
	private Object list;

	/**
	 * Static Method for instance a new Instance of And Object.
	 *
	 * @param conditions	All Conditions.
	 * @return 			The new Instance
	 */
	public static And create(ObjectCondition... conditions) {
		return new And().with(conditions);
	}
	
	public And with(ObjectCondition... conditions) {
		if(conditions == null) {
			return this;
		}
		if(this.list == null && conditions.length == 1 && conditions[0] instanceof ChainCondition == false) {
			this.list = conditions[0];
			return this;
		}
		ConditionSet list;
		if(this.list instanceof ConditionSet) {
			list = (ConditionSet) this.list;
		} else {
			list = new ConditionSet();
			list.with(this.list);
			this.list = list;
		}
		for(ObjectCondition condition : conditions) {
			list.add(condition);
		}
		return this;
	}

	public ConditionSet getList() {
		if(this.list instanceof ConditionSet) { 
			return (ConditionSet)this.list;
		}
		ConditionSet result = new ConditionSet();
		result.with(this.list);
		return result;
	}

	@Override
	public boolean update(Object evt) {
		Set<ObjectCondition> conditions = getList();
		for (ObjectCondition condition : conditions) {
			if (!condition.update(evt)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String[] getProperties() {
		return new String[] {CHILD};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new And();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (CHILD.equalsIgnoreCase(attribute)) {
			return ((And) entity).getList();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (CHILD.equalsIgnoreCase(attribute)) {
			((And) entity).with((ObjectCondition) value);
			return true;
		}
		return false;
	}

	@Override
	public String getKey() {
		return TAG;
	}
	
	@Override
	public boolean isExpression() {
		return true;
	}

	@Override
	public Object getValue(LocalisationInterface variables) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		buffer.skip();
		buffer.skip();
		ObjectCondition expression = parser.parsing(buffer, customTemplate, true);
		this.with(expression);
	}
	
	@Override
	public String toString() {
		Set<ObjectCondition> conditions = getList();
		CharacterBuffer buffer=new CharacterBuffer();
		for(ObjectCondition condition : conditions) {
			if(buffer.length()>0) {
				buffer.with("&&");
			}
			buffer.with(condition.toString());
		}
		return buffer.toString();
	}
}
