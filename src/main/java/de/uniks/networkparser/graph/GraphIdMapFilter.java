package de.uniks.networkparser.graph;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
 */
import de.uniks.networkparser.Filter;

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
	public GraphIdMapFilter clone(Filter newInstance) {
		return (GraphIdMapFilter) super.clone(newInstance);
	}

	public String getTyp() {
		return typ;
	}

	public GraphIdMapFilter withTyp(String typ) {
		this.typ = typ;
		return this;
	}
}
