package de.uniks.networkparser.graph.util;

import de.uniks.networkparser.buffer.CharacterBuffer;
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
import de.uniks.networkparser.graph.Clazz;
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

	public ClazzSet hasName(String otherValue) {
		return filter(Clazz.NAME.equals(otherValue));
	}

	public String toString(String splitter) {
		if(size() == 0) {
			return null;
		}
		CharacterBuffer buffer = new CharacterBuffer();
		for(Clazz clazz : this) {
			if(buffer.length()>0) {
				buffer.with(splitter);
			}
			buffer.with(clazz.getName());
		}
		return buffer.toString();
	}
	
	@Override
	public Clazz[] toArray() {
		return super.toArray(new Clazz[size()]);
	}
	
	@Override
	public ClazzSet getNewList(boolean keyValue) {
		return new ClazzSet();
	}
}
