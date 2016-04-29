package de.uniks.networkparser.ext.generic;

import java.util.Iterator;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleGrammar;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class GenericJsonGrammar extends SimpleGrammar {
	@Override
	public SendableEntityCreator getSuperCreator(IdMap map, boolean searchForSuperCreator, Object modelItem) {
		if(modelItem == null && !searchForSuperCreator) {
			return null;
		}
		Class<?> search;
		if(modelItem instanceof Class<?>) {
			search = (Class<?>) modelItem;
		}else if(modelItem != null) {
			search = modelItem.getClass();
		} else {
			return null;
		}
		for(Iterator<SendableEntityCreator> i =map.iterator();i.hasNext();){
			SendableEntityCreator item = i.next();
			Object prototyp = item.getSendableInstance(true);
			if(prototyp instanceof Class<?>) {
				if(((Class<?>)prototyp).isAssignableFrom(search)){
					return item;
				}
			}
		}
		return null;
	}

	@Override
	public Object getNewEntity(SendableEntityCreator creator, String className, boolean prototype) {
		Object entity = creator.getSendableInstance(prototype);
		if(entity instanceof Class<?> == false || className == null){
			return entity;
		}
		try {
			Class<?> forName = Class.forName(className);
			return forName.newInstance();
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}

	@Override
	protected Class<?> getClassForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
}
