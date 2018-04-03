package de.uniks.networkparser.logic;

import de.uniks.networkparser.graph.Pattern;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

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
		Object match = pattern.getMatch();
		if(match == null || pattern.getMap() == null) {
			return false;
		}
		SendableEntityCreator creator = pattern.getMap().getCreatorClass(match);
		if(creator == null) {
			return false;
		}
		if(this.link != null) {
			Object newMatch = creator.getValue(match, link);
			if(this.value == null || this.value.equals(newMatch)) {
				return true;
			}
		}
		return false;
	}
}
