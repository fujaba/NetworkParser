package de.uniks.networkparser.graph;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class PatternEvent.
 *
 * @author Stefan
 */
public class PatternEvent extends SimpleEvent {
	private static final long serialVersionUID = 1L;
	/** The candidates. */
	private SimpleList<Object> candidates;
	
	/**
	 * Instantiates a new pattern event.
	 *
	 * @param pattern the pattern
	 * @param property the property
	 */
	public PatternEvent(Pattern pattern, String property) {
		super(pattern, property, null, null);
	}

	/**
	 * Gets the new value.
	 *
	 * @return the new value
	 */
	@Override
	public Object getNewValue() {
		return getModelValue();
	}
	
	/**
	 * Adds the candidate.
	 *
	 * @param values the values
	 * @return the pattern event
	 */
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

	/**
	 * Sets the candidate.
	 *
	 * @param values the values
	 * @return the pattern event
	 */
	public PatternEvent setCandidate(Object... values) {
		if(this.candidates != null) {
			this.candidates.clear();
		}
		this.addCandidate(values);
		return this;
	}
}
