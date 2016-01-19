package de.uniks.networkparser.graph;

import de.uniks.networkparser.StringTokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.IdMapDecoder;
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
public class Annotation extends GraphMember implements IdMapDecoder {
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
	
	//Redirect
	@Override
	public Annotation with(String name) {
		super.with(name);
		return this;
	}

	@Override
	public Object decode(BaseItem value) {
		return null;
	}

	@Override
	public Annotation decode(String value) {
		StringTokener tokener = new StringTokener();
		tokener.withBuffer(value);
		decode(tokener, (char)0, null);
		return this;
	}
	
	Annotation addValue(Annotation... values) {
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
	
	public Annotation decode(StringTokener tokener, char endTag, Annotation parent) {
		tokener.startToken();
		char item = tokener.getCurrentChar();
		boolean charCount=false;
		while(item!=0 && item != endTag) {
			if(item=='"') {
				charCount=!charCount;
			}
			if(charCount) {
				item = tokener.next();
				continue;
			}
			if( item == ' ') {
				this.name = tokener.getToken(this.name);
				item = tokener.getCurrentChar();
			}
			// Subannotation
			if(item == '(' ) {
				this.name = tokener.getToken(this.name);
				Annotation child = new Annotation();
				addValue(child);
				child.decode(tokener, ')', this);
				return this;
			} else if( item == '{') {
				
				this.name = tokener.getToken(this.name);
				decode(tokener, '}', parent);
				return this;
			} else if( item == '='  ) {
				this.name = tokener.getToken(this.name);
				this.keyValue = true;
				Annotation child = new Annotation();
				addValue(child);
				child.decode(tokener, endTag, parent);
				item = tokener.getCurrentChar();
				if(item!=',') {
					break;
				}
			}
			if( item == ','  ) {
				this.name = tokener.getToken(this.name);
				if(parent != null) {
					Annotation child = new Annotation();
					parent.addValue(child);
					child.decode(tokener, endTag, parent);
				}
				break;
			}
			item = tokener.next();
			
			if( item == '@' ) {
				this.name = tokener.getToken(this.name);
				tokener.back();
				this.nextAnnotaton = new Annotation().decode(tokener, (char)0, null);
				return this;
			}

		}
		if(item==0 || item == endTag ) {
			this.name = tokener.getToken(this.name);
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

	public String getValue(String key, String defaultText) {
		if(key == null || value == null) {
			return defaultText;
		}
		if( keyValue && value.size() == 1) { 
			if(key.equalsIgnoreCase(getName())) {
				return value.first().getName();
			}else{
				return defaultText;
			}
		}
		for(Annotation item : value) {
			String result = item.getValue(key, defaultText);
			if(result != defaultText) {
				return result;
			}
		}
		return defaultText;
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
		return parentNode; 
	}
}
