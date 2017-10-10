package de.uniks.networkparser.graph;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.list.SimpleList;
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
/**
 * Annotation of Methods or Attributes or Classes
 *
 * @author Stefan Lindel
 *
 */
public class Annotation extends GraphMember {
	public static final StringFilter<Annotation> NAME = new StringFilter<Annotation>(GraphMember.PROPERTY_NAME);

	// ==========================================================================
	public static final Annotation DEPRECATED = new Annotation("Deprecated");

	// ==========================================================================
	public static final Annotation OVERRIDE = new Annotation("Override");

	// ==========================================================================
	public static final Annotation SAFE_VARGARGS = new Annotation("SafeVarargs");

	// ==========================================================================
	public static final Annotation SUPPRESS_WARNINGS = new Annotation("SuppressWarnings");

	private SimpleList<Annotation> value;
	private boolean keyValue;
	private Annotation nextAnnotaton;

	Annotation() {
	}

	public Annotation(String name) {
		super.with(name);
	}

	public static Annotation create(String value) {
		Annotation annotation = new Annotation();
		annotation.decode(value);
		return annotation;
	}

	public Annotation newInstance() {
		Annotation annotation = new Annotation();
		annotation.decode(this.name);
		return annotation;
	}

	//Redirect
	@Override
	public Annotation with(String name) {
		super.with(name);
		return this;
	}

	public Annotation decode(String value) {
		CharacterBuffer tokener = new CharacterBuffer();
		tokener.with(value);
		decode(tokener, (char)0, null);
		return this;
	}

	protected Annotation addValue(Annotation... values) {
		if(values==null) {
			return this;
		}
		if(this.value == null) {
			this.value = new SimpleList<Annotation>();
		}
		for(Annotation item : values) {
			if(item != null) {
				this.value.add(item);
			}
		}
		return this;
	}

	public Annotation decode(BufferItem tokener, char endTag, Annotation parent) {
		char item = tokener.getCurrentChar();
		CharacterBuffer token=new CharacterBuffer();
		boolean charCount=false;
		while(item!=0 && item != endTag) {
			if(item=='"') {
				charCount=!charCount;
			}
			if(charCount) {
				token.with(item);
				item = tokener.getChar();
				continue;
			}
			if( item == ' ') {
				item = tokener.getChar();
				continue;
			}
			// Subannotation
			if(item == '(' ) {
				this.name = token.toString();
				tokener.skip();
				Annotation child = new Annotation();
				addValue(child);
				child.decode(tokener, ')', this);
				return this;
			} else if( item == '{' ) {
				this.name = token.toString();
				tokener.skip();
				decode(tokener, '}', parent);
				return this;
			} else if( item == '=' ) {
				this.name = token.toString();
				this.keyValue = true;
				tokener.skip();
				Annotation child = new Annotation();
				addValue(child);
				child.decode(tokener, endTag, parent);
				item = tokener.getCurrentChar();
				if(item!=',') {
					break;
				}
			}
			if( item == ',' ) {
				this.name = token.toString();
				tokener.skip();
				if(parent != null) {
					Annotation child = new Annotation();
					parent.addValue(child);
					child.decode(tokener, endTag, parent);
				}
				break;
			}
			token.with(item);
			item = tokener.getChar();

			if( item == '@' ) {
				this.name = token.toString();
				this.nextAnnotaton = new Annotation().decode(tokener, (char)0, null);
				return this;
			}
		}
		if(item==0 || item == endTag ) {
			this.name = token.toString();
		}
		return this;
	}

	public SimpleList<Annotation> getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append(this.name);
		if(value==null) {
			return sb.toString();
		}
		if(keyValue && value.size()==1) {
			sb.append("=");
			sb.append(value.first().toString());
			return sb.toString();
		}
		sb.append("(");
		if(value.size()>0) {
			sb.append(value.first());
		}
		for(int i=1;i<value.size();i++) {
			sb.append(",");
			sb.append(value.get(i));
		}
		sb.append(")");
		return sb.toString();
	}

	public boolean hasNext() {
		return nextAnnotaton != null;
	}

	public Annotation next() {
		return nextAnnotaton;
	}

	public Annotation getAnnotation(String key) {
		if(key==null) {
			return null;
		}
		if(key.equalsIgnoreCase(getName())) {
			return this;
		}
		if(nextAnnotaton == null) {
			return null;
		}
		return nextAnnotaton.getAnnotation(key);
	}

	public GraphMember getParent() {
		return (GraphMember) parentNode;
	}
}
