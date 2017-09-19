package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.SymTabEntry;

public class SourceCode extends GraphMember {
	private CharSequence content;
	private SimpleKeyValueList<String, SimpleList<SymTabEntry>> keys=new SimpleKeyValueList<String, SimpleList<SymTabEntry>>();
	private boolean fileBodyHasChanged = false;
	private int size;
	private int endOfClassName;
	private int endOfExtendsClause;
	private int endOfImports;
	private int endOfImplementsClause;
	private int bodyStart;
	private int endOfAttributeInitialization;

	public CharSequence getContent() {
		return content;
	}
	public SourceCode withContent(CharSequence content) {
		this.content = content;
		this.size = content.length();
		return this;
	}
	
	@Override
	public int size() {
		return size;
	}
	
	public SimpleList<SymTabEntry> getSymbolEntries(String type) {
		SimpleList<SymTabEntry> list = keys.get(type);
		if(list == null) {
			list = new SimpleList<SymTabEntry>();
			keys.add(type, list);
		}
		return list;
	}
	
	public SimpleKeyValueList<String, SimpleList<SymTabEntry>> getSymbolTab() {
		return keys;
	}
	
	/**
	 * Set the Parent of Element
	 * @param file  The Name of Element
	 * @return The Instance
	 */
	public GraphMember with(Clazz parent) {
		this.parentNode = parent;
		parent.withChildren(this);
		return this;
	}
	
	public boolean isFileBodyHasChanged() {
		return fileBodyHasChanged;
	}
	public void setFileBodyHasChanged(boolean fileBodyHasChanged) {
		this.fileBodyHasChanged = fileBodyHasChanged;
	}

	public CharSequence subString(int start, int end) {
		return content.subSequence(start, end);
	}
	public int getEndOfClassName() {
		return endOfClassName;
	}
	public SourceCode withEndOfClassName(int value) {
		this.endOfClassName = value;
		return this;
	}
	public int getEndOfExtendsClause() {
		return endOfExtendsClause;
	}
	public SourceCode withEndOfExtendsClause(int value) {
		this.endOfExtendsClause = value;
		return this;
	}
	public int getEndOfImports() {
		return endOfImports;
	}
	public SourceCode withEndOfImports(int value) {
		this.endOfImports = value;
		return this;
	}
	public SourceCode withEndOfImplementsClause(int value) {
		this.endOfImplementsClause = value;
		return this;
	}
	
	public int getEndOfImplementsClause() {
		return endOfImplementsClause;
	}
	public SourceCode withStartBody(int value) {
		this.bodyStart = value;
		return this;
	}
	
	public int getBodyStart() {
		return bodyStart;
	}
	public SourceCode withEndOfAttributeInitialization(int value) {
		this.endOfAttributeInitialization = value;
		return this;
	}
	
	public int getEndOfAttributeInitialization() {
		return endOfAttributeInitialization;
	}
}
