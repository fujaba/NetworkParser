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

public class GraphDiff extends GraphMember{
	private int count;
	private GraphMember match;
	private GraphEntity mainFile;

	public GraphDiff withMain(GraphEntity node) {
		this.mainFile = node;
		return this;
	}

	public GraphEntity getMainFile() {
		return mainFile;
	}

	public GraphMember getMatch() {
		return match;
	}

	public GraphDiff with(GraphMember value) {
		if (this.match != value) {
			GraphMember oldValue = this.match;
			if (oldValue != null) {
				this.match = null;
				oldValue.without(this);
			}
			this.match = value;
			if (value != null) {
				value.withChildren(this);
			}
		}
		return this;
	}

	public int getCount() {
		return count;
	}
	void addCounter() {
		this.count++;
	}
}