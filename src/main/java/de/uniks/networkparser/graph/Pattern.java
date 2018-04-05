package de.uniks.networkparser.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleIterator;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.PatternCondition;

public class Pattern implements Iterator<Object>, Iterable<Object>{
	private Object match;

	private SimpleSet<Pattern> children;
	private Pattern parent;
	private SimpleSet<Object> candidates=null;
	private ObjectCondition condition;
	private IdMap map;
	private SimpleIterator<Object> iterator;
	private SimpleSet<Pattern> chain;
	
	public Pattern getRoot() {
		if(this.getParent() != null) {
			return this.getParent().getRoot();
		}
		return this;
	}
	
	public IdMap getMap() {
		return getRoot().map;
	}

	public Pattern(IdMap map, Object match) {
		this();
		this.match = match;
		this.map = map;
		if(candidates == null) {
			candidates = new SimpleSet<Object>();
		}
		this.candidates.add(match);
	}
	
	public Pattern() {
		this.chain = new SimpleSet<Pattern>();
		this.chain.add(this);
	}
	public Pattern(Pattern parent, ObjectCondition condition) {
		if(parent != null) {
			this.parent = parent;
			parent.addToChain(this);
		}
		this.condition = condition;
	}

	public Pattern has(String property) {
		return has(PatternCondition.create(property));
	}
	
	public Pattern has(ObjectCondition condition) {
		Pattern root = getRoot();
		if(root != this && this.condition == null) {
			this.condition = condition;
			return this;
		}
		Pattern subPattern = new Pattern(this, condition);
		if(children == null) {
			children = new SimpleSet<Pattern>();
		}
		this.children.add(subPattern);
		subPattern.find();
		return subPattern;
	}

	private void addToChain(Pattern value) {
		getChain().add(value);
	}

	public static <T> SimpleList<T> createListOfType(Class<T> type) {
		return new SimpleList<T>();
	}

	@SuppressWarnings("unchecked")
	public <ST extends List<Object>> ST allMatches() {
		if(this.match == null) {
			find();
		}
		if(match == null) {
			return (ST) new SimpleList<Object>();
		}
		SimpleList<? extends Object> result = createListOfType(match.getClass());
		while(find()) {
			result.add(this.match);
		}
		return (ST) result;
	}

	public Object getMatch() {
		return match;
	}

	@SuppressWarnings("unchecked")
	public <ST extends Object> ST getMatch(Class<ST> clazz) {
		return (ST) match;
	}

	public boolean find() {
		SimpleSet<Pattern> chain = getChain();
		Pattern last = chain.last();
		if(last == this ) {
			return finding(true);
		}
		return last.finding(true);
	}

	public SimpleSet<Pattern> getChain() {
		return getRoot().chain;
	}

	@Override
	public boolean hasNext() {
		SimpleSet<Pattern> chain = getChain();
		Pattern last = chain.last();
		if(last == this ) {
			return finding(false);
		}
		return last.finding(false);
	}

	private boolean finding(boolean save) {
		// Backwards
		if(condition == null || condition.update(this) == false) {
			// Not found
			if(parent == null) {
				return false;
			}
			boolean finding = parent.finding(save);
			if(save) {
				this.candidates = null;
				this.match = null;
				this.iterator = null;
				if(condition != null) {
					condition.update(this);
				}
			}

			if(finding == false) {
				return this.match != null;
			}
			if(children != null) {
				for(Pattern child : children) {
					finding = child.find();
					if(finding == false) {
						break;
					}
				}
				if(finding == false) {
					return false;
				}
			}
		}
		if(save) {
			this.match = iterator.current();
		}
		return this.match != null;
	}

	@Override
	public Object next() {
		if(find()) {
			return getMatch();
		}
		return null;
	}

	@Override
	public Iterator<Object> iterator() {
		return this;
	}

	public SimpleSet<Object> getCandidates() {
		return candidates;
	}

	public Pattern getParent() {
		return parent;
	}

	public Pattern withCandidates(Object newValue) {
		if(this.candidates == null) {
			this.candidates = new SimpleSet<Object>();
			this.iterator = (SimpleIterator<Object>) this.candidates.iterator();
			this.iterator.withCheckPointer(false);
		}
		if(newValue instanceof Collection<?>) {
			this.candidates.withList((Collection<?>) newValue);
		} else {
			this.candidates.with(newValue);
		}
		return this;
	}
	
	public SimpleIterator<Object> getIterator() {
		return iterator;
	}

	public Pattern withMatch(Object candidate) {
		this.match = candidate;
		return this;
	}
}
