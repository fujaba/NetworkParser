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
import java.beans.PropertyChangeEvent;
import javafx.beans.Observable;
import javafx.scene.paint.Color;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class ModelListenerColorProperty extends ModelListenerProperty<Color> {
	public ModelListenerColorProperty(SendableEntityCreator creator, Object item, String property) {
		super(creator, item, property);
	}

	@Override
	public void invalidated(Observable observable) {
		System.out.println(observable);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getNewValue() instanceof String) {
			Color color = Color.web((String) evt.getNewValue());
			super.propertyChange(new PropertyChangeEvent(evt.getSource(), evt.getPropertyName(), evt.getOldValue(), color));
			return;
		}
		super.propertyChange(evt);
	}
	
	@Override
	public Color getValue() {
		Object value = creator.getValue(item, property);
		if(value instanceof String){
			return Color.web((String) value);
		}
		if(value==null){
			return Color.WHITE;
		}
		return (Color) value;
	}
	
	@Override
	public void setValue(Color value) {
		 int green = (int) (value.getGreen()*255);
		 String greenString = (green<16 ? "0" : "") + Integer.toHexString(green);

		 int red = (int) (value.getRed()*255);
		 String redString = (red<16 ? "0" : "") + Integer.toHexString(red);

		 int blue = (int) (value.getBlue()*255);
		 String blueString = (blue<16 ? "0" : "") + Integer.toHexString(blue);

		 String hexColor = "#"+redString+greenString+blueString;
		
		creator.setValue(item, property, hexColor, IdMap.NEW);
//		super.setValue(value);
	}
}
