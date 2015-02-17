package de.uniks.networkparser.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import de.uniks.networkparser.interfaces.BaseItem;

public class AbstractArray implements BaseItem  {
	/** Is ENTITYSIZE in Flag */
	public static final byte ENTITYSIZE = 0x01;
	/** Is Allow Duplicate Items in List	 */
	public static final byte ALLOWDUPLICATE = 0x02;
	/** Is Allow Empty Value in List (null)  */
	public static final byte ALLOWEMPTYVALUE = 0x04;
	/** Is The List is Visible for Tree Editors  */
    public static final byte VISIBLE = 0x08;
	/** Is Key is String and is Allow Casesensitive  */
	public static final byte CASESENSITIVE = 0x10;
	/** Is List is ReadOnly */
	public static final byte READONLY = 0x20;
	/** Is The List has Key,Value */
	public static final byte MAP = 0x40;
	/** Is List is Key,Value and Value, Key */
	public static final byte BIDI = (byte) 0x80;
		
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
    	return 0;
    }
    
    /** Init-List with Collection */
    @SuppressWarnings("unchecked")
	public <ST extends AbstractArray> ST init(Collection<?> list){
    	withList(list);
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
    
    public AbstractArray withFlag(int value)  {
    	this.flag = (byte) (this.flag & value);
    	if(value == BIDI){
    		this.flag = (byte) (this.flag & MAP);
    	}
    	return this;
    }
    
    boolean isBig() {
    	return size>MINHASHINGSIZE && elements.length <= (BIG_VALUE+1);
    }

    boolean isComplex() {
    	return (flag & MAP) == MAP || size > MINHASHINGSIZE;
    }
    
	int getArrayFlag(int size) {
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
		return (flag & ENTITYSIZE) + 1;
	}
    
	public AbstractArray withEntitySize(int size) {
		flag = (byte) (flag - (flag & ENTITYSIZE) + size);
		return this;
	}
	
	public byte getFlag(){
		return flag;
	}
	
	public int calcNewSize(int listSize) {
		return listSize * entitySize() * 2;
	}
	
	
	public int usedSize(){
		return size() * entitySize();
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
		int tmp = (hashKey + hashKey % entitySize()) % len;

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
		//FIXME SHRINK
//		resize(minCapacity);
	}
	
	void grow(int minCapacity) {
		if (elements == null){
			if((flag & MAP)==0){
				elements = new Object[minCapacity + minCapacity / 2	+ 4];
				return;
			}
			elements = new Object[getArrayFlag(minCapacity)];
			elements[SMALL_KEY] = new Object[minCapacity + minCapacity / 2 + 4];
			elements[SMALL_VALUE] = new Object[minCapacity + minCapacity / 2 + 4];
			return;
		}
		int arrayFlag = getArrayFlag( minCapacity );
		// elements wrong size
		if(arrayFlag== 1 && minCapacity<MINHASHINGSIZE) {
			if(minCapacity > elements.length) {
				// resize Array
				elements =resizeSmall(minCapacity + minCapacity / 2	+ 4, elements);
			}
			return;
		}
		if(arrayFlag != elements.length) {
			// Change Single to BigList
			Object[] old = elements;
			elements = new Object[arrayFlag];
			elements[SMALL_KEY] = old;
			return;
		}
		
		
		if (elements[BIG_KEY]== null || minCapacity >= ((Object[])elements[BIG_KEY]).length * MAXUSEDLIST){
			resizeBig(minCapacity, BIG_KEY);
			if(arrayFlag > 4){
				resizeBig(minCapacity, BIG_VALUE);
			}
			elements[DELETED] = null;
		}
	}
	
	void resizeBig(int minCapacity, int index) {
		Object[] items = (Object[]) elements[index - 1];
		Object[] newItems = new Object[minCapacity*entitySize()*2]; 
		elements[index] = newItems;
		for(int pos=0;pos<items.length;pos++) {
			addHashItem(pos, items[pos], newItems);
		}
	}
	Object[] resizeSmall(int newCapacity, Object[] items) {
		Object[] dest = new Object[newCapacity];
		System.arraycopy(items, 0, dest, 0, size);
		return dest;
	}

	/**
	 * Add a Element to the List
	 * 
	 * @param element to add a Value
	 * @return boolean if success add the Value
	 */
	protected int checkKey(Object element){
		if (element == null)
			return -1;
		if (isComparator()) {
			for (int i = 0; i < size(); i++) {
				int result = comparator().compare(getKey(i), element);
				if (result >= 0) {
					if (!isAllowDuplicate() && getKey(i) == element) {
						return -1;
					}
					grow(size + 1);
					return i;
				}
			}
		}
		if (!isAllowDuplicate()) {
			if (this.contains(element)) {
				return -1;
			}
		}
		grow(size + 1);
		return size;
	}

	public Object getKey(int index) {
		if(index<0) {
			index = size + 1 - index;
		}
		if(index>=0 && index<size){
			if(isBig()) {
				return ((Object[])elements[SMALL_KEY])[index];
			}
			return elements[index];
		}
		return null;
	}
	
	public Object getValue(int index) {
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
        Object beforeKey = this.getKey(size);
        size++;
        fireProperty(null, key, beforeKey, value);
		return pos;
	}
	
	
	/**
	 * Add a Key to internal List and Array if nesessary
	 *
	 * @param element
	 *            the new Value
	 * @param pos
	 *            the new Position -1 = End
	 * @return if value is added
	 */
	protected int addKey(int pos, Object element) {
		int i = size();
		Object[] keys;
		if(isBig()) {
			keys = (Object[]) elements[SMALL_KEY];
			addHashItem(pos, element, (Object[])elements[BIG_KEY]);
		}else{
			keys = elements;
		}
		while(i>pos) {
			keys[i] = keys[--i]; 	
		}
		keys[pos] = element;
        Object beforeElement = null;
        if (pos > 0)
        {
        	beforeElement = this.getKey(pos-1);
        }
        size++;
        fireProperty(null, element, beforeElement, null);
		return pos;
	}

	/**
	 * Add a Key to internal List and Array if nesessary
	 *
	 * @param element
	 *            the new Value
	 * @param pos
	 *            the new Position -1 = End
	 * @return if boolean if all added
	 */
	protected boolean addAllKeys(int pos, Collection<?> values) {
		int size = values.size();
		int i = pos+size;
		grow(i);
		
		Object[] keys;
		if(isBig()) {
			keys = (Object[]) elements[SMALL_KEY];
			for(Object element : values) {
				addHashItem(pos, element, (Object[])elements[BIG_KEY]);
			}
		}else{
			keys = elements;
		}
		while(i>pos) {
			keys[i] = keys[--i-size]; 	
		}
		for(Object element : values) {
			keys[pos] = element;
			size++;
			Object beforeElement = null;
			if (size > 1) {
				beforeElement = this.getKey(size - 1);
			}
			fireProperty(null, element, beforeElement, null);
		}
		return true;
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
		sb.append("E"+entitySize());
		return sb.toString();
	}

	
	public AbstractArray withAll(Object... values) {
		if(values==null){
			return this;
		}
		for (Object value : values) {
			int pos = checkKey(value);
			if(pos>=0) {
				this.addKey(pos, value);
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
		if(getArrayFlag(size)>1){
			Object[] items = (Object[]) elements[offset];
			Object oldValue = items[pos];
			items[pos] = value;
			int position = getPositionKey(oldValue);
			if(position>=0) {
				items = ((Object[]) elements[offset+1]);
			}
			return;
		}
		elements[pos] = value;
	}

	public AbstractArray withList(Collection<?> list) {
    	grow(list.size());
    	for(Iterator<?> i = list.iterator();i.hasNext();) {
    		this.withAll(i.next());
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
        if (o == null)
       		return -1;

    	if(size>MINHASHINGSIZE && entitySize()==2) {
    		return getPositionKey(o);
    	}
        for (int i = 0; i < size; i++)
            if (o.equals(elements[i]))
                return i;
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
    	if(size>MINHASHINGSIZE && entitySize()==2) {
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
	
	private int getPosition(Object o, int offset) {
		if (o == null) {
			return -1;
		}
		Object[] items = (Object[])elements[offset];
		int index = hashKey(o.hashCode(), items.length);
		Object value = items[index];
		while (!checkValue(value, o)) {
			if (value == null)
				return -1;
			index = (index + entitySize()) % items.length;
			value = items[index];
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

	public int getLastPositionKey(Object o) {
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
			index = (index + entitySize()) % items.length;
			value = items[index];
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
		if(isBig()) {
    		return getPositionKey(o)>=0;
    	}
		return indexOf(o) >= 0;
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
		int index = indexOf(key);
		if (index < 0) {
			return -1;
		}
		removeByIndex(index, SMALL_KEY);
		return index;
	}
	
	protected Object removeByIndex(int index, int offset) {
		Object oldValue = null;
		if(!isComplex()){
			// One Dimension
			oldValue = elements[index];
			System.arraycopy(elements, index + 1, elements, index, size - index);
			size--;
			return oldValue;
		}
		Object[] items = ((Object[])elements[offset]);
		oldValue = items[index];
		System.arraycopy(items, index + 1, items, index, size - index);
		
		int indexPos = hashKey(oldValue.hashCode(), items.length);
		Object value = items[indexPos];
		size--;
		
		while (!checkValue(value, oldValue)) {
			if (value == null)
				return oldValue;
			indexPos = (indexPos + entitySize()) % items.length;
			value = items[indexPos];
		}
		items[indexPos] = null;
		
		if(elements[DELETED]==null) {
			elements[DELETED]=new Integer[]{index};
		}else{
			Integer[] positions=new Integer[((Object[])elements[DELETED]).length+1];
			elements[DELETED] = positions;
			int i=0;
			while(positions[i]<index){
				i++;
			}
			System.arraycopy(positions, i, positions, i + 1, positions.length - i - 1);
			positions[i]=index;
		}
		return oldValue;
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
