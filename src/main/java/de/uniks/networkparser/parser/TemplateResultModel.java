package de.uniks.networkparser.parser;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class TemplateResultModel.
 *
 * @author Stefan
 */
public class TemplateResultModel extends SimpleList<TemplateResultFile>
		implements SendableEntityCreator, LocalisationInterface {
	
	/** The Constant PROPERTY_FEATURE. */
	public static final String PROPERTY_FEATURE = "features";
	
	/** The Constant PROPERTY_TEMPLATE. */
	public static final String PROPERTY_TEMPLATE = "templates";
	
	/** The Constant PROPERTY_TEXT. */
	public static final String PROPERTY_TEXT = "text";
	
	/** The Constant PROPERTY_CHILD. */
	public static final String PROPERTY_CHILD = "child";
	private SimpleSet<Feature> features;
	private SimpleKeyValueList<String, ParserCondition> customTemplate;
	private LocalisationInterface language;

	/**
	 * With template.
	 *
	 * @param templates the templates
	 * @return the template result model
	 */
	public TemplateResultModel withTemplate(SimpleKeyValueList<String, ParserCondition> templates) {
		this.customTemplate = templates;
		return this;
	}

	/**
	 * With template.
	 *
	 * @param templates the templates
	 * @return the template result model
	 */
	public TemplateResultModel withTemplate(ParserCondition... templates) {
		if (templates == null) {
			return this;
		}
		if (customTemplate == null) {
			customTemplate = new SimpleKeyValueList<String, ParserCondition>();
		}
		for (ParserCondition template : templates) {
			if (template != null) {
				customTemplate.add(template.getKey(), template);
			}
		}
		return this;
	}

	/**
	 * Gets the custom template.
	 *
	 * @return the custom template
	 */
	public SimpleKeyValueList<String, ParserCondition> getCustomTemplate() {
		return customTemplate;
	}

	/**
	 * With language.
	 *
	 * @param customLanguage the custom language
	 * @return the template result model
	 */
	public TemplateResultModel withLanguage(LocalisationInterface customLanguage) {
		this.language = customLanguage;
		return this;
	}

	/**
	 * Gets the text.
	 *
	 * @param label the label
	 * @param model the model
	 * @param gui the gui
	 * @return the text
	 */
	@Override
	public String getText(CharSequence label, Object model, Object gui) {
		if (this.language != null) {
			return this.language.getText(label, model, gui);
		}
		Object value = this.getValue(label);
		if (value != null) {
			return value.toString();
		}
		return null;
	}

	/**
	 * Put.
	 *
	 * @param label the label
	 * @param object the object
	 * @return the string
	 */
	@Override
	public String put(String label, Object object) {
		if (this.language != null) {
			return this.language.put(label, object);
		}
		return null;
	}

	/**
	 * Gets the template.
	 *
	 * @param tag the tag
	 * @return the template
	 */
	public ParserCondition getTemplate(String tag) {
		if (customTemplate == null || tag == null) {
			return null;
		}
		return customTemplate.get(tag.toLowerCase());
	}

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public LocalisationInterface getLanguage() {
		return language;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TemplateResultModel();
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_TEMPLATE, PROPERTY_TEXT };
	}

	/**
	 * Get Value from ResultModel.
	 *
	 * @param entity TemplateResultModel
	 * @param attribute like FEATURE
	 * @return value of Attribute
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (!(entity instanceof TemplateResultModel)) {
			return null;
		}
		TemplateResultModel model = (TemplateResultModel) entity;
		int pos = attribute.indexOf('.');
		String attrName;
		if (pos > 0) {
			attrName = attribute.substring(0, pos);
		} else {
			attrName = attribute;
		}
		if (PROPERTY_FEATURE.equalsIgnoreCase(attrName)) {
			if (pos > 0) {
				attribute = attribute.substring(pos + 1);
				pos = attribute.indexOf('.');
				if (pos > 0) {
					attrName = attribute.substring(0, pos);
				} else {
					attrName = attribute;
				}
				Feature feature = model.getFeature(attrName);
				if (feature != null && pos > 0) {
					return feature.getValue(attribute.substring(pos + 1));
				}
				return feature;
			}
			return model.getFeatures();
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (value instanceof TemplateResultFile) {
			return super.add((TemplateResultFile) value);
		}
		return false;
	}

	/**
	 * Gets the features.
	 *
	 * @return the features
	 */
	public SimpleSet<Feature> getFeatures() {
		return features;
	}

	/**
	 * Gets the feature.
	 *
	 * @param name the name
	 * @return the feature
	 */
	public Feature getFeature(String name) {
		if (features == null || name == null) {
			return null;
		}
		for (Feature prop : features) {
			if (name.equalsIgnoreCase(prop.getName().toString())) {
				return prop;
			}
		}
		return null;
	}

	/**
	 * With features.
	 *
	 * @param features the features
	 * @return the template result model
	 */
	public TemplateResultModel withFeatures(SimpleSet<Feature> features) {
		this.features = features;
		return this;
	}

}
