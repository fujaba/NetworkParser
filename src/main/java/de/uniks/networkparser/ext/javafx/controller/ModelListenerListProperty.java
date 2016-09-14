package de.uniks.networkparser.ext.javafx.controller;

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
