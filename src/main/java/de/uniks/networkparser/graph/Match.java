package de.uniks.networkparser.graph;

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

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
 * The Class Match.
 *
 * @author Stefan
 */
public class Match extends GraphMember implements Comparable<Match> {
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

	/**
	 * Creates the.
	 *
	 * @param owner the owner
	 * @param role the role
	 * @param type the type
	 * @param oldValue the old value
	 * @param newValue the new value
	 * @return the match
	 */
	public static Match create(GraphMember owner, ObjectCondition role, String type, Object oldValue, Object newValue) {
		Match graphDiff = new Match();
		graphDiff.withRole(role);
		graphDiff.with(owner);
		graphDiff.withType(type);
		graphDiff.withOldValue(oldValue);
		graphDiff.withNewValue(newValue);
		return graphDiff;
	}

	/**
	 * Creates the match.
	 *
	 * @param node the node
	 * @param model the model
	 * @param isFileMatch the is file match
	 * @return the match
	 */
	public static Match createMatch(GraphEntity node, GraphMember model, boolean isFileMatch) {
		Match match = new Match().withMain(node);
		match.match = model;
		if (isFileMatch) {
			match.isFileMatch = true;
			return match;
		}
		return match;
	}

	/**
	 * Creates the potent match.
	 *
	 * @param oldValue the old value
	 * @param newValue the new value
	 * @return the match
	 */
	public static Match createPotentMatch(GraphMember oldValue, GraphMember newValue) {
		Match match = new Match();
		match.match = oldValue;
		match.newValue = newValue;
		return match;
	}

	/**
	 * With metda match.
	 *
	 * @param value the value
	 * @return the match
	 */
	public Match withMetdaMatch(GraphMember value) {
		this.metamatch = value;
		return this;
	}

	/**
	 * With owner.
	 *
	 * @param owner the owner
	 * @return the match
	 */
	public Match withOwner(GraphMatcher owner) {
		this.owner = owner;
		return this;
	}

	/**
	 * Gets the meta match.
	 *
	 * @return the meta match
	 */
	public GraphMember getMetaMatch() {
		return metamatch;
	}

	/**
	 * Gets the source match.
	 *
	 * @return the source match
	 */
	public GraphMember getSourceMatch() {
		return sourcematch;
	}

	/**
	 * With other match.
	 *
	 * @param other the other
	 * @return the match
	 */
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

	/**
	 * Gets the other match.
	 *
	 * @return the other match
	 */
	public Match getOtherMatch() {
		return otherMatch;
	}

	/**
	 * Gets the potent match.
	 *
	 * @return the potent match
	 */
	public GraphMember getPotentMatch() {
		return potentMatch;
	}

	/**
	 * With potent match.
	 *
	 * @param value the value
	 * @return the match
	 */
	public Match withPotentMatch(GraphMember value) {
		this.potentMatch = value;
		return this;
	}

	/**
	 * Checks if is potent match.
	 *
	 * @return true, if is potent match
	 */
	public boolean isPotentMatch() {
		return potentMatch != null;
	}

	/**
	 * With main.
	 *
	 * @param node the node
	 * @return the match
	 */
	public Match withMain(GraphEntity node) {
		this.mainFile = node;
		return this;
	}

	/**
	 * Gets the main file.
	 *
	 * @return the main file
	 */
	public GraphEntity getMainFile() {
		return mainFile;
	}

	/**
	 * Gets the match.
	 *
	 * @return the match
	 */
	public GraphMember getMatch() {
		return match;
	}

	/**
	 * With.
	 *
	 * @param value the value
	 * @return the match
	 */
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

	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	protected void addCounter() {
		this.count++;
	}

	/**
	 * Gets the old value.
	 *
	 * @return the old value
	 */
	public Object getOldValue() {
		return oldValue;
	}

	/**
	 * Sets the old value.
	 *
	 * @param oldValue the old value
	 * @return true, if successful
	 */
	public boolean setOldValue(Object oldValue) {
		if (oldValue != this.oldValue) {
			this.oldValue = oldValue;
			return true;
		}
		return false;
	}

	/**
	 * With old value.
	 *
	 * @param oldValue the old value
	 * @return the match
	 */
	public Match withOldValue(Object oldValue) {
		setOldValue(oldValue);
		return this;
	}

	/**
	 * Gets the new value.
	 *
	 * @return the new value
	 */
	public Object getNewValue() {
		return newValue;
	}

	/**
	 * Sets the new value.
	 *
	 * @param newValue the new value
	 * @return true, if successful
	 */
	public boolean setNewValue(Object newValue) {
		if (newValue != this.newValue) {
			this.newValue = newValue;
			return true;
		}
		return false;
	}

	/**
	 * With new value.
	 *
	 * @param newValue the new value
	 * @return the match
	 */
	public Match withNewValue(Object newValue) {
		setNewValue(newValue);
		return this;
	}

	/**
	 * With type.
	 *
	 * @param value the value
	 * @return the match
	 */
	public Match withType(String value) {
		this.type = value;
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
	 * Compare to.
	 *
	 * @param o the o
	 * @return the int
	 */
	@Override
	public int compareTo(Match o) {
		String action = getType();
		if (o == null || action == null) {
			return 1;
		}
		/* REMOVE, NEW, UPDATE */
		if (action.equals(o.getType())) {
			return 0;
		}
		if (action.equals(SendableEntityCreator.REMOVE)) {
			/* o.getAction() must be UPDATE OR NEW */
			return -1;
		}
/*		if(action.equals(SendableEntityCreator.UPDATE) && SendableEntityCreator.REMOVE.equals(o.getType())) { */
/*		if(action.equals(SendableEntityCreator.NEW)) { o.getAction() must be REMOVE OR UPDATE 1 */
		return 1;
	}

	/**
	 * Gets the update.
	 *
	 * @return the update
	 */
	public String getUpdate() {
		if (this.oldValue == null) {
			if (this.newValue != null) {
				return SendableEntityCreator.NEW;
			}
		} else if (this.newValue == null) {
			return SendableEntityCreator.REMOVE;
		}
		return SendableEntityCreator.UPDATE;
	}

	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public GraphMatcher getOwner() {
		return owner;
	}

	/**
	 * Checks if is meta match.
	 *
	 * @return true, if is meta match
	 */
	public boolean isMetaMatch() {
		return metamatch != null;
	}

	/**
	 * Checks if is other match.
	 *
	 * @return true, if is other match
	 */
	public boolean isOtherMatch() {
		return otherMatch != null;
	}

	/**
	 * Checks if is file match.
	 *
	 * @return true, if is file match
	 */
	public boolean isFileMatch() {
		return isFileMatch;
	}

	/**
	 * Checks if is meta source match.
	 *
	 * @return true, if is meta source match
	 */
	public boolean isMetaSourceMatch() {
		return metamatch != null && sourcematch != null;
	}

	/**
	 * Checks if is source match.
	 *
	 * @return true, if is source match
	 */
	public boolean isSourceMatch() {
		return sourcematch != null;
	}
}
