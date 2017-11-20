package de.uniks.networkparser.ext.generic.ecore;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class EClassifierFactory implements SendableEntityCreator {
	protected Object value;
	
	public EClassifierFactory(Object value) {
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

	public String getInstanceClassName() {
		return ""+ReflectionLoader.call("getInstanceClassName", this.value);
	}
	
	public Object getValue() {
		return this.value;
	}

	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return true;
		}
		if(obj instanceof EClassifierFactory == false) {
			return false;
		}
		EClassifierFactory other = (EClassifierFactory) obj;
		return this.value.equals(other.getValue());
	}
}
