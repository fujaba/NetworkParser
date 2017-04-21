package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.SimpleSet;

/**
 * @author Stefan
 * FeatureCondition for ModelFilter
 * 
 * Format {{#feature SETCLASS=SimpleSet}} 
 */
public class FeatureCondition implements ParserCondition {
	private static final String PROPERTY_FEATURE="variable.features";
	private static final String PROPERTY_MEMBER="member";
	private static final char SPLITEND='}';
	private static final char ENTER='=';
	public static final String TAG="feature";
	private FeatureProperty feature;
	
	@Override
	public String getKey() {
		return TAG;
	}

	@Override
	public CharSequence getValue(LocalisationInterface variables) {
		return null;
	}

	
	@Override
	public boolean update(Object value) {
		if(feature == null) {
			return true;
		}
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			Object model = creator.getValue(creator, PROPERTY_FEATURE);
			GraphMember member = (GraphMember) creator.getValue(creator, PROPERTY_MEMBER);
			Clazz clazz = member.getClazz();
			if(model != null && model instanceof SimpleSet<?>) {
				SimpleSet<?> list=(SimpleSet<?>) model;
				for(Object item : list) {
					if(item instanceof FeatureProperty == false) {
						continue;
					}
					FeatureProperty feature = (FeatureProperty) item;
					if(this.feature.getName() != feature.getName()) {
						continue;
					}
					return hasFeatureProperty(feature, clazz);
				}
			}
		}
		return false;
	}

	public boolean hasFeatureProperty(FeatureProperty property, Clazz... values) {
		if(property != null) {
			if(values == null) {
				return true;
			}
			for(int i=0;i<values.length;i++) {
				if(property.match(values[i]) == false) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public FeatureCondition create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		CharacterBuffer temp = buffer.nextToken(false, SPLITEND, ENTER);
		this.feature = Feature.valueOf(temp.toString()).create();
		temp = buffer.nextToken(false, SPLITEND);
		if(temp.length()>0) {
			String string = temp.toString();
			if(SimpleSet.class.getSimpleName().equals(string)) {
				this.feature.withClazzValue(SimpleSet.class);
//			}else {
//				this.feature.withClazzValue(ReflectionLoader.getClass(string));
			}
		}
		return this;
	}

	@Override
	public boolean isExpression() {
		return true;
	}
	
	@Override
	public String toString() {
		CharacterBuffer buffer=new CharacterBuffer();
		buffer.with("{{");
		buffer.with(this.feature.getName().toString());
		String stringValue = this.feature.getStringValue();
		if(stringValue != null) {
			buffer.with(' ');
			buffer.with(stringValue);
		}
		buffer.with("}}");
		return buffer.toString();
	}

}
