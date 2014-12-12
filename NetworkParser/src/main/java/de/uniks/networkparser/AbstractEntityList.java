package de.uniks.networkparser;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
import java.util.List;

public abstract class AbstractEntityList<V> extends AbstractList<V> implements
		List<V> {
	public boolean addAll(int index, Collection<? extends V> c) {
		for (Iterator<? extends V> i = c.iterator(); i.hasNext();) {
			V item = i.next();
			add(index++, item);
		}
		return true;
	}

	public boolean add(Iterator<? extends V> list) {
		while (list.hasNext()) {
			V item = list.next();
			if (item != null) {
				if (!addEntity(item)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean addAll(Collection<? extends V> list) {
		return add(list.iterator());
	}

	public void add(int index, V element) {
		if (!contains(element)) {
			keys.add(index, element);
			hashTableAddKey(element, index);
			V beforeValue = null;
			if (index > 0) {
				beforeValue = get(index - 1);
				fireProperty(null, element, beforeValue, null);
			}
		}
	}

	/**
	 * Add a Element after the Element from the second Parameter
	 *
	 * @param element
	 *            element to add
	 * @param beforeElement
	 *            element before the element
	 * @return the List
	 */
	public AbstractEntityList<V> withBefore(V element, V beforeElement) {
		int index = getIndex(beforeElement);
		if (index >= 0) {
			add(index, element);
		}
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public AbstractList<V> with(Object... values) {
		if (values == null) {
			return this;
		}
		for (Object item : values) {
			this.addEntity((V) item);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public AbstractList<V> withAll(V... values) {
		if (values == null) {
			return this;
		}
		for (V item : values) {
			this.addEntity(item);
		}
		return this;
	}

	
	
	public Collection<V> values() {
		return keys;
	}
}
