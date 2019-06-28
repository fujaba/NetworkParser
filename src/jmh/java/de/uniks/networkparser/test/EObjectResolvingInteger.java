package de.uniks.networkparser.test;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.xml.type.impl.AnyTypeImpl;

public class EObjectResolvingInteger extends EObjectResolvingEList<EInt>{
	private static final long serialVersionUID = 1L;

	public EObjectResolvingInteger() {
		super(EInt.class, new AnyTypeImpl() {
			
		}, 1);
	}
	

	public boolean add(EInt object)
	  {
	      addUnique(object);
	      return true;
	  }
	@Override
	public String toString() {
		return super.toString();
	}
}
