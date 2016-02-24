package de.uniks.networkparser.list;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;

public abstract class AbstractArray<V> implements BaseItem, Iterable<V>  {
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
	public static final Integer REMOVED = -1;

	static final byte MINSIZE = 4;
	static final int MAXDELETED = 42;
	static final int MINHASHINGSIZE = 420; // Minimum (SIZE_BIG: 5)
	static final float MINUSEDLIST = 0.2f;
	static final float MAXUSEDLIST = 0.7f;

	static final int SMALL_KEY = 0;
	static final int BIG_KEY = 1;
	static final int DELETED = 2;
	static final int SMALL_VALUE = 3;
	static final int BIG_VALUE = 4;
	static final int SIZE_BIG = 6;

	/**
	 * Start index of Elements-Array
	 */
	int index;
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
	public byte flag = VISIBLE+CASESENSITIVE; // Flag of
	/**
	 * The array buffer into which the elements of the ArrayList are stored.
	 * The capacity of the ArrayList is the length of this array buffer. Any
	 * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
	 * will be expanded to DEFAULT_CAPACITY when the first element is added.
	 */

	/** May be
	 * [...] elements for simple List or
	 * [
	 *		 SimpleList&lt;K&gt;,
	 *		 BigList&lt;K + Index&gt;,
	 *		 DeleteItem&lt;Index-Sorted&gt;,
	 *		 SimpleValue&lt;V&gt;,
	 *		 BigList&lt;V + Index&gt; for BIDIMAP
	 * ]
	 */
	Object[] elements; // non-private to simplify nested class access

	/** The size of the ArrayList (the number of elements it contains).  */
	int size;

	/** Init-List with Collection
	 *
	 * @param list add all new Items
	 * @param <ST> Container Class
	 * @return return self
	 */
	@SuppressWarnings("unchecked")
	public <ST extends AbstractArray<V>> ST init(Collection<?> list){
		if(list instanceof AbstractArray){
			this.flag = ((AbstractArray<V>)list).flag();
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
	 * @param offset the startoffset of Items
	 * @param <ST> Container Class
	 * @return return self
	 * */
	@SuppressWarnings("unchecked")
	public <ST extends AbstractArray<V>> ST init(Object[] items, int size, int offset){
		elements = items;
		this.size = size;
		this.index = offset;
		return (ST) this;
	}

	@SuppressWarnings("unchecked")
	public <ST extends AbstractArray<V>> ST withFlag(byte value)  {
		this.flag = (byte) (this.flag | value);
		if(value == BIDI){
			this.flag = (byte) (this.flag | MAP);
		}
		return (ST) this;
	}

	final boolean isComplex(int size) {
		return (flag & MAP) == MAP || size >= MINHASHINGSIZE;
//				|| (size > SIZE_BIG && elements.length<SIZE_BIG);
	}

	final int getArrayFlag(int size ) {
		if(size==0) {
			return 0;
		}
		if((flag & BIDI)>0){
			return 5;
		}
		if((flag & MAP)>0){
			return 4;
		}
		if(size>=MINHASHINGSIZE || (size > SIZE_BIG && elements != null && elements.length<SIZE_BIG)) {
			return 3;
		}
		return 1;
	}

	public byte flag(){
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
	public final boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Is Allow Duplicate Entity in the List
	 *
	 * @return boolean if the List allow duplicate Entities
	 */
	public final boolean isAllowEmptyValue() {
		return (flag & ALLOWEMPTYVALUE) != 0;
	}

	@SuppressWarnings("unchecked")
	public <ST extends AbstractArray<V>> ST  withAllowEmptyValue(boolean value) {
		this.flag = (byte) (this.flag | ALLOWEMPTYVALUE);
		if(!value) {
			this.flag -= ALLOWEMPTYVALUE;
		}
		return (ST) this;
	}
	
	public void setAllowEmptyValue(boolean value) {
		this.flag = (byte) (this.flag | ALLOWEMPTYVALUE);
		if(!value) {
			this.flag -= ALLOWEMPTYVALUE;
		}
	}

	/**
	 * Is Visible Entity
	 *
	 * @return boolean if the List is Visible
	 */
	public final boolean isVisible() {
		return (flag & VISIBLE) != 0;
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
	public final boolean isCaseSensitive() {
		return (flag & CASESENSITIVE) != 0;
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
	public final boolean isAllowDuplicate() {
		return (flag & ALLOWDUPLICATE) != 0;
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
	public final boolean isReadOnly() {
		return (flag & READONLY) != 0;
	}

	public void reset() {
		this.elements = null;
		this.size = 0;
		this.index = 0;
		return;
	}

	public void clear() {
		int arrayFlag = getArrayFlag(size);
		if(arrayFlag<1) {
			this.elements = null;
			return;
		}
		size = 0;
		this.index = 0;
		if(arrayFlag==1) {
			for(int i=elements.length - 1;i > 0;i--) {
				fireProperty(IdMap.REMOVE, elements[i], null, elements[i - 1], null);
			}
			fireProperty(IdMap.REMOVE, elements[0], null, null, null);
			this.elements = null;
			return;
		}
		Object[] items = (Object[]) elements[SMALL_KEY];
		if(arrayFlag>3) {
			for(int i=items.length - 1;i > 0;i--) {
				fireProperty(IdMap.REMOVE, items[i], null, items[i - 1], ((Object[])elements[SMALL_VALUE])[i]);
			}
			fireProperty(IdMap.REMOVE, items[0], null, null, ((Object[])elements[SMALL_VALUE])[0]);
			this.elements = null;
			return;
		}
		for(int i=items.length - 1;i > 0;i--) {
			fireProperty(IdMap.REMOVE, items[i], null, items[i - 1], null);
		}
		fireProperty(IdMap.REMOVE, items[0], null, null, null);
		this.elements = null;
	}

	/**
	 * Get the HashKey from a Object with Max HashTableIndex and StepSize of
	 * EntitySize
	 *
	 * @param hashKey
	 *			the hashKey of a Object
	 * @param len
	 *			the max Length of all Hashvalues
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
	 *			the new Value
	 * @param pos
	 *			the new Position -1 = End
	 * @param items the HashList for searching
	 *
	 * @return  ths pos
	 */
	protected int addHashItem(int pos, Object newValue, Object[] items) {
		int hashKey = 0;
		if(newValue != null) {
			hashKey = hashKey(newValue.hashCode(), items.length);
		}
		while (true) {
			if (items[hashKey] == null || items[hashKey] == REMOVED) {
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
			this.index = 0;
			return true;
		}

		int arrayFlag = getArrayFlag(size);
		int newSize = minCapacity + minCapacity / 2 + 5;
		if(arrayFlag > 1) {
			if((flag & MAP) != 0) {
				// MAP
				boolean change=false;
				if(minCapacity < ((Object[])elements[SMALL_KEY]).length * MINUSEDLIST) {
					resizeSmall(newSize, SMALL_KEY);
					resizeSmall(newSize, SMALL_VALUE);
					this.index = 0;
					change = true;
				}
				if(elements[BIG_KEY]!= null) {
					change = true;
					resizeBig(newSize, BIG_KEY);
					if(elements.length>BIG_VALUE && elements[BIG_VALUE]!= null) {
						resizeBig(newSize, BIG_VALUE);
					}
				}
				return change;
			}else if(minCapacity < ((Object[])elements[SMALL_KEY]).length * MINUSEDLIST) {
				// Change Simple Complexlist to SimpleList
				elements = (Object[]) elements[SMALL_KEY];
				return true;
			}
		}else if(minCapacity < elements.length * MINUSEDLIST) {
			resizeSmall(newSize);
			this.index = 0;
			return true;
		}
		return false;
	}

	void grow(int minCapacity) {
		int arrayFlag = getArrayFlag( minCapacity );
		if(elements == null ){
			// Init List
			int newSize = minCapacity + minCapacity / 2 + 5;
			if(arrayFlag==1){
				elements = new Object[newSize];
				return;
			}
			elements = new Object[arrayFlag];
			elements[SMALL_KEY] = new Object[newSize];
			if((flag & MAP) != 0){
				elements[SMALL_VALUE] = new Object[newSize];
			}
			return;
		}
		if( arrayFlag > 1 && arrayFlag != elements.length) {
			// Change Single to BigList
			Object[] old = elements;
			elements = new Object[arrayFlag];
			elements[SMALL_KEY] = old;
			if((flag & MAP) != 0){
				elements[SMALL_VALUE] = new Object[old.length];
			}
			return;
		}

		// Array has wrong size
		if(isComplex(minCapacity)) {
			int newSize = minCapacity + minCapacity / 2 + 5;
			if(minCapacity >= ((Object[])elements[SMALL_KEY]).length) {
				resizeSmall(newSize, SMALL_KEY);
				if((flag & MAP) != 0) {
					resizeSmall(newSize, SMALL_VALUE);
				}
			}
			if(minCapacity>=MINHASHINGSIZE) {
				boolean size=false;
				if(elements[BIG_KEY] != null && minCapacity >= ((Object[])elements[BIG_KEY]).length * MAXUSEDLIST) {
					resizeBig(newSize*2, BIG_KEY);
					size = true;
				}
				if((flag & BIDI) != 0 && elements[BIG_VALUE] != null && minCapacity >= ((Object[])elements[BIG_VALUE]).length * MAXUSEDLIST) {
					resizeBig(newSize*2, BIG_VALUE);
					size = true;
				}
				if(size) {
					elements[DELETED] = null;
				}
			}
		} else if(size < MINHASHINGSIZE) {
			if(minCapacity > elements.length) {
				int newSize = minCapacity + minCapacity / 2 + 5;
				resizeSmall(newSize);
			}
		}
	}

	void resizeBig(int minCapacity, int index) {
		Object[] items = (Object[]) elements[index - 1];
		Object[] newItems = new Object[minCapacity];
		elements[index] = newItems;
		int i=this.index;
		for(int pos=0;pos<size;pos++) {
			if(i==items.length) {
				i=0;
			}
			addHashItem(i, items[i], newItems);
			i++;
		}
	}

	void resizeSmall(int newCapacity, int index) {
		Object[] dest = new Object[newCapacity];
		if(this.index==0) {
			System.arraycopy(elements[index], 0, dest, 0, size);
		} else {
			int len = ((Object[])elements[index]).length;
			if(size > len - this.index) {
				System.arraycopy(elements[index], this.index, dest, 0, len - this.index);
				System.arraycopy(elements[index], 0, dest, len - this.index, len - size);
			}else {
				System.arraycopy(elements[index], this.index, dest, 0, size);
			}
		}
		elements[index] = dest;
	}

	void resizeSmall(int newCapacity) {
		elements = arrayCopy(elements, newCapacity);
		this.index = 0;
	}

	Object[] arrayCopy(Object[] source, int newCapacity){
		Object[] dest = new Object[newCapacity];
		if(source == null) {
			return null;
		}
		int end = source.length - this.index;
		if(size > end) {
			System.arraycopy(source, this.index, dest, 0, end);
			int len = size - end;
			System.arraycopy(source, 0, dest, end, len);
		}else{
			System.arraycopy(source, this.index, dest, 0, size);
		}
		return dest;
	}

	/**
	 * Add a Element to the List
	 *
	 * @param element to add a Value
	 * @return int the Position of the insert
	 */
	final int hasKey(Object element){
		if (element == null || isReadOnly()) {
			return REMOVED;
		}
		if(isComparator()) {
			boolean allowDuplicate = isAllowDuplicate();
			for (int i = 0; i < this.size; i++) {
				Object value = getKeyByIndex(i);
				if (comparator().compare(value, element) >= 0) {
					if (!allowDuplicate && value.equals(element)) {
						return REMOVED;
					}
					return i;
				}
			}
			return this.size;
		}
		if (isAllowDuplicate() == false) {
			if(indexOf(element, size) >= 0) {
				return REMOVED;
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
			return REMOVED;
		if (isComparator()) {
			for (int i = 0; i < this.size; i++) {
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
		return getByIndex(SMALL_KEY, index + this.index, size);
	}

	protected Object getByIndex(int offset, int index, int size) {
		if(size==0) {
			return null;
		}
		if(index<0) {
			index = size + 1 + index;
			if(index<0) {
				return null;
			}
		}
		Object[] items;
		if(isComplex(size)) {
			items = ((Object[])elements[offset]);
		}else {
			items = elements;
		}
		if(items == null ){
			return null;
		}
		if(index >= items.length ) {
			index = index % items.length;
		}
		return items[index];
	}

	protected int addKeyValue(int pos, Object key, Object value) {
		Object[] keys = (Object[]) elements[SMALL_KEY];
		Object[] values = (Object[]) elements[SMALL_VALUE];
		if(pos == 0 && this.size>0) {
			if(this.index == 0) {
				this.index = keys.length - 1;
			}else {
				this.index--;
			}
			pos = this.index;
		}else {
			pos = (this.index + pos) % keys.length;
			int i = (size() + this.index) % keys.length;
			while(i>pos) {
				keys[i] = keys[i-1];
				values[i] = values[--i];
			}
		}
		keys[pos] = key;
		values[pos] = value;
		Object beforeKey = this.getByIndex(SMALL_KEY, size, size);
		size++;
		if(isComplex(size)) {
			if(elements[BIG_KEY]!= null){
				addHashItem(pos, key, (Object[])elements[BIG_KEY]);
			}
			if ((flag & BIDI)==BIDI && elements[BIG_VALUE] != null)
			{
			   addHashItem(pos, value, (Object[])elements[BIG_VALUE]);
			}
		}
		fireProperty(IdMap.NEW, null, key, beforeKey, value);
		return pos;
	}

	/**
	 * Add a Key to internal List and Array if nesessary
	 * Method to manipulate Array
	 *
	 * @param element
	 *			the new Value
	 * @param pos
	 *			the new Position -1 = End
	 * @param size the newSize of the List
	 * @return if value is added
	 */
	final int addKey(int pos, Object element, int size) {
		Object[] keys;

		if(isComplex(size)) {
			keys = (Object[]) elements[SMALL_KEY];
			if(elements[BIG_KEY]!= null){
				int newPos = retransformIndex(pos, size);
				addHashItem(newPos, element, (Object[])elements[BIG_KEY]);
			}
		}else{
			keys = elements;
		}

		if(pos == 0 && this.size>0) {
			if(this.index == 0) {
				this.index = keys.length - 1;
			}else {
				this.index--;
			}
			pos = this.index;
		}else if(this.size == pos && this.index == 0){
		} else {
			//MOVE ALL ONE ELEMENT NEXT
			pos = (this.index + pos) % keys.length;
			int sizePos = (this.index + this.size) % keys.length;
			while(sizePos!=pos) {
				if(sizePos==0) {
					keys[sizePos] = keys[keys.length - 1];
					sizePos = keys.length - 1;
				}else{
					keys[sizePos] = keys[--sizePos];
				}
			}
		}
		keys[pos] = element;
		Object beforeElement = null;
		this.size++;
		if (pos > 0) {
			beforeElement = this.getByIndex(SMALL_KEY, pos-1, size);
		}
		fireProperty(IdMap.NEW, null, element, beforeElement, null);
		return pos;
	}

	public String toString() {
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
		sb.append("(").append(this.size).append(")");
		return sb.toString();
	}

	@Override
	public AbstractArray<V> with(Object... values) {
		if(values==null){
			return this;
		}
		int newSize = size + values.length;
		grow(newSize);
		for (Object value : values) {
			int pos = hasKey(value);
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
		Object[] items;
		if(getArrayFlag(size)>1){
			items = (Object[]) elements[offset];
		}else{
			items = elements;
		}
		Object oldValue = items[pos];
		items[pos] = value;
		Object beforeElement = null;
		if(pos>0) {
			beforeElement = items[pos - 1];
		}
		fireProperty(IdMap.UPDATE, oldValue, value, beforeElement, null);
	}

	public AbstractArray<V> withList(Collection<?> list) {
		if(list==null || this.size + list.size()== 0 ){
			return this;
		}
		int newSize = this.size + list.size();
		grow(newSize);
		if (isAllowDuplicate() == false) {
			for(Iterator<?> i = list.iterator();i.hasNext();) {
				Object item = i.next();
				if(indexOf(item, newSize) < 0) {
					this.addKey(size, item, newSize);
				}
			}
		}else {
			for(Iterator<?> i = list.iterator();i.hasNext();) {
				Object item = i.next();
				this.addKey(size, item, newSize);
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

	int indexOf(Object o, int size) {
		if (o == null || elements == null)
			   return REMOVED;

		if(size>=MINHASHINGSIZE ) {
			   return getPosition(o, SMALL_KEY, false);
		}
		if((flag & MAP)==MAP) {
			return search((Object[]) elements[SMALL_KEY], o);
		}
		return search(elements, o);
	}

	int search(Object[] items, Object o) {
		int pos = this.index;
		if(pos==0) {
			while(pos < this.size) {
				if(checkValue(o, items[pos])) {
					return pos;
				}
				pos++;
			}
			return REMOVED;
		}
		for (int i = 0; i < this.size; i++) {
			if(pos==items.length) {
				pos=0;
			}
			if(checkValue(o, items[pos])) {
				return i;
			}
			pos++;
		}
		return REMOVED;
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
	public int lastIndexOf(Object o) {
		if (o == null)
			return REMOVED;
		if(size>MINHASHINGSIZE) {
			return getPosition(o, SMALL_KEY, true);
		}
		for (int i = size - 1; i >= 0; i--)
			if (o.equals(get(i)))
				return i;
		return REMOVED;
	}

	public int getPositionKey(Object o, boolean last) {
		if(!isComplex(size)) {
			if(last) {
				return lastIndexOf(o);
			}
			return indexOf(o);
		}
		return getPosition(o, SMALL_KEY, last);
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

	int getPosition(Object o, int offset, boolean last) {
		if (o == null || elements == null) {
			return REMOVED;
		}
		Object[] hashCodes;
		if(elements[offset + 1] != null) {
			hashCodes = (Object[])elements[offset + 1];
		}else {
			int len = ((Object[])elements[offset]).length;
			resizeBig(len*2, offset + 1);
			hashCodes = (Object[])elements[offset + 1];
		}
		int index = hashKey(o.hashCode(), hashCodes.length);
		if(hashCodes[index]==null){
			return REMOVED;
		}
		int len = ((Object[]) elements[offset]).length;
		int indexItem=-1;
		int lastIndex=-1;

		while(hashCodes[index]!=null) {
			if(hashCodes[index]==REMOVED){
				index = (index + 1) % hashCodes.length;
				continue;
			}
			indexItem = transformIndex((Integer)hashCodes[index], len);
			if(checkValue(o, getByIndex(offset, indexItem, size))) {
				if(!last) {
					break;
				}
				lastIndex = indexItem;
			} else if(lastIndex > 0) {
				break;
			}
			index = (index + 1) % hashCodes.length;
		}
		if(last) {
			return lastIndex;
		}
		if(hashCodes[index]==null) {
			return REMOVED;
		}
		return indexItem;
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
	 * @throws ClassCastException			{@inheritDoc}
	 * @throws NullPointerException		  {@inheritDoc}
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
			return REMOVED;
		}
		removeByIndex(index, SMALL_KEY, this.index);
		return index;
	}

	Object removeByIndex(int index, int offset, int offsetIndex) {
		Object item = removeItem(index, offset, offsetIndex);
		if(item != null){
			size--;
			if(!shrink(size)){
				if(offsetIndex == index ) {
					return item;
				}
				if(isComplex(size) ){
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
		}
		return item;
	}

	Object removeItem(int index, int offset, int oldIndex)
	{
		if(elements==null) {
			return null;
		}
		Object[] items;

		int complex = getArrayFlag(size);

		if(complex>1) {
			items = ((Object[])elements[offset]);
		} else {
			// One Dimension
			items = elements;
		}

		index = (index + oldIndex) % items.length; // Fix for index+this.index > length

		Object oldValue = items[index];
		if(oldValue==null) {
			return null;
		}

		// REMOVE FROM HASH-Codes
		if(complex > 1 && complex>(offset+1) && elements[offset + 1] != null) {
			Object[] hashCodes = ((Object[])elements[offset + 1]);
			int indexPos = hashKey(oldValue.hashCode(), hashCodes.length);
			int indexHash = (Integer) hashCodes[indexPos];
			int pos = transformIndex(indexHash, items.length);
			while(pos != index) {
				indexPos = (indexPos + 1) % hashCodes.length;
				if(hashCodes[indexPos] == null) {
					break;
				}
				indexHash = (Integer)hashCodes[indexPos];
				if(indexHash==REMOVED){
					continue;
				}
				pos = transformIndex(indexHash, items.length);
			}
			if(pos == index) {
				hashCodes[indexPos] = -1;
			}
		}

		if(index == oldIndex) {
			items[index] = null;
			if(offset==SMALL_KEY) {
				this.index++;
				if(this.index==items.length) {
					this.index = 0;
				}
			}
			return oldValue;
		}

		if((this.index + size - 1) % items.length == index)
		{
			items[index] = null;
		}
		else
		{
		 if(index > this.index)
		 {
			// move later elements to the right, maybe wrap around
			// [ef____axcd] -> [f_____acde]
			int end = (this.index + size - 1);

			if (end >= items.length)
			{
			   // wrap
			   int len = items.length - index - 1;
			   System.arraycopy(items, index+1, items, index, len);
			   items[items.length-1] = items[0];
			   end = end % items.length;
			   System.arraycopy(items, 1, items, 0, end);
			   items[end] = null;
			}
			else
			{
			   // no wrap
			   System.arraycopy(items, index+1, items, index, end-index);
			   items[end] = null;
			}
			}
		 else
		 {
			// remove within the fraction of the data that is at the start of the array, move elements after index to the left
			// [cdxf____ab] -> [cdf_____ab]
				int end = (this.index + size - 1) % items.length;
				int len = end - index;

				System.arraycopy(items, index+1, items, index, len);

				items[index + len] = null;
			}
		}
		return oldValue;
	}

	static final Object[] emptyArray = new Object[] {};

	public Object[] toArray() {
		if (elements == null)
		{
		   return emptyArray;
		}
		if(isComplex(size)) {
			return arrayCopy((Object[])elements[SMALL_KEY], size);
		}
		return arrayCopy(elements, size);
	}

	public Object getValue(Object key) {
		int pos = indexOf(key);
		if (pos >= 0) {
			return this.getByIndex(SMALL_VALUE, pos, size);
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

		Object child = getByIndex(SMALL_VALUE, indexOf(keyString.substring(0, len)), size);
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
								result.with(((AbstractList<?>) items
										.get(z)).getValue(keyString
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
									.getValue(keyString.substring(end + 1));
						}
					}
				} else {
					return ((SimpleKeyValueList<?, ?>) child)
							.getValue(keyString.substring(end + 1));
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
	 * @throws ClassCastException			{@inheritDoc}
	 * @throws NullPointerException		  {@inheritDoc}
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
	 *		  be stored, if it is big enough; otherwise, a new array of the
	 *		  same runtime type is allocated for this purpose.
	 * @param <T> the ContainerClass
	 * @return an array containing the elements of the list
	 * @throws ArrayStoreException if the runtime type of the specified array
	 *		 is not a supertype of the runtime type of every element in
	 *		 this list
	 * @throws NullPointerException if the specified array is null
	 */
	public <T> T[] toArray(T[] a) {
		if(a==null)
			return null;
		Object[] elementData;
		if(isComplex(size)) {
			elementData = (Object[]) elements[SMALL_KEY];
		}else{
			elementData = elements;
		}
		if (elementData == null)
		{
		   return a; // should be empty
		}
		if (a.length < size) {
			// Make a new array of a's runtime type, but my contents:
//			return (T[]) Arrays.copyOf(elementData, size, a.getClass());
			return null;
		}
		System.arraycopy(elementData, 0, a, 0, size);
		if (a.length > size)
			a[size] = null;
		return a;
	}

	@SuppressWarnings("unchecked")
	public V get(int index) {
		return (V) getByIndex(SMALL_KEY, index + this.index, size);
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

	public abstract BaseItem getNewList(boolean keyValue);

	/**
	 * Returns a view of the portion of this list between the specified
	 * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.  (If
	 * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
	 * empty.)
	 *
	 * @param fromIndex low endpoint (inclusive) of the subList
	 * @param toIndex high endpoint (exclusive) of the subList
	 * @return a view of the specified range within this list
	 */
	public BaseItem subList(int fromIndex, int toIndex) {
		BaseItem newInstance = getNewList(false);
		if(fromIndex<0) {
			fromIndex += size;
		}
		if(toIndex > size() || toIndex == 0) {
			toIndex = size();
		}
		if( fromIndex<0 || fromIndex>toIndex ){
			return newInstance;
		}

		while(fromIndex<toIndex) {
			newInstance.with(get(fromIndex++));
		}
		return newInstance;
	}
	@SuppressWarnings("unchecked")
	public <ST extends AbstractArray<V>> ST withSize(int size) {
		if(size<this.size) {
			this.shrink(size);
		} else {
			this.grow(size);
		}
		return (ST) this;
	}

	public void pack() {
		boolean complex = isComplex(size);
		if((flag & MAP) == 0) {
			if(complex) {
				elements = arrayCopy((Object[])elements[SMALL_KEY], size);
			} else {
				elements = arrayCopy(elements, size);
			}
			this.index = 0;
			return;
		}
		elements[SMALL_KEY] = arrayCopy((Object[])elements[SMALL_KEY], size);
		elements[BIG_KEY] = null;
		elements[DELETED] = null;
		elements[SMALL_VALUE] = arrayCopy((Object[])elements[SMALL_VALUE], size);
		if(elements.length>BIG_VALUE){
			elements[BIG_VALUE] = null;
		}
		this.index = 0;
	}

	protected boolean fireProperty(String type, Object oldElement, Object newElement,
			Object beforeElement, Object value) {
		return true;
	}

	public boolean move(int from, int to) {
		if(from == to) {
			return true;
		}
		if(from<0 || to < 0 || from > size() || to > size() ) {
			return false;
		}
		if((flag & MAP) != 0) {
			Object[] keys = (Object[]) elements[SMALL_KEY];
			Object[] values = (Object[]) elements[SMALL_VALUE];
			Object temp = keys[from];
			keys[from] = keys[to];
			keys[to] = temp;

			temp = values[from];
			values[from] = values[to];
			values[to] = temp;
			elements[BIG_KEY] = null;
			elements[DELETED] = null;
			if(elements.length>BIG_VALUE){
				elements[BIG_VALUE] = null;
			}
		} else if(isComplex(size())) {
			Object[] keys = (Object[]) elements[SMALL_KEY];
			Object temp = keys[from];
			keys[from] = keys[to];
			keys[to] = temp;
			elements[BIG_KEY] = null;
			elements[DELETED] = null;
		} else {
			Object temp = elements[from];
			elements[from] = elements[to];
			elements[to] = temp;
		}
		return true;
	}
	
	protected String parseItem(EntityStringConverter converter) {
		return "";
	}
	
	/**
	 * Make a prettyprinted Text of this Entity.
	 * <p>
	 * @param converter
	 *			Converter for transform Item to STring
	 */
	@Override
	public String toString(Converter converter) {
		if(converter == null) {
			return null;
		}
		if(converter instanceof EntityStringConverter) {
			return parseItem((EntityStringConverter) converter);
		}
		return converter.encode(this);
	}
}
