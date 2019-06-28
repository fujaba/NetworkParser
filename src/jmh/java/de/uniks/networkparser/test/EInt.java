package de.uniks.networkparser.test;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;

public class EInt extends EObjectImpl implements Cloneable, EObject, Comparable<EInt>{
	private int value;
	
	public EInt(int value) {
		this.value = value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	@Override
	public int compareTo(EInt o) {
		if(o==null) {
			return 1;
		}
		Integer i2 =o.getValue();
		Integer i1 =getValue();
		return i1.compareTo(i2);
	}
}
