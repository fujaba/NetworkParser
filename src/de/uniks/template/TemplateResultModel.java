package de.uniks.template;

import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class TemplateResultModel extends SimpleList<TemplateResultFile> implements SendableEntityCreator, LocalisationInterface{
	public static final String PROPERTY_FEATURE="features";
	public static final String PROPERTY_TEMPLATE="templates";
	public static final String PROPERTY_TEXT="text";
	public static final String PROPERTY_CHILD="child";
	private SimpleSet<FeatureProperty> features;
	private SimpleKeyValueList<String, ParserCondition> customTemplate;
	private LocalisationInterface language;

	public TemplateResultModel withTemplate(SimpleKeyValueList<String, ParserCondition> templates) {
		this.customTemplate = templates;
		return this;
	}
	
	public TemplateResultModel withTemplate(ParserCondition... templates) {
		if(templates == null) {
			return this;
		}
		if(customTemplate == null) {
			customTemplate = new SimpleKeyValueList<String, ParserCondition>();
		}
		for(ParserCondition template : templates) {
			if(template != null) {
				customTemplate.add(template.getKey(), template);
			}
		}
		return this;
	}
	
	public SimpleKeyValueList<String, ParserCondition> getCustomTemplate() {
		return customTemplate;
	}
	
	public TemplateResultModel withLanguage(LocalisationInterface customLanguage) {
		this.language = customLanguage;
		return this;
	}
	
	@Override
	public String getText(CharSequence label, Object model, Object gui) {
		if(this.language != null) {
			return this.language.getText(label, model, gui);
		}
		return null;
	}

	@Override
	public String put(String label, Object object) {
		if(this.language != null) {
			return this.language.put(label, object);
		}
		return null;
	}

	public ParserCondition getTemplate(String tag) {
		if(customTemplate == null) {
			return null;
		}
		return customTemplate.get(tag);
	}

	public LocalisationInterface getLanguage() {
		return language;
	}

	
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TemplateResultModel();
	}
	
	@Override
	public String[] getProperties() {
		return new String[] {PROPERTY_TEMPLATE, PROPERTY_TEXT};
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(entity instanceof TemplateResultModel == false) {
			return null;
		}
		TemplateResultModel model = (TemplateResultModel) entity;
		if(PROPERTY_FEATURE.equalsIgnoreCase(attribute)) {
			return model.getFeatures();
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(value instanceof TemplateResultFile) {
			return super.add((TemplateResultFile)value);
		}
		return false;
	}

	public SimpleSet<FeatureProperty> getFeatures() {
		return features;
	}

	public TemplateResultModel withFeatures(SimpleSet<FeatureProperty> features) {
		this.features = features;
		return this;
	}

}