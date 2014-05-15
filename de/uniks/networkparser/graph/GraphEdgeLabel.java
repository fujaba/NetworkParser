package de.uniks.networkparser.graph;
import de.uniks.networkparser.AbstractEntityList;
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
import de.uniks.networkparser.AbstractList;

public class GraphEdgeLabel extends AbstractEntityList<GraphNode> {
	private String cardinality;
	private String property;

	public String getCardinality() {
		return cardinality;
	}
	public GraphEdgeLabel withCardinality(String cardinality) {
		this.cardinality = cardinality;
		return this;
	}

	public String getProperty() {
		return property;
	}
	public GraphEdgeLabel withProperty(String property) {
		this.property = property;
		return this;
	}

	public String getInfo(){
		return property+"<br>0.."+this.cardinality;
	}
	
	@Override
	public AbstractList<GraphNode> getNewInstance() {
		return new GraphEdgeLabel();
	}
	
	@Override
	public GraphEdgeLabel with(Object... values) {
		for(Object value : values){
			if(value instanceof GraphNode){
				add((GraphNode) value);
			}
		}
		return this;
	}
}
