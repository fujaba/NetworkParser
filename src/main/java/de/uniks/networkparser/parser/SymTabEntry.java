package de.uniks.networkparser.parser;

import java.util.ArrayList;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.SourceCode;
import de.uniks.networkparser.interfaces.BaseItem;

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

/**
 * The Class SymTabEntry.
 *
 * @author Stefan
 */
public class SymTabEntry {
	
	/** The Constant TYPE_VOID. */
	public static final String TYPE_VOID = "void";

	/** The Constant TYPE_IMPORT. */
	public static final String TYPE_IMPORT = "import";

	/** The Constant TYPE_INTERFACE. */
	public static final String TYPE_INTERFACE = "interface";
	
	/** The Constant TYPE_CLASS. */
	public static final String TYPE_CLASS = "class";
	
	/** The Constant TYPE_ENUM. */
	public static final String TYPE_ENUM = "enum";

	/** The Constant TYPE_EXTENDS. */
	public static final String TYPE_EXTENDS = "extends";
	
	/** The Constant TYPE_IMPLEMENTS. */
	public static final String TYPE_IMPLEMENTS = "implements";

	/** The Constant TYPE_ANNOTATION. */
	public static final String TYPE_ANNOTATION = "annotation";
	
	/** The Constant TYPE_ATTRIBUTE. */
	public static final String TYPE_ATTRIBUTE = "attribute";
	
	/** The Constant TYPE_ENUMVALUE. */
	public static final String TYPE_ENUMVALUE = "enumvalue";
	
	/** The Constant TYPE_METHOD. */
	public static final String TYPE_METHOD = "method";
	
	/** The Constant TYPE_PACKAGE. */
	public static final String TYPE_PACKAGE = "package";
	
	/** The Constant TYPE_COMMENT. */
	public static final String TYPE_COMMENT = "comment";
	
	/** The Constant TYPE_JAVADOC. */
	public static final String TYPE_JAVADOC = "javadoc";
	
	/** The Constant TYPE_BLOCK. */
	public static final String TYPE_BLOCK = "block";

	/** The Constant TYPE_CONSTRUCTOR. */
	public static final String TYPE_CONSTRUCTOR = "constructor";

	/** The Constant PROPERTY_TYPE. */
	public static final String PROPERTY_TYPE = "type";
	
	/** The Constant PROPERTY_VALUE. */
	public static final String PROPERTY_VALUE = "value";
	
	/** The Constant NOGEN. */
	public static final String NOGEN = "//XXX NOGEN";

	private String value;
	private String type;

	private SymTabEntry next;
	private SymTabEntry prev;

	/* SDMLIb Parser */
	private int startPos;
	private int endPos;
	private int annotationsStartPos;
/*	private int preCommentStartPos; */
/*	private int preCommentEndPos; */
	private String modifiers;
	private String throwsTags;
	private String annotations;

	private ArrayList<ArrayList<String>> initCallSequence;

	private int annotationsEndPos;
	private int bodyStartPos;
	private String dataType; /* DataType of Attribute or ReturnType of Method */
	private String params; /* Parameter of Methods */
	private SourceCode parent;
	private String body;

	private String name;

	private long startLine;

	private long endLine;

	/**
	 * With parent.
	 *
	 * @param parent the parent
	 * @return the sym tab entry
	 */
	public SymTabEntry withParent(SourceCode parent) {
		this.parent = parent;
		return this;
	}

	/**
	 * Instantiates a new sym tab entry.
	 *
	 * @param parent the parent
	 */
	public SymTabEntry(SourceCode parent) {
		this.parent = parent;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setValue(String value) {
		if ((this.value == null && value != null) || (this.value != null && !this.value.equals(value))) {
			this.value = value;
			return true;
		}
		return false;
	}

	/**
	 * Adds the.
	 *
	 * @param string the string
	 */
	public void add(CharSequence string) {
		if (this.name == null) {
			this.name = "" + string;
		} else {
			this.name += string;
		}
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the sym tab entry
	 */
	public SymTabEntry withValue(String value) {
		setValue(value);
		return this;
	}

	/**
	 * With name.
	 *
	 * @param value the value
	 * @return the sym tab entry
	 */
	public SymTabEntry withName(CharacterBuffer value) {
		if (value != null) {
			this.name = value.toString();
		}
		return this;
	}

	/**
	 * With name.
	 *
	 * @param value the value
	 * @return the sym tab entry
	 */
	public SymTabEntry withName(String value) {
		this.name = value;
		return this;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the type.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setType(String value) {
		if ((this.type == null && value != null) || (this.type != null && !this.type.equals(value))) {
			this.type = value;
			return true;
		}
		return false;
	}

	/**
	 * With type.
	 *
	 * @param value the value
	 * @return the sym tab entry
	 */
	public SymTabEntry withType(String value) {
		setType(value);
		return this;
	}

	/**
	 * Sets the next.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setNext(SymTabEntry value) {
		boolean changed = false;

		if (this.next != value) {
			SymTabEntry oldValue = this.next;
			if (this.next != null) {
				this.next = null;
				oldValue.setPrev(null);
			}
			this.next = value;

			if (value != null) {
				value.setPrev(this);
			}
			changed = true;
		}
		return changed;
	}

	/**
	 * Sets the prev.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setPrev(SymTabEntry value) {
		boolean changed = false;

		if (this.prev != value) {
			SymTabEntry oldValue = this.prev;
			if (this.prev != null) {
				this.prev = null;
				oldValue.setNext(null);
			}
			this.prev = value;

			if (value != null) {
				value.setNext(this);
			}
			changed = true;
		}
		return changed;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		CharacterBuffer sb = new CharacterBuffer();
		toString(sb);
		return sb.toString();
	}

	/**
	 * To string.
	 *
	 * @param sb the sb
	 * @return true, if successful
	 */
	public boolean toString(CharacterBuffer sb) {
		if (sb == null) {
			return false;
		}
		sb.with(this.name);
		if (this.next != null) {
			this.next.toString(sb);
		}
		return true;
	}

	/**
	 * With position.
	 *
	 * @param start the start
	 * @param end the end
	 * @param startLine the start line
	 * @param endLine the end line
	 * @return the sym tab entry
	 */
	public SymTabEntry withPosition(int start, int end, long startLine, long endLine) {
		this.startPos = start;
		this.endPos = end;
		this.startLine = startLine;
		this.endLine = endLine;
		return this;
	}

	/**
	 * Gets the start pos.
	 *
	 * @return the start pos
	 */
	public int getStartPos() {
		return startPos;
	}

	/**
	 * Gets the end pos.
	 *
	 * @return the end pos
	 */
	public int getEndPos() {
		return endPos;
	}

	/**
	 * Gets the start line.
	 *
	 * @return the start line
	 */
	public long getStartLine() {
		return startLine;
	}

	/**
	 * Gets the end line.
	 *
	 * @return the end line
	 */
	public long getEndLine() {
		return endLine;
	}

	/**
	 * With annotations.
	 *
	 * @param start the start
	 * @param end the end
	 * @return the sym tab entry
	 */
	public SymTabEntry withAnnotations(int start, int end) {
		this.annotationsStartPos = start;
		this.annotationsEndPos = end;
		return this;
	}

	/**
	 * With annotations start.
	 *
	 * @param start the start
	 * @return the sym tab entry
	 */
	public SymTabEntry withAnnotationsStart(int start) {
		this.annotationsStartPos = start;
		return this;
	}

	/**
	 * Gets the annotations end pos.
	 *
	 * @return the annotations end pos
	 */
	public int getAnnotationsEndPos() {
		return annotationsEndPos;
	}

	/**
	 * Gets the annotations start pos.
	 *
	 * @return the annotations start pos
	 */
	public int getAnnotationsStartPos() {
		return annotationsStartPos;
	}

	/**
	 * With modifiers.
	 *
	 * @param modifiers the modifiers
	 * @return the sym tab entry
	 */
	public SymTabEntry withModifiers(String modifiers) {
		this.modifiers = modifiers;
		return this;
	}

	/**
	 * Gets the modifiers.
	 *
	 * @return the modifiers
	 */
	public String getModifiers() {
		return modifiers;
	}

	/**
	 * With throws tags.
	 *
	 * @param throwsTags the throws tags
	 * @return the sym tab entry
	 */
	public SymTabEntry withThrowsTags(String throwsTags) {
		this.throwsTags = throwsTags;
		return this;
	}

	/**
	 * Gets the throws tags.
	 *
	 * @return the throws tags
	 */
	public String getThrowsTags() {
		return throwsTags;
	}

	/**
	 * With annotations.
	 *
	 * @param annotations the annotations
	 * @return the sym tab entry
	 */
	public SymTabEntry withAnnotations(String annotations) {
		this.annotations = annotations;
		return this;
	}

	/**
	 * Gets the annotations.
	 *
	 * @return the annotations
	 */
	public String getAnnotations() {
		return annotations;
	}

	/**
	 * With init sequence.
	 *
	 * @param initCallSequence the init call sequence
	 * @return the sym tab entry
	 */
	public SymTabEntry withInitSequence(ArrayList<ArrayList<String>> initCallSequence) {
		this.initCallSequence = initCallSequence;
		return this;
	}

	/**
	 * Gets the inits the call sequence.
	 *
	 * @return the inits the call sequence
	 */
	public ArrayList<ArrayList<String>> getInitCallSequence() {
		return initCallSequence;
	}

	/**
	 * With body start pos.
	 *
	 * @param value the value
	 * @return the sym tab entry
	 */
	public SymTabEntry withBodyStartPos(int value) {
		this.bodyStartPos = value;
		return this;
	}

	/**
	 * Gets the body start pos.
	 *
	 * @return the body start pos
	 */
	public int getBodyStartPos() {
		return bodyStartPos;
	}

	/**
	 * With body.
	 *
	 * @param value the value
	 * @return the sym tab entry
	 */
	public SymTabEntry withBody(String value) {
		this.body = value;
		return this;
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Checks if is no gen.
	 *
	 * @return true, if is no gen
	 */
	public boolean isNoGen() {
		if (this.body == null) {
			return false;
		}
		return body.indexOf(NOGEN) >= 0;
	}

	/**
	 * With data type.
	 *
	 * @param value the value
	 * @return the sym tab entry
	 */
	public SymTabEntry withDataType(String value) {
		this.dataType = value;
		return this;
	}

	/**
	 * Gets the data type.
	 *
	 * @return the data type
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * With params.
	 *
	 * @param params the params
	 * @return the sym tab entry
	 */
	public SymTabEntry withParams(String params) {
		this.params = params;
		return this;
	}

	/**
	 * Gets the params.
	 *
	 * @return the params
	 */
	public String getParams() {
		return params;
	}

	/**
	 * Write body.
	 *
	 * @param value the value
	 */
	public void writeBody(String value) {
		if (this.parent != null) {
			this.parent.replaceAll(this.bodyStartPos + 1, value);
			this.body = "{" + BaseItem.CRLF + "\t" + value + BaseItem.CRLF + "}";
		}
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
