package de.uniks.networkparser.gui;

import com.sun.javafx.collections.NonIterableChange.SimplePermutationChange;
import com.sun.javafx.collections.SortableList;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.util.Callback;

/**
 * A List wrapper class that implements observability.
 * 
 */
public class TableListFX extends ModifiableObservableListBase<Object> implements
		ObservableList<Object>, SortableList<Object>, RandomAccess {
	// public class ObservableListWrapper<Object>

	private final List<Object> backingList;

	private final ElementObserver elementObserver;

	public ObservableListWrapper(List<Object> list) {
		backingList = list;
		elementObserver = null;
	}

	public ObservableListWrapper(List<Object> list,
			Callback<Object, Observable[]> extractor) {
		backingList = list;
		this.elementObserver = new ElementObserver(extractor,
				new Callback<Object, InvalidationListener>() {

					@Override
					public InvalidationListener call(final E e) {
						return new InvalidationListener() {

							@Override
							public void invalidated(Observable observable) {
								beginChange();
								int i = 0;
								final int size = size();
								for (; i < size; ++i) {
									if (get(i) == e) {
										nextUpdate(i);
									}
								}
								endChange();
							}
						};
					}
				}, this);
		final int sz = backingList.size();
		for (int i = 0; i < sz; ++i) {
			elementObserver.attachListener(backingList.get(i));
		}
	}

	@Override
	public Object get(int index) {
		return backingList.get(index);
	}

	@Override
	public int size() {
		return backingList.size();
	}

	@Override
	protected void doAdd(int index, Object element) {
		if (elementObserver != null)
			elementObserver.attachListener(element);
		backingList.add(index, element);
	}

	@Override
	protected E doSet(int index, E element) {
		E removed = backingList.set(index, element);
		if (elementObserver != null) {
			elementObserver.detachListener(removed);
			elementObserver.attachListener(element);
		}
		return removed;
	}

	@Override
	protected E doRemove(int index) {
		E removed = backingList.remove(index);
		if (elementObserver != null)
			elementObserver.detachListener(removed);
		return removed;
	}

	@Override
	public int indexOf(Object o) {
		return backingList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return backingList.lastIndexOf(o);
	}

	@Override
	public boolean contains(Object o) {
		return backingList.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return backingList.containsAll(c);
	}

	@Override
	public void clear() {
		if (elementObserver != null) {
			final int sz = size();
			for (int i = 0; i < sz; ++i) {
				elementObserver.detachListener(get(i));
			}
		}
		if (hasListeners()) {
			beginChange();
			nextRemove(0, this);
		}
		backingList.clear();
		++modCount;
		if (hasListeners()) {
			endChange();
		}
	}

	@Override
	public void remove(int fromIndex, int toIndex) {
		beginChange();
		for (int i = fromIndex; i < toIndex; ++i) {
			remove(fromIndex);
		}
		endChange();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		beginChange();
		boolean modified = false;
		for (int i = 0; i < size(); ++i) {
			if (c.contains(get(i))) {
				remove(i);
				--i;
				modified = true;
			}
		}
		endChange();
		return modified;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		beginChange();
		boolean modified = false;
		for (int i = 0; i < size(); ++i) {
			if (!c.contains(get(i))) {
				remove(i);
				--i;
				modified = true;
			}
		}
		endChange();
		return modified;
	}

	private SortHelper helper;

	@Override
	@SuppressWarnings("unchecked")
	public void sort() {
		if (backingList.isEmpty()) {
			return;
		}
		int[] perm = getSortHelper().sort(
				(List<? extends Comparable>) backingList);
		fireChange(new SimplePermutationChange<Object>(0, size(), perm, this));
	}

	@Override
	public void sort(Comparator<? super E> comparator) {
		if (backingList.isEmpty()) {
			return;
		}
		int[] perm = getSortHelper().sort(backingList, comparator);
		fireChange(new SimplePermutationChange<Object>(0, size(), perm, this));
	}

	private SortHelper getSortHelper() {
		if (helper == null) {
			helper = new SortHelper();
		}
		return helper;
	}
}