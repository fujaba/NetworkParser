package de.uniks.networkparser.ext.javafx.controller;

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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

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
