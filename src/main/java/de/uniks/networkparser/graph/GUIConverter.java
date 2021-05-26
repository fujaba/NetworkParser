package de.uniks.networkparser.graph;

import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.gui.controls.Button;
import de.uniks.networkparser.gui.controls.Control;
import de.uniks.networkparser.gui.controls.Group;
import de.uniks.networkparser.gui.controls.Label;
import de.uniks.networkparser.gui.controls.NumberField;
import de.uniks.networkparser.gui.controls.TextField;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.XMLEntity;

public class GUIConverter implements Converter {
	private SimpleKeyValueList<String, Control> factory;

	public GUIConverter() {
		factory = new SimpleKeyValueList<String, Control>();
		factory.add("textArea", new TextField());
		factory.withGroup(new Group(), "vbox", "hbox");
		factory.add("label", new Label());
		factory.add("spinner", new NumberField());
		factory.add("button", new Button());
	}

	@Override
	public String encode(BaseItem entity) {
		if (entity instanceof Entity) {
			return convert((Entity) entity).toString();
		}
		return null;
	}

	public Control convert(String value) {
		return convert(new XMLEntity().withValue(value));
	}

	public Control convert(Buffer value) {
		return convert(new XMLEntity().withValue(value));
	}

	public Control convert(Entity value) {
		if (value instanceof XMLEntity) {
			Group group = new Group();
			group.addElement(parsingXMLEntity((XMLEntity) value));
			return group;
		}
		return null;
	}

	public Control parsingXMLEntity(XMLEntity element) {
		String tag = null;
		if (element != null) {
			tag = element.getTag();
		}
		if (tag == null) {
			return null;
		}

		Control factory = this.factory.get(tag.toLowerCase());
		if (factory != null) {
			Control child = factory.newInstance();
			XMLEntity children = (XMLEntity) element.getElementBy(XMLEntity.PROPERTY_TAG, "children");
			if (children != null) {
				boolean add = false;
				for (int i = 0; i < children.sizeChildren(); i++) {
					if (child.setValue(Control.PROPERTY_ELEMENTS, parsingXMLEntity((XMLEntity) children.getChild(i)))) {
						add = true;
					}
				}
				if (add) {
					return child;
				}
				return null;
			}
			return child;
		}
		return null;
	}
}
