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
public class GraphAnnotation implements IdMapDecoder, GraphMember{
	// ==========================================================================
	public static final String DEPRECATED = "Deprecated";

	// ==========================================================================
	public static final String OVERRIDE = "Override";

	// ==========================================================================
	public static final String SUPPRESS_WARNINGS = "SuppressWarnings";

	private String id;
	private SimpleList<GraphAnnotation> value;
	private boolean keyValue;
	private GraphAnnotation nextAnnotaton;
	private GraphNode parentNode;

	public static GraphAnnotation create(String value) {
		GraphAnnotation annotation = new GraphAnnotation();
		annotation.decode(value);
		return annotation;
	}

	@Override
	public Object decode(BaseItem value) {
		return null;
	}

	@Override
	public GraphAnnotation decode(String value) {
		StringTokener tokener = new StringTokener();
		tokener.withBuffer(value);
		decode(tokener, (char)0, null);
		return this;
	}
	
	GraphAnnotation addValue(GraphAnnotation... values) {
		if(values==null) {
			return this;
		}
		if(this.value == null) {
			this.value = new SimpleList<GraphAnnotation>();
		}
		for(GraphAnnotation item : values) {
			if(item != null) {
				this.value.add(item);
			}
		}
		return this;
	}
	
	public GraphAnnotation decode(StringTokener tokener, char endTag, GraphAnnotation parent) {
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
				this.id = tokener.getToken(this.id);
				item = tokener.getCurrentChar();
			}
			// Subannotation
			if(item == '(' ) {
				this.id = tokener.getToken(this.id);
				GraphAnnotation child = new GraphAnnotation();
				addValue(child);
				child.decode(tokener, ')', this);
				return this;
			} else if( item == '{') {
				
				this.id = tokener.getToken(this.id);
//				GraphAnnotation child = new GraphAnnotation().decode(tokener, '}', parent);
				decode(tokener, '}', parent);
				return this;
//				return child;
			} else if( item == '='  ) {
				this.id = tokener.getToken(this.id);
				this.keyValue = true;
				GraphAnnotation child = new GraphAnnotation();
				addValue(child);
				child.decode(tokener, endTag, parent);
				item = tokener.getCurrentChar();
				if(item!=',') {
					break;
				}
			}
			if( item == ','  ) {
				this.id = tokener.getToken(this.id);
				if(parent != null) {
					GraphAnnotation child = new GraphAnnotation();
					parent.addValue(child);
					child.decode(tokener, endTag, parent);
				}
				break;
			}
			item = tokener.next();
			
			if( item == '@' ) {
				this.id = tokener.getToken(this.id);
				tokener.back();
				this.nextAnnotaton = new GraphAnnotation().decode(tokener, (char)0, null);
				return this;
			}

		}
		if(item==0 || item == endTag ) {
			this.id = tokener.getToken(this.id);
		}
		return this;
	}

	public String getId() {
		return id;
	}

	public GraphAnnotation withId(String name) {
		this.id = name;
		return this;
	}

	public SimpleList<GraphAnnotation> getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append(this.id);
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
	
	public GraphAnnotation next() {
		return nextAnnotaton;
	}
	public String getValue(String key) {
		return getValue(key, null);
	}
	public String getValue(String key, String defaultText) {
		if(key == null || value == null) {
			return defaultText;
		}
		if( keyValue && value.size() == 1) { 
			if(key.equalsIgnoreCase(getId())) {
				return value.first().getId();
			}else{
				return defaultText;
			}
		}
		for(GraphAnnotation item : value) {
			String result = item.getValue(key, defaultText);
			if(result != defaultText) {
				return result;
			}
		}
		return defaultText;
	}
	
	public GraphAnnotation getAnnotation(String key) {
		if(key==null) {
			return null;
		}
		if(key.equalsIgnoreCase(getId())) {
			return this;
		}
		if(nextAnnotaton == null) {
			return null;
		}
		return nextAnnotaton.getAnnotation(key);
	}

	@Override
	public GraphAnnotation withParent(GraphNode value) {
		if (this.parentNode != value) {
			GraphNode oldValue = this.parentNode;
			if (this.parentNode != null) {
				this.parentNode = null;
				oldValue.without(this);
			}
			this.parentNode = value;
			if (value != null) {
				value.with(this);
			}
		}
		return this;
	}
}
