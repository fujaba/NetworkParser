package de.uniks.networkparser.graph;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/



/**
 * Associations types
 * Edge           - Edge           : normal Edge
 * Association    - Association    : Bidirectional Association
 * Association    - Edge           : Undirectional but search for back Assoc
 * Undirectional  - Edge           : Undirectional Association
 * Aggregation    - Undirectional  : Undirectional Aggregation
 * Aggregation    - Edge           : Aggregation
 * Composition    - Edge           : Composition
 * Generalisation - Edge           : Generalisation
 * Implements     - Edge           : Implements
 * Dependency     - Edge           : Dependency
 * 
 * @author Stefan
 *
 */
public enum AssociationTypes {
	ASSOCIATION("assoc"), EDGE("edge"), GENERALISATION("generalisation"), IMPLEMENTS("implements"), UNDIRECTIONAL("unidirectional"), AGGREGATION("aggregation"), COMPOSITION("Composition"), DEPENDENCY("Dependency");

	private AssociationTypes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static boolean isEdge(AssociationTypes value) {
		if(value == null) {
			return false;
		}
		return (value.equals(ASSOCIATION) ||
				value.equals(EDGE) ||
				value.equals(UNDIRECTIONAL) ||
				value.equals(AGGREGATION) ||
				value.equals(COMPOSITION)
				);
	}
	public static Object isImplements(AssociationTypes value) {
		if(value == null) {
			return false;
		}
		return (value.equals(GENERALISATION) ||
				value.equals(IMPLEMENTS) ||
				value.equals(EDGE)
				);
		}
	private String value;
	
	@Override
	public String toString() {
		return this.value;
	}
}
