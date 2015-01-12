package de.uniks.networkparser.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

import de.uniks.networkparser.sort.EntityComparator;
import de.uniks.networkparser.sort.SortingDirection;

public class SimpleSmallList<V> implements SimpleInterface<V>{
	// FOR EXTENDS ARRAYLIST
	public static final float MAXUSEDLIST = 0.7f;
	public static final byte ALLOWDUPLICATE=0x04;
	public static final byte ALLOWEMPTYVALUE=0x08;
	public static final byte VISIBLE=0x10;
	public static final byte CASESENSITIVE =0x20;
	public static final float MINUSEDLIST = 0.2f;
	protected Comparator<V> cpr;
	protected byte flag=1; // SIZE
	/**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer. Any
     * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * will be expanded to DEFAULT_CAPACITY when the first element is added.
     */
	Object[] elementData; // non-private to simplify nested class access
    /** The size of the ArrayList (the number of elements it contains).  */
    private int size;
	
	/** Standard Constructor */
    public SimpleSmallList(){}

    /** Constructor with list*/
    public SimpleSmallList(Collection<V> list){
    	for(Iterator<V> i = list.iterator();i.hasNext();) {
    		this.add(i.next());
    	}
    	if(list instanceof SimpleSmallList<?>){
    		this.flag = ((SimpleSmallList<?>)list).getFlag();
    	}
    }
	
    public int minSize(){
    	return 0;
    }

	public int entitySize() {
		return flag & 0x03;
	}
	
	public byte getFlag(){
		return flag;
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
		return elementData.length;
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
		removeByIndex(index);
		return index;
	}
	
	public int getPositionKey(Object o) {
		if (o == null) {
			return -1;
		}
		// search from the end as in models we frequently ask for elements that
		// have just been added to the end
		int pos = this.size() - 1;
		for (ListIterator<V> i = iteratorReverse(); i.hasPrevious();) {
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
	
	public int transformIndex(int index, Object value) {
		return index;
	}

	@Override
	public int size() {
		return size;
	}
	
	
	@Override
	public boolean isEmpty() {
        return size == 0;
    }

	@Override
	public boolean contains(Object o) {
        return indexOf(o) >= 0;
	}
	
    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }
    
	@Override
	public Object set(int index, V element) {
		Object oldValue = elementData[index];
        elementData[index] = element;
        return oldValue;
	}

	public void add(int index, V element) {
		int i = size()+1;
		while(i>index) {
			elementData[i] = elementData[i - 1]; 	
		}
        elementData[index] = element;
        size++;
    }
    
	@Override
	public Iterator<V> iterator() {
		return new SimpleIterator<V>(this);
	}

	public ListIterator<V> iteratorReverse() {
		return new SimpleIterator<V>(this, size());
	}
	
	@Override
	public Object[] toArray() {
        return Arrays.copyOf(elementData, elementData.length);
	}
	
	@Override
	public boolean add(V e) {
		grow(size, elementData);
		elementData[size++] = e;
		return true;
	}
	
	private Object grow(int minCapacity, Object[] elements) {
		Object[] result;
		if(minCapacity > elements.length * SimpleSmallList.MAXUSEDLIST) {
			// bigger
			result = new Object[elements.length*2];
		} else if (minCapacity < elements.length * SimpleSmallList.MINUSEDLIST) {
			// smaller
			result = new Object[minCapacity*2];
		}else{
			return elements;
		}
		for(int i=0;i<minCapacity;i++) {
			result[i] = elements[i];
		}
		return result;
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(int index) {
		if(index<0) {
			index = size + 1 - index;
		}
		if(index>=0 && index<size){
			return (V) elementData[index];
		}
		return null;
	}

	@Override
	public void clear() {
		grow(0, new Object[10]);
		size = 0;
	}
	
	@Override
	public boolean remove(Object o) {
		return removeItemByObject(o)>=0;
	}

	private void removeByIndex(int index) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
	
	
    
    
    
    
    //FIXME



	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}




	

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	
//TODO	public void clone(){
//	}
}
