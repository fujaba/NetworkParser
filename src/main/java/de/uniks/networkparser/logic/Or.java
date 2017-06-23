package de.uniks.networkparser.logic;

import java.beans.PropertyChangeListener;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.TemplateParser;
/**
 * Or Clazz for Or Conditions.
 *
 * @author Stefan Lindel
 */

public class Or extends ListCondition {
	public static final String TAG="or";

	/**
	 * Static Method for instance a new Instance of Or Object.
	 *
	 * @param conditions	All Conditions.
	 * @return 			The new Instance
	 */
	public static Or create(ObjectCondition... conditions) {
		return new Or().with(conditions);
	}
	
	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		buffer.skip();
		buffer.skip();
		ObjectCondition expression = parser.parsing(buffer, customTemplate, true, "endor");
		this.with(expression);		
	}
	
	@Override
	public Or with(ObjectCondition... values) {
		super.with(values);
		return this;
	}
	
	@Override
	public Or with(PropertyChangeListener... values) {
		super.with(values);
		return this;
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
		return new Or();
	}
}
