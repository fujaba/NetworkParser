package de.uniks.networkparser.converter;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;

public class EntityStringConverter implements Converter {
	private int indentFactor;
	private int indent;
	private String relativePath;
	public static final String EMPTY = "";

	public EntityStringConverter() {
	}

	public EntityStringConverter(int indentFactor, int indent) {
		this.indentFactor = indentFactor;
		this.indent = indent;
	}

	public EntityStringConverter(int indentFactor) {
		this.indentFactor = indentFactor;
	}

	public EntityStringConverter withPath(String path) {
		this.relativePath = path;
		return this;
	}

	public String getPath() {
		return relativePath;
	}

	@Override
	public String encode(BaseItem entity) {
		if (entity instanceof Entity) {
			return ((Entity) entity).toString(getIndentFactor());
		}
		if (entity != null) {
			return ((BaseItem) entity).toString(this);
		}
		return null;
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
		if (indent < 1) {
			return EMPTY;
		}
		char[] buf = new char[indent + 2];
		buf[0] = '\r';
		buf[1] = '\n';
		for (int i = 0; i < indent; i++) {
			buf[i + 2] = IdMap.SPACE;
		}
		return new String(buf);
	}

	public String getPrefix() {
		if (indent + indentFactor == 0) {
			return EMPTY;
		}
		char[] buf = new char[indent + 2];
		buf[0] = '\r';
		buf[1] = '\n';
		for (int i = 0; i < indent; i++) {
			buf[i + 2] = IdMap.SPACE;
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
