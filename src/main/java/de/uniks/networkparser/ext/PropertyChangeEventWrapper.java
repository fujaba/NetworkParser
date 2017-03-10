package de.uniks.networkparser.ext;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreatorWrapper;

public class PropertyChangeEventWrapper extends SendableEntityCreatorWrapper {
	public static final String PROPERTY_SOURCE="source";
	public static final String PROPERTY_PROPERTY="property";
	public static final String PROPERTY_OLDVALUE="oldValue";
	public static final String PROPERTY_NEWVALUE="newValue";
	public static final String SENDABLECLASSSTRING="java.beans.PropertyChangeEvent";

	@Override
	public String[] getProperties() {
		return new String[]{PROPERTY_SOURCE, PROPERTY_PROPERTY, PROPERTY_OLDVALUE, PROPERTY_NEWVALUE};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new PropertyChangeEvent(this,null,null,null);
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(entity instanceof PropertyChangeEvent) {
			if(PROPERTY_SOURCE.equalsIgnoreCase(attribute)) {
				return ((PropertyChangeEvent)entity).getSource();
			}
			if(PROPERTY_PROPERTY.equalsIgnoreCase(attribute)) {
				return ((PropertyChangeEvent)entity).getPropertyName();
			}
			if(PROPERTY_OLDVALUE.equalsIgnoreCase(attribute)) {
				return ((PropertyChangeEvent)entity).getOldValue();
			}
			if(PROPERTY_NEWVALUE.equalsIgnoreCase(attribute)) {
				return ((PropertyChangeEvent)entity).getNewValue();
			}
		}
		return null;
	}

	@Override
	public Object newInstance(Entity item) {
		Object source = item.getValue(PROPERTY_SOURCE);
		String property = ""+item.getValue(PROPERTY_PROPERTY);
		Object oldValue = item.getValue(PROPERTY_OLDVALUE);
		Object newValue = item.getValue(PROPERTY_NEWVALUE);
		return new PropertyChangeEvent(source, property, oldValue, newValue);
	}
}
