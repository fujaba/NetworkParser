package de.uniks.networkparser.gui.javafx.controller;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import com.sun.javafx.collections.ObservableListWrapper;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class ModelListenerListProperty extends ModelListenerProperty<ObservableList<String>> {
	private ObservableValue<ObservableList<String>> values = new SimpleObjectProperty<ObservableList<String>>(new ObservableListWrapper<String>(new ArrayList<String>()));
	private SendableEntityCreator childCreator;
	private String childProperty;

	public ModelListenerListProperty(SendableEntityCreator creator,
			Object item, String property, String childProperty, SendableEntityCreator childCreator) {
		super(creator, item, property);

		this.childCreator = childCreator;
		this.childProperty = childProperty;
		refreshList();
	}
	public void refreshList(){
		Object value = creator.getValue(item, property);
		if(value instanceof Collection<?>){
			ObservableList<String> observableList = values.getValue();
			observableList.clear();
			for(Iterator<?> iterator = ((Collection<?>)value).iterator();iterator.hasNext();){
				Object child = iterator.next();
				Object childValue = childCreator.getValue(child, childProperty);
				new ModelListenerStringProperty(childCreator, child, childProperty).addListener(new ObjectListener());
				
				observableList.add(""+childValue);
			}
		}
	}

	@Override
	public void invalidated(Observable observable) {
		
	}

	@Override
	public ObservableList<String> getValue() {
		return values.getValue();
	}
	
	class ObjectListener implements ChangeListener<Object>{
		@Override
		public void changed(ObservableValue<? extends Object> observable,
				Object oldValue, Object newValue) {
			refreshList();
		}
	}
}
