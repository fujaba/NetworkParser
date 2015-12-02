package de.uniks.networkparser.list;

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
import java.util.Collection;
import java.util.Iterator;

import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.string.CharList;


public abstract class SDMSet<T> extends SimpleSet<T> {
	@SuppressWarnings("unchecked")
	public <ST extends SDMSet<T>> ST withReadOnly() {
		withFlag(SimpleSet.READONLY);
		return (ST) this;
	}

	@Override
	public String toString() {
		return toString(", ").withStart('(').with(")").toString();
	}

	public CharList toString(String separator) {
		CharList stringList = new CharList();
		int len = this.size();
		for (T elem : this) {
			stringList.with(elem.toString());
			if (len > 1) {
				stringList.with(separator);
			}
			len--;
		}
		return stringList;
	}

	public <ST extends SDMSet<T>> ST union(Collection<? extends T> other) {
		@SuppressWarnings("unchecked")
		ST result = (ST) this.getNewList(false);
		result.addAll(this);
		result.addAll(other);

		return result;
	}

	public <ST extends SDMSet<T>> ST intersection(Collection<? extends T> other) {
		@SuppressWarnings("unchecked")
		ST result = (ST) this.getNewList(false);
		result.addAll(this);
		result.retainAll(other);
		return result;
	}

	@SuppressWarnings("unchecked")
	public <ST extends SDMSet<T>> ST minus(Object other) {
		ST result = (ST) this.getNewList(false);
		result.addAll(this);

		if (other instanceof Collection) {
			result.removeAll((Collection<?>) other);
		} else {
			result.remove(other);
		}

		return result;
	}

	public <ST extends SDMSet<T>> ST has(Condition<T> condition) {
		@SuppressWarnings("unchecked")
		ST result = (ST) this.getNewList(false);
		result.addAll(this);

		for (T elem : this) {
			if (!condition.check(elem)) {
				result.remove(elem);
			}
		}
		;
		return result;
	}

	public Iterator<T> cloneIterator() {
		return super.clone().iterator();
	}

	// ReadOnly Add all
	@Override
	public T set(int index, T element) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException("set(" + index + ")");
		}
		return super.set(index, element);
	}

	@Override
	public void add(int index, T element) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException("add(" + index + ")");
		}
		super.add(index, element);
	}

	@Override
	public T remove(int index) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException("remove(" + index + ")");
		}
		return super.remove(index);
	}

	@Override
	public boolean add(T newValue) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException("add()");
		}
		return super.add(newValue);
	}
}
