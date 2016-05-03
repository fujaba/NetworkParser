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
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class MethodSet extends SimpleSet<Method>{
	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for(Method item : this) {
			collection.add(item.getClazz());
		}
		return collection;
	}
	public ParameterSet getParameters() {
		ParameterSet collection = new ParameterSet();
		for(Method item : this) {
			collection.addAll(item.getParameter());
		}
		return collection;
	}
	public AnnotationSet getAnnotations() {
		AnnotationSet collection = new AnnotationSet();
		for(Method item : this) {
			collection.add(item.getAnnotation());
		}
		return collection;
	}
	public ModifierSet getModifiers() {
		ModifierSet collection = new ModifierSet();
		for(Method item : this) {
			collection.add(item.getModifier());
		}
		return collection;
	}

	public DateTypeSet getReturnTypes() {
		DateTypeSet collection = new DateTypeSet();
		for(Method item : this) {
			collection.add(item.getReturnType());
		}
		return collection;
	}

	@Override
	public MethodSet filter(Condition<Method> newValue) {
		MethodSet collection = new MethodSet();
		filterItems( collection, newValue);
		return collection;
	}

	public MethodSet hasName(String otherValue) {
		return filter(Method.NAME.equals(otherValue));
	}
}
