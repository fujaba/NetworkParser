package de.uniks.networkparser.ext.javafx.controller;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.javafx.controller.ModelListenerProperty.PROPERTYTYPE;
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

public class ModelListenerFactory {
	public static ModelListenerProperty create(Object node, Object item){
		return create(node, item, null);
	}
	public static ModelListenerProperty create(Object node, Object item, String field){
		GenericCreator creator = new GenericCreator(item);
		return create(node, creator, item, field);
	}
	public static ModelListenerProperty create(Object node, IdMap map, Object item, String field) {
		return create(node, map.getCreatorClass(item), item, field);
	}
	public static ModelListenerProperty create(Object node, SendableEntityCreator creator, Object item, String field){
		if(node == null) {
			return null;
		}
		if(field == null) {
			field = ""+ReflectionLoader.call(node, "getId");
		}
		Object property;
		// Check for Controls
		if(ReflectionLoader.PROPERTY.isAssignableFrom(node.getClass())) {
			if(ReflectionLoader.STRINGPROPERTY.isAssignableFrom(node.getClass())) {
				return createProperty(PROPERTYTYPE.STRING, node, creator, item, field);
			}
			if(ReflectionLoader.BOOLEANPROPERTY.isAssignableFrom(node.getClass())) {
				return createProperty(PROPERTYTYPE.BOOLEAN, node, creator, item, field);
			}
			if(ReflectionLoader.INTEGERPROPERTY.isAssignableFrom(node.getClass())) {
				return createProperty(PROPERTYTYPE.INTEGER, node, creator, item, field);
			}
			if(ReflectionLoader.DOUBLEPROPERTY.isAssignableFrom(node.getClass())) {
				return createProperty(PROPERTYTYPE.DOUBLE, node, creator, item, field);
			}
			return createProperty(PROPERTYTYPE.OBJECT, node, creator, item, field);
		}
		if(ReflectionLoader.COLORPICKER.isAssignableFrom(node.getClass())) {
			property = ReflectionLoader.call(node, "valueProperty");
			return createProperty(PROPERTYTYPE.COLOR, property, creator, item, field);
		}
		if(ReflectionLoader.TEXTFIELD.isAssignableFrom(node.getClass())) {
			property = ReflectionLoader.call(node, "textProperty");
			return createProperty(PROPERTYTYPE.STRING, property, creator, item, field);
		}
		if(ReflectionLoader.COMBOBOX.isAssignableFrom(node.getClass())) {
			property = ReflectionLoader.call(node, "valueProperty");
			return createProperty(PROPERTYTYPE.STRING, property, creator, item, field);
		}
		if(ReflectionLoader.LABEL.isAssignableFrom(node.getClass())) {
			property = ReflectionLoader.call(node, "textProperty");
			return createProperty(PROPERTYTYPE.STRING, property, creator, item, field);
		}
		if(ReflectionLoader.CHECKBOX.isAssignableFrom(node.getClass())) {
			property = ReflectionLoader.call(node, "selectedProperty");
			return createProperty(PROPERTYTYPE.BOOLEAN, property, creator, item, field);
		}
		if(ReflectionLoader.RADIOBUTTON.isAssignableFrom(node.getClass())) {
			property = ReflectionLoader.call(node, "selectedProperty");
			return createProperty(PROPERTYTYPE.BOOLEAN, property, creator, item, field);
		}
		return null;
	}

	private static ModelListenerProperty createProperty(PROPERTYTYPE typ,Object property, SendableEntityCreator creator, Object item, String field){
		ModelListenerProperty listener = new ModelListenerProperty(creator, item, field, PROPERTYTYPE.STRING);
		Object proxy = listener.getProxy();
		ReflectionLoader.call(property, "bindBidirectional", ReflectionLoader.PROPERTY, proxy);
		return listener;
	}
}
