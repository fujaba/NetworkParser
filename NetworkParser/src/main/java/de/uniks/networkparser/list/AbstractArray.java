package de.uniks.networkparser.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import de.uniks.networkparser.interfaces.BaseItem;

public class AbstractArray<V> implements BaseItem, Iterable<V>  {
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
	 * 		SimpleList&lt;K&gt;, 
	 * 		BigList&lt;K + Index&gt;, 
	 * 		DeleteItem&lt;Index-Sorted&gt;, 
	 * 		SimpleValue&lt;V&gt;, 
	 * 		BigList&lt;V + Index&gt; for BIDIMAP 
	 * ]
	 */ 
	Object[] elements; // non-private to simplify nested class access

	/** The size of the ArrayList (the number of elements it contains).  */
    int size;
    
    public byte initFlag() {
    	return 0+VISIBLE+CASESENSITIVE;
    }
    
    /** Init-List with Collection
     * 
	 * @param list add all new Items
	 * @param <ST> Container Class
	 * @return return self 
     */
    @SuppressWarnings("unchecked")
	public <ST extends AbstractArray<V>> ST init(Collection<?> list){
    	if(list instanceof AbstractArray){
    		this.flag = ((AbstractArray<V>)list).getSignalFlag();
    	}
    	withList(list);
    	return (ST)this;
    }
    
    /** Init-List with Size-Integer
     * 
     * 
	 * @param initSize the new Size of the List
	 * @param <ST> Container Class
	 * @return return self
     */
    @SuppressWarnings("unchecked")
	public <ST extends AbstractArray<V>> ST init(int initSize){
    	if(initSize<1){
    		return (ST)this;
    	}
    	grow(initSize);
    	return (ST)this;
    }

    /** Init-List with Size-Integer 
     * 
     * @param items Array of the new List
	 * @param size the new Size of the List
	 * @param <ST> Container Class
	 * @return return self
     * */
    @SuppressWarnings("unchecked")
	public <ST extends AbstractArray<V>> ST init(Object[] items, int size){
    	elements = items;
    	this.size = size;
    	return (ST) this;
    }
    
    public AbstractArray<V> withFlag(int value)  {
    	this.flag = (byte) (this.flag | value);
    	if(value == BIDI){
    		this.flag = (byte) (this.flag | MAP);
    	}
    	return this;
    }
    
    final boolean isBig() {
    	return size>MINHASHINGSIZE && elements.length <= (BIG_VALUE+1);
    }

    final boolean isComplex(int size) {
    	return (flag & MAP) == MAP || size >= MINHASHINGSIZE || (size >= 6 && elements.length < 6);
    }
    
    final int getArrayFlag(int size ) {
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

	@SuppressWarnings("unchecked")
	public <ST extends AbstractArray<V>> ST  withAllowEmptyValue(boolean value) {
		this.flag = (byte) (this.flag | ALLOWEMPTYVALUE);
		if(!value) {
			this.flag -= ALLOWEMPTYVALUE;
		}
		return (ST) this;
	}

	/**
	 * Is Visible Entity 
	 *
	 * @return boolean if the List is Visible
	 */
	public boolean isVisible() {
		return (flag & VISIBLE)==VISIBLE;
	}

	public AbstractArray<V> withVisible(boolean value) {
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

	@SuppressWarnings("unchecked")
	public <ST extends AbstractArray<V>> ST  withCaseSensitive(boolean value) {
		if(value) {
			this.flag = (byte) (this.flag | CASESENSITIVE);
		} else {
			this.flag = (byte) (this.flag & (0xff - CASESENSITIVE));
		}
		return (ST) this;
	}
	
	/**
	 * Is Allow Duplicate Entity in the List
	 *
	 * @return boolean if the List allow duplicate Entities
	 */
	public boolean isAllowDuplicate() {
		return (flag & ALLOWDUPLICATE)==ALLOWDUPLICATE;
	}

	@SuppressWarnings("unchecked")
	public <ST extends AbstractArray<V>> ST withAllowDuplicate(
			boolean value) {
		if(value) {
			this.flag = (byte) (this.flag | ALLOWDUPLICATE);
		} else {
			this.flag = (byte) (this.flag & (0xff - ALLOWDUPLICATE));
		}
		return (ST) this;
	}
	
	/**
	 * Is Allow Duplicate Entity in the List
	 *
	 * @return boolean if the List allow duplicate Entities
	 */
	public boolean isReadOnly() {
		return (flag & READONLY)==READONLY;
	}

	@SuppressWarnings("unchecked")
	public <ST extends AbstractArray<V>> ST withReadOnly(
			boolean value) {
		if(value) {
			this.flag = (byte) (this.flag | READONLY);
		} else {
			this.flag = (byte) (this.flag & (0xff - READONLY));
		}
		return (ST) this;
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
	 * @param items the HashList for searching
	 * 
	 * @return  ths pos
	 */
	protected int addHashItem(int pos, Object newValue, Object[] items) {
		int hashKey = hashKey(newValue.hashCode(), items.length);
		while (true) {
			if (items[hashKey] == null || (Integer)items[hashKey] == -1) {
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
		if(isComplex(size)) {
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
			if(minCapacity >= ((Object[])elements[SMALL_KEY]).length) {
				resizeSmall(newSize, SMALL_KEY);
				if((flag & MAP)==MAP) {
					resizeSmall(newSize, SMALL_VALUE);
				}
			}
			if(minCapacity>=MINHASHINGSIZE && (elements[BIG_KEY]==null || minCapacity >= ((Object[])elements[BIG_KEY]).length * MAXUSEDLIST)) {
				resizeBig((int)(minCapacity*2.5), BIG_KEY);
				if((flag & MAP)==MAP) {
					resizeBig((int)(minCapacity*2.5), BIG_VALUE);
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
	 * @param size the new Size of the List
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
	 * @param size the newSize of the List
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

	
	public AbstractArray<V> withAll(Object... values) {
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
	
	public AbstractArray<V> without(Object... values) {
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

	public AbstractArray<V> withList(Collection<?> list) {
		if(list==null || this.size + list.size()== 0 ){
			return this;
		}
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
	 *
     * @param o Element for search
     * @return the index of the first found index of the element
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
     * 
     * @param o Element for search
     * @return the index of the last found index of the element
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
    			if(((Integer)items[i])>index){
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
    			if(((Integer)items[i])>index){
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
		if((Integer)hashCodes[index]>-1){
			indexItem = transformIndex((Integer)hashCodes[index], items.length);
			value = items[ indexItem ];
		}
		while(!checkValue(o, value)){
			index = (index + 1) % hashCodes.length;
			if(hashCodes[index]==null) {
				return -1;
			}
			if((Integer)hashCodes[index]==-1){
				continue;
			}
			indexItem = transformIndex((Integer)hashCodes[index], items.length);
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
    			if(((Integer)items[i])>index){
    				break;
    			}
				index += (Integer)items[i];
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
		if(c==null)
			return true;
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
			if(!shrink(size) && isComplex(size) ){
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
		if(!isComplex(size)){
			if(elements==null) {
				return null;
			}
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
		int indexHash = (Integer)hashCodes[indexPos];
		if(indexHash>-1){
			value = items[transformIndex(indexHash, items.length)];
		}
		while(!checkValue(value, oldValue)){
			indexPos = (indexPos + 1) % items.length;
			indexHash = (Integer)hashCodes[indexPos];
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
	
	public Object getValueItem(Object key) {
		int pos = indexOf(key);
		if (pos >= 0) {
			return this.getValueByIndex(pos);
		}
		if (!(key instanceof String)) {
			return null;
		}
		String keyString = "" + key;
		int len = 0;
		int end = 0;
		int id = 0;
		for (; len < keyString.length(); len++) {
			char temp = keyString.charAt(len);
			if (temp == '[') {
				for (end = len + 1; end < keyString.length(); end++) {
					temp = keyString.charAt(end);
					if (keyString.charAt(end) == ']') {
						end++;
						break;
					} else if (temp > 47 && temp < 58 && id >= 0) {
						id = id * 10 + temp - 48;
					} else if (temp == 'L') {
						id = -2;
					}
				}
				if (end == keyString.length()) {
					end = 0;
				}
				break;
			} else if (temp == '.') {
				end = len;
				id = -1;
				break;
			}
		}
		if (end == 0 && len == keyString.length()) {
			id = -1;
		}

		Object child = getValueByIndex(indexOf(keyString.substring(0, len)));
		if (child != null) {
			if (end == 0) {
				if (id >= 0 || id == -2) {
					if (child instanceof AbstractList<?>) {
						AbstractList<?> list = (AbstractList<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							return list.get(id);
						}
					}
				} else {
					return child;
				}
			} else {
				if (id >= 0 || id == -2) {
					if (child instanceof AbstractArray) {
						if (end == len + 2) {
							// Get List
							BaseItem result = this.getNewList(true);
							AbstractList<?> items = (AbstractList<?>) child;
							for (int z = 0; z < items.size(); z++) {
								result.withAll(((AbstractList<?>) items
										.get(z)).getValueItem(keyString
										.substring(end + 1)));
							}
							return result;
						}
						AbstractList<?> list = (AbstractList<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							return ((SimpleKeyValueList<?, ?>) list.get(id))
									.getValueItem(keyString.substring(end + 1));
						}
					}
				} else {
					return ((SimpleKeyValueList<?, ?>) child)
							.getValueItem(keyString.substring(end + 1));
				}
			}
		}
		return null;
	}
	
    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over this collection, checking each
     * element returned by the iterator in turn to see if it's contained
     * in the specified collection.  If it's not so contained, it's removed
     * from this collection with the iterator's <tt>remove</tt> method.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by the
     * <tt>iterator</tt> method does not implement the <tt>remove</tt> method
     * and this collection contains one or more elements not present in the
     * specified collection.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     *
     * @see #contains(Object)
     */
    public boolean retainAll(Collection<?> c) {
    	if(c==null){
    		return false;
    	}
        boolean modified = false;
        Iterator<?> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }
    
    public Iterator<V> iterator() {
		return new SimpleIterator<V>(this);
	}
    
    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array.  If the list fits in the
     * specified array, it is returned therein.  Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of
     * this list.
     *
     * <p>If the list fits in the specified array with room to spare
     * (i.e., the array has more elements than the list), the element in
     * the array immediately following the end of the collection is set to
     * <tt>null</tt>.  (This is useful in determining the length of the
     * list <i>only</i> if the caller knows that the list does not contain
     * any null elements.)
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @param <T> the ContainerClass
     * @return an array containing the elements of the list
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
    	if(a==null)
    		return null;
    	Object[] elementData;
    	if(isBig()) {
    		elementData = (Object[]) elements[SMALL_KEY];
    	}else{
    		elementData = elements;
    	}
        if (a.length < size) {
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        }
        if (elementData == null)
        {
           return a; // should be empty
        }
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }
    
	@SuppressWarnings("unchecked")
	public V get(int index) {
		return (V) getKeyByIndex(index, size);
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
	
	public BaseItem getNewList(boolean keyValue) {
		return new AbstractArray<V>();
	}

	public BaseItem subList(int fromIndex, int toIndex) {
		BaseItem newInstance = getNewList(false);
		if(fromIndex<0) {
			fromIndex += size;
		}
		if(toIndex >= size() || toIndex == 0) {
			toIndex = size() - 1;	
		}
		if( fromIndex<0 || fromIndex>toIndex ){
			return newInstance;
		}
		
		while(fromIndex<toIndex) {
			newInstance.withAll(get(fromIndex++));
		}
		return newInstance;
	}
	
	protected void fireProperty(Object oldElement, Object newElement,
			Object beforeElement, Object value) {
		//FIXME
	}
	
	public void move(int from, int to) {
		//FIXME
	}
}
