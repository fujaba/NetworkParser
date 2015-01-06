package de.uniks.networkparser.list;

import java.util.Collection;
import java.util.Iterator;

public class SimpleBigList<V> extends SimpleSmallList<V>{
	private static final long serialVersionUID = 1L;
	protected Object[] items = null;
    public static final int MINHASHINGSIZE = 420;
    
    /** Standard Constructor */
    public SimpleBigList(){}

    /** Constructor with Collection */
    public SimpleBigList(Collection<V> list){
		resize(calcNewSize(list.size()), list);
    }
    
    @Override
    public int minSize(){
    	return MINHASHINGSIZE;
    }
    
    public int realSize() {
    	return items.length;
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
	protected int addKey(int pos, Object newValue) {
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
	
	protected void resize(int size, Collection<?> keys) {
		this.items = new Object[size];
		int count = 0;
		for(Iterator<?> i = keys.iterator();i.hasNext();){
			addKey(count++, i.next());
		}
	}

	@Override
	public int removeItemByObject(Object key) {
		int index;
		if(items==null){
			return -1;
		}
		if (entitySize() == 1) {
			// change hashTable to Object with ids
			this.flag++;
			resize(items.length * 2, this);
		}
		index = getPositionKey(key);
		if (index < 0) {
			return -1;
		}
		index = (int) this.items[index + 1];
		int diff = index;
		if (index > this.size()) {
			diff = this.size() - 1;
		}
		while (this.get(diff) != key) {
			diff--;
		}
		
		remove(diff);
		if (index - diff > 1000) {
			resize(items.length, this);
		}
		return diff;	
	}

	@Override
	public int getPositionKey(Object o) {
		if (o == null) {
			return -1;
		}
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

	@Override
	public boolean remove(Object oldValue) {
		if (items == null)
			return false;

		int hashKey = hashKey(oldValue.hashCode(), items.length);

		while (true) {
			Object oldEntry = items[hashKey];
			if (oldEntry == null)
				return false;
			if (oldEntry.equals(oldValue)) {
				int gapIndex = hashKey;
				int lastIndex = gapIndex;

				// search later element to put in this gap
				while (true) {
					hashKey = (hashKey + entitySize()) % items.length;
					oldEntry = items[hashKey];
					if (oldEntry == null) {
						items[gapIndex] = items[lastIndex];
						items[lastIndex] = null;
						if (entitySize() == 2) {
							items[gapIndex + 1] = items[lastIndex + 1];
							items[lastIndex + 1] = null;
						}
						return true;
					}

					if (hashKey(oldEntry.hashCode(), items.length) <= gapIndex) {
						lastIndex = hashKey;
					}
				}
			}
			hashKey = (hashKey + entitySize()) % items.length;
		}
	}
	
	/**
	 * Transform a Value to the real Index of List.
	 *
	 * @param index
	 *            The Index for search
	 * @param value
	 *            Value for search
	 * @param array
	 *            The Array of Items
	 * @param list
	 *            The List
	 * @return The Index of Key
	 */
	public int transformIndex(int index, Object value) {
		if (items!= null && index >= 0 && entitySize() == 2) {
			index = (int) items[index + 1];
			if (index >= size()) {
				index = size() - 1;
			}
			while (!value.equals(get(index))) {
				index--;
			}
			return index;
		}
		return super.transformIndex(index, value);
	}
}
