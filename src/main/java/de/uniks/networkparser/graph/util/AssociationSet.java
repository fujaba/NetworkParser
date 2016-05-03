package de.uniks.networkparser.graph.util;

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
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;
public class AssociationSet extends SimpleSet<Association>

{
	public static final AssociationSet EMPTY_SET = new AssociationSet();
   
   public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for(Association item : this) {
			collection.add(item.getClazz());
		}
		return collection;
	}

	public AssociationSet getOther() {
		AssociationSet collection = new AssociationSet();
		for(Association item : this) {
			collection.add(item.getOther());
		}
		return collection;
	}

	public ClazzSet getOtherClazz() {
		ClazzSet collection = new ClazzSet();
		for(Association item : this) {
			collection.add(item.getOtherClazz());
		}
		return collection;
	}
	
	@Override
	public boolean add(Association newValue) {
		if(newValue.getOther() != null) {
			if(indexOf(newValue.getOther()) >= 0) {
				return false;
			}
		}
		return super.add(newValue);
	}
	
	@Override
	public AssociationSet filter(Condition<Association> newValue) {
		AssociationSet collection = new AssociationSet();
		filterItems( collection, newValue);
		return collection;
	}

	public AssociationSet hasName(String otherValue) {
		return filter(Association.NAME.equals(otherValue));
	}
}
