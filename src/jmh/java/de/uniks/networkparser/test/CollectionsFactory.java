package de.uniks.networkparser.test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import org.eclipse.emf.common.util.BasicEList;
import org.magicwerk.brownies.collections.BigList;
import org.magicwerk.brownies.collections.GapList;
import org.magicwerk.brownies.collections.primitive.IntObjGapList;

import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.SpeedList;

public class CollectionsFactory {
	private ArrayList<Collection<?>> collections=new ArrayList<Collection<?>>();
	
	public static void main(String[] args) {
		CollectionsFactory collection = new CollectionsFactory();
		System.out.println(collection.toString());
	}
	
	public CollectionsFactory() {
		collections.add(new ArrayList<Object>());
		collections.add(new ArrayDeque<Object>());
		collections.add(new LinkedList<Object>());
		
		collections.add(new GapList<Object>());
		collections.add(new BigList<Object>());
		collections.add(new IntObjGapList());
		
		collections.add(new SimpleList<Object>());
		collections.add(new SpeedList<Object>());
		
		collections.add(new BasicEList<EInt>());
		
		collections.add(new EObjectResolvingInteger());

		collections.add(new HashSet<Object>());
		collections.add(new LinkedHashSet<Object>());
		collections.add(new TreeSet<Object>());
		collections.add(new SimpleSet<Object>());
	}

	public Collection<?> getInstance(int count) {
		if(count>=0 && count<=collections.size()) {
			Collection<?> collection = collections.get(count);
			collection.clear();
			return collection;
		}
		return null;
	}
	public Collection<?> getInstance(String name) {
		for(int i=0;i<collections.size();i++) {
			Collection<?> collection = collections.get(i);
			if(collection.getClass().getSimpleName().equals(name)) {
				collection.clear();
				return collection;
			}
		}
		return null;
	}
	
	
	public Collection<?> getInstance(String name, int size) {
		for(int i=0;i<collections.size();i++) {
			Collection<?> collection = collections.get(i);
			if(collection.getClass().getSimpleName().equals(name)) {
				collection.clear();
				if(collection instanceof IntObjGapList) {
					IntObjGapList iObjCollection = (IntObjGapList)collection;
					
					for(int c=0;c<size;c++) {
						iObjCollection.add(collection.size());
					}
					return iObjCollection;
				}
				@SuppressWarnings("unchecked")
				Collection<Object> eList = (Collection<Object>)collection;
				for(int c=0;c<size;c++) {
					eList.add(new EInt(collection.size()));
				}
				return collection;
			}
		}
		return null;
	}
	
	public EInt[] getArray(int size) {
		EInt[] array = new EInt[size];
		for(int c=0;c<size;c++) {
			array[c] = new EInt(c);
		}
		return array;
	}
	
	
	public final String[] toArray() {
		ArrayList<String>  sb=new ArrayList<String>();
		for(int i=0;i<collections.size();i++) {
			sb.add(collections.get(i).getClass().getSimpleName());
		}
		return sb.toArray(new String[collections.size()]);
//		return collections.toArray(new String[collections.size()]);
	}
	
	public int size() {
		return collections.size();
	}
	
	public final String toString() {
		StringBuilder  sb=new StringBuilder("{");
		for(int i=0;i<collections.size();i++) {
			if(i>0) {
				sb.append(",");
			}
			sb.append("\""+collections.get(i).getClass().getSimpleName()+"\"");
		}
		sb.append("}");
		return sb.toString();
	}

	public ArrayList<Collection<?>> getCollections() {
		return collections;
	}

	public void setNewValue(Collection<?> collection, EInt[] array) {
		collection.clear();
		if(collection instanceof IntObjGapList) {
			IntObjGapList iObjCollection = (IntObjGapList)collection;
			for(int c=0;c<array.length;c++) {
				iObjCollection.add(c);
			}
			return;
		}
		@SuppressWarnings("unchecked")
		Collection<Object> eList = (Collection<Object>)collection;
		for(int i=0;i<array.length;i++) {
			eList.add(array[i]);
		}
	}
}
 