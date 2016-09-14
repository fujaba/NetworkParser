package de.uniks.networkparser.list;

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
import java.util.List;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Condition;

public class SimpleList<V> extends AbstractList<V> implements List<V>, Cloneable {
	public SimpleList() {
		withFlag(SimpleList.ALLOWDUPLICATE);
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SimpleList<V>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SimpleList<V> clone() {
		return ((SimpleList<V>)getNewList(false)).init(this);
	}
	
	@SuppressWarnings("unchecked")
	public SimpleList<V> subList(int fromIndex, int toIndex) {
		return (SimpleList<V>) super.subList(fromIndex, toIndex);
	}

	@Override
	public boolean remove(Object o) {
		return super.removeByObject(o)>=0;
	}

	public SimpleList<V> filter(Condition<V> newValue) {
		SimpleList<V> filterList = new SimpleList<V>();
		filterItems(filterList, newValue);
		return filterList;
	}

	@Override
	public SimpleList<V> with(Object... values) {
		super.with(values);
		return this;
	}
}
