/*
   Copyright (c) 2018 Stefan
   
   Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
   and associated documentation files (the "Software"), to deal in the Software without restriction, 
   including without limitation the rights to use, copy, modify, merge, publish, distribute, 
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
   furnished to do so, subject to the following conditions: 
   
   The above copyright notice and this permission notice shall be included in all copies or 
   substantial portions of the Software. 
   
   The Software shall be used for Good, not Evil. 
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */

package de.uniks.networkparser.test.model.ludo.util;

import de.uniks.networkparser.interfaces.AggregatedEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.ludo.Field;
import de.uniks.networkparser.test.model.ludo.Label;

public class LabelCreator implements AggregatedEntityCreator {
	public static final LabelCreator it = new LabelCreator();

	private final String[] properties = new String[] { Label.PROPERTY_NAME, Label.PROPERTY_FIELD, };

	private final String[] upProperties = new String[] {};

	private final String[] downProperties = new String[] {};

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public String[] getUpProperties() {
		return upProperties;
	}

	@Override
	public String[] getDownProperties() {
		return downProperties;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Label();
	}

	@Override
	public Object getValue(Object target, String attrName) {
		int pos = attrName.indexOf('.');
		String attribute = attrName;

		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		}

		if (Label.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return ((Label) target).getName();
		}

		if (Label.PROPERTY_FIELD.equalsIgnoreCase(attribute)) {
			return ((Label) target).getField();
		}

		return null;
	}

	@Override
	public boolean setValue(Object target, String attrName, Object value, String type) {
		if (Label.PROPERTY_NAME.equalsIgnoreCase(attrName)) {
			((Label) target).setName((String) value);
			return true;
		}

		if (SendableEntityCreator.REMOVE_YOU.equals(type)) {
			((Label) target).removeYou();
			return true;
		}
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attrName = attrName + type;
		}

		if (Label.PROPERTY_FIELD.equalsIgnoreCase(attrName)) {
			((Label) target).withField((Field) value);
			return true;
		}

		if ((Label.PROPERTY_FIELD + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName)) {
			((Label) target).withoutField((Field) value);
			return true;
		}

		return false;
	}

	// ==========================================================================
	public void removeObject(Object entity) {
		((Label) entity).removeYou();
	}
}
