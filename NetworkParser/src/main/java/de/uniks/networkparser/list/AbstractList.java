package de.uniks.networkparser.list;

import java.nio.channels.SeekableByteChannel;
import java.util.Collection;
import java.util.Iterator;

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

    
	/** Standard Constructor */
    public AbstractList(){}

    /** Constructor with list*/
    public AbstractList(Collection<?> list){
    	withAll(list);
    	if(list instanceof AbstractList){
    		this.flag = ((AbstractList)list).getFlag();
    	}		
    }
    
	int getArrayFlag() {
		if(size==0) {
			return 0;
		}
		if((flag & BIDI)>0){
			return 5;
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
	
	
	private Object grow(int minCapacity) {
		Object[] items;
	    if(minCapacity>MINHASHINGSIZE) {
	    	if(items.length>5) {
	    		items = elements;
	    		elements =
	    	}
	    }
		Object[] elements;

		
		if(id==0){
			elements=this.elements;
		}else {
			elements = (Object[]) this.elements[id];
		}
		//FIXME
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
	
	public String flag() {
		
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
	
	protected void fireProperty(Object oldElement, Object newElement,
			Object beforeElement, Object value) {
		//FIXME
	}
}
