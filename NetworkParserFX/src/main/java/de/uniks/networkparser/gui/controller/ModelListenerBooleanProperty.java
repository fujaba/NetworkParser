package de.uniks.networkparser.gui.controller;

/*
 NetworkParser
 Copyright (c) 2011 - 2014, Stefan Lindel
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
import javafx.beans.Observable;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.logic.Condition;
import de.uniks.networkparser.logic.ValuesMap;

public class ModelListenerBooleanProperty extends ModelListenerProperty<Boolean> {
	private Condition<ValuesMap> condition;
	public ModelListenerBooleanProperty(SendableEntityCreator creator, Object item, String property) {
        super(creator, item, property);
    }

	@Override
	public void invalidated(Observable observable) {		
	}

	@Override
	public Boolean getValue() {
		Object value = creator.getValue(item, property);
		if(condition!=null){
			return condition.check(ValuesMap.with(item, property, value));
		}
		return false;
    }
	
	@Override
	public void setValue(Boolean value) {
		Object oldValue = creator.getValue(item, property);
		if(oldValue instanceof Boolean){
			super.setValue(value);
		}
	}
	
	public ModelListenerBooleanProperty withCondition(Condition<ValuesMap> condition){
		this.condition = condition;
		return this;
	}
}
