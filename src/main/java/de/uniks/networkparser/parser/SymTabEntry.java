package de.uniks.networkparser.parser;

import java.util.ArrayList;

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

public class SymTabEntry {
	public static final String TYPE_IMPORT = "import";

	public static final String TYPE_CLASS = "class";

	public static final String TYPE_EXTENDS = "extends";
	public static final String TYPE_IMPLEMENTS = "implements";

	public static final String TYPE_ATTRIBUTE = "attribute";
	public static final String TYPE_ENUMVALUE = "enumvalue";
	public static final String TYPE_METHOD = "method";
	public static final String TYPE_PACKAGE = "package";
	public static final String TYPE_COMMENT = "comment";
	public static final String TYPE_JAVADOC = "javadoc";

	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_VALUE = "value";

	private String value;
	private String type;

	private SymTabEntry next;
	private SymTabEntry prev;
	
	// SDMLIb Parser
	private String kind;
	private String memberName;
	private int startPos;
	private int endPos;
	private int annotationsStartPos;
	private int preCommentStartPos;
	private int preCommentEndPos;
	private int bodyStartPos;
	private String modifiers;
	private String throwsTags;
	private String annotations;

	private ArrayList<ArrayList<String>> initCallSequence;

	public String getValue() {
		return this.value;
	}

	public boolean setValue(String value) {
		if((this.value == null && value != null) ||
			(this.value != null && this.value.equals(value) == false)) {
			this.value = value;
			return true;
		}
		return false;
	}

	public void add(CharSequence string) {
		if(this.value == null) {
			this.value = ""+string;
		} else {
			this.value += string;
		}
	}

	public SymTabEntry withValue(String value) {
		setValue(value);
		return this;
	}

	public String getType() {
		return this.type;
	}

	public boolean setType(String value) {
		if((this.type == null && value != null) ||
			(this.type != null && this.type.equals(value) == false)) {
			this.type = value;
			return true;
		}
		return false;
	}

	public SymTabEntry withType(String value) {
		setType(value);
		return this;
	}

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

	public String toString() {
		StringBuilder sb= new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	public void toString(StringBuilder sb) {
		sb.append(this.value);
		if(this.next != null) {
			this.next.toString(sb);
		}
	}

	public String getKind() {
		return kind;
	}

	public SymTabEntry withKind(String kind) {
		this.kind = kind;
		return this;
	}

	public String getMemberName() {
		return memberName;
	}

	public SymTabEntry withMemberName(String memberName) {
		this.memberName = memberName;
		return this;
	}

	public SymTabEntry withStartPos(int pos) {
		this.startPos = pos;
		return this;
	}
	
	public int getStartPos() {
		return startPos;
	}
	
	public SymTabEntry withEndPos(int pos) {
		this.endPos = pos;
		return this;
	}
	
	public int getEndPos() {
		return endPos;
	}

	public SymTabEntry withAnnotationsStartPos(int pos) {
		this.annotationsStartPos = pos;
		return this;
	}
	
	public int getAnnotationsStartPos() {
		return annotationsStartPos;
	}

	public SymTabEntry withPreCommentStartPos(int pos) {
		this.preCommentStartPos = pos;
		return this;
	}
	
	public int getPreCommentStartPos() {
		return preCommentStartPos;
	}
	public SymTabEntry withPreCommentEndPos(int pos) {
		this.preCommentEndPos = pos;
		return this;
	}
	
	public int getPreCommentEndPos() {
		return preCommentEndPos;
	}

	public SymTabEntry withBodyStartPos(int pos) {
		this.bodyStartPos = pos;
		return this;
	}
	
	public int getBodyStartPos() {
		return bodyStartPos;
	}

	public SymTabEntry withModifiers(String modifiers) {
		this.modifiers= modifiers;
		return this;
	}
	
	public String getModifiers() {
		return modifiers;
	}

	public SymTabEntry withThrowsTags(String throwsTags) {
		this.throwsTags = throwsTags;
		return this;
	}
	public String getThrowsTags() {
		return throwsTags;
	}

	public SymTabEntry withAnnotations(String annotations) {
		this.annotations = annotations;
		return this;
	}
	
	public String getAnnotations() {
		return annotations;
	}

	public SymTabEntry withInitSequence(ArrayList<ArrayList<String>> initCallSequence) {
		this.initCallSequence = initCallSequence;
		return this;
	}
	
	public ArrayList<ArrayList<String>> getInitCallSequence() {
		return initCallSequence;
	}
}
