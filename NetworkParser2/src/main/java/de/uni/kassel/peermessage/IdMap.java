package de.uni.kassel.peermessage;

import java.util.HashMap;

import de.uni.kassel.peermessage.interfaces.IdCounter;
import de.uni.kassel.peermessage.interfaces.SendableEntity;
import de.uni.kassel.peermessage.interfaces.SendableEntityCreator;

public class IdMap<T extends SendableEntityCreator> {
	
	private HashMap<Object, String> keys = new HashMap<Object, String>();
	private HashMap<String, Object> values = new HashMap<String, Object>();
	private HashMap<String, T> creators = new HashMap<String, T>();
	protected IdMap<T> parent;
	protected boolean isId = true;
	private IdCounter counter;

	public IdMap(){
		keys = new HashMap<Object, String>();
		values = new HashMap<String, Object>();
		creators = new HashMap<String, T>();
	}
	public IdMap(IdMap<T> parent){
		this.parent=parent;
	}

	public void setCounter(IdCounter counter){
		this.counter=counter;
	}
	public IdCounter getCounter(){
		if(counter==null){
			counter=new SimpleIdCounter();
		}
		return counter;
	}
	
	public void setSessionId(String sessionId) {
		getCounter().setPrefixId(sessionId);
	}
	

	// Key Value paar
	public String getKey(Object obj) {
		if(parent!=null){
			return parent.getKey(obj);
		}
		return keys.get(obj);
	}

	public Object getObject(String key) {
		if(parent!=null){
			return parent.getObject(key);
		}
		return values.get(key);
	}

	public String getId(Object obj) {
		if(!isId){
			return "";
		}
		if(parent!=null){
			return parent.getId(obj);
		}
		String key = keys.get(obj);
		if(key==null){
			key=getCounter().getId(obj);
		}
		put(key, obj);
		
		return key;
	}
	

	public void put(String jsonId, Object object) {
		if(parent!=null){
			parent.put(jsonId, object);
		}else{
			values.put(jsonId, object);
			keys.put(object, jsonId);
			if(object instanceof SendableEntity){
				((SendableEntity)object).setRemoveListener(new RemoveListener(this, object));
			}
		}
	}

	public boolean remove(Object oldValue) {
		if(parent!=null){
			return parent.remove(oldValue);
		}
		String key = getKey(oldValue);
		if (key != null) {
			keys.remove(key);
			values.remove(oldValue);
			return true;
		}
		return false;
	} 
	public boolean isId(){
		return isId;
	}


	public int size() {
		if(parent!=null){
			return parent.size();
		}
		return keys.size();
	}

	public T getCreatorClasses(String className) {
		if(parent!=null){
			return (T)parent.getCreatorClasses(className);
		}
		return creators.get(className);
	}

	public T getCreatorClass(Object reference) {
		if(parent!=null){
			return (T)parent.getCreatorClass(reference);
		}
		return creators.get(reference.getClass().getName());
	}
	
	public boolean addCreater(T createrClass) {
		if(parent!=null){
			return parent.addCreater(createrClass);
		}else{
			Object reference = createrClass.getSendableInstance(true);
			creators.put(reference.getClass().getName(), createrClass);
			return true;
		}
	}
}
