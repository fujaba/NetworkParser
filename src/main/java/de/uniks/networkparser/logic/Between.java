package de.uniks.networkparser.logic;

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

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class Between implements ObjectCondition, SendableEntityCreator {
	public static final String FROM = "from";
	public static final String TO = "to";

	private double fromValue;
	private double toValue;

	public Between withRange(double from, double to) {
		this.fromValue = from;
		this.toValue = to;
		return this;
	}

	public Between withFrom(double from) {
		this.fromValue = from;
		return this;
	}

	public Between withTo(double to) {
		this.toValue = to;
		return this;
	}

	public double getFrom() {
		return fromValue;
	}

	public double getTo() {
		return toValue;
	}

	@Override
	public boolean update(Object evt) {
		if(evt instanceof PropertyChangeEvent == false) {
			return false;
		}
		PropertyChangeEvent event = (PropertyChangeEvent) evt;
		Object newValue = event.getNewValue();
		
		if (newValue instanceof Double) {
			return (((Double) newValue) >= fromValue && ((Double) newValue) <= toValue);
		}else if (newValue instanceof Integer) {
			return (((Integer) newValue) >= fromValue && ((Integer) newValue) <= toValue);
		}
		return false;
	}

	@Override
	public String[] getProperties() {
		return new String[] {FROM, TO };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Between();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (FROM.equalsIgnoreCase(attribute)) {
			return ((Between) entity).getFrom();
		}
		if (TO.equalsIgnoreCase(attribute)) {
			return ((Between) entity).getTo();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (FROM.equalsIgnoreCase(attribute)) {
			if(value instanceof  Double) {
				((Between) entity).withFrom((Double) value);
			} else if(value instanceof Integer) {
				((Between) entity).withFrom((Integer) value);
			}
			return true;
		}
		if (TO.equalsIgnoreCase(attribute)) {
			if(value instanceof  Double) {
				((Between) entity).withTo((Double) value);
			} else if(value instanceof Integer) {
				((Between) entity).withTo((Integer) value);
			}
			return true;
		}
		return false;
	}
}
