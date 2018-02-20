package de.uniks.networkparser.logic;

import java.beans.PropertyChangeListener;
import java.util.Collection;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.ConditionSet;

public class ChainCondition extends ListCondition {

	public ChainCondition enableHook() {
		this.chain = false;
		return this;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		// CHAIN CANT CREATE
	}

	@Override
	public ChainCondition with(ObjectCondition... values) {
		super.with(values);
		return this;
	}

	@Override
	public ChainCondition with(PropertyChangeListener... values) {
		super.with(values);
		return this;
	}

	public ChainCondition with(Collection<ObjectCondition> values) {
		ConditionSet list;

		if(this.list instanceof ConditionSet) {
			list = (ConditionSet) this.list;
		} else {
			list = new ConditionSet();
			list.with(this.list);
			this.list = list;
		}
		list.withList(values);
		return this;
	}


	@Override
	public boolean isExpression() {
		return false;
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public Object getSendableInstance(boolean isExpression) {
		return new ChainCondition();
	}
}
