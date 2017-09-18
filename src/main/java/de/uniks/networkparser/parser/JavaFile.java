package de.uniks.networkparser.parser;

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
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class JavaFile {
	public static final char EOF = Character.MIN_VALUE;
	public static final char COMMENT_START = 'c';
	public static final char LONG_COMMENT_END = 'd';
	public static char NEW_LINE = '\n';
	private CharSequence content;
	private SimpleKeyValueList<String, SimpleList<SymTabEntry>> keys=new SimpleKeyValueList<String, SimpleList<SymTabEntry>>();
	private boolean fileBodyHasChanged = false;
	private int endPos;
	

	public JavaFile(CharSequence content) {
		this.content = content;
		this.endPos = content.length();

	}
	
	public ParserEntity parse() {
		this.keys.clear();
		return new ParserEntity(this);
	}

	public CharSequence subString(int start, int end) {
		return content.subSequence(start, end);
	}
	
	public boolean isFileBodyHasChanged() {
		return fileBodyHasChanged;
	}

	public boolean setFileBodyHasChanged(boolean value) {
		if(value != this.fileBodyHasChanged) {
			this.fileBodyHasChanged = value;
			return true;
		}
		return false;
	}
	
	public int getSize() {
		return endPos;
	}

	public CharSequence getContent() {
		return content;
	}

	public SimpleList<SymTabEntry> getSymbolEntries(String type) {
		SimpleList<SymTabEntry> list = keys.get(type);
		if(list == null) {
			list = new SimpleList<SymTabEntry>();
			keys.add(type, list);
		}
		return list;
	}
}
