package de.uniks.networkparser.ext.generic.ecore;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class EReferencesFactory implements SendableEntityCreator {
	private Object value;

	public EReferencesFactory(Object item) {
		this.value = item;
	}
	
	@Override
	public String[] getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public EReferencesFactory getEOpposite() {
		return new EReferencesFactory(ReflectionLoader.call("getEOpposite", this.value));
	}
	
	public Object getValue() {
		return this.value;
	}

	public EClassFactory getEType() {
		return new EClassFactory(ReflectionLoader.call("getEType", this.value));
	}

	public int getUpperBound() {
		return (int)ReflectionLoader.call("getUpperBound", this.value);
	}

	public String getName() {
		return "" + ReflectionLoader.call("getName", this.value);
	}
}
