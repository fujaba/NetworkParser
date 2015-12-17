package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.Tree;

public class TreeCreator implements SendableEntityCreator {
	   private final String[] properties = new String[] {Tree.PROPERTY_NAME, Tree.PROPERTY_PERSON};

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (Tree.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return ((Tree) entity).getName();
		}
		if (Tree.PROPERTY_PERSON.equalsIgnoreCase(attribute)) {
			return ((Tree) entity).getPerson();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attrName, Object value, String type) {
		 if (AppleTree.PROPERTY_NAME.equalsIgnoreCase(attrName))
	      {
	         ((AppleTree) entity).setName(""+value);
	         return true;
	      }
		 if (AppleTree.PROPERTY_PERSON.equalsIgnoreCase(attrName))
	      {
	         ((AppleTree) entity).setPerson((Person)value);
	         return true;
	      }
		 return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return Tree.class;
	}

}
