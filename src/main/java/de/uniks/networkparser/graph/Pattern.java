package de.uniks.networkparser.graph;

import java.util.Iterator;
import java.util.List;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.PatternCondition;

public class Pattern implements Iterator<Pattern>, Iterable<Pattern>{
	private SimpleSet<Pattern> children;
	private SimpleSet<Object> candidates=null;
	private Object match;
	private ObjectCondition condition;
	private Pattern root;
	private IdMap map;
	
	public Pattern getRoot() {
		return root;
	}
	
	public IdMap getMap() {
		return root.map;
	}

	public Pattern(Object match) {
		this.match = match;
		if(candidates == null) {
			candidates = new SimpleSet<Object>();
		}
		this.candidates.add(match);
		this.root = this;
	}
	
	public Pattern() {
		this.root = this;
	}

	public Pattern has(String property) {
		return has(new PatternCondition().withLinkName(property));
	}
	
	public Pattern has(ObjectCondition condition) {
		if(this.condition == null) {
			this.condition = condition;
			if(match == null)
			return this;
		}
		Pattern subPattern = new Pattern().has(condition);
		if(children == null) {
			children = new SimpleSet<Pattern>();
		}
		this.children.add(subPattern);
		subPattern.root = this.root;
		subPattern.find();
		return subPattern;
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

	public boolean find() {
		if(candidates == null) {
			
		}
		if(condition == null || condition.update(candidates)) {
			return true;
		}
		//
		return false;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public Pattern next() {
		find();
		return this;
	}

	@Override
	public Iterator<Pattern> iterator() {
		return this;
	}
}
