package de.uniks.networkparser.graph;

import de.uniks.networkparser.StringTokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.IdMapDecoder;
import de.uniks.networkparser.list.SimpleList;

public class GraphAnnotation implements IdMapDecoder{
	private String name;
	private SimpleList<GraphAnnotation> value;
	private boolean keyValue;
	private GraphAnnotation nextAnnotaton;

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
		tokener.withText(value);
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
			this.value.add(0, item);
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
				this.name = tokener.getToken(this.name);
				item = tokener.getCurrentChar();
			}
			// Subannotation
			if(item == '(' ) {
				this.name = tokener.getToken(this.name);
				addValue(new GraphAnnotation().decode(tokener, ')', this));
				return this;
			} else if( item == '{') {
				
				this.name = tokener.getToken(this.name);
				GraphAnnotation child = new GraphAnnotation().decode(tokener, '}', parent);
//				if(this.name.length()<1 && child.getValue() == null) {
//					return child;
//				}
				return child;
			} else if( item == '='  ) {
				this.name = tokener.getToken(this.name);
				this.keyValue = true;
				this.addValue(new GraphAnnotation().decode(tokener, endTag, parent));
			} else if( item == ','  ) {
				this.name = tokener.getToken(this.name);
				if(parent != null) {
					parent.addValue(new GraphAnnotation().decode(tokener, endTag, parent));
				}
				break;
			}
			item = tokener.next();
			
			if( item == '@' ) {
				this.name = tokener.getToken(this.name);
				tokener.back();
				this.nextAnnotaton = new GraphAnnotation().decode(tokener, (char)0, null);
				return this;
			}

		}
		if(item==0 || item == endTag ) {
			this.name = tokener.getToken(this.name);
		}
		return this;
	}

	public String getName() {
		return name;
	}

	public GraphAnnotation withName(String name) {
		this.name = name;
		return this;
	}

	public SimpleList<GraphAnnotation> getValue() {
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
	
	public GraphAnnotation next() {
		return nextAnnotaton;
	}
	
	public String getValue(String key) {
		if(key == null || value != null) {
			return null;
		}
		if( keyValue && value.size() == 1) { 
			if(key.equalsIgnoreCase(getName())) {
				return value.first().getName();
			}else{
				return null;
			}
		}
		for(GraphAnnotation item : value) {
			String result = item.getValue(key);
			if(result != null) {
				return result;
			}
		}
		return null;
	}
}
