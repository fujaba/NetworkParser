package de.uniks.jism.yuml;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import de.uniks.jism.interfaces.BaseEntityList;
import de.uniks.jism.interfaces.JISMEntity;

public class YUMLList implements BaseEntityList{
	private LinkedHashMap<String, YUMLEntity> children=new LinkedHashMap<String, YUMLEntity>();
	private int typ;

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
	public String toString(int indentFactor) {
		return toString(0, 0);
	}

	@Override
	public String toString(int indentFactor, int intent) {
		if (children.size() > 0) {
			Iterator<YUMLEntity> i=children.values().iterator();
			YUMLEntity item = i.next();
			String result = item.toString(indentFactor, intent);
			while (i.hasNext()) {
				result += "," + i.next().toString(indentFactor, intent, typ);
			}
			return result;
		}
		return null;
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
}
