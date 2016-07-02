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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.UpdateListener;
import javafx.beans.Observable;

public class ModelListenerBooleanProperty extends ModelListenerProperty<Boolean> {
	private UpdateListener condition;

	public ModelListenerBooleanProperty(SendableEntityCreator creator, Object item, String property) {
		super(creator, item, property);
	}

	@Override
	public void invalidated(Observable observable) {
	}

	@Override
	public Boolean getValue() {
		Object value = creator.getValue(item, property);
		if(condition!=null){
			return condition.update(new PropertyChangeEvent(value, null, null, null));
		}
		return false;
	}

	@Override
	public void setValue(Boolean value) {
		Object oldValue = creator.getValue(item, property);
		if(oldValue instanceof Boolean){
			super.setValue(value);
		}
	}

	public ModelListenerBooleanProperty withCondition(UpdateListener condition){
		this.condition = condition;
		return this;
	}
}
