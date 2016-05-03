package de.uniks.networkparser.ext.generic;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
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
