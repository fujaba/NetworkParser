package de.uniks.networkparser;

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
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;

public class ChainListener implements ObjectCondition{
	private boolean chain=true;
	private SimpleList<Object> list = new SimpleList<Object>();

	public ChainListener enableHook() {
		this.chain = false;
		return this;
	}

	@Override
	public boolean update(Object evt) {
		if(evt instanceof PropertyChangeEvent) {
			return updatePCE((PropertyChangeEvent) evt);
		}
		return false;
	}

	public boolean updatePCE(PropertyChangeEvent evt) {
		for(Iterator<Object> i = list.iterator();i.hasNext();) {
			Object listener = i.next();
			if(listener instanceof ObjectCondition) {
				if(((ObjectCondition)listener).update(evt) == false) {
					if(chain) {
						return false;
					}
				}
			} else if(listener instanceof PropertyChangeListener) {
				((PropertyChangeListener)listener).propertyChange(evt);
			}
		}
		return true;
	}

	public ChainListener with(ObjectCondition... values) {
		if(values ==null) {
			return this;
		}
		for(ObjectCondition item : values) {
			list.with(item);
		}
		return this;
	}
	public ChainListener with(PropertyChangeListener... values) {
		if(values ==null) {
			return this;
		}
		for(PropertyChangeListener item : values) {
			list.with(item);
		}
		return this;
	}
}
