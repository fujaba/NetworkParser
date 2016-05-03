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
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class AttributeSet extends SimpleSet<Attribute>{
	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for(Attribute item : this) {
			collection.add(item.getClazz());
		}
		return collection;
	}
	public AnnotationSet getAnnotations() {
		AnnotationSet collection = new AnnotationSet();
		for(Attribute item : this) {
			collection.add(item.getAnnotation());
		}
		return collection;
	}

	public ModifierSet getModifiers() {
		ModifierSet collection = new ModifierSet();
		for(Attribute item : this) {
			collection.add(item.getModifier());
		}
		return collection;
	}

	public DateTypeSet getDataTypes() {
		DateTypeSet collection = new DateTypeSet();
		for(Attribute item : this) {
			collection.add(item.getType());
		}
		return collection;
	}

	@Override
	public AttributeSet filter(Condition<Attribute> newValue) {
		AttributeSet collection = new AttributeSet();
		filterItems( collection, newValue);
		return collection;
	}

	public AttributeSet hasName(String otherValue) {
		return filter(Attribute.NAME.equals(otherValue));
	}
}
