package de.uniks.networkparser.list;

public class AbstractList {
	public static final float MAXUSEDLIST = 0.7f;
	public static final byte ALLOWDUPLICATE=0x04;
	public static final byte ALLOWEMPTYVALUE=0x08;
	public static final byte VISIBLE=0x10;
	public static final byte CASESENSITIVE =0x20;
	public static final float MINUSEDLIST = 0.2f;
	protected byte flag=1; // SIZE
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
	Object[] elementKey; // non-private to simplify nested class access
    /** The size of the ArrayList (the number of elements it contains).  */
    private int size;

}
