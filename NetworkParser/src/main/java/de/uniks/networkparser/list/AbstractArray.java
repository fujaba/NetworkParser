package de.uniks.networkparser.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import de.uniks.networkparser.interfaces.BaseItem;

public class AbstractArray implements BaseItem  {
	/** Is Allow Duplicate Items in List	 */
	public static final byte ALLOWDUPLICATE = 0x01;
	/** Is Allow Empty Value in List (null)  */
	public static final byte ALLOWEMPTYVALUE = 0x02;
	/** Is The List is Visible for Tree Editors  */
    public static final byte VISIBLE = 0x04;
	/** Is Key is String and is Allow Casesensitive  */
	public static final byte CASESENSITIVE = 0x08;
	/** Is List is ReadOnly */
	public static final byte READONLY = 0x10;
	/** Is The List has Key,Value */
	public static final byte MAP = 0x20;
	/** Is List is Key,Value and Value, Key */
	public static final byte BIDI = 0x40;
		
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
	private byte flag=initFlag(); // Flag of
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
    
    public byte initFlag() {
    	return 0+VISIBLE+CASESENSITIVE;
    }
    
    /** Init-List with Collection */
    @SuppressWarnings("unchecked")
	public <ST extends AbstractArray> ST init(Collection<?> list){
    	if(list instanceof AbstractArray){
    		this.flag = ((AbstractArray)list).getSignalFlag();
    	}
    	withList(list);
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
	public <ST extends AbstractArray> ST init(Object[] items, int size){
    	elements = items;
    	this.size = size;
    	return (ST) this;
    }
    
    public AbstractArray withFlag(int value)  {
    	this.flag = (byte) (this.flag | value);
    	if(value == BIDI){
    		this.flag = (byte) (this.flag | MAP);
    	}
    	return this;
    }
    
    boolean isBig() {
    	return size>MINHASHINGSIZE && elements.length <= (BIG_VALUE+1);
    }

    boolean isComplex() {
    	return isComplex(size);
    }
    boolean isComplex(int size) {
    	return (flag & MAP) == MAP || size >= MINHASHINGSIZE || (size >= 6 && elements.length < 6);
    }
    
	int getArrayFlag(int size ) {
		if(size==0) {
			return 0;
		}
		if((flag & BIDI)>0){
			if(size>=MINHASHINGSIZE){
				return 5;
			}else {
				return 4;
			}
		}
		if((flag & MAP)>0){
			return 4;
		}
		if(size>=MINHASHINGSIZE) {
			return 3;
		}
		return 1;
	}
    
	public byte getSignalFlag(){
		return flag;
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
		if(value) {
			this.flag = (byte) (this.flag | CASESENSITIVE);
		} else {
			this.flag = (byte) (this.flag & (0xff - CASESENSITIVE));
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
			boolean value) {
		if(value) {
			this.flag = (byte) (this.flag | ALLOWDUPLICATE);
		} else {
			this.flag = (byte) (this.flag & (0xff - ALLOWDUPLICATE));
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
		if(value) {
			this.flag = (byte) (this.flag | READONLY);
		} else {
			this.flag = (byte) (this.flag & (0xff - READONLY));
		}
		return this;
	}
	
	public void clear() {
		int arrayFlag = getArrayFlag(size);
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
		int tmp = hashKey  % len;

		return (tmp < 0) ? -tmp : tmp;
	}
	
	public Comparator<Object> comparator(){
		return null;
	}
	public boolean isComparator() {
		return false;
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
			if (items[hashKey] == null || (int)items[hashKey] == -1) {
				items[hashKey] = pos;
				return hashKey;
			}
			hashKey = (hashKey + 1) % items.length;
		}
	}
	
	boolean shrink(int minCapacity) {
		//Shrink the Array
		if(minCapacity==0){
			elements = null;
			return true;
		}

		int newSize = minCapacity + minCapacity / 2 + 4;
		if(isComplex()) {
			if((flag & MAP)==MAP) {
				// MAP
				boolean change=false;
				if(minCapacity < ((Object[])elements[SMALL_KEY]).length * MINUSEDLIST) {
					resizeSmall(newSize, SMALL_KEY);
					resizeSmall(newSize, SMALL_VALUE);
					change = true;
				}
				if(minCapacity < ((Object[])elements[SMALL_VALUE]).length * MINUSEDLIST) {
					change = true;
					resizeBig(newSize, BIG_KEY);
					if(elements[BIG_VALUE]!= null) {
						resizeBig(newSize, BIG_VALUE);
					}
				}
				return change;
			}else if(minCapacity < ((Object[])elements[SMALL_KEY]).length * MINUSEDLIST) {
				// Change Simple Complexlist to SImpleList
				elements = (Object[]) elements[SMALL_KEY];
				return true;
			}
		}else if(minCapacity < elements.length * MINUSEDLIST) {
			resizeSmall(newSize);
			return true;
		}
		return false;
	}
	
	void grow(int minCapacity) {
		int newSize = minCapacity + minCapacity / 2 + 4;
		int arrayFlag = getArrayFlag( minCapacity );
		if(elements == null ){
			// Init List
			if(arrayFlag==1){
				elements = new Object[newSize];
				return;
			}
			elements = new Object[arrayFlag];
			elements[SMALL_KEY] = new Object[newSize];
			if(newSize>MINHASHINGSIZE) {
				resizeBig(minCapacity*2, BIG_KEY);
			}
			if((flag & MAP)==MAP){
				elements[SMALL_VALUE] = new Object[newSize];
			}
			return;
		}
		if( arrayFlag > 1 && arrayFlag != elements.length) {
			// Change Single to BigList
			Object[] old = elements;
			elements = new Object[arrayFlag];
			elements[SMALL_KEY] = old;
			if((flag & MAP)==MAP){
				elements[SMALL_VALUE] = new Object[newSize];
			}
		}

		// Array has wrong size
		if(isComplex(minCapacity)) {
			if(minCapacity >= ((Object[])elements[SMALL_KEY]).length * MAXUSEDLIST) {
				resizeSmall(newSize, SMALL_KEY);
				if((flag & MAP)==MAP) {
					resizeSmall(newSize, SMALL_VALUE);
				}
			}
			if(minCapacity>=MINHASHINGSIZE && (elements[BIG_KEY]==null || minCapacity >= ((Object[])elements[BIG_KEY]).length * MAXUSEDLIST)) {
				resizeBig(minCapacity*2, BIG_KEY);
				if((flag & MAP)==MAP) {
					resizeBig(minCapacity*2, BIG_VALUE);
				}
				elements[DELETED] = null;
			}
		} else if(size < MINHASHINGSIZE) {
			if(minCapacity >= elements.length) {
				resizeSmall(newSize);
			}
		}
	}
	
	void resizeBig(int minCapacity, int index) {
		Object[] items = (Object[]) elements[index - 1];
		Object[] newItems = new Object[minCapacity]; 
		elements[index] = newItems;
		for(int pos=0;pos<items.length;pos++) {
			if(items[pos]==null){
				break;
			}
			addHashItem(pos, items[pos], newItems);
		}
	}
	
	void resizeSmall(int newCapacity, int index) {
		Object[] dest = new Object[newCapacity];
		System.arraycopy(elements[index], 0, dest, 0, size);
		elements[index] = dest;
	}
	
	void resizeSmall(int newCapacity) {
		Object[] dest = new Object[newCapacity];
		System.arraycopy(elements, 0, dest, 0, size);
		elements = dest;
	}

	/**
	 * Add a Element to the List
	 * 
	 * @param element to add a Value
	 * @return int the Position of the insert
	 */
	protected int hasKey(Object element, int size){
		if (element == null || isReadOnly())
			return -1;
		if (isComparator()) {
			for (int i = 0; i < size(); i++) {
				if (comparator().compare(getKeyByIndex(i, size), element) >= 0) {
					if (!isAllowDuplicate() && getKeyByIndex(i, size) == element) {
						return -1;
					}
					return i;
				}
			}
			return this.size;
		}
		if (!isAllowDuplicate()) {
			int pos = indexOf(element, size);
			if(pos>=0) {
				return -1;
			}
		}
		return this.size;
	}
	
	/**
	 * Add a Element to the List
	 * 
	 * @param element to add a Value
	 * @return boolean if success add the Value
	 */
	protected int hasKeyAndPos(Object element){
		if (element == null || isReadOnly())
			return -1;
		if (isComparator()) {
			for (int i = 0; i < size(); i++) {
				if (comparator().compare(getKeyByIndex(i), element) >= 0) {
					return i;
				}
			}
			return size;
		}
		if (!isAllowDuplicate()) {
			int pos = indexOf(element, size);
			if(pos>=0) {
				return pos;
			}
		}
		return size;
	}

	public Object getKeyByIndex(int index) {
		return getKeyByIndex(index, size);
	}
	
	protected Object getKeyByIndex(int index, int size) {
		if(index<0) {
			index = size + 1 - index;
		}
		if(index>=0 && index<size){
			if(isComplex(size)) {
				return ((Object[])elements[SMALL_KEY])[index];
			}
			return elements[index];
		}
		return null;
	}
	
	public Object getValueByIndex(int index) {
		if(index<0) {
			index = size + 1 - index;
		}
		if(index>=0 && index<size){
			return ((Object[])elements[SMALL_VALUE])[index];
		}
		return null;
	}
	
	protected int addKeyValue(int pos, Object key, Object value) {
		int i = size();
		Object[] keys, values;
		keys = (Object[]) elements[SMALL_KEY];
		values = (Object[]) elements[SMALL_VALUE];
		if(isBig()) {
			addHashItem(pos, key, (Object[])elements[BIG_KEY]);
			addHashItem(pos, value, (Object[])elements[BIG_VALUE]);
		}
		while(i>pos) {
			keys[i] = keys[i-1];
			values[i] = values[--i];
		}
		keys[pos] = key;
		values[pos] = value;
        Object beforeKey = this.getKeyByIndex(size, size);
        size++;
        fireProperty(null, key, beforeKey, value);
		return pos;
	}
	
	
	/**
	 * Add a Key to internal List and Array if nesessary
	 * Method to manipulate Array
	 *
	 * @param element
	 *            the new Value
	 * @param pos
	 *            the new Position -1 = End
	 * @return if value is added
	 */
	protected int addKey(int pos, Object element, int size) {
		Object[] keys;
		
		if(isComplex(size + 1)) {
			keys = (Object[]) elements[SMALL_KEY];
			if(elements[BIG_KEY]!= null){
				int newPos = retransformIndex(pos, size);
				addHashItem(newPos, element, (Object[])elements[BIG_KEY]);
			}
		}else{
			keys = elements;
		}
		int i = this.size;
		while(i>pos) {
			keys[i] = keys[--i]; 	
		}
		keys[pos] = element;
        Object beforeElement = null;
        this.size++;
        if (pos > 0)
        {
        	beforeElement = this.getKeyByIndex(pos-1, size);
        }
        fireProperty(null, element, beforeElement, null);
		return pos;
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
		if(isVisible()) {
			sb.append("Visible ");
		}
		if(isReadOnly()) {
			sb.append("ReadOnly ");
		}
		if(isCaseSensitive()) {
			sb.append("CaseSensitive ");
		}
		return sb.toString();
	}

	
	public AbstractArray withAll(Object... values) {
		if(values==null){
			return this;
		}
		int newSize = size + values.length; 
		grow(newSize);
		for (Object value : values) {
			int pos = hasKey(value, newSize);
			if(pos>=0) {
				this.addKey(pos, value, newSize);
			}
		}
		return this;
	}
	
	public AbstractArray without(Object... values) {
		if(values==null){
			return this;
		}
		for (Object value : values) {
			this.removeByObject(value);
		}
		return this;
	}
	
	protected void setValue(int pos, Object value, int offset) {
		if(pos>=size){
			grow(pos + 1);
		}
		if(getArrayFlag(size)>1){
			Object[] items = (Object[]) elements[offset];
			Object oldValue = items[pos];
			items[pos] = value;
			if(elements.length > offset+1) {
				int position = getPositionKey(oldValue);
				if(position>=0) {
					items = ((Object[]) elements[offset+1]);
				}
			}
			return;
		}
		elements[pos] = value;
	}

	public AbstractArray withList(Collection<?> list) {
		int newSize = this.size + list.size();
		grow(newSize);
    	for(Iterator<?> i = list.iterator();i.hasNext();) {
    		Object item = i.next();
    		int pos = hasKey(item, newSize);
			if(pos>=0) {
				this.addKey(pos, item, newSize);
			}
    	}
    	return this;
	}
	
    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
	public int indexOf(Object o) {
		return indexOf(o, size);
	}
	
    public int indexOf(Object o, int size) {
        if (o == null || elements == null)
       		return -1;

    	if(size>=MINHASHINGSIZE ) {
   			return getPositionKey(o);
    	}
    	Object[] items;
    	if((flag & MAP)==MAP) {
    		items = (Object[]) elements[SMALL_KEY];
    	}else{
    		items = elements;
    	}
        for (int i = 0; i < this.size; i++) {
        	if(checkValue(o, items[i]))
                return i;
        }
        return -1;
    }
    
    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    public int lastindexOf(Object o) {
        if (o == null)
        	return -1;
    	if(size>MINHASHINGSIZE) {
    		return getLastPositionKey(o);
    	}
    	for (int i = size - 1; i >= 0; i--)
            if (o.equals(elements[i]))
                return i;
        return -1;
    }
    
	public int getPositionKey(Object o) {
		return getPosition(o, SMALL_KEY);
	}
	
	public int getPositionValue(Object o) {
		return getPosition(o, SMALL_VALUE);
	}
	
	private int transformIndex(int index, int size){
		if(elements[DELETED] != null) {
			Object[] items = (Object[]) elements[DELETED];
    		for(int i=0;i<items.length;i++){
    			if(((int)items[i])>index){
    				break;
    			}
				index --;
    		}
    	}
		if(index<0){
			index += size;
		}
		return index;
	}
	
	private int retransformIndex(int index, int size){
		if(elements[DELETED] != null) {
			Object[] items = (Object[]) elements[DELETED];
    		for(int i=0;i<items.length;i++){
    			if(((int)items[i])>index){
    				break;
    			}
				index++;
    		}
    	}
		return index;
	}
	
	private int getPosition(Object o, int offset) {
		if (o == null) {
			return -1;
		}
		Object[] hashCodes = (Object[])elements[offset + 1];
		Object[] items = (Object[])elements[offset];
		int index = hashKey(o.hashCode(), hashCodes.length);
		if(hashCodes[index]==null){
			return -1;
		}
		
		Object value = null;
		int indexItem=-1;
		if((int)hashCodes[index]>-1){
			indexItem = transformIndex((int)hashCodes[index], items.length);
			value = items[ indexItem ];
		}
		while(!checkValue(o, value)){
			index = (index + 1) % hashCodes.length;
			if(hashCodes[index]==null) {
				return -1;
			}
			if((int)hashCodes[index]==-1){
				continue;
			}
			indexItem = transformIndex((int)hashCodes[index], items.length);
			value = items[indexItem];
		}
		return indexItem;
	}

	public int getLastPositionKey(Object o) {
		//FIXME transformIndex
		if (o == null) {
			return -1;
		}
		Object[] items = (Object[])elements[SMALL_KEY];
		int index = hashKey(o.hashCode(), items.length);
		Object value = items[index];
		int found=-1;
		while (!checkValue(value, o)) {
			if (value == null)
				return found;
			index = (index + 1) % items.length;
			value = items[index];
//			value = items[transformIndex((int)items[index])];
		}
		if(elements[DELETED] != null) {
    		items = (Object[]) elements[DELETED];
    		for(int i=0;i<items.length;i++){
    			if(((int)items[i])>index){
    				break;
    			}
				index += (int)items[i];
    		}
    	}
		return index;
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
		return indexOf(o, size) >= 0;
	}
	
	public boolean containsAll(Collection<?> c) {
		for (Object e : c)
			if (!contains(e))
				return false;
		return true;
	}
	
	  /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over this collection, checking each
     * element returned by the iterator in turn to see if it's contained
     * in the specified collection.  If it's so contained, it's removed from
     * this collection with the iterator's <tt>remove</tt> method.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by the
     * <tt>iterator</tt> method does not implement the <tt>remove</tt> method
     * and this collection contains one or more elements in common with the
     * specified collection.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     *
     * @see #remove(Object)
     * @see #contains(Object)
     */
	public boolean removeAll(Collection<?> c) {
		if (c == null) {
			return false;
		}
		boolean modified = false;
		for (Object i : c) {
			if (contains(i)) {
				removeByObject(i);
				modified = true;
			}
		}
		return modified;
	}
	
	public int removeByObject(Object key) {
		int index = indexOf(key, size);
		if (index < 0) {
			return -1;
		}
		removeByIndex(index, SMALL_KEY);
		return index;
	}
	
	protected Object removeByIndex(int index, int offset) {
		Object item = removeItem(index, offset);
		if(item != null){

			size--;
			if(!shrink(size) && isComplex() ){
				if(elements[DELETED]==null) {
					elements[DELETED]=new Integer[]{index};
				}else{
					Integer[] oldPos = (Integer[]) elements[DELETED]; 
					int i=0;
					while(i<oldPos.length && oldPos[i]<=index){
						i++;
					}
					Integer[] positions=new Integer[((Object[])elements[DELETED]).length+1];
					System.arraycopy(oldPos, 0, positions, 0, i);
					positions[i]=index;
					System.arraycopy(oldPos, i, positions, i + 1, positions.length - i - 1);
					elements[DELETED] = positions;
				}
			}
		}
		return item;
	}
	
	Object removeItem(int index, int offset) {
		Object oldValue = null;
		if(!isComplex()){
			// One Dimension
			oldValue = elements[index];
			if(oldValue==null){
				return null;
			}
			System.arraycopy(elements, index + 1, elements, index, size - index);
			return oldValue;
		}
		
		Object[] items = ((Object[])elements[offset]);
		oldValue = items[index];
		if(oldValue == null ) {
			return null;
		}
		if(offset<elements.length){
			System.arraycopy(items, index + 1, elements[offset], index, size - index);
			return oldValue;
		}
		Object[] hashCodes = ((Object[])elements[offset + 1]);
		if(hashCodes == null) {
			System.arraycopy(items, index + 1, elements[offset], index, size - index);
			return oldValue;
		}
		
		int indexPos = hashKey(oldValue.hashCode(), items.length);
		Object value = null;
		int indexHash = (int)hashCodes[indexPos];
		if(indexHash>-1){
			value = items[transformIndex(indexHash, items.length)];
		}
		while(!checkValue(value, oldValue)){
			indexPos = (indexPos + 1) % items.length;
			indexHash = (int)hashCodes[indexPos];
			if(indexHash==-1){
				continue;
			}
			value = items[indexHash];
			if (value == null) {
				indexPos=-1;
				break;
			}
		}
		
	
		if(indexPos>=0) {
			hashCodes[indexPos] = -1;
		}
		System.arraycopy(items, index + 1, items, index, size - index);
 		

		return oldValue;
	}

	public static Object[] emptyArray = new Object[] {};
	
	public Object[] toArray() {
		if(isBig()) {
			return Arrays.copyOf((Object[])elements[SMALL_KEY], size);	
		}
		if (elements == null)
		{
		   return emptyArray;
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
