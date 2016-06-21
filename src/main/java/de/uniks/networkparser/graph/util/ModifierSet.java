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
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class ModifierSet extends SimpleSet<Modifier> {
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for(Modifier item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}
	public AttributeSet getAttributes() {
		AttributeSet collection = new AttributeSet();
		for(Modifier item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for(Modifier item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	@Override
	public ModifierSet filter(Condition<Modifier> newValue) {
		ModifierSet collection = new ModifierSet();
		filterItems( collection, newValue);
		return collection;
	}

	public ModifierSet hasName(String otherValue) {
		return filter(Modifier.NAME.equals(otherValue));
	}

	@Override
	public ModifierSet with(Object... values) {
		super.with(values);
		return this;
	}
}
