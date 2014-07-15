package de.uniks.networkparser;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractEntityList<V> extends AbstractList<V> implements List<V>{
	public boolean addAll(int index, Collection<? extends V> c) {
    	for(Iterator<? extends V> i = c.iterator();i.hasNext();){
    		V item = i.next();
    		add(index++, item);
    	}
    	return true;
    }
    
	public boolean add(Iterator<? extends V> list){
		while(list.hasNext()){
			V item = list.next();
			if(item!=null){
				if(!addEntity(item)){
					return false;
				}	
			}
		}
		return true;
	}
	
	public boolean addAll(Collection<? extends V> list){
		return add(list.iterator());
	}
	
	public void add(int index, V element) {
    	if( ! contains(element) ){
    		keys.add(index, element);
    		hashTableAdd(element, index);
    		V beforeValue = null;
    		if(index>0){
    			beforeValue = get(index - 1);
    			fireProperty(null, element, beforeValue, null);
    		}
    	}
    }
	
	    /**
     * Add a Element after the Element from the second Parameter
     * @param element element to add
     * @param beforeElement element before the element
     * @return the List
     */
    public AbstractEntityList<V> with(V element, V beforeElement) {
    	int index = getIndex(beforeElement);
    	add(index, element);
    	return this;
    }

	@Override
	@SuppressWarnings("unchecked")
	public AbstractList<V> with(Object... values) {
		if(values==null){
			return this;
		}
		for(Object item : values){
			this.addEntity((V)item);
		}
		return this;
	}
    
	public Collection<V> values() {
		return keys;
	}
}
