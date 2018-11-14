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
	private ArrayList<Collection<Integer>> collections=new ArrayList<Collection<Integer>>();
	
	public static void main(String[] args) {
		CollectionsFactory collection = new CollectionsFactory();
		System.out.println(collection.toString());
	}
	
	public CollectionsFactory() {
		collections.add(new ArrayList<Integer>());
		collections.add(new ArrayDeque<Integer>());
		collections.add(new LinkedList<Integer>());
		
		collections.add(new GapList<Integer>());
		collections.add(new BigList<Integer>());
		collections.add(new IntObjGapList());
		
		collections.add(new SimpleList<Integer>());
		collections.add(new SpeedList<Integer>());
		
		collections.add(new BasicEList<Integer>());
		
		collections.add(new EObjectResolvingInteger());

		collections.add(new HashSet<Integer>());
		collections.add(new LinkedHashSet<Integer>());
		collections.add(new TreeSet<Integer>());
		collections.add(new SimpleSet<Integer>());
	}
//	private void setup(int value) {
//		for(int i=0;i<value;i++) {
////			list.add(list.size());
//		}
//	}
	public Collection<Integer> getInstance(int count) {
		if(count>=0 && count<=collections.size()) {
			Collection<Integer> collection = collections.get(count);
			collection.clear();
			return collection;
		}
		return null;
	}
	public Collection<Integer> getInstance(String name) {
		for(int i=0;i<collections.size();i++) {
			Collection<Integer> collection = collections.get(i);
			if(collection.getClass().getSimpleName().equals(name)) {
				collection.clear();
				return collection;
			}
		}
		return null;
	}
	
	
	public Collection<Integer> getInstance(String name, int size) {
		for(int i=0;i<collections.size();i++) {
			Collection<Integer> collection = collections.get(i);
			if(collection.getClass().getSimpleName().equals(name)) {
				collection.clear();
				for(int c=0;c<size;c++) {
					collection.add(collection.size());
				}
				return collection;
			}
		}
		return null;
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

	public ArrayList<Collection<Integer>> getCollections() {
		return collections;
	}
}
 