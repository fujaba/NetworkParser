package de.uniks.networkparser.logic;

import java.beans.PropertyChangeListener;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.TemplateParser;

public class And extends ListCondition {
	public static final String TAG="and";

	/**
	 * Static Method for instance a new Instance of And Object.
	 *
	 * @param conditions	All Conditions.
	 * @return 			The new Instance
	 */
	public static And create(ObjectCondition... conditions) {
		return new And().with(conditions);
	}

	@Override
	public And with(ObjectCondition... values) {
		super.with(values);
		return this;
	}
	@Override
	public And with(PropertyChangeListener... values) {
		super.with(values);
		return this;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		buffer.skip();
		buffer.skip();
		ObjectCondition expression = parser.parsing(buffer, customTemplate, true, true, "endand");
		this.with(expression);
		buffer.skipTo(SPLITEND, false);
		buffer.skip();
		buffer.skip();
	}

	@Override
	public boolean isExpression() {
		return true;
	}

	@Override
	public String getKey() {
		return TAG;
	}

	@Override
	public Object getSendableInstance(boolean isExpression) {
		return new And();
	}
}
