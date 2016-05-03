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
import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class AnnotationSet extends SimpleSet<Annotation> {
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for(Annotation item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}
	public AttributeSet getAttributes() {
		AttributeSet collection = new AttributeSet();
		for(Annotation item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for(Annotation item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}
	@Override
	public AnnotationSet filter(Condition<Annotation> newValue) {
		AnnotationSet collection = new AnnotationSet();
		filterItems( collection, newValue);
		return collection;
	}

	public AnnotationSet hasName(String otherValue) {
		return filter(Annotation.NAME.equals(otherValue));
	}
}
