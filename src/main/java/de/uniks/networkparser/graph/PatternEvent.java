package de.uniks.networkparser.graph;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.list.SimpleList;

public class PatternEvent extends SimpleEvent {
	private static final long serialVersionUID = 1L;
	private SimpleList<Object> candidates;
	
	public PatternEvent(Pattern pattern, String property) {
		super(pattern, property, null, null);
	}

	@Override
	public Object getNewValue() {
		return getModelValue();
	}
	
	public PatternEvent addCandidate(Object... values) {
		if(values == null || values.length<1) {
			return this;
		}
		if(this.candidates == null) {
			this.candidates = new SimpleList<Object>();
		}
		this.candidates.add(values);
		this.withModelValue(values[values.length-1]);
		return this;
	}

	public PatternEvent setCandidate(Object... values) {
		if(this.candidates != null) {
			this.candidates.clear();
		}
		this.addCandidate(values);
		return this;
	}
}
