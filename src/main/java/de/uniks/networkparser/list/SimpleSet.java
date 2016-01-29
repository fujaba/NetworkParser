package de.uniks.networkparser.list;

import java.util.Collection;
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
import java.util.Set;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.Condition;

public class SimpleSet<V> extends AbstractList<V> implements Set<V> {
	@Override
	public SimpleSet<V> getNewList(boolean keyValue) {
		return new SimpleSet<V>();
	}

	@Override
	public boolean remove(Object o) {
		return super.removeByObject(o)>=0;
	}
	
	public SimpleSet<V> clone() {
		return ((SimpleSet<V>)getNewList(false)).init(this);
	}
	
	@SuppressWarnings("unchecked")
	public SimpleSet<V> subList(int fromIndex, int toIndex) {
		return (SimpleSet<V>) super.subList(fromIndex, toIndex);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends V> values) {
		return super.addAll(index, values);
	}
	
	@Override
	public boolean addAll(Collection<? extends V> c) {
		return super.addAll(c);
	}
	
	public SimpleSet<V> withoutAll(Object... values) {
		super.withoutAll(values);
		return this;
	}
	
	public SimpleSet<V> filter(Condition<V> newValue) {
		SimpleSet<V> newList = getNewList(false);
		filterItems(newList, newValue);
		return this;
	}
	
	// Add Methods from SDMLib
	@Override
	public String toString() {
		return toBuffer(", ").withStart('(').with(")").toString();
	}
	
	public String toString(String separator) {
		return toBuffer(separator).toString();
	}
	CharacterBuffer toBuffer(String separator) {
		CharacterBuffer stringList = new CharacterBuffer();
		int len = this.size();
		for (V elem : this) {
			stringList.with(elem.toString());
			if (len > 1) {
				stringList.with(separator);
			}
			len--;
		}
		return stringList;
	}

	// ReadOnly Add all
	@Override
	public V set(int index, V element) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException("set(" + index + ")");
		}
		return super.set(index, element);
	}

	@Override
	public void add(int index, V element) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException("add(" + index + ")");
		}
		super.add(index, element);
	}

	@Override
	public V remove(int index) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException("remove(" + index + ")");
		}
		return super.remove(index);
	}

	@Override
	public boolean add(V newValue) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException("add()");
		}
		return super.add(newValue);
	}
	
	@SuppressWarnings("unchecked")
	public <ST extends SimpleSet<V>> ST union(Collection<? extends V> other) {
		ST result = (ST) this.getNewList(false);
		result.addAll(this);
		result.addAll(other);
		return result;
	}

	@SuppressWarnings("unchecked")
	public <ST extends SimpleSet<V>> ST intersection(Collection<? extends V> other) {
		ST result = (ST) this.getNewList(false);
		result.addAll(this);
		result.retainAll(other);
		return result;
	}

	@SuppressWarnings("unchecked")
	public <ST extends SimpleSet<V>> ST minus(Object other) {
		ST result = (ST) this.getNewList(false);
		result.addAll(this);
		if (other instanceof Collection) {
			result.removeAll((Collection<?>) other);
		} else {
			result.remove(other);
		}
		return result;
	}
}
