package de.uniks.networkparser.gui;

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
