package de.uniks.networkparser.graph;

/**
 * The Class Throws.
 *
 * @author Stefan
 */
public class Throws extends GraphMember {
	
	/**
	 * Instantiates a new throws.
	 *
	 * @param name the name
	 */
	public Throws(String name) {
		super.with(name);
	}

	/**
	 * With.
	 *
	 * @param name the name
	 * @return the throws
	 */
	@Override
	public Throws with(String name) {
		super.with(name);
		return this;
	}
}
