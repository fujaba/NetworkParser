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
import de.uniks.networkparser.list.SimpleSet;

public class IdFilterElements extends SimpleSet<Object> implements ObjectCondition {
	private ObjectCondition condition;

	public IdFilterElements(ObjectCondition condition) {
		this.condition = condition;
	}
	public IdFilterElements(Class<?> clazzConditon) {
		this.condition = InstanceOf.create(clazzConditon);
	}

	@Override
	public boolean update(Object evt) {
		if(condition!=null) {
			if(condition.update(evt)) {
				return add(((PropertyChangeEvent)evt).getNewValue());
			}
		}
		return false;
	}
}
