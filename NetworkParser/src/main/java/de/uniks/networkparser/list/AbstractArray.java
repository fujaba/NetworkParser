package de.uniks.networkparser.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class AbstractArray {
	/** Is Allow Duplicate Items in List	 */
	public static final byte ALLOWDUPLICATE = 0x04;
	/** Is Allow Empty Value in List (null)  */
	public static final byte ALLOWEMPTYVALUE = 0x08;
    /** Is The List is Visible for Tree Editors  */
    public static final byte VISIBLE = 0x10;
	/** Is Key is String and is Allow Casesensitive  */
	public static final byte CASESENSITIVE = 0x20;
	/** Is List is ReadOnly */
	public static final byte READONLY = 0x30;
	/** Is The List has Key,Value */
	public static final byte MAP = 0x40;
	/** Is List is Key,Value and Value, Key */
	public static final byte BIDI = 0x50;
	
	public static final byte MINSIZE = 4;
	public static final int MAXDELETED = 42;
	public static final int MINHASHINGSIZE = 420;
	public static final float MINUSEDLIST = 0.2f;
	public static final float MAXUSEDLIST = 0.7f;
	
	public static final int SMALL_KEY = 0;
	public static final int BIG_KEY = 1;
	public static final int DELETED = 2;
	public static final int SMALL_VALUE = 3;
	public static final int BIG_VALUE = 4;

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
    int size;
    
    /** Init-List with Collection */
    @SuppressWarnings("unchecked")
	public <ST extends AbstractArray> ST init(Collection<?> list){
    	withAll(list);
    	if(list instanceof AbstractArray){
    		this.flag = ((AbstractArray)list).getFlag();
    	}
    	return (ST)this;
    }
    
    /** Init-List with Size-Integer */
    @SuppressWarnings("unchecked")
	public <ST extends AbstractArray> ST init(int initSize){
    	grow(initSize);
    	return (ST)this;
    }

    /** Init-List with Size-Integer */
    @SuppressWarnings("unchecked")
	public <ST extends AbstractArray> ST init(Object[] items){
    	elements = items;
    	size = items.length;
    	return (ST) this;
    }
    
    boolean isBig() {
    	return size>MINHASHINGSIZE && elements.length<= (BIG_VALUE+1);
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
    
	public AbstractArray withEntitySize(int size) {
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
	
	/**
	 * If the List is Empty
	 *
	 * @return boolean of size
	 */
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

	public AbstractArray withAllowEmptyValue(boolean value) {
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

	public AbstractArray withVisible(boolean value) {
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

	public AbstractArray withCaseSensitive(boolean value) {
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

	public AbstractArray withAllowDuplicate(
			boolean allowDuplicate) {
		this.flag = (byte) (this.flag | ALLOWDUPLICATE);
		if(!allowDuplicate) {
			this.flag -= ALLOWDUPLICATE;
		}
		return this;
	}
	
	/**
	 * Is Allow Duplicate Entity in the List
	 *
	 * @return boolean if the List allow duplicate Entities
	 */
	public boolean isReadOnly() {
		return (flag & READONLY)==READONLY;
	}

	public AbstractArray withReadOnly(
			boolean value) {
		this.flag = (byte) (this.flag | READONLY);
		if(!value) {
			this.flag -= READONLY;
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
	
	void shrink(int minCapacity) {
		if(minCapacity >= elements.length * MINHASHINGSIZE) {
			return;
		}
//		resize(minCapacity);
	}
	
	void grow(int minCapacity) {
		if (elements == null){
			elements = new Object[minCapacity];
			return;
		}
		int arrayFlag = getArrayFlag();
		// elements wrong size
		if(arrayFlag== 1 && minCapacity<MINHASHINGSIZE) {
			if(minCapacity >= elements.length * MAXUSEDLIST) {
				// resize Array
				
			}
			return;
		}
		if(arrayFlag != elements.length) {
			
		}
		
		
		if(isBig()) {
			if (minCapacity >= ((Object[])elements[BIG_KEY]).length * MAXUSEDLIST){
				resizeBig(minCapacity, BIG_KEY);
				if(arrayFlag > 4){
					resizeBig(minCapacity, BIG_VALUE);
				}
			}
		} else if (minCapacity <= elements.length * MINHASHINGSIZE){
		}
//		resize(minCapacity);
	}
	
	void resizeBig(int minCapacity, int index) {
		Object[] items = (Object[]) elements[index - 1];
		Object[] newItems = new Object[minCapacity*entitySize()*2]; 
		elements[index] = newItems;
		for(int pos=0;pos<items.length;pos++) {
			addHashItem(pos, items[pos], newItems);
		}
	}
	void resizeSmall(int minCapacity, Object[] oldValue) {
		Object[] newItems = new Object[minCapacity*entitySize()*2]; 
		elements[index] = newItems;
		for(int pos=0;pos<items.length;pos++) {
			addHashItem(pos, items[pos], newItems);
		}
	}
	
	
	void resizeSmallOLD(int minCapacity) {
		int arrayFlag = getArrayFlag();
		Object[] keys = null;
		Object[] values = null;
		
		if(elements.length>5) {
			keys = elements;
		}else{
			keys = (Object[]) elements[SMALL_KEY];
			if(elements.length>3){
				values = (Object[]) elements[SMALL_VALUE];
			}
		}
		elements = new Object[arrayFlag];
		if(arrayFlag== 0){
			// Null-Value
			return;
		}
		if(arrayFlag>=3){
			// LIST
			elements[SMALL_KEY] = addSmallItem(minCapacity+minCapacity/2+4, keys);
			if(size>MINHASHINGSIZE) {
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
			elements[SMALL_VALUE] = addSmallItem(minCapacity+minCapacity/2+4, values);
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
    }
	
	public String flag() {
		StringBuilder sb = new StringBuilder();
		
		if((flag & BIDI)>0) {
			sb.append("BIDI-Map ");
		}else if((flag & MAP)>0) {
			sb.append("Map ");
		}else {
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
		if(isReadOnly()) {
			sb.append("ReadOnly ");
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

	public AbstractArray with(Object item) {
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
        	if(size>MINHASHINGSIZE && entitySize()==2) {
        		int index = getPositionKey(o);
        		return transformIndex(index);
        	}
            for (int i = 0; i < size; i++)
                if (elements[i]==null)
                    return i;
        } else {
        	if(size>MINHASHINGSIZE && entitySize()==2) {
        		int index = getPositionKey(o);
        		return transformIndex(index);
        	}
            for (int i = 0; i < size; i++)
                if (o.equals(elements[i]))
                    return i;
        }
        return -1;
    }
    
    private int transformIndex(int index) {
    	if(index<0){
    		return index;
    	}
    	if(elements[DELETED] != null) {
    		Object[] items = (Object[]) elements[DELETED];
    		for(int i=0;i<items.length;i++){
    			if(((int)items[i])>index){
    				break;
    			}
				index += (int)items[i];
    		}
    	}
    	return index;
    }

	public int getPositionKey(Object o) {
		if (o == null) {
			return -1;
		}
		Object[] items = (Object[])elements[SMALL_KEY];
		int hashKey = hashKey(o.hashCode(), items.length);
		while (true) {
			Object value = items[hashKey];
			if (value == null)
				return -1;
			if (checkValue(value, o))
				return hashKey;
			hashKey = (hashKey + entitySize()) % items.length;
		}
	}
	
	protected boolean checkValue(Object a, Object b) {
		if(!isCaseSensitive()) {
			if (a instanceof String && b instanceof String ) {
				return ((String)a).equalsIgnoreCase((String)b);
			}
		}
		return a.equals(b);
	}
    
	public boolean contains(Object o) {
		if(isBig()) {
    		return getPositionKey(o)>=0;
    	}
		return indexOf(o) >= 0;
	}
	
	public Object[] toArray() {
		if(isBig()) {
			return Arrays.copyOf((Object[])elements[SMALL_KEY], size);	
		}
		return Arrays.copyOf(elements, size);
        
	}
	
	protected void fireProperty(Object oldElement, Object newElement,
			Object beforeElement, Object value) {
		//FIXME
	}
	
	public void move(int from, int to) {
		//FIXME
	}
}
