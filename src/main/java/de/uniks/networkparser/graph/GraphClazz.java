package de.uniks.networkparser.graph;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
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

public class GraphClazz extends GraphNode {
	private String className;

	public String getClassName(boolean shortName) {
		if (!shortName || className == null || className.lastIndexOf(".") < 0) {
			return className;
		}
		return className.substring(className.lastIndexOf(".") + 1);
	}

	public GraphClazz withClassName(String className) {
		this.className = className;
		return this;
	}

	public String getClassName() {
		return className;
	}

	public String getTyp(String typ, boolean shortName) {
		if (typ.equals(GraphIdMap.OBJECT)) {
			return getId();
		} else if (typ.equals(GraphIdMap.CLASS)) {
			return getClassName(shortName);
		}
		return "";
	}

	@Override
	public GraphClazz withTyp(String typ, String value) {
		if (typ.equals(GraphIdMap.OBJECT)) {
			withId(value);
		} else if (typ.equals(GraphIdMap.CLASS)) {
			withClassName(value);
		}
		return this;
	}

	@Override
	public String toString() {
		if (getId() == null) {
			return className;
		}
		return getId();
	}

	public GraphClazz withId(String id) {
		super.withId(id);
		return this;
	}
	
	public GraphClazz withParent(GraphNode value) {
		super.withParent(value);
		return this;
	}
}
