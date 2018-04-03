package de.uniks.networkparser.graph;

import java.util.Iterator;
import java.util.List;

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.Equals;

public class Pattern implements Iterator<Pattern>, Iterable<Pattern>{
	private Pattern children;
	private SimpleList<Object> candidates=new SimpleList<Object>();
	private Object match;
	private ObjectCondition condition;
	private Pattern root;

	public Pattern(Object match) {
		this.match = match;
		this.candidates.add(match);
	}
	
	public Pattern() {
	}

	public Pattern has(String property) {
		return has(new Equals().withKey(property));
	}
	
	public Pattern has(ObjectCondition condition) {
		if(this.condition == null) {
			this.condition = condition;
			return this;
		}
		Pattern subPattern = new Pattern().has(condition);
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
