package de.uniks.jism.yuml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import de.uniks.jism.interfaces.BaseEntityList;
import de.uniks.jism.interfaces.JISMEntity;

public class YUMLList implements BaseEntityList{
	private LinkedHashMap<String, YUMLEntity> children=new LinkedHashMap<String, YUMLEntity>();
	private int typ;
	private ArrayList<Cardinality> cardinalityValues=new ArrayList<Cardinality>();


	@Override
	public BaseEntityList initWithMap(Collection<?> value) {
		for(Iterator<?> i = value.iterator();i.hasNext();){
			Object item = i.next();
			if(item instanceof YUMLEntity){
				YUMLEntity entity = (YUMLEntity) item;
				children.put(entity.getId(), entity);
			}
		}
		return this;
	}

	@Override
	public BaseEntityList put(Object value) {
		if(value instanceof YUMLEntity){
			YUMLEntity entity = (YUMLEntity) value;
			children.put(entity.getId(), entity);
		}
		return this;
	}

	@Override
	public int size() {
		return children.size();
	}

	@Override
	public boolean add(Object value) {
		if(value instanceof YUMLEntity){
			YUMLEntity entity = (YUMLEntity) value;
			children.put(entity.getId(), entity);
			return true;
		}
		return false;
	}
	@Override
	public Object get(int z) {
		Iterator<Entry<String, YUMLEntity>> iterator = children.entrySet().iterator();
		while(z>0&&iterator.hasNext()){
			iterator.next();
		}
		if(z==0){
			return iterator.next().getValue();
		}
		return null;
	}
	
	public YUMLEntity getById(String id) {
		return children.get(id);
	}

	@Override
	public BaseEntityList getNewArray() {
		return new YUMLList();
	}

	@Override
	public JISMEntity getNewObject() {
		return new YUMLEntity();
	}
	
	@Override
	public String toString() {
		return toString(0, 0);
	}

	@Override
	public String toString(int indentFactor) {
		return toString(0, 0);
	}

	@Override
	public String toString(int indentFactor, int intent) {
		if (children.size() > 0) {
			StringBuilder sb=new StringBuilder();
			Iterator<YUMLEntity> i=children.values().iterator();
			
			HashMap<String, HashSet<Cardinality>> links = new HashMap<String, HashSet<Cardinality>>();
			String[] items=new String[2];
			for(Cardinality element : cardinalityValues){
				if(typ==YUMLIdParser.OBJECT){
					items[0] = element.getSourceID();
					items[1] = element.getTargetID();
				}else{
					items[0] = element.getSourceClazz();
					items[1] = element.getTargetClazz();
				}
				for(int z=0;z<2;z++){
					if(links.containsKey(items[z])){
						links.get(items[z]).add(element.reset());
					}else{
						HashSet<Cardinality> hashSet = new HashSet<Cardinality>();
						hashSet.add(element.reset());
						links.put(items[z], hashSet);
					}
				}
			}
			
			HashSet<YUMLEntity> visitedObj=new HashSet<YUMLEntity>();
			
			parse(i.next(), sb, visitedObj, links);
			while (i.hasNext()) {
				parse(i.next(), sb, visitedObj, links);
			}
			return sb.toString();
		}
		return null;
	}
	
	public void parse(YUMLEntity item, StringBuilder sb, HashSet<YUMLEntity> visited, HashMap<String, HashSet<Cardinality>> links){
		String key;
		if(typ==YUMLIdParser.OBJECT){
			key = item.getId();
		}else{
			key = item.getClassName();
		}
		HashSet<Cardinality> showedLinks = links.get(key);
		if(showedLinks==null){
			sb.append( item.toString(typ, visited.contains(item)) );
			visited.add(item);
			return;
		}
		Iterator<Cardinality> iterator = showedLinks.iterator();
		if(iterator.hasNext()){
			Cardinality entry = iterator.next();
			while(iterator.hasNext()&&entry.isShowed()){
				entry = iterator.next();
			}
			if(!entry.isShowed()){
				if(sb.length()>0){
					sb.append( "," );
				}
				sb.append( item.toString(typ, visited.contains(item)) );
				visited.add(item);
				sb.append( "-" );
				YUMLEntity target = children.get( entry.getTargetID() );
				sb.append(target.toString(typ, visited.contains(target)) );
				visited.add(target);
				entry.withShowed(true);
				while(iterator.hasNext()){
					sb.append( "," );
					sb.append( item.toString(typ, true) );
					sb.append( "-" );
					visited.add(item);
					sb.append( item.toString(typ, visited.contains(item)) );
					entry.withShowed(true);
				}
			}
		}
	}

	@Override
	public JISMEntity withVisible(boolean value) {
		return this;
	}

	@Override
	public boolean isVisible() {
		return true;
	}
	
	public int getTyp() {
		return typ;
	}

	public YUMLList withTyp(int typ) {
		this.typ = typ;
		return this;
	}

	public boolean addCardinality(Cardinality cardinality) {
		return this.cardinalityValues.add(cardinality);
	}
}
