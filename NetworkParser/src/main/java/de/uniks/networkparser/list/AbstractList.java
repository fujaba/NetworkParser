package de.uniks.networkparser.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import de.uniks.networkparser.listold.SimpleSmallList;

public class AbstractList {
	/** Is Allow Duplicate Items in List	 */
	public static final byte ALLOWDUPLICATE = 0x04;
	/** Is Allow Empty Value in List (null)  */
	public static final byte ALLOWEMPTYVALUE = 0x08;
    /** Is The List is Visible for Tree Editors  */
    public static final byte VISIBLE = 0x10;
	/** Is Key is String and is Allow Casesensitive  */
	public static final byte CASESENSITIVE = 0x20;
	/** Is The List has Key,Value */
	public static final byte MAP = 0x40;
	/** Is List is Key,Value and Value, Key */
	public static final byte BIDI = 0x50;
	
	public static final byte MINSIZE = 4;
	public static final int MAXDELETED = 42;
	public static final int MINHASHINGSIZE = 420;
	public static final float MINUSEDLIST = 0.2f;
	public static final float MAXUSEDLIST = 0.7f;

	/**
	 * The Flag of List. It contains the options
	 * EntitySize 1,2,3
	 * @see ALLOWDUPLICATE
	 * @see ALLOWEMPTYVALUE
	 * @see VISIBLE
	 * @see CASESENSITIVE
	 * @see MAP
	 * @see BIDI
	 */
	protected byte flag=1; // Flag of
	/**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer. Any
     * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * will be expanded to DEFAULT_CAPACITY when the first element is added.
     */
	
	/** May be 
	 * [...] elements for simple List or 
	 * [
	 * 		SimpleList<K>, 
	 * 		BigList<K + Index>, 
	 * 		DeleteItem<Index-Sorted>, 
	 * 		SimpleValue<V>, 
	 * 		BigList<V + Index> for BIDIMAP 
	 * ]
	 */ 
	Object[] elements; // non-private to simplify nested class access

	/** The size of the ArrayList (the number of elements it contains).  */
    private int size;
    
    /** Init-List with Collection */
    public AbstractList init(Collection<?> list){
    	withAll(list);
    	if(list instanceof AbstractList){
    		this.flag = ((AbstractList)list).getFlag();
    	}
    	return this;
    }
    
    /** Init-List with Size-Integer */
    public AbstractList init(int initSize){
    	grow(initSize);
    	return this;
    }

    /** Init-List with Size-Integer */
    public AbstractList init(Object[] items){
    	elements = items;
    	size = items.length;
    	return this;
    }
    
    
	int getArrayFlag() {
		if(size==0) {
			return 0;
		}
		if((flag & BIDI)>0){
			if(size>MINHASHINGSIZE){
				return 5;
			}else {
				return 4;
			}
		}
		if((flag & MAP)>0){
			return 4;
		}
		if(size>MINHASHINGSIZE) {
			return 3;
		}
		return 1;
	}
    
	public int entitySize() {
		return flag & 0x03;
	}
    
	public AbstractList withEntitySize(int size) {
		flag = (byte) (flag - (flag & 0x03) + size);
		return this;
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

	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
        return size == 0;
    }

	
	/**
	 * @return the Size of Array
	 */
	public int realSize() {
		return elements.length;
	}
	/**
	 * Is Allow Duplicate Entity in the List
	 *
	 * @return boolean if the List allow duplicate Entities
	 */
	public boolean isAllowEmptyValue() {
		return (flag & ALLOWEMPTYVALUE)==ALLOWEMPTYVALUE;
	}

	public AbstractList withAllowEmptyValue(boolean value) {
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

	public AbstractList withVisible(boolean value) {
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

	public AbstractList withCaseSensitive(boolean value) {
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

	public AbstractList withAllowDuplicate(
			boolean allowDuplicate) {
		this.flag = (byte) (this.flag | ALLOWDUPLICATE);
		if(!allowDuplicate) {
			this.flag -= ALLOWDUPLICATE;
		}
		return this;
	}
	
	public void clear() {
		int arrayFlag = getArrayFlag();
		size = 0;
		if(arrayFlag<1) {
			this.elements = null;
			return;
		}
		Object beforeElement = null;
		if(arrayFlag==1) {
			for(Object item : elements) {
				fireProperty(item, null, beforeElement, null);
				beforeElement = item;
			}
			return;
		}
		Object[] items = (Object[]) elements[0];
		if(arrayFlag>3) {
			for(Object item : items) {
				fireProperty(item, null, beforeElement, elements[3]);
				beforeElement = item;
			}
			return;
		}
		for(Object item : items) {
			fireProperty(item, null, beforeElement, null);
			beforeElement = item;
		}
		this.elements = null;
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
	
	/**
	 * Add a Key to internal List and Array if nesessary
	 *
	 * @param newValue
	 *            the new Value
	 * @param pos
	 *            the new Position -1 = End
	 * @return  ths pos
	 */
	protected int addHashItem(int pos, Object newValue, Object[] items) {
		int hashKey = hashKey(newValue.hashCode(), items.length);
		while (true) {
			Object oldEntry = items[hashKey];
			if (oldEntry == null) {
				items[hashKey] = newValue;
				if (entitySize() == 2) {
					items[hashKey + 1] = pos;
				}
				return hashKey;
			}

			if (oldEntry.equals(newValue))
				return -1;

			hashKey = (hashKey + entitySize()) % items.length;
		}
	}
	
	protected Object[] addSmallItem(int size, Object[] items) {
		Object[] dest = new Object[size];
		System.arraycopy(items, 0, dest, 0, size);
		return dest;	
	}
	
	
	void shrink(int minCapacity) {
		if(minCapacity >= elements.length * SimpleSmallList.MINUSEDLIST) {
			return;
		}
	}
	
	void grow(int minCapacity) {
		if (minCapacity <= elements.length * SimpleSmallList.MAXUSEDLIST){
			return;
		}

		int arrayFlag = getArrayFlag();
		Object[] keys = null;
		Object[] values = null;
		
		if(elements.length>5) {
			keys = elements;
		}else{
			keys = (Object[]) elements[0];
			if(elements.length>3){
				values = (Object[]) elements[3];
			}
		}
		elements = new Object[arrayFlag];
		if(arrayFlag== 0){
			// Null-Value
			return;
		}
		if(arrayFlag>=3){
			// LIST
			elements[0] = addSmallItem(minCapacity+minCapacity/2+4, keys);
			if(size>MINHASHINGSIZE){
				if(keys != null) {
					Object[] newItems = new Object[minCapacity*entitySize()*2];
					elements[1] = newItems;
					for(int pos=0;pos<keys.length;pos++) {
						addHashItem(pos, keys[pos], newItems);
					}
				}
			}
//			elements[2] = new Object[MAXDELETED];
		}
		if(arrayFlag>=4){
			//MAP
			elements[3] = addSmallItem(minCapacity+minCapacity/2+4, values);
		}
		if(arrayFlag>4){
			// BIDI-MAP
			if(values != null) {
				Object[] newItems = new Object[minCapacity*entitySize()*2]; 
				elements[4] = newItems;
				for(int pos=0;pos<values.length;pos++) {
					addHashItem(pos, values[pos], newItems);
				}
			}
		}
		// create Old Values
		
		//TODO COPY SIMPLE LIST
		if(size>MINHASHINGSIZE){
			if(keys != null) {
				for(int pos=0;pos<keys.length;pos++) {
					addHashItem(pos, keys[pos], keys);
				}
			}
			if(values != null) {
				for(int pos=0;pos<values.length;pos++) {
					addHashItem(pos, values[pos], keys);
				}
			}
		}

		
		
		
//		Object[] items = elements;
//		int arrayFlag = getArrayFlag();
//		
//		if(minCapacity > elements.length * SimpleSmallList.MAXUSEDLIST) {
//			// bigger
//			
//			result = new Object[elements.length*2];
//		} else if (minCapacity < elements.length * SimpleSmallList.MINUSEDLIST) {
//			// smaller
//			result = new Object[minCapacity*2];
//		}else{
//			return elements;
//		}
//		
//	    if(minCapacity>MINHASHINGSIZE) {
//	    	if(elements.length>5) {
//	    		// change
//	    		createArray(flag);
//	    		elements[0] = items;
//	    	}else if(elements.length>0) {
//	    		
//	    		if(minCapacity > elements.length * SimpleSmallList.MAXUSEDLIST) {
//	    			// bigger
//	    			result = new Object[elements.length*2];
//	    		} else if (minCapacity < elements.length * SimpleSmallList.MINUSEDLIST) {
//	    			// smaller
//	    			result = new Object[minCapacity*2];
//	    		}else{
//	    			return elements;
//	    		}
//	    		
//	    		if(arrayFlag==5) {
//	    			elements[0]
//	    		}
//	    	}else if(minCapacity>0) {
//	    		elements = new Object[arrayFlag];
//	    		// Create all Items
//	    	}
//	    }
//		Object[] elements;
//
//		
//		if(id==0){
//			elements=this.elements;
//		}else {
//			elements = (Object[]) this.elements[id];
//		}
//		//FIXME
//		Object[] result;
//		if(minCapacity > elements.length * SimpleSmallList.MAXUSEDLIST) {
//			// bigger
//			result = new Object[elements.length*2];
//		} else if (minCapacity < elements.length * SimpleSmallList.MINUSEDLIST) {
//			// smaller
//			result = new Object[minCapacity*2];
//		}else{
//			return elements;
//		}
//		for(int i=0;i<minCapacity;i++) {
//			result[i] = elements[i];
//		}
//		return result;
    }
	
	public String flag() {
		StringBuilder sb = new StringBuilder();
		
		if((flag & BIDI)>0) {
			sb.append("BIDI-Map ");
		}else if((flag & MAP)>0) {
			sb.append("Map ");
		}else if((flag & MAP)>0) {
			sb.append("LIST ");
		}
		if(isAllowDuplicate()) {
			sb.append("AllowDuplicate ");
		}
		if(isAllowEmptyValue()) {
			sb.append("AllowEmptyValue ");
		}
		if(isVisible()) {
			sb.append("Visible ");
		}
		if(isCaseSensitive()) {
			sb.append("CaseSensitive ");
		}
		sb.append("E"+entitySize());
		return sb.toString();
	}

	protected void addItem(Object element){
//		this
		
	}

	public AbstractList with(Object item) {
		//FIXME
		return this;
	}
	
	public void withAll(Collection<?> list) {
    	grow(list.size());
    	for(Iterator<?> i = list.iterator();i.hasNext();) {
    		this.with(i.next());
    	}
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
        	if(!isAllowEmptyValue()) {
        		return -1;
        	}
            for (int i = 0; i < size; i++)
                if (elementKey[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementKey[i]))
                    return i;
        }
        return -1;
    }

	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}
	
	protected void fireProperty(Object oldElement, Object newElement,
			Object beforeElement, Object value) {
		//FIXME
	}
}
