package de.uniks.networkparser.graph;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.SymTabEntry;

public class SourceCode extends GraphMember {
	public static final String NAME="SourceCode";
	private CharacterBuffer content;
	private SimpleKeyValueList<String, SimpleList<SymTabEntry>> keys=new SimpleKeyValueList<String, SimpleList<SymTabEntry>>();
	private boolean fileBodyHasChanged = false;
	private int size;
	private int startOfImports;
	private int endOfClassName;
	private int endOfExtendsClause;
	private int endOfImports;
	private int endOfImplementsClause;
	private int bodyStart;
	private int endOfAttributeInitialization;
	private int endOfBody;
	
	public SourceCode() {
		super();
		this.name = NAME;
	}
	
	public CharacterBuffer getContent() {
		return content;
	}
	public SourceCode withContent(CharacterBuffer content) {
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
	
	public SymTabEntry getSymbolEntry(String type, String name) {
		if(name == null || type == null) {
			return null;
		}
		SimpleList<SymTabEntry> list = keys.get(type.toLowerCase());
		if(list != null) {
			for(SymTabEntry entry : list) {
				if(name.equals(entry.getValue())) {
					return entry;
				}
			}
		}
		return null;
	}
	
	public SimpleKeyValueList<String, SimpleList<SymTabEntry>> getSymbolTab() {
		return keys;
	}
	
	/**
	 * Set the Parent of Element
	 * @param parent  Set The Parent Element
	 * @return The Instance
	 */
	public GraphMember with(Clazz parent) {
		this.parentNode = parent;
		//REMOVE OLD SOURCE CODE
		GraphSimpleSet children = new GraphSimpleSet();
		children.withList(parent.getChildren());
		for(GraphMember item : children) {
			if(item instanceof SourceCode) {
				if(item != this) {
					parent.remove(item);
				}
			}
		}
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
	
	public void replaceAll(int bodyStartPos, String value) {
		this.content.replace(bodyStartPos, bodyStartPos, value);
		this.fileBodyHasChanged = true;
	}
	
	@Override
	public String toString() {
		return content.toString();
	}

	public SourceCode withEndBody(int value) {
		this.endOfBody = value;
		return this;
	}
	
	public int getEndOfBody() {
		return endOfBody;
	}

	public SourceCode withStartImports(int value) {
		this.startOfImports = value;
		return this;
	}
	
	public int getStartOfImports() {
		return startOfImports;
	}
}
