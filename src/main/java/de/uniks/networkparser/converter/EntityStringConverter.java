package de.uniks.networkparser.converter;

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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;

public class EntityStringConverter implements Converter {
	private int indentFactor;
	private int indent;
	public static final String EMPTY="";
	
	public EntityStringConverter() {
	}

	public EntityStringConverter(int indentFactor, int indent) {
		this.indentFactor = indentFactor;
		this.indent = indent;
	}
	public EntityStringConverter(int indentFactor) {
		this.indentFactor = indentFactor;
	}

	@Override
	public String encode(BaseItem entity) {
		if(entity instanceof Entity) {
			return ((Entity) entity).toString(getIndentFactor());
		}
		return ((BaseItem) entity).toString(this);
	}

	public int getIndentFactor() {
		return indentFactor;
	}

	public int getIndent() {
		return indent;
	}
	
	public String getStep() {
		char[] buf = new char[indentFactor];
		for (int i = 0; i < indentFactor; i++) {
			buf[i] = IdMap.SPACE;
		}
		return new String(buf);
	}
	public String getPrefixFirst() {
		if(indent == 0) {
			return EMPTY;
		}
		char[] buf = new char[indent + 2];
		buf[0] = '\r';
		buf[1] = '\n';
		for (int i = 0; i < indent; i++) {
			buf[i+2] = IdMap.SPACE;
		}
		return new String(buf);
	}

	public String getPrefix() {
		if(indent + indentFactor == 0) {
			return EMPTY;
		}
		char[] buf = new char[indent + 2];
		buf[0] = '\r';
		buf[1] = '\n';
		for (int i = 0; i < indent; i++) {
			buf[i+2] = IdMap.SPACE;
		}
		return new String(buf);
	}

	public void add() {
		this.indent = this.indent + this.indentFactor; 
	}
	public void minus() {
		this.indent = this.indent - this.indentFactor; 
	}
}
