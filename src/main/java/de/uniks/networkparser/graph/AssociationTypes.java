package de.uniks.networkparser.graph;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
 * Associations types Edge - Edge : normal Edge Association - Association :
 * Bidirectional Association Association - Edge : Undirectional but search for
 * back Assoc Undirectional - Edge : Undirectional Association Aggregation -
 * Undirectional : Undirectional Aggregation Aggregation - Edge : Aggregation
 * Composition - Edge : Composition Generalisation - Edge : Generalisation
 * Implements - Edge : Implements Dependency - Edge : Dependency
 *
 * @author Stefan
 *
 */
public class AssociationTypes {
	public static final AssociationTypes ASSOCIATION = new AssociationTypes("assoc");
	public static final AssociationTypes EDGE = new AssociationTypes("edge");
	public static final AssociationTypes GENERALISATION = new AssociationTypes("generalisation");
	public static final AssociationTypes IMPLEMENTS = new AssociationTypes("implements");
	public static final AssociationTypes UNDIRECTIONAL = new AssociationTypes("unidirectional");
	public static final AssociationTypes AGGREGATION = new AssociationTypes("aggregation");
	public static final AssociationTypes COMPOSITION = new AssociationTypes("composition");
	public static final AssociationTypes DEPENDENCY = new AssociationTypes("dependency");

	private AssociationTypes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static boolean isEdge(AssociationTypes value) {
		if (value == null) {
			return false;
		}
		return (value.equals(ASSOCIATION) || value.equals(EDGE) || value.equals(UNDIRECTIONAL)
				|| value.equals(AGGREGATION) || value.equals(COMPOSITION));
	}

	public boolean IsSame(Object value) {
		if (value instanceof AssociationTypes) {
			return this == value;
		}
		if (value instanceof String && this.getValue() != null) {
			return this.getValue().equals(value);
		}
		return false;
	}

	public static boolean isImplements(AssociationTypes value) {
		if (value == null) {
			return false;
		}
		return (value.equals(GENERALISATION) || value.equals(IMPLEMENTS) || value.equals(EDGE));
	}

	private String value;

	@Override
	public String toString() {
		return this.value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof String) {
			return ((String) obj).equalsIgnoreCase(this.getValue());
		}
		return super.equals(obj);
	}

	public static AssociationTypes create(String value) {
		if (ASSOCIATION.equals(value)) {
			return ASSOCIATION;
		}
		if ("association".equalsIgnoreCase(value)) {
			return ASSOCIATION;
		}
		if (EDGE.equals(value)) {
			return EDGE;
		}
		if (GENERALISATION.equals(value)) {
			return GENERALISATION;
		}
		if (IMPLEMENTS.equals(value)) {
			return IMPLEMENTS;
		}
		if (UNDIRECTIONAL.equals(value)) {
			return UNDIRECTIONAL;
		}
		if (AGGREGATION.equals(value)) {
			return AGGREGATION;
		}
		if (COMPOSITION.equals(value)) {
			return COMPOSITION;
		}
		if (DEPENDENCY.equals(value)) {
			return DEPENDENCY;
		}
		return null;
	}
}
