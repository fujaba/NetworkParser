package de.uniks.networkparser.gui.controller;

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
import de.uniks.networkparser.gui.GenericCreator;
import de.uniks.networkparser.gui.controller.ModelListenerProperty.PROPERTYTYPE;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class ModelListenerFactory {
	public static ModelListenerProperty<?> create(Node node, Object item, String field){
		GenericCreator creator = new GenericCreator(item);
		if(field == null) {
			field = node.getId();
		}
		if(node instanceof ColorPicker) {
			return createProperty(PROPERTYTYPE.COLOR, ((ColorPicker)node).valueProperty(), creator, item, field);
		}
		if(node instanceof TextField) {
			return createProperty(PROPERTYTYPE.STRING, ((TextField)node).textProperty(), creator, item, field);
		}
		if(node instanceof ComboBox) {
			return createProperty(PROPERTYTYPE.STRING, ((ComboBox<?>)node).valueProperty(), creator, item, field);
		}
		if(node instanceof Label) {
			return createProperty(PROPERTYTYPE.STRING, ((Label)node).textProperty(), creator, item, field);
		}
		if(node instanceof CheckBox) {
			return createProperty(PROPERTYTYPE.BOOLEAN, ((CheckBox)node).selectedProperty(), creator, item, field);
		}
		if(node instanceof RadioButton) {
			return createProperty(PROPERTYTYPE.BOOLEAN, ((RadioButton)node).selectedProperty(), creator, item, field);
		}
		return null;
	}
	
	public static ModelListenerProperty<?> create(Label node, Object item, String field){
		return createProperty(PROPERTYTYPE.STRING, node.textProperty(), new GenericCreator(item), item, field);
	}
	
	public static ModelListenerProperty<?> create(Property<?> property, IdMap map, Object item, String field){
		return create(property, map.getCreatorClass(item), item, field);
	}
	public static ModelListenerProperty<?> create(Property<?> property, SendableEntityCreator creator, Object item, String field){
		if(property instanceof StringProperty){
			return createProperty(PROPERTYTYPE.STRING, property, creator, item, field);
		}
		if(property instanceof BooleanProperty){
			return createProperty(PROPERTYTYPE.BOOLEAN, property, creator, item, field);
		}
		if(property instanceof IntegerProperty){
			return createProperty(PROPERTYTYPE.INT, property, creator, item, field);
		}
		if(property instanceof LongProperty){
			return createProperty(PROPERTYTYPE.LONG, property, creator, item, field);
		}
		if(property instanceof FloatProperty){
			return createProperty(PROPERTYTYPE.FLOAT, property, creator, item, field);
		}
		if(property instanceof DoubleProperty){
			return createProperty(PROPERTYTYPE.DOUBLE, property, creator, item, field);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static ModelListenerProperty<?> createProperty(PROPERTYTYPE typ, Property<?> property, SendableEntityCreator creator, Object item, String field){
		if(PROPERTYTYPE.STRING==typ) {
			ModelListenerStringProperty listener = new ModelListenerStringProperty(creator, item, field);
			if(property instanceof StringProperty) {
				((StringProperty)property).bindBidirectional(listener);
			}else if(property instanceof ObjectProperty) {
				((ObjectProperty<String>)property).bindBidirectional(listener);
			}
			return listener;
		}
		if(PROPERTYTYPE.COLOR==typ) {
			ModelListenerColorProperty listener = new ModelListenerColorProperty(creator, item, field);
			((ObjectProperty<Color>)property).bindBidirectional(listener);
			return listener;
		}
		if(PROPERTYTYPE.BOOLEAN==typ) {
			ModelListenerBooleanProperty listener = new ModelListenerBooleanProperty(creator, item, field);
			if(property instanceof BooleanProperty) {
				((BooleanProperty)property).bindBidirectional(listener);
			}else if(property instanceof ObjectProperty) {
				((ObjectProperty<Boolean>)property).bindBidirectional(listener);
			}
			return listener;
		}
		if(PROPERTYTYPE.INT==typ) {
			ModelListenerNumberProperty listener = new ModelListenerNumberProperty(creator, item, field);
			if(property instanceof IntegerProperty) {
				((IntegerProperty)property).bindBidirectional(listener);
			}else if(property instanceof ObjectProperty) {
				((ObjectProperty<Number>)property).bindBidirectional(listener);
			}
			return listener;
		}
		if(PROPERTYTYPE.LONG==typ) {
			ModelListenerNumberProperty listener = new ModelListenerNumberProperty(creator, item, field);
			if(property instanceof LongProperty) {
				((LongProperty)property).bindBidirectional(listener);
			}else if(property instanceof ObjectProperty) {
				((ObjectProperty<Number>)property).bindBidirectional(listener);
			}
			return listener;
		}
		if(PROPERTYTYPE.FLOAT==typ) {
			ModelListenerNumberProperty listener = new ModelListenerNumberProperty(creator, item, field);
			if(property instanceof FloatProperty) {
				((FloatProperty)property).bindBidirectional(listener);
			}else if(property instanceof ObjectProperty) {
				((ObjectProperty<Number>)property).bindBidirectional(listener);
			}
			return listener;
		}
		if(PROPERTYTYPE.DOUBLE==typ) {
			ModelListenerNumberProperty listener = new ModelListenerNumberProperty(creator, item, field);
			if(property instanceof DoubleProperty) {
				((DoubleProperty)property).bindBidirectional(listener);
			}else if(property instanceof ObjectProperty) {
				((ObjectProperty<Number>)property).bindBidirectional(listener);
			}
			return listener;
		}
		return null;
	}
}
