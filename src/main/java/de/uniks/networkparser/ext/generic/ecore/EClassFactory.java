package de.uniks.networkparser.ext.generic.ecore;

import java.util.List;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.list.SimpleList;

public class EClassFactory extends EClassifierFactory {
	
	public EClassFactory(Object value) {
		super(value);
	}
	
	public SimpleList<EAttributesFactory> getEAttributes() {
		SimpleList<EAttributesFactory> items = new SimpleList<EAttributesFactory>();
		List<Object> callList = ReflectionLoader.callList("getEAttributes", this.value);
		for(Object item : callList) {
			if(item != null) {
				items.add(new EAttributesFactory(item));
			}
		}
		return items;
	}

	public SimpleList<EClassFactory> getESuperTypes() {
		SimpleList<EClassFactory> list=new SimpleList<EClassFactory>();
		List<Object> callList = ReflectionLoader.callList("getESuperTypes", this.value);
		for(Object item : callList) {
			if(item != null) {
				list.add(new EClassFactory(item));
			}
		}
		return list;
	}
	
	public SimpleList<EReferencesFactory> getEReferences() {
		SimpleList<EReferencesFactory> list=new SimpleList<EReferencesFactory>();
		List<Object> callList = ReflectionLoader.callList("getEReferences", this.value);
		for(Object item : callList) {
			if(item != null) {
				list.add(new EReferencesFactory(item));
			}
		}
		return list;
	}
}
