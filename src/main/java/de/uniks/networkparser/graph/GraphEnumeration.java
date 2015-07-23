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
public class GraphEnumeration extends GraphAbstractClazz {
	public GraphEnumeration withId(String id) {
		super.withId(id);
		return this;
	}
	public GraphEnumeration withParent(GraphNode value) {
		super.withParent(value);
		return this;
	}
	public GraphEnumeration withExternal(boolean value) {
		super.withExternal(value);
		return this;
	}
	public GraphEnumeration withTyp(String typ, String value) {
		super.withTyp(typ, value);
		return this;
	}
}
