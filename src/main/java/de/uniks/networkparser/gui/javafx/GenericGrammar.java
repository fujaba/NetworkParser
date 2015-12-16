package de.uniks.networkparser.gui.javafx;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.Grammar;

public class GenericGrammar extends Grammar {
	@Override
	public Object getNewEntity(SendableEntityCreator creator, String className) {
		Object entity = creator.getSendableInstance(false);
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
}
