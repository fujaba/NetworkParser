package de.uniks.template;

import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class TemplateResultModel extends SimpleList<TemplateResultFile> implements TemplateInterface, LocalisationInterface{
	public static final String PROPERTY_TEMPLATE="templates";
	public static final String PROPERTY_TEXT="text";
	private SimpleKeyValueList<String, ParserCondition> customTemplate;
	private LocalisationInterface language;

	@Override
	public boolean add(TemplateInterface result) {
		if(result instanceof TemplateResultFile) {
			return super.add(result);
		}
		return false;
	}
	
	public TemplateResultModel withTemplate(SimpleKeyValueList<String, ParserCondition> templates) {
		this.customTemplate = templates;
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
		return null;
	}

	@Override
	public String get(CharSequence label) {
		return null;
	}

	public ParserCondition getTemplate(String tag) {
		if(customTemplate == null) {
			return null;
		}
		return customTemplate.get(tag);
	}

	@Override
	public boolean setParent(TemplateInterface templateResultFile) {
		return false;
	}

	@Override
	public TemplateInterface getParent() {
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		// TODO Auto-generated method stub
		return false;
	}


}