package de.uniks.networkparser.logic;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.graph.Pattern;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleIterator;

public class PatternCondition implements ObjectCondition{
	private String link;
	private Object value;
	
	public PatternCondition withLinkName(String value) {
		this.link = value;
		return this;
	}
	
	public PatternCondition withValue(Object value) {
		this.value = value;
		return this;
	}
	
	@Override
	public boolean update(Object value) {
		if(value instanceof Pattern == false) {
			return false;
		}
		Pattern pattern = (Pattern) value;
		IdMap map = pattern.getMap();
		if(map == null) {
			return false;
		}
		if(pattern.getCandidates() == null && pattern.getParent()!= null) {
			Object match2 = pattern.getParent().getMatch();
			if(match2 != null) {
				SendableEntityCreator creator = map.getCreatorClass(match2);
				if(creator != null) {
					Object newValue = creator.getValue(match2, getLinkName());
					pattern.withCandidates(newValue);
				}
			}
		}

		SimpleIterator<Object> i = pattern.getIterator();
		if(i == null) {
			return false;
		}
		if(pattern.getMatch() != null && pattern.getMatch() != i.current() ) {
			if(this.value == null || this.value.equals(pattern.getMatch())) {
				return true;
			}
		}
		while(i.hasNext()) {
			Object candidate = i.next();
			if(candidate == null) {
				return false;
			}
			if(this.value == null || this.value.equals(candidate)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static final PatternCondition create(String linkName) {
		PatternCondition pattern = new PatternCondition();
		pattern.withLinkName(linkName);
		return pattern;
	}

	public String getLinkName() {
		return link;
	}
	
	public Object getValue() {
		return value;
	}
}
