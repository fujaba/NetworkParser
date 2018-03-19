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
import de.uniks.networkparser.graph.Annotation;
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

	public AnnotationSet hasName(String otherValue) {
		return filter(Annotation.NAME.equals(otherValue));
	}
	
	@Override
	public AnnotationSet getNewList(boolean keyValue) {
		return new AnnotationSet();
	}
}
