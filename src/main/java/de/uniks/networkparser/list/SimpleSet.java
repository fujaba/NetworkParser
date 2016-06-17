package de.uniks.networkparser.list;

import java.util.Collection;
import java.util.Set;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.logic.SimpleEvent;

public class SimpleSet<V> extends AbstractList<V> implements Set<V>, Cloneable {
	public static final String PROPERTY="items";
	private UpdateListener listener;

	@Override
	public SimpleSet<V> getNewList(boolean keyValue) {
		return new SimpleSet<V>();
	}

	@Override
	public boolean remove(Object o) {
		return super.removeByObject(o)>=0;
	}

	@Override
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

	public SimpleSet<V> filter(Condition<V> newValue) {
		SimpleSet<V> newList = getNewList(false);
		filterItems(newList, newValue);
		return newList;
	}

	// Add Methods from SDMLib
	@Override
	public String toString() {
		CharacterBuffer buffer = new CharacterBuffer();
		buffer.with('(');
		return toBuffer(buffer, ", ").with(')').toString();
	}

	public String toString(String separator) {
		CharacterBuffer buffer = new CharacterBuffer();
		return toBuffer(buffer, separator).toString();
	}
	CharacterBuffer toBuffer(CharacterBuffer buffer, String separator) {
		int len = this.size();
		for (V elem : this) {
			buffer.with(elem.toString());
			if (len > 1) {
				buffer.with(separator);
			}
			len--;
		}
		return buffer;
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

	public SimpleSet<V> withListener(UpdateListener listener) {
		this.listener = listener;
		return this;
	}

	@Override
	protected boolean fireProperty(String type, Object oldElement, Object newElement, Object beforeElement, Object value) {
		if(this.listener != null) {
			this.listener.update(new SimpleEvent(type, this, PROPERTY, oldElement, newElement, beforeElement, value));
		}
		return super.fireProperty(type, oldElement, newElement, beforeElement, value);
	}
}
