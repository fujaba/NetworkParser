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
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class ClazzSet extends SimpleSet<Clazz> {
	public AttributeSet getAttributes() {
		AttributeSet collection = new AttributeSet();
		for (Clazz item : this) {
			collection.addAll(item.getAttributes());
		}
		return collection;
	}

	public AssociationSet getAssociations() {
		AssociationSet collection = new AssociationSet();
		for (Clazz item : this) {
			collection.addAll(item.getAssociations());
		}
		return collection;
	}

	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for (Clazz item : this) {
			collection.addAll(item.getMethods());
		}
		return collection;
	}

	public AnnotationSet getAnnotations() {
		AnnotationSet collection = new AnnotationSet();
		for (Clazz item : this) {
			collection.add(item.getAnnotation());
		}
		return collection;
	}

	public ModifierSet getModifiers() {
		ModifierSet collection = new ModifierSet();
		for (Clazz item : this) {
			collection.add(item.getModifier());
		}
		return collection;
	}

	@Override
	public ClazzSet filter(Condition<Clazz> newValue) {
		ClazzSet collection = new ClazzSet();
		filterItems(collection, newValue);
		return collection;
	}

	public ClazzSet hasName(String otherValue) {
		return filter(Clazz.NAME.equals(otherValue));
	}

	@Override
	public ClazzSet without(Object... values) {
		super.without(values);
		return this;
	}
}
