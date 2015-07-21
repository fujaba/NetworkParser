package de.uniks.networkparser.emf;

import de.uniks.networkparser.StringTokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.IdMapDecoder;
import de.uniks.networkparser.list.SimpleList;

public class Annotation implements IdMapDecoder{
	private String name;
	private SimpleList<Annotation> value;
	private Annotation keyvalue;
	private Annotation nextAnnotaton;

	public static Annotation create(String value) {
		Annotation annotation = new Annotation();
		annotation.decode(value);
		return annotation;
	}

	@Override
	public Object decode(BaseItem value) {
		return null;
	}

	@Override
	public Annotation decode(String value) {
		StringTokener tokener = new StringTokener();
		tokener.withText(value);
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
			this.value.add(item);
		}
		return this;
	}
	
	public Annotation decode(StringTokener tokener, char endTag, Annotation parent) {
		int pos = tokener.position();
		boolean charCount=tokener.getCurrentChar()=='"';
		char item = tokener.next();
			
		while(item!=0 && item != endTag) {
			if(item=='"') {
				charCount=!charCount;
			}
			if(charCount) {
				item = tokener.next();
				continue;
			}

			// Subannotation
			if(item == '(' ) {
				this.name = tokener.substring(pos, tokener.position());
				tokener.nextClean();
				addValue(new Annotation().decode(tokener, ')', this));
				return this;
			} else if( item == '{') {
				this.name = tokener.substring(pos, tokener.position());
				tokener.nextClean();
				addValue(new Annotation().decode(tokener, '}', this));
				return this;
			} else if( item == ' ') {
				this.name = tokener.substring(pos, tokener.position());
				item = tokener.nextClean();
				break;
			} else if( item == ','  || item == '=') {
				this.name = tokener.substring(pos, tokener.position());
				break;
			}
			item = tokener.next();
		}
		if(item == '=') {
			// Key Value
			tokener.nextClean();
			this.keyvalue = new Annotation().decode(tokener, endTag, parent);
		}else if(item == ',') {
			// Must be a list
			tokener.nextClean();
			if(parent!=null) {
				parent.addValue(new Annotation().decode(tokener, endTag, this));
			}
//			addValue(this);
		}else if(item == '(' ) {
			tokener.nextClean();
			addValue(new Annotation().decode(tokener, ')', this));
			return this;
		} else if( item == '{') {tokener.nextClean();
			tokener.nextClean();
			addValue(new Annotation().decode(tokener, '}', this));
			return this;
		}
		if(item==0 || item == endTag ) {
			this.name = tokener.substring(pos, tokener.position());
		}
		return this;
	}

	public String getName() {
		return name;
	}

	public Annotation withName(String name) {
		this.name = name;
		return this;
	}

	public SimpleList<Annotation> getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append(this.name);
		if(value == null && keyvalue == null) {
			return sb.toString();
		}
		if(keyvalue != null) {
			sb.append("=");
			sb.append(keyvalue.toString());
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
}
