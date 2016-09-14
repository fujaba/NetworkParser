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
