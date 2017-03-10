package de.uniks.networkparser.ext.javafx.controller;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.generic.GenericCreator;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class ModelListenerPropertyFactory {
	public static ModelListenerProperty<?> create(Node node, Object item, String field){
		GenericCreator creator = new GenericCreator(item);
		if(field == null) {
			field = node.getId();
		}
		if(node instanceof ColorPicker) {
			return createProperty(ModelListenerProperty.PROPERTYTYPE.COLOR, ((ColorPicker)node).valueProperty(), creator, item, field);
		}
		if(node instanceof TextField) {
			return createProperty(ModelListenerProperty.PROPERTYTYPE.STRING, ((TextField)node).textProperty(), creator, item, field);
		}
		if(node instanceof ComboBox) {
			return createProperty(ModelListenerProperty.PROPERTYTYPE.STRING, ((ComboBox<?>)node).valueProperty(), creator, item, field);
		}
		if(node instanceof CheckBox) {
			return createProperty(ModelListenerProperty.PROPERTYTYPE.BOOLEAN, ((CheckBox)node).selectedProperty(), creator, item, field);
		}
		if(node instanceof RadioButton) {
			return createProperty(ModelListenerProperty.PROPERTYTYPE.BOOLEAN, ((RadioButton)node).selectedProperty(), creator, item, field);
		}
		return null;
	}

	public static ModelListenerProperty<?> create(Label node, Object item, String field){
		return createProperty(ModelListenerProperty.PROPERTYTYPE.STRING, node.textProperty(), new GenericCreator(item), item, field);
	}

	public static ModelListenerProperty<?> create(Property<?> property, IdMap map, Object item, String field){
		return create(property, map.getCreatorClass(item), item, field);
	}
	public static ModelListenerProperty<?> create(Property<?> property, SendableEntityCreator creator, Object item, String field){
		if(property instanceof StringProperty){
			return createProperty(ModelListenerProperty.PROPERTYTYPE.STRING, property, creator, item, field);
		}
		if(property instanceof BooleanProperty){
			return createProperty(ModelListenerProperty.PROPERTYTYPE.BOOLEAN, property, creator, item, field);
		}
		if(property instanceof IntegerProperty){
			return createProperty(ModelListenerProperty.PROPERTYTYPE.INT, property, creator, item, field);
		}
		if(property instanceof LongProperty){
			return createProperty(ModelListenerProperty.PROPERTYTYPE.LONG, property, creator, item, field);
		}
		if(property instanceof FloatProperty){
			return createProperty(ModelListenerProperty.PROPERTYTYPE.FLOAT, property, creator, item, field);
		}
		if(property instanceof DoubleProperty){
			return createProperty(ModelListenerProperty.PROPERTYTYPE.DOUBLE, property, creator, item, field);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static ModelListenerProperty<?> createProperty(ModelListenerProperty.PROPERTYTYPE typ, Property<?> property, SendableEntityCreator creator, Object item, String field){
		if(ModelListenerProperty.PROPERTYTYPE.STRING==typ) {
			ModelListenerStringProperty listener = new ModelListenerStringProperty(creator, item, field);
			((StringProperty)property).bindBidirectional(listener);
			return listener;
		}
		if(ModelListenerProperty.PROPERTYTYPE.COLOR==typ) {
			ModelListenerColorProperty listener = new ModelListenerColorProperty(creator, item, field);
			((ObjectProperty<Color>)property).bindBidirectional(listener);
			return listener;
		}
		if(ModelListenerProperty.PROPERTYTYPE.BOOLEAN==typ) {
			ModelListenerBooleanProperty listener = new ModelListenerBooleanProperty(creator, item, field);
			((BooleanProperty)property).bindBidirectional(listener);
			return listener;
		}
		if(ModelListenerProperty.PROPERTYTYPE.INT==typ) {
			ModelListenerNumberProperty listener = new ModelListenerNumberProperty(creator, item, field);
			((IntegerProperty)property).bindBidirectional(listener);
			return listener;
		}
		if(ModelListenerProperty.PROPERTYTYPE.LONG==typ) {
			ModelListenerNumberProperty listener = new ModelListenerNumberProperty(creator, item, field);
			((LongProperty)property).bindBidirectional(listener);
			return listener;
		}
		if(ModelListenerProperty.PROPERTYTYPE.FLOAT==typ) {
			ModelListenerNumberProperty listener = new ModelListenerNumberProperty(creator, item, field);
			((FloatProperty)property).bindBidirectional(listener);
			return listener;
		}
		if(ModelListenerProperty.PROPERTYTYPE.DOUBLE==typ) {
			ModelListenerNumberProperty listener = new ModelListenerNumberProperty(creator, item, field);
			((DoubleProperty)property).bindBidirectional(listener);
			return listener;
		}
		return null;
	}
}
