package de.uniks.networkparser.ext.javafx.controller;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
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
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.scene.Node;

public class ModelListenerStringProperty extends ModelListenerProperty<String> {
	public ModelListenerStringProperty(SendableEntityCreator creator, Object item, String property) {
		super(creator, item, property);
	}
	
	@Override
	public void invalidated(Observable observable) {
	}

	@Override
	public String getValue() {
		Object item = getItemValue();
		if(item!=null){
			return ""+item;
		}
		return "";
	}

	@Override
	public String parseValue(Object value) {
		return ""+value;
	}

	@SuppressWarnings("unchecked")
	public static ModelListenerStringProperty create(Node node, Object item, String property, Condition<SimpleEvent> listener) {
		if(node == null) {
			return null;
		}
		GenericCreator creator = new GenericCreator(item);
		ModelListenerStringProperty stringProperty = new ModelListenerStringProperty(creator, item, property);
		stringProperty.withCallBack(listener);
		Property<String> nodeProperty = (Property<String>) ModelListenerFactory.getProperty(node);
		if(nodeProperty!= null) {
			stringProperty.bind(nodeProperty);
			stringProperty.executeCallBack();
		}
		return stringProperty;
	}

	private ModelListenerStringProperty withCallBack(Condition<SimpleEvent> listener) {
		this.callBack = listener;
		return this;
	}
}
