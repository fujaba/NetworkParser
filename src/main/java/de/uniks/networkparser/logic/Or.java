package de.uniks.networkparser.logic;

import java.beans.PropertyChangeListener;
import java.util.Set;

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
	
	/** The Constant TAG. */
	public static final String TAG = "or";

	/**
	 * Static Method for instance a new Instance of Or Object.
	 *
	 * @param conditions All Conditions.
	 * @return The new Instance
	 */
	public static Or create(ObjectCondition... conditions) {
		return new Or().with(conditions);
	}

	/**
	 * Creates the.
	 *
	 * @param buffer the buffer
	 * @param parser the parser
	 * @param customTemplate the custom template
	 */
	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		if(buffer == null) {
			return;
		}
		buffer.skip();
		buffer.skip();
		ObjectCondition expression = parser.parsing(buffer, customTemplate, true, true, "endor");
		this.with(expression);

		/* SKIP TO END */
		buffer.skipTo(SPLITEND, false);
		buffer.skip();
		buffer.skip();
	}

	/**
	 * Update set.
	 *
	 * @param evt the evt
	 * @return true, if successful
	 */
	@Override
	public boolean updateSet(Object evt) {
		Set<ObjectCondition> list = getList();
		for (ObjectCondition item : list) {
			if (item.update(evt)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * With.
	 *
	 * @param values the values
	 * @return the or
	 */
	@Override
	public Or with(ObjectCondition... values) {
		super.with(values);
		return this;
	}

	/**
	 * With.
	 *
	 * @param values the values
	 * @return the or
	 */
	@Override
	public Or with(PropertyChangeListener... values) {
		super.with(values);
		return this;
	}

	/**
	 * Checks if is expression.
	 *
	 * @return true, if is expression
	 */
	@Override
	public boolean isExpression() {
		return true;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	@Override
	public String getKey() {
		return TAG;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param isExpression the is expression
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean isExpression) {
		return new Or();
	}
}
