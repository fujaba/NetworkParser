package de.uniks.networkparser.graph.util;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.list.SimpleSet;

public class MethodSet extends SimpleSet<Method>{
	public MethodSet() {
		this.withType(Method.class);
	}

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

	public MethodSet hasName(String otherValue) {
		return filter(Method.NAME.equals(otherValue));
	}
}
