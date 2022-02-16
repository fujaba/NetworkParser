package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureSet;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.SimpleSet;

/**
 * FeatureCondition for a Global Feature like Set, or PropertyChange.
 *
 * @author Stefan FeatureCondition for ModelFilter
 * 
 *         Format {{#feature SETCLASS=SimpleSet}}
 */
public class FeatureCondition extends CustomCondition<GraphMember> {
	private static final String PROPERTY_FEATURE = "variable.features";
	
	/** The Constant TAG. */
	public static final String TAG = "feature";
	private Feature feature;

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
	 * Creates the.
	 *
	 * @param buffer the buffer
	 * @param parser the parser
	 * @param customTemplate the custom template
	 */
	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		if (buffer == null) {
			return;
		}
		CharacterBuffer temp = buffer.nextToken(false, SPLITEND, ENTER);
		this.feature = Feature.valueOf(temp.toString()).create();
		temp = buffer.nextToken(false, SPLITEND);
		if (temp.length() > 0) {
			String string = temp.toString();
			if (SimpleSet.class.getSimpleName().equals(string) || SimpleSet.class.getName().equals(string)) {
				this.feature.withClazzValue(SimpleSet.class);
			} else {
				this.feature.withStringValue(string);
			}
			buffer.skipChar(SPLITEND);
		}
		buffer.skip();
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param isExpression the is expression
	 * @return the sendable instance
	 */
	@Override
	public FeatureCondition getSendableInstance(boolean isExpression) {
		return new FeatureCondition().withExpression(isExpression);
	}

	/**
	 * Gets the value.
	 *
	 * @param creator the creator
	 * @param member the member
	 * @return the value
	 */
	@Override
	public Object getValue(SendableEntityCreator creator, GraphMember member) {
		return null;
	}

	/**
	 * Gets the value.
	 *
	 * @param value the value
	 * @return the value
	 */
	@Override
	public CharSequence getValue(LocalisationInterface value) {
		Feature feature = getFeature(value);
		if (feature != null) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			Class<?> classValue = feature.getClassValue();
			if (classValue != null) {
				creator.setValue(value, "headers", classValue.getName(), SendableEntityCreator.NEW);
				return classValue.getSimpleName();
			}
			String stringValue = feature.getStringValue();
			if (stringValue != null) {
				creator.setValue(value, "headers", stringValue, SendableEntityCreator.NEW);
				return stringValue;
			}
		}
		return null;
	}

	/**
	 * Gets the feature.
	 *
	 * @param value the value
	 * @return the feature
	 */
	public Feature getFeature(Object value) {
		if (value instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			FeatureSet features = (FeatureSet) creator.getValue(creator, PROPERTY_FEATURE);
			return features.getFeature(this.feature);
		}
		if(this.isExpression) {
		  return this.feature;
		}
		return null;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (feature == null) {
			return true;
		}
		if (!this.isExpression && value instanceof ObjectCondition) {
			return ((ObjectCondition) value).update(this);
		}
		Feature feature = getFeature(value);
		if (feature != null) {
			Clazz clazz = getMember(value).getClazz();
			return hasFeatureProperty(feature, clazz);
		}
		return false;
	}
	
	/**
	 * With feature.
	 *
	 * @param cond the cond
	 * @return the feature condition
	 */
	public FeatureCondition withFeature(Feature cond) {
	  this.feature = cond;
	  this.isExpression=true;
	  return this; 
	}

	/**
	 * Checks for feature property.
	 *
	 * @param property the property
	 * @param values the values
	 * @return true, if successful
	 */
	public boolean hasFeatureProperty(Feature property, Clazz... values) {
		if (property != null) {
			if (values == null) {
				return true;
			}
			for (int i = 0; i < values.length; i++) {
				if (!property.match(values[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		CharacterBuffer buffer = new CharacterBuffer();
		buffer.with("{{");
		if (this.feature != null) {
			buffer.with(this.feature.getName().toString());
			String stringValue = this.feature.getStringValue();
			if (stringValue != null) {
				buffer.with(' ');
				buffer.with(stringValue);
			}
		}
		buffer.with("}}");
		return buffer.toString();
	}
}
