package de.uniks.networkparser.list;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.util.List;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;

/** Simple one Dimension List 
 * @author Stefan Lindel
 * @param <V> Type of Elements
 */
public class SimpleList<V> extends AbstractList<V> implements List<V> {
	public static final String PROPERTY = "items";
	private ObjectCondition listener;

	public SimpleList() {
		withFlag(SimpleList.ALLOWDUPLICATE);
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SimpleList<V>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SimpleList<V> subList(int fromIndex, int toIndex) {
		return (SimpleList<V>) super.subList(fromIndex, toIndex);
	}

	@Override
	public boolean add(V e) {
		return super.add(e);
	}

	public SimpleList<V> withListener(ObjectCondition listener) {
		this.listener = listener;
		return this;
	}

	@SuppressWarnings("unchecked")
	public AbstractList<V> clone() {
		return ((AbstractList<V>) getNewList(false)).init(this);
	}

	@Override
	protected boolean fireProperty(String type, Object oldElement, Object newElement, Object beforeElement, int index,
			Object value) {
		if (this.listener != null) {
			this.listener
					.update(new SimpleEvent(type, this, PROPERTY, index, newElement, oldElement, value, beforeElement));
		}
		return super.fireProperty(type, oldElement, newElement, beforeElement, index, value);
	}
	
	public boolean containsKey(String key) {
	    if(key == null) {
	        return false;
	    }
	    for(int i=0;i<this.size();i++) {
	        V keyObj = this.get(i);
	        if(keyObj instanceof String) {
	            String search = (String) keyObj;
	            if(search.startsWith("*")) {
	                if (key.endsWith(search.substring(1))) {
	                    return true;
	                }
	            }else if(search.endsWith("*")) {
	                if (key.startsWith(search.substring(0, search.length() - 1))) {
                        return true;
                    }
	            } else {
	                if (key.equals(search)) {
                        return true;
                    }
	            }
	        }
	    }
	    return false;
	}
}
