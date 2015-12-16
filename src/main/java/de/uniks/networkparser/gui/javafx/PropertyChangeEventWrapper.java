package de.uniks.networkparser.gui.javafx;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import java.beans.PropertyChangeEvent;
import de.uniks.networkparser.interfaces.BaseItem;
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
	public Object newInstance(BaseItem item) {
		Object source = item.getValueItem(PROPERTY_SOURCE);
		String property = ""+item.getValueItem(PROPERTY_PROPERTY);
		Object oldValue = item.getValueItem(PROPERTY_OLDVALUE);
		Object newValue = item.getValueItem(PROPERTY_NEWVALUE);
		return new PropertyChangeEvent(source, property, oldValue, newValue);
	}
}
