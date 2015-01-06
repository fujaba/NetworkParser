package de.uniks.networkparser.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

import de.uniks.networkparser.sort.EntityComparator;
import de.uniks.networkparser.sort.SortingDirection;

public class SimpleSmallList<V> extends ArrayList<V> {
	// FOR EXTENDS ARRAYLIST
	private static final long serialVersionUID = 1L;
	public static final float MAXUSEDLIST = 0.7f;
	public static final byte ALLOWDUPLICATE=0x04;
	public static final byte ALLOWEMPTYVALUE=0x08;
	public static final byte VISIBLE=0x16;
	public static final byte CASESENSITIVE =0x32;
	protected Comparator<V> cpr;
	protected byte flag=1; // SIZE
	
	/** Standard Constructor */
    public SimpleSmallList(){}

    /** Constructor with list*/
    public SimpleSmallList(Collection<V> list){
    	for(Iterator<V> i = list.iterator();i.hasNext();) {
    		this.add(i.next());
    	}
    }
	
    public int minSize(){
    	return 0;
    }

	public int entitySize() {
		return flag & 0x03;
	}
	
	public int calcNewSize(int listSize) {
		return listSize * entitySize() * 2;
	}
	
	
	public int usedSize(){
		return size() * (flag & 0x03);
	}
	
	/**
	 * @return the SIze of Array
	 */
	public int realSize() {
		return super.size();
	}
	/**
	 * Is Allow Duplicate Entity in the List
	 *
	 * @return boolean if the List allow duplicate Entities
	 */
	public boolean isAllowEmptyValue() {
		return (flag & ALLOWEMPTYVALUE)==ALLOWEMPTYVALUE;
	}

	public SimpleSmallList<V> withAllowEmptyValue(boolean value) {
		this.flag = (byte) (this.flag | ALLOWEMPTYVALUE);
		if(!value) {
			this.flag -= ALLOWEMPTYVALUE;
		}
		return this;
	}

	/**
	 * Is Visible Entity 
	 *
	 * @return boolean if the List is Visible
	 */
	public boolean isVisible() {
		return (flag & VISIBLE)==VISIBLE;
	}

	public SimpleSmallList<V> withVisible(boolean value) {
		this.flag = (byte) (this.flag | VISIBLE);
		if(!value) {
			this.flag -= VISIBLE;
		}
		return this;
	}

	/**
	 * Is Item is CaseSensitive 
	 *
	 * @return boolean if the List is CaseSentive
	 */
	public boolean isCaseSensitive() {
		return (flag & CASESENSITIVE)==CASESENSITIVE;
	}

	public SimpleSmallList<V> withCaseSensitive(boolean value) {
		this.flag = (byte) (this.flag | CASESENSITIVE);
		if(!value) {
			this.flag -= CASESENSITIVE;
		}
		return this;
	}
	
	/**
	 * Is Allow Duplicate Entity in the List
	 *
	 * @return boolean if the List allow duplicate Entities
	 */
	public boolean isAllowDuplicate() {
		return (flag & ALLOWDUPLICATE)==ALLOWDUPLICATE;
	}

	public SimpleSmallList<V> withAllowDuplicate(
			boolean allowDuplicate) {
		this.flag = (byte) (this.flag | ALLOWDUPLICATE);
		if(!allowDuplicate) {
			this.flag -= ALLOWDUPLICATE;
		}
		return this;
	}

	/**
	 * Get the HashKey from a Object with Max HashTableIndex and StepSize of
	 * EntitySize
	 *
	 * @param hashKey
	 *            the hashKey of a Object
	 * @param len
	 *            the max Length of all Hashvalues
	 * @return the hasKey
	 */
	protected int hashKey(int hashKey, int len) {
		int tmp = (hashKey + hashKey % entitySize()) % len;

		return (tmp < 0) ? -tmp : tmp;
	}
	
	/** @return the First Element of the List */
	public V first() {
		if (this.size() > 0) {
			return this.get(0);
		}
		return null;
	}

	/** @return the Last Element of the List */
	public V last() {
		if (this.size() > 0) {
			return this.get(this.size() - 1);
		}
		return null;
	}

	/**
	 * Add a Element to the List
	 * 
	 * @param newValue to add a Value
	 * @return boolean if success add the Value
	 */
	public boolean addEntity(V newValue) {
		if (newValue == null)
			return false;
		if (cpr != null) {
			for (int i = 0; i < size(); i++) {
				int result = comparator().compare(get(i), newValue);
				if (result >= 0) {
					if (!isAllowDuplicate() && get(i) == newValue) {
						return false;
					}
					addKey(i, newValue);
					V beforeElement = null;
					if (i > 0) {
						beforeElement = this.get(i - 1);
					}
					fireProperty(null, newValue, beforeElement, null);
					return true;
				}
			}
		}

		if (!isAllowDuplicate()) {
			if (this.contains(newValue)) {
				return false;
			}
		}

		if (addKey(-1, newValue) >= 0) {
			V beforeElement = null;
			if (size() > 1) {
				beforeElement = this.get(size() - 1);
			}
			fireProperty(null, newValue, beforeElement, null);
			return true;
		}
		return false;
	}

	private void fireProperty(Object object, V newValue, V beforeElement,
			Object object2) {
		// TODO Auto-generated method stub
	}

	/**
	 * Add a Key to internal List and Array if nesessary
	 *
	 * @param newValue
	 *            the new Value
	 * @param pos
	 *            the new Position -1 = End
	 */
	protected int addKey(int pos, V newValue) {
		if (pos == -1) {
			this.add(newValue);
			return size(); 
		}
		this.add(pos, newValue);
		return pos;
	}
	
	public Comparator<V> comparator() {
		if (this.cpr == null) {
			withComparator(new EntityComparator<V>().withColumn(
					EntityComparator.LIST).withDirection(SortingDirection.ASC));
		}
		return cpr;
	}
	
	public boolean isComparator() {
		return (this.cpr != null);
	}

	public SimpleSmallList<V> withComparator(Comparator<V> comparator) {
		this.cpr = comparator;
		return this;
	}

	public SimpleSmallList<V> withComparator(String column) {
		this.cpr = new EntityComparator<V>().withColumn(column).withDirection(
				SortingDirection.ASC);
		return this;
	}

	public int removeItemByObject(Object key) {
		int index = getPositionKey(key);
		if (index < 0) {
			return -1;
		}
		remove(index);
		return index;
	}
	
	public int getPositionKey(Object o) {
		if (o == null) {
			return -1;
		}
		// search from the end as in models we frequently ask for elements that
		// have just been added to the end
		int pos = this.size() - 1;
		for (ListIterator<V> i = listIteratorReverse(); i.hasPrevious();) {
			if (checkValue(i.previous(), o)) {
				return pos;
			}
			pos--;
		}
		return -1;
	}
	
	protected boolean checkValue(Object a, Object b) {
		if(!isCaseSensitive()) {
			if (a instanceof String && b instanceof String ) {
				return ((String)a).equalsIgnoreCase((String)b);
			}
		}
		return a.equals(b);
	}
	
	public ListIterator<V> listIteratorReverse() {
		return listIterator(size());
	}
	
	public int transformIndex(int index, Object value) {
		return index;
	}

//TODO	public void clone(){
//		
//	}
	
//	@Override
//	public int size() {
//		return size;
//	}
//
//	@Override
//	public boolean isEmpty() {
//        return size == 0;
//    }
//
//	@Override
//	public boolean contains(Object o) {
//        return indexOf(o) >= 0;
//	}
//
//	@Override
//	public Iterator<V> iterator() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Object[] toArray() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public <T> T[] toArray(T[] a) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean add(V e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean remove(Object o) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean containsAll(Collection<?> c) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean addAll(Collection<? extends V> c) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean addAll(int index, Collection<? extends V> c) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean removeAll(Collection<?> c) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean retainAll(Collection<?> c) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void clear() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public V get(int index) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public V set(int index, V element) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void add(int index, V element) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public V remove(int index) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public int indexOf(Object o) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public int lastIndexOf(Object o) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public ListIterator<V> listIterator() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ListIterator<V> listIterator(int index) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<V> subList(int fromIndex, int toIndex) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
