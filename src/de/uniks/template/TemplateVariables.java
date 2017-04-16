package de.uniks.template;

import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class TemplateVariables implements LocalisationInterface, SendableEntityCreator{
	public static final String PROPERTY_TEMPLATE="template";
	private TemplateResultFragment fragment;
	private LocalisationInterface variables;
	
	TemplateVariables() {
		
	}
	public TemplateVariables(TemplateResultFragment owner, LocalisationInterface variables) {
		this.fragment = owner;
		this.variables = variables;
	}
	
	public TemplateResultModel getTemplateModel() {
		TemplateInterface item = fragment;
		while(item != null) {
			item = fragment.getParent();
			if(item instanceof TemplateResultModel) {
				return (TemplateResultModel)item;
			}
		}
		return null;
	}

	@Override
	public String getText(CharSequence label, Object model, Object gui) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get(CharSequence label) {
		// Global Variables
		if(this.variables != null) {
			String value = variables.get(label);
			if(value != null) {
				return value;
			}
		}
		// Global Variables
		TemplateResultModel templateModel = getTemplateModel();
		if(templateModel != null) {
			String value = templateModel.get(label);
			if(value != null) {
				return value;
			}
		}
		return null;
	}

	public LocalisationInterface getVariables() {
		return variables;
	}

	@Override
	public String[] getProperties() {
		return null;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(entity instanceof TemplateVariables == false) {
			return null;
		}
		TemplateVariables element = (TemplateVariables) entity;
		int pos = attribute.indexOf('.');
		String attrName;
		if(pos>0) {
			attrName = attribute.substring(0, pos);
		}else {
			attrName = attribute;
		}
		if(PROPERTY_TEMPLATE.equalsIgnoreCase(attrName)) {
			if(pos>0) {
				TemplateResultFragment item = element.getTemplate();
				return item.getValue(item, attribute.substring(pos+1));
			}
			return element.getTemplate();
		}
		return element.get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TemplateVariables();
	}
	public TemplateResultFragment getTemplate() {
		return fragment;
	}
}
