package de.uniks.template.condition;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.template.TemplateResultFragment;

/**
 * @author Stefan
 * FeatureCondition for ModelFilter
 * 
 * Format {{#feature SETCLASS=SimpleSet}} 
 */
public class FeatureCondition implements ParserCondition {
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
		if(value instanceof TemplateResultFragment) {
			TemplateResultFragment parser = (TemplateResultFragment) value;
			parser.getMember();
			
		}
		//.getFeature(Feature.SETCLASS).getClassValue().equals(SimpleSet.class))
//		Clazz clazz;
//		this.modelFactory.getFeature(Feature.SETCLASS, clazz).getClassValue().equals(SimpleSet.class));

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ObjectCondition create(CharacterBuffer buffer) {
		CharacterBuffer temp = buffer.nextToken(false, SPLITEND, ENTER);
		this.feature = Feature.valueOf(temp.toString()).create();
		temp = buffer.nextToken(false, SPLITEND);
		if(temp.length()>0) {
			String string = temp.toString();
			if(SimpleSet.class.getSimpleName().equals(string)) {
				this.feature.withClazzValue(SimpleSet.class);
			}else {
				this.feature.withClazzValue(ReflectionLoader.getClass(string));
			}
		}
		return this;
	}

	@Override
	public boolean isExpression() {
		return true;
	}
}
