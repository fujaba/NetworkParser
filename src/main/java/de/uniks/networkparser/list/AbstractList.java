package de.uniks.networkparser.list;

/*
 * NetworkParser The MIT License Copyright (c) 2010-2016 Stefan Lindel
 * https://www.github.com/fujaba/NetworkParser/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Condition;

/**
 * The Class is for generic implementation of List and Sets.
 *
 * @author Stefan
 * @param <V> generic Parameter for Simple-Collection
 */
public abstract class AbstractList<V> extends AbstractArray<V> implements Iterable<V>, Cloneable {
  private Class<?> type;

  /**
   * With type.
   *
   * @param <ST> the generic type
   * @param type the type
   * @return the st
   */
  @SuppressWarnings("unchecked")
  public <ST extends AbstractList<V>> ST withType(Class<?> type) {
    this.type = type;
    return (ST) this;
  }

  /**
   * Gets the typ class.
   *
   * @return the typ class
   */
  public Class<?> getTypClass() {
    return type;
  }

  @Override
  protected int addKey(int pos, Object element, int size) {
    if (this.type != null && !this.type.isAssignableFrom(element.getClass())) {
      return -1;
    }
    return super.addKey(pos, element, size);
  }

  /**
   * Removes the.
   *
   * @param o the o
   * @return true, if successful
   */
  public boolean remove(Object o) {
    return this.removeByObject(o) >= 0;
  }

  /**
   * <p>
   * This implementation iterates over the specified collection, and adds each object returned by the
   * iterator to this collection, in turn.
   *
   * <p>
   * Note that this implementation will throw an <code>UnsupportedOperationException</code> unless
   * <code>add</code> is overridden (assuming the specified collection is non-empty).
   * 
   * @param c List of Elements for adding
   * @return success
   * @see #add(Object...)
   */
  public boolean addAll(Collection<? extends V> c) {
    if (c == null) {
      return false;
    }
    boolean modified = false;
    for (V e : c)
      if (add(e))
        modified = true;
    return modified;
  }

  /**
   * List iterator.
   *
   * @return the list iterator
   */
  public ListIterator<V> listIterator() {
    return new SimpleIterator<V>(this);
  }

  /**
   * List iterator.
   *
   * @param index the index
   * @return the list iterator
   */
  public ListIterator<V> listIterator(int index) {
    return new SimpleIterator<V>(this, index);
  }

  /**
   * Iterator reverse.
   *
   * @return the list iterator
   */
  public ListIterator<V> iteratorReverse() {
    return new SimpleIterator<V>(this, size());
  }

  /**
   * With list.
   *
   * @param values the values
   * @return the abstract list
   */
  @Override
  public AbstractList<V> withList(Collection<?> values) {
    super.withList(values);
    return this;
  }

  /**
   * Copy entity.
   *
   * @param target the target
   * @param pos the pos
   */
  public void copyEntity(BaseItem target, int pos) {
    if (target != null)
      target.add(get(pos));
  }

  /**
   * Sub set.
   *
   * @param fromElement the from element
   * @param toElement the to element
   * @return the base item
   */
  public BaseItem subSet(V fromElement, V toElement) {
    BaseItem newList = getNewList(false);
    int end = indexOf(toElement);
    /* MUST COPY */
    for (int pos = indexOf(fromElement); pos < end; pos++) {
      copyEntity(newList, pos);
    }
    return newList;
  }

  /**
   * Get the next bigger Value of a Set.
   *
   * @param element Element for check
   * @param sameElement boolean for switch return sameElement
   * @return the element or higher Element
   */
  public V ceiling(V element, boolean sameElement) {
    int pos = indexOf(element);
    if (pos < 0) {
      return null;
    }
    if (pos < size) {
      return get(pos + 1);
    }
    if (sameElement) {
      return element;
    }
    return null;
  }

  /**
   * Get the before lower Value of a Set.
   *
   * @param element Element for check
   * @param sameElement boolean for switch return sameElement
   * @return the element or higher Element
   */
  public V lower(V element, boolean sameElement) {
    int pos = indexOf(element);
    if (pos > 0) {
      return get(pos - 1);
    }
    if (sameElement) {
      return element;
    }
    return null;
  }

  /**
   * Adds the.
   *
   * @param index the index
   * @param element the element
   */
  public void add(int index, V element) {
    int pos = hasKey(element);
    if (pos >= 0) {
      grow(size + 1);
      addKey(index, element, size + 1);
    }
  }

  /**
   * Sets the.
   *
   * @param index the index
   * @param element the element
   * @return the v
   */
  public V set(int index, V element) {
    if (index < 0 || index > size) {
      return null;
    }
    setValue(index, element, SMALL_KEY);
    return element;
  }

  /**
   * Removes the.
   *
   * @param index the index
   * @return the v
   */
  @SuppressWarnings("unchecked")
  public V remove(int index) {
    if (index < 0 || index > size) {
      return null;
    }
    return (V) removeByIndex(index, SMALL_KEY, this.index);
  }

  /**
   * Adds the all.
   *
   * @param index the index
   * @param values the values
   * @return true, if successful
   */
  public boolean addAll(int index, Collection<? extends V> values) {
    if (values == null) {
      return false;
    }
    boolean allAdded = true;
    int newSize = size + values.size();
    grow(newSize);
    for (Iterator<? extends V> i = values.iterator(); i.hasNext();) {
      if (addKey(index++, i.next(), newSize) < 0) {
        allAdded = false;
      }
    }
    return allAdded;
  }

  @SuppressWarnings("unchecked")
  protected <ST extends AbstractList<V>> ST filterItems(ST filterCollection, Condition<?> newValue) {
    for (int i = 0; i < size(); i++) {
      V item = get(i);
      Condition<Object> filter = (Condition<Object>) newValue;
      if (filter.update(item)) {
        filterCollection.add(item);
      }
    }
    return (ST) filterCollection;
  }

  /**
   * Filter.
   *
   * @param <ST> the generic type
   * @param newValue the new value
   * @return the st
   */
  @SuppressWarnings("unchecked")
  public <ST extends AbstractList<V>> ST filter(Condition<?> newValue) {
    ST filterList = (ST) getNewList(false);
    filterItems(filterList, newValue);
    return filterList;
  }

  /**
   * Iterator.
   *
   * @return the iterator
   */
  public Iterator<V> iterator() {
    return new SimpleIterator<V>(this, true);
  }

  /**
   * Iterator.
   *
   * @param checkPointer the check pointer
   * @return the iterator
   */
  public Iterator<V> iterator(boolean checkPointer) {
    return new SimpleIterator<V>(this).withCheckPointer(checkPointer);
  }

  /**
   * Equals.
   *
   * @param obj the obj
   * @return true, if successful
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Collection<?>)) {
      return false;
    }
    Collection<?> collection = (Collection<?>) obj;
    if (collection.size() != this.size()) {
      return false;
    }
    for (Object item : this) {
      if (!collection.contains(item)) {
        return false;
      }
    }
    return true;
  }
}
