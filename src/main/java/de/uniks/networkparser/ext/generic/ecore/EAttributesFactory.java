package de.uniks.networkparser.ext.generic.ecore;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class EAttributesFactory implements SendableEntityCreator{
	private Object value;

	public EAttributesFactory(Object value) {
		this.value = value;
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
	
	public String getName() {
		return ""+ReflectionLoader.call("getName", this.value);
	}

	public EClassifierFactory getEType() {
		return new EClassifierFactory(ReflectionLoader.call("getEType", this.value));
	}

}
