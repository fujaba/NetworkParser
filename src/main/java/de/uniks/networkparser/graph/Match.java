package de.uniks.networkparser.graph;

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

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

public class Match extends GraphMember implements Comparable<Match>{
	private int count;
	private GraphMember match;
	private GraphEntity mainFile;
	private Object oldValue;
	private Object newValue;
	private String type;

	protected boolean isFileMatch = false;
	protected boolean isMetaMatch = false;
	protected Match otherMatch;
	protected GraphMember potentMatch = null;
	private GraphMember metamatch;
	private GraphMember sourcematch;
	private GraphMatcher owner;
	
	public static Match create(GraphMember owner, ObjectCondition role, String type, Object oldValue, Object newValue) {
		Match graphDiff = new Match();
		graphDiff.withRole(role);
		graphDiff.with(owner);
		graphDiff.withType(type);
		graphDiff.withOldValue(oldValue);
		graphDiff.withNewValue(newValue);
		return graphDiff;
	}
	
	public static Match createMatch(GraphEntity node,GraphMember model, boolean isFileMatch) {
		Match match = new Match().withMain(node);
		match.match = model;
		if(isFileMatch) {
//			match.sourceParent = model;
			match.isFileMatch = true;
			return match;
		}
//		match.parent = model;
		return match;
	}

	public static Match createPotentMatch(GraphMember oldValue, GraphMember newValue) {
		Match match = new Match();
		match.match = oldValue;
		match.newValue = newValue;
		return match;
	}
	
	public Match withMetdaMatch(GraphMember value) {
		this.metamatch = value;
		return this;
	}

	public Match withOwner(GraphMatcher owner) {
		this.owner = owner;
		return this;
	}

	public GraphMember getMetaMatch() {
		return metamatch;
	}
	
	public GraphMember getSourceMatch() {
		return sourcematch;
	}

	public Match withOtherMatch(Match other) {
		if (this.otherMatch != other) {
			Match oldMatch = this.otherMatch;
			if (this.otherMatch != null) {
				this.otherMatch = null;
				oldMatch.withOtherMatch(null);
			}
			this.otherMatch = other;
			if (other != null) {
				other.withOtherMatch(this);
			}
		}
		return this;
	}
	
	public Match getOtherMatch() {
		return otherMatch;
	}

	public GraphMember getPotentMatch() {
		return potentMatch;
	}

	public Match withPotentMatch(GraphMember value) {
		this.potentMatch = value;
		return this;
	}

	public boolean isPotentMatch() {
		return potentMatch != null;
	}

	public Match withMain(GraphEntity node) {
		this.mainFile = node;
		return this;
	}

	public GraphEntity getMainFile() {
		return mainFile;
	}

	public GraphMember getMatch() {
		return match;
	}

	public Match with(GraphMember value) {
		if (this.match != value) {
			GraphMember oldValue = this.match;
			if (oldValue != null) {
				this.match = null;
				oldValue.remove(this);
			}
			this.match = value;
			if (value != null) {
				value.withChildren(this);
			}
		}
		return this;
	}

	public int getCount() {
		return count;
	}
	protected void addCounter() {
		this.count++;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public boolean setOldValue(Object oldValue) {
		if(oldValue != this.oldValue) {
			this.oldValue = oldValue;
			return true;
		}
		return false;
	}
	
	public Match withOldValue(Object oldValue) {
		setOldValue(oldValue);
		return this;
	}

	public Object getNewValue() {
		return newValue;
	}

	public boolean setNewValue(Object newValue) {
		if(newValue != this.newValue) {
			this.newValue = newValue;
			return true;
		}
		return false;
	}
	
	public Match withNewValue(Object newValue) {
		setNewValue(newValue);
		return this;
	}
	
	public Match withType(String value) {
		this.type = value;
		return this;
	}
	
	public String getType() {
		return this.type;
	}
	
	@Override
	public int compareTo(Match o) {
		String action = getType();
		if(o == null || action == null) {
			return 1;
		}
		// REMOVE, NEW, UPDATE
		if(action.equals(o.getType())) {
			return 0;
		}
		if(action.equals(SendableEntityCreator.REMOVE)) {
			// o.getAction() must be UPDATE OR NEW
			return -1;
		}
//		if(action.equals(SendableEntityCreator.UPDATE) && SendableEntityCreator.REMOVE.equals(o.getType())) {
//		if(action.equals(SendableEntityCreator.NEW)) { // o.getAction() must be REMOVE OR UPDATE 1
		return 1;
	}

	public String getUpdate() {
		if(this.oldValue == null) {
			if(this.newValue != null) {
				return SendableEntityCreator.NEW;
			}
		} else if(this.newValue == null) {
			return SendableEntityCreator.REMOVE;
		}
		return SendableEntityCreator.UPDATE;
	}

	public GraphMatcher getOwner() {
		return owner;
	}

	public boolean isMetaMatch() {
		return metamatch != null;
	}

	public boolean isOtherMatch() {
		return otherMatch != null;
	}

	public boolean isFileMatch() {
		return isFileMatch;
	}

	public boolean isMetaSourceMatch() {
		return metamatch != null && sourcematch != null;
//		return false;
	}

	public boolean isSourceMatch() {
		return sourcematch != null;
	}
}
