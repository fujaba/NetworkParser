package de.uniks.networkparser;	

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.sort.EntityComparator;
import de.uniks.networkparser.sort.SortingDirection;

/**
 * The Class EntityList.
 */
public abstract class AbstractList<V> implements BaseItem {
	protected List<V> keys = new ArrayList<V>();
	protected Object[] hashTable = null;
   
	private boolean allowDuplicate = initAllowDuplicate();
	protected Comparator<V> cpr;

	protected static final int hashTableStartHashingThreshold = 420;
	protected static final float hashTableLoadThreshold = 0.7f;
	protected int entitySize = 1;

	protected boolean initAllowDuplicate(){
		return true;
	}
	
	public Comparator<V> comparator() {
		if(this.cpr==null){
			withComparator(new EntityComparator<V>().withColumn(EntityComparator.LIST).withDirection(SortingDirection.ASC));
		}
		return cpr;
	}
	
	public boolean isComparator(){
		return (this.cpr!=null);
	}
	
	public AbstractList<V> withComparator(Comparator<V> comparator){
		this.cpr = comparator;
		return this;
	}

	public AbstractList<V> withComparator(String column){
		this.cpr = new EntityComparator<V>().withColumn(column).withDirection(SortingDirection.ASC);
		return this;
	}
	
	protected void hashTableAdd(Object newValue, int pos)
	{
      if (hashTable == null)
      {
         if (this.keys.size() <= hashTableStartHashingThreshold) return;
      }
      
      ensureHashTableCapacity(this.keys.size());
      
      int hashKey = hashKey(newValue.hashCode());
      
      while (true)
      {
         Object oldEntry = hashTable[hashKey];
         if (oldEntry == null) 
         {
            hashTable[hashKey] = newValue;
            if(entitySize==2){
            	hashTable[hashKey + 1] = pos;
            }
            
            return;
         }
         
         if (oldEntry.equals(newValue)) return;
         
         hashKey = (hashKey + entitySize) % hashTable.length;
      }
   }

   private void ensureHashTableCapacity(int size)
   {
      if (hashTable == null){
    	  if(size <= hashTableStartHashingThreshold){
    		  return;
    	  }
          hashTable = new Object[hashTableStartHashingThreshold*3];
      }else {
    	  	if (size < hashTableStartHashingThreshold / 10){
		         hashTable = null;
		         return;
    	  	}
      }
      
      if (size > hashTable.length * hashTableLoadThreshold)
      {
         // double hashTable size
    	  resizeHashMap(this.hashTable.length*2);
      }
      else if (size < hashTable.length / 20)
      {
         // shrink hashTable size to a loadThreshold of 33%
    	 resizeHashMap(size*3);
      }
   }

   /**
	 * Get the object value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return An object value.
	 * @throws RuntimeException
	 *             If there is no value for the index.
	 */
	public V get(int index) throws RuntimeException {
		V object = this.keys.get(index);
		if (object == null) {
			throw new RuntimeException("EntityList[" + index + "] not found.");
		}
		return object;
	}
	
	/**
	 * Compares the two specified Object values. The sign of the integer value returned is the same as that of the integer that would be returned by the call:
	 *   new Object(o1).compareTo(new Object(o2))
	 * @param o1 the first Object to compare
	 * @param o2 the second Object to compare
	 * @return the value 0 if o1 is numerically equal to o2; a value less than 0 if o1 is numerically less than o2; and a value greater than 0 if o1 is numerically greater than o2.
	 */
	public int compare(V o1, V o2){
		return comparator().compare(o1, o2);
	}
	
	public abstract AbstractList<V> getNewInstance();
	
	public void copyEntity(AbstractList<V> target, int pos){
		target.addEntity(get(pos));
	}
	
	protected boolean addEntity(V newValue) {
		if (newValue == null)
			return false;
		if (cpr != null) {
			for (int i = 0; i < size(); i++) {
				int result = compare(get(i), newValue);
				if (result >= 0) {
					if (!isAllowDuplicate() && get(i) == newValue) {
						return false;
					}
					this.keys.add(i, newValue);
					V beforeElement = null;
					if (i > 0) {
						beforeElement = this.keys.get(i - 1);
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

		boolean result = this.keys.add(newValue);
		if (result) {
			this.hashTableAdd(newValue, this.keys.size());
			V beforeElement = null;
			if (size() > 1) {
				beforeElement = this.keys.get(size() - 1);
			}
			fireProperty(null, newValue, beforeElement, null);
		}
		return result;
	}
	
	public AbstractList<V> subSet(V fromElement, V toElement) {
		AbstractList<V> newList = getNewInstance();
		
		// PRE WHILE
		int pos=0;
		int size=size();
		while(pos<size){
			if(compare(get(pos), fromElement)>=0){
				copyEntity(newList, pos);
				break;
			}
			pos++;
		}
		
		// MUST COPY
		while(pos<size){
			if(compare(get(pos), toElement)>=0){
				break;
			}
			copyEntity(newList, pos++);
		}
		return newList;
	}
	
	/**
	 * Returns a view of the portion of this list between the specified fromIndex, inclusive, and toIndex, exclusive. (If fromIndex and toIndex are equal, 
	 * the returned list is empty.) The returned list is backed by this list, so non-structural changes in the returned list are reflected in this list,
	 * and vice-versa. The returned list supports all of the optional list operations supported by this list.
	 * 
	 * This method eliminates the need for explicit range operations (of the sort that commonly exist for arrays).
	 * Any operation that expects a list can be used as a range operation by passing a subList view instead of a whole list.
	 * For example, the following idiom removes a range of elements from a list:
	 * 
	 * @param fromIndex low endpoint (inclusive) of the subList
	 * @param toIndex high endpoint (exclusive) of the subList
	 * @return a view of the specified range within this list
	 */
	public List<V> subList(int fromIndex, int toIndex) {
		return this.keys.subList(fromIndex, toIndex);
	}

    /**
     * Returns a view of the portion of this map whose keys are less than (or
     * equal to, if {@code inclusive} is true) {@code toKey}.  The returned
     * map is backed by this map, so changes in the returned map are reflected
     * in this map, and vice-versa.  The returned map supports all optional
     * map operations that this map supports.
     *
     * <p>The returned map will throw an {@code IllegalArgumentException}
     * on an attempt to insert a key outside its range.
     *
     * @param toElement high endpoint of the keys in the returned map
     * @param inclusive {@code true} if the high endpoint
     *        is to be included in the returned view
     * @return result a list with less item then the key       
     * 
	*/
	public AbstractList<V> headSet(V toElement, boolean inclusive) {
		AbstractList<V> newList = getNewInstance();

		// MUST COPY
		for(int pos=0;pos<size();pos++){
			int compare = compare(get(pos), toElement);
			if(compare==0){
				if(inclusive){
					copyEntity(newList, pos);
				}
				break;
			}else if(compare>0){
				copyEntity(newList, pos);
				break;
			}
		}
		return newList;
	}

	/**
     * Returns a view of the portion of this map whose keys are greater than (or
     * equal to, if {@code inclusive} is true) {@code fromKey}.
     *
     * @param fromElement low endpoint of the keys in the returned map
     * @param inclusive {@code true} if the low endpoint
     *        is to be included in the returned view
     * @return a view of the portion of this map whose keys are greater than
     *         (or equal to, if {@code inclusive} is true) {@code fromKey}
     *         
     */
	public AbstractList<V> tailSet(V fromElement, boolean inclusive) {
		AbstractList<V> newList = getNewInstance();

		// PRE WHILE
		int pos=0;
		for(;pos<size();pos++){
			int compare = compare(get(pos), fromElement);
			if(compare==0){
				if(inclusive){
					copyEntity(newList, pos);
				}
				break;
			}else if(compare>0){
				copyEntity(newList, pos);
				break;
			}
		}
	
		// MUST COPY
		while(pos<size()){
			copyEntity(newList, pos++);
		}
		return newList;
	}

	/**
	 * @return the First Element of the List
	 */
	public V first() 
	{
	   if (this.keys.size() > 0)
	   {
	      return this.keys.get(0);
	   }
	   return null;
	}

	/**
	 * @return the Last Element of the List
	 */
	public V last() 
	{
	   if (this.keys.size() > 0)
	   {
	      return this.keys.get(this.size()-1);
	   }
	   
	   return null;
	}
	
	/**
	 * @param index of value
	 * @return the entity
	 */
	public Object getKey(int index){
		if(index<0 || index>this.keys.size()){
			return null;
		}
		return this.keys.get(index);
	}

	/**
	 * Get the boolean value associated with an index. The string values "true"
	 * and "false" are converted to boolean.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The truth.
	 * @throws RuntimeException
	 *             If there is no value for the index or if the value is not
	 *             convertible to boolean.
	 */
	public boolean getBoolean(int index) throws RuntimeException {
		if(index==-1){
			return false;
		}
		Object object = getItem(index);
		if (object.equals(Boolean.FALSE)
				|| (object instanceof String && ((String) object)
						.equalsIgnoreCase("false"))) {
			return false;
		} else if (object.equals(Boolean.TRUE)
				|| (object instanceof String && ((String) object)
						.equalsIgnoreCase("true"))) {
			return true;
		}
		throw new RuntimeException("EntityList[" + index
				+ "] is not a boolean.");
	}

	/**
	 * Get the double value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The value.
	 * @throws RuntimeException
	 *             If the key is not found or if the value cannot be converted
	 *             to a number.
	 */
	public double getDouble(int index) throws RuntimeException {
		Object object = getItem(index);
		try {
			return object instanceof Number ? ((Number) object).doubleValue()
					: Double.parseDouble((String) object);
		} catch (Exception e) {
			throw new RuntimeException("EntityList[" + index
					+ "] is not a number.");
		}
	}
	
	protected Object getItem(int index){
		return getKey(index);
	}

	/**
	 * Get the int value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The value.
	 * @throws RuntimeException
	 *             If the key is not found or if the value is not a number.
	 */
	public int getInt(int index) throws RuntimeException {
		Object object = getItem(index);
		try {
			return object instanceof Number ? ((Number) object).intValue()
					: Integer.parseInt((String) object);
		} catch (Exception e) {
			throw new RuntimeException("EntityList[" + index
					+ "] is not a number.");
		}
	}

	/**
	 * Get the long value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The value.
	 * @throws RuntimeException
	 *             If the key is not found or if the value cannot be converted
	 *             to a number.
	 */
	public long getLong(int index) throws RuntimeException {
		Object object = getItem(index);
		try {
			return object instanceof Number ? ((Number) object).longValue()
					: Long.parseLong((String) object);
		} catch (Exception e) {
			throw new RuntimeException("EntityList[" + index
					+ "] is not a number.");
		}
	}

	/**
	 * Get the string associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return A string value.
	 * @throws RuntimeException
	 *             If there is no value for the index.
	 */
	public String getString(int index) throws RuntimeException {
		return getItem(index).toString();
	}

	/**
	 * Put or replace an object value in the EntityList. If the index is greater
	 * than the length of the EntityList, then null elements will be added as
	 * necessary to pad it out.
	 * 
	 * @param index
	 *            The subscript.
	 * @param key
	 *            The value to put into the array. The value should be a
	 *            Boolean, Double, Integer, EntityList, Entity, Long, or String
	 *            object.
	 * @return this.
	 * @throws RuntimeException
	 *             If the index is negative or if the the value is an invalid
	 *             number.
	 */
	public AbstractList<V> put(int index, V key) throws RuntimeException {
		if (index < 0) {
			throw new RuntimeException("EntityList[" + index + "] not found.");
		}
		if (index < size()) {
			
			V oldValue = null;
			if(index>0){
				oldValue = this.keys.get(index - 1);
			}
			this.keys.set(index, key);
			fireProperty(oldValue, key, null, null);
		} else {
			addEntity(key);
		}
		return this;
	}
	
	/**
	 * Is Allow Duplicate Entity in the List
	 * @return boolean if the List allow duplicate Entities
	 */
	public boolean isAllowDuplicate() {
		return allowDuplicate;
	}

	/**
	 * Set the Flag for Duplicate Entities
	 * @param allowDuplicate isAllowDuplicate
	 * @return the List
	 */
	public AbstractList<V> withAllowDuplicate(boolean allowDuplicate) {
		this.allowDuplicate = allowDuplicate;
		return this;
	}
	
	/**
	 * Remove an index and close the hole.
	 * 
	 * @param index
	 *            The index of the element to be removed.
	 * @return The value that was associated with the index, or null if there
	 *         was no value.
	 */
	public V remove(int index) {
		return removeItemByIndex(index, false);
	}

	protected V removeItemByIndex(int index, boolean refresh){
		if(index<0){
			return null;
		}
		V oldValue = get(index);
		V beforeValue = null;
		if(index>0){
			beforeValue = get(index - 1);
		}
		listRemove(index);
		hashTableRemove(oldValue);
		fireProperty(oldValue, null, beforeValue, null);
		if(refresh){
			// Refactoring
			resizeHashMap(this.hashTable.length);
		}

		return oldValue;
	}
	
	protected void listRemove(int index){
		this.keys.remove(index);
	}
	
	protected void resizeHashMap(int size){
		this.hashTable = new Object[size];
        for(int i=0;i<this.keys.size();i++){
            hashTableAdd(this.keys.get(i), i);
        }
	}
	
	protected int removeItemByObject(Object key){
		if(entitySize==1 && this.hashTable != null){
			// change hashTable to Object with ids
	         this.entitySize = 2;
	         resizeHashMap(this.hashTable.length*2);
		}
    	int index=getPosition(key);
    	if(index<0){
    		return -1;
    	}
    	if(this.hashTable != null){
    		if(this.entitySize==2){
    			index = (int) this.hashTable[index + 1];
    			int diff = index;
    			if(index>this.keys.size()){
    				diff = this.keys.size() - 1;
    			}
	    		while( this.keys.get(diff)!=key ){
	    			diff--;
	    		}
    			if(index - diff > 1000){
    				 removeItemByIndex(diff, true);
    				 return diff;
    			}
    			index = diff;
    		}
    	}
		removeItemByIndex(index, false);
		return index;
	}
	
	private void hashTableRemove(V oldValue)
   {
	   if (hashTable == null) return;
	   
	   int hashKey = hashKey(oldValue.hashCode());
	   
	   while (true)
	   {
	      Object oldEntry = hashTable[hashKey];
	      if (oldEntry == null) return;
	      if (oldEntry.equals(oldValue))
	      {
//	    	 int origHashKey = hashKey;
	         int gapIndex = hashKey;
	         int lastIndex = gapIndex;
	         
	         // search later element to put in this gap
	         while (true)
	         {
	            hashKey = (hashKey + entitySize) % hashTable.length;
	            oldEntry = hashTable[hashKey];
	            if (oldEntry == null)
	            {
	               hashTable[gapIndex] = hashTable[lastIndex];
	               hashTable[lastIndex] = null;
	               if(entitySize==2){
	            	   hashTable[gapIndex + 1] = hashTable[lastIndex + 1];
	            	   hashTable[lastIndex + 1] = null;
	               }
	               return;
	            }
	            
	            if (hashKey(oldEntry.hashCode()) <= gapIndex )
	            {
	               lastIndex = hashKey;
	            }
	         }
	      }
	      hashKey = (hashKey + entitySize) % hashTable.length;
	   }
   }

   /**
     * Locate the Entity in the List
     * @param key Entity
     * @return the position of the Entity or -1
     */
    public int getIndex(Object key){
    	int pos=getPosition(key);
    	if(this.hashTable != null){
    		if(this.entitySize==2){
    			return (int) this.hashTable[pos + 1];
    		}
    	}
    	return pos; 
    }
        
    public AbstractList<V> withCopyList(List<V> reference){
        this.keys = reference;
        return this;
    }
	
	public AbstractList<V> withReference(AbstractList<V> reference){
		this.cpr = reference.comparator();
		this.allowDuplicate = reference.isAllowDuplicate();
		return this;
	}
	
	/**
	 * If the List is Empty
	 * 
	 * @return boolean of size
	 */
	public boolean isEmpty() {
        return keys.size() < 1;
    }

	public boolean contains(Object o){
		return getPosition(o)>=0;
	}
	
    public int getPosition(Object o) 
    {
        if (this.hashTable != null)
        {
           int hashKey = hashKey(o.hashCode());
           while (true)
           {
              Object value = hashTable[hashKey];
              if (value == null) return -1;
              if (value.equals(o)) return hashKey;
              hashKey = (hashKey + entitySize) % hashTable.length;
           }
        }
        
        // search from the end as in models we frequently ask for elements that have just been added to the end
        int pos  = this.keys.size() - 1;
        for(ListIterator<V> i = reverseListIterator();i.hasPrevious();){
           if(i.previous().equals(o)){
              return pos; 
           }
           pos--;
        }
        return -1;
    }
   
    /**
     * Get the HashKey from a Object with Max HashTableIndex and StepSize of EntitySize
     * @param hashKey the hashKey of a Object
     * @return the hasKey
     */
    public int hashKey(int hashKey)
    {
        return (hashKey + hashKey % entitySize) % this.hashTable.length;
    }

    public Iterator<V> iterator() {
        return keys.iterator();
    }
    
    public Object[] toArray() {
        return keys.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return keys.toArray(a);
    }

    public boolean containsAll(Collection<?> c) {
       for (Object o : c)
       {
          if ( ! this.contains(o) ) return false;
       }
       return true;
    }
	
    public boolean removeAll(Collection<?> c) {
        return removeAll(c.iterator());
    }
    
    @SuppressWarnings("unchecked")
	public boolean removeAll(Iterator<?> i) {
		while(i.hasNext()){
			removeItemByObject((V)i.next());
		}
		return true;
	}
    
    public void clear() {
    	removeAll(iterator());
    }

    public V set(int index, V element) {
        return keys.set(index, element);
    }

    @SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST withoutList(Collection<?> values) {
		for (Iterator<?> i = values.iterator(); i.hasNext();) {
			without(i.next());
		}
		return (ST) this;
	}

	@SuppressWarnings("unchecked")
	public  <ST extends AbstractList<V>> ST without(Object... values){
		if(values==null){
			return null;
		}
		for(Object item : values){
			
			removeItemByObject((V) item);
		}
		return (ST)this;
	}

	public boolean retainAll(Collection<?> c) {
		for(int i=0;i<size();i++){
			if(!c.contains(get(i))){
				remove(i);
			}
		}
		return true;
	}
	
	@Override
   public AbstractList<V> clone() {
	   return this.getNewInstance().with((Collection<?>)this);
   }
	public abstract AbstractList<V> with(Object... values);
    
	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST withList(Collection<?> values) {
		for (Iterator<?> i = values.iterator(); i.hasNext();) {
			with(i.next());
		}
		return (ST) this;
	}

    public int indexOf(Object o) {
        return keys.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return keys.lastIndexOf(o);
    }

    public ListIterator<V> listIterator() {
        return keys.listIterator();
    }

    public ListIterator<V> listIterator(int index) {
        return keys.listIterator(index);
	}
    
    public ListIterator<V> reverseListIterator() {
       return keys.listIterator(keys.size());
    }
	
	public int size() {
		return this.keys.size();
	}

	protected void fireProperty(Object oldElement, Object newElement, Object beforeElement, Object value){
	}
}