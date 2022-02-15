package de.uniks.networkparser.graph;

/*
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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.SymTabEntry;

/**
 * The Class SourceCode.
 *
 * @author Stefan
 */
public class SourceCode extends GraphMember {
	private CharacterBuffer content;
	private SimpleKeyValueList<String, SimpleList<SymTabEntry>> keys = new SimpleKeyValueList<String, SimpleList<SymTabEntry>>();
	private boolean fileBodyHasChanged = false;
	private String fileName;
	private int size;
	private int startOfImports;
	private int endOfClassName;
	private int endOfExtendsClause;
	private int endOfImports;
	private int endOfImplementsClause;
	private int bodyStart;
	private int endOfAttributeInitialization;
	private int endOfBody;
	private long endofBodyLine;
	private long bodyStartLine;

	/**
	 * Instantiates a new source code.
	 */
	public SourceCode() {
		super();
	}

	/**
	 * With file name.
	 *
	 * @param name the name
	 * @return the source code
	 */
	public SourceCode withFileName(String name) {
		this.fileName = name;
		return this;
	}

	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public CharacterBuffer getContent() {
		if (content == null) {
			this.content = new CharacterBuffer();
		}
		return content;
	}

	/**
	 * With content.
	 *
	 * @param content the content
	 * @return the source code
	 */
	public SourceCode withContent(CharacterBuffer content) {
		this.content = content;
		if (content != null) {
			this.size = content.length();
		} else {
			this.size = 0;
		}
		return this;
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return size;
	}

	/**
	 * Gets the symbol entries.
	 *
	 * @param type the type
	 * @return the symbol entries
	 */
	public SimpleList<SymTabEntry> getSymbolEntries(String type) {
		SimpleList<SymTabEntry> list = keys.get(type);
		if (list == null) {
			list = new SimpleList<SymTabEntry>();
			keys.add(type, list);
		}
		return list;
	}

	/**
	 * Gets the symbol entry.
	 *
	 * @param type the type
	 * @param name the name
	 * @return the symbol entry
	 */
	public SymTabEntry getSymbolEntry(String type, String name) {
		if (name == null || type == null) {
			return null;
		}
		SimpleList<SymTabEntry> list = keys.get(type.toLowerCase());
		if (list != null) {
			for (SymTabEntry entry : list) {
				if (name.equals(entry.getName())) {
					return entry;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the symbol tab.
	 *
	 * @return the symbol tab
	 */
	public SimpleKeyValueList<String, SimpleList<SymTabEntry>> getSymbolTab() {
		return keys;
	}

	/**
	 * Set the Parent of Element.
	 *
	 * @param parent Set The Parent Element
	 * @return The Instance
	 */
	public GraphMember with(Clazz parent) {
		this.parentNode = parent;
		/* REMOVE OLD SOURCE CODE */
		if (parent == null) {
			return this;
		}
		GraphSimpleSet children = new GraphSimpleSet();
		children.withList(parent.getChildren());
		for (GraphMember item : children) {
			if (item instanceof SourceCode) {
				if (item != this) {
					parent.remove(item);
				}
			}
		}
		parent.withChildren(this);
		return this;
	}

	/**
	 * Checks if is file body has changed.
	 *
	 * @return true, if is file body has changed
	 */
	public boolean isFileBodyHasChanged() {
		return fileBodyHasChanged;
	}

	/**
	 * Sets the file body has changed.
	 *
	 * @param fileBodyHasChanged the new file body has changed
	 */
	public void setFileBodyHasChanged(boolean fileBodyHasChanged) {
		this.fileBodyHasChanged = fileBodyHasChanged;
	}

	/**
	 * Sub string.
	 *
	 * @param start the start
	 * @param end the end
	 * @return the char sequence
	 */
	public CharSequence subString(int start, int end) {
		if (content == null) {
			return null;
		}
		return content.subSequence(start, end);
	}

	/**
	 * Gets the end of class name.
	 *
	 * @return the end of class name
	 */
	public int getEndOfClassName() {
		return endOfClassName;
	}

	/**
	 * With end of class name.
	 *
	 * @param value the value
	 * @return the source code
	 */
	public SourceCode withEndOfClassName(int value) {
		this.endOfClassName = value;
		return this;
	}

	/**
	 * Gets the end of extends clause.
	 *
	 * @return the end of extends clause
	 */
	public int getEndOfExtendsClause() {
		return endOfExtendsClause;
	}

	/**
	 * With end of extends clause.
	 *
	 * @param value the value
	 * @return the source code
	 */
	public SourceCode withEndOfExtendsClause(int value) {
		this.endOfExtendsClause = value;
		return this;
	}

	/**
	 * Gets the end of imports.
	 *
	 * @return the end of imports
	 */
	public int getEndOfImports() {
		return endOfImports;
	}

	/**
	 * With end of imports.
	 *
	 * @param value the value
	 * @return the source code
	 */
	public SourceCode withEndOfImports(int value) {
		this.endOfImports = value;
		return this;
	}

	/**
	 * With end of implements clause.
	 *
	 * @param value the value
	 * @return the source code
	 */
	public SourceCode withEndOfImplementsClause(int value) {
		this.endOfImplementsClause = value;
		return this;
	}

	/**
	 * Gets the end of implements clause.
	 *
	 * @return the end of implements clause
	 */
	public int getEndOfImplementsClause() {
		return endOfImplementsClause;
	}

	/**
	 * With start body.
	 *
	 * @param value the value
	 * @param line the line
	 * @return the source code
	 */
	public SourceCode withStartBody(int value, long line) {
		this.bodyStart = value;
		this.bodyStartLine = line;
		return this;
	}

	/**
	 * Gets the body start line.
	 *
	 * @return the body start line
	 */
	public long getBodyStartLine() {
		return bodyStartLine;
	}

	/**
	 * Gets the start body.
	 *
	 * @return the start body
	 */
	public int getStartBody() {
		return bodyStart;
	}

	/**
	 * With end of attribute initialization.
	 *
	 * @param value the value
	 * @return the source code
	 */
	public SourceCode withEndOfAttributeInitialization(int value) {
		this.endOfAttributeInitialization = value;
		return this;
	}

	/**
	 * Gets the end of attribute initialization.
	 *
	 * @return the end of attribute initialization
	 */
	public int getEndOfAttributeInitialization() {
		return endOfAttributeInitialization;
	}

	/**
	 * Replace all.
	 *
	 * @param bodyStartPos the body start pos
	 * @param value the value
	 */
	public void replaceAll(int bodyStartPos, String value) {
		this.content.replace(bodyStartPos, bodyStartPos, value);
		this.fileBodyHasChanged = true;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return getContent().toString();
	}

	/**
	 * With end body.
	 *
	 * @param value the value
	 * @param line the line
	 * @return the source code
	 */
	public SourceCode withEndBody(int value, long line) {
		this.endOfBody = value;
		this.endofBodyLine = line;
		return this;
	}

	/**
	 * Gets the end of body.
	 *
	 * @return the end of body
	 */
	public int getEndOfBody() {
		return endOfBody;
	}

	/**
	 * Gets the endof body line.
	 *
	 * @return the endof body line
	 */
	public long getEndofBodyLine() {
		return endofBodyLine;
	}

	/**
	 * With start imports.
	 *
	 * @param value the value
	 * @return the source code
	 */
	public SourceCode withStartImports(int value) {
		this.startOfImports = value;
		return this;
	}

	/**
	 * Gets the start of imports.
	 *
	 * @return the start of imports
	 */
	public int getStartOfImports() {
		return startOfImports;
	}
}
