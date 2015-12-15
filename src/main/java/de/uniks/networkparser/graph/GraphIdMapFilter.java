package de.uniks.networkparser.graph;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;

public class GraphIdMapFilter extends Filter {
	/** The show line. */
	private boolean isShowLine;

	private boolean isShowCardinality;

	private String typ;

	/**
	 * Checks if is show line.
	 *
	 * @return true, if is show line for objects
	 */
	public boolean isShowLine() {
		return this.isShowLine;
	}

	/**
	 * Sets the show line.
	 *
	 * @param value
	 *            the new show line
	 * @return Itself
	 */
	public GraphIdMapFilter withShowLine(boolean value) {
		this.isShowLine = value;
		return this;
	}

	public boolean isShowCardinality() {
		return isShowCardinality;
	}

	public GraphIdMapFilter withShowCardinality(boolean value) {
		this.isShowCardinality = value;
		return this;
	}
	
	@Override
	public GraphIdMapFilter newInstance(Filter referenceFilter) {
		if(referenceFilter == null) {
			referenceFilter = new GraphIdMapFilter();
		}
		return (GraphIdMapFilter) super.newInstance(referenceFilter);
	}

	public String getTyp() {
		return typ;
	}

	public GraphIdMapFilter withTyp(String typ) {
		this.typ = typ;
		return this;
	}
	
	public GraphIdMapFilter withMap(IdMap map) {
		super.withMap(map);
		return this;
	}
}
