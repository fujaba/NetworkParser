package de.uniks.networkparser.ext.generic.ecore;

import java.util.List;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;

public class EPackageFactory implements SendableEntityCreator {
	public Object value;
	
	public EPackageFactory(Object value) {
		this.value = value;
	}
	
	@Override
	public String[] getProperties() {
		return null;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return ReflectionLoader.newInstance(ReflectionLoader.EPACKAGE);
	}

	public List<EClassifierFactory> getEClassifiers() {
		SimpleList<EClassifierFactory> items = new SimpleList<EClassifierFactory>();
		List<Object> callList = ReflectionLoader.callList("getEClassifiers", this.value);
		for(Object item : callList) {
			if(item != null) {
				items.add(new EClassifierFactory(item));
			}
		}
		return items;
	}
	public List<EClassFactory> getEClasses() {
		SimpleList<EClassFactory> items = new SimpleList<EClassFactory>();
		List<Object> callList = ReflectionLoader.callList("getEClassifiers", this.value);
		for(Object item : callList) {
			if(item != null && ReflectionLoader.ECLASS.isAssignableFrom(item.getClass())) {
				items.add(new EClassFactory(item));
			}
		}
		return items;
	}

}
