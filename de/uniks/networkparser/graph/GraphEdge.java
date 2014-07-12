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
import java.util.List;

import de.uniks.networkparser.AbstractEntityList;
import de.uniks.networkparser.AbstractList;

public class GraphEdge extends AbstractEntityList<GraphNode> implements List<GraphNode>{
	public static final String PROPERTY_NODE = "node";
	public static final String PROPERTY_CARDINALITY = "cardinality";
	public static final String PROPERTY_PROPERTY = "property";
	private Cardinality cardinality;
	private String property;
	private GraphEdge other;
	
	public GraphEdge(){
		
	}

	public GraphEdge(GraphNode node, Cardinality cardinality, String property) {
		with((Object)node);
		with(cardinality);
		with(property);
	}



	public Cardinality getCardinality() {
		return cardinality;
	}
	
	public String getCardinalityText() {
		return cardinality.getValue();
	}

	public String getProperty() {
		return property;
	}
	public GraphEdge with(String property) {
		this.property = property;
		return this;
	}

	public String getInfo(){
		return property+"<br>0.."+this.cardinality;
	}
	
	@Override
	public AbstractList<GraphNode> getNewInstance() {
		return new GraphEdge();
	}
	
	@Override
	public GraphEdge with(Object... values) {
		if(values==null){
			return this;
		}
		for(Object value : values){
			if(value instanceof GraphNode){
				add((GraphNode) value);
			}
			if(value instanceof GraphEdge){
				with((GraphEdge)value);
			}
			if(value instanceof Cardinality){
				with((Cardinality)value);
			}
		}
		return this;
	}
	
	public GraphEdge with(GraphEdge value){
		 if (this.getOther() == value){
	         return this;
	      }
		 this.other = value;
		 getOther().with(this);
		 return this;
	}
	
	public GraphEdge with(Cardinality cardinality) {
		this.cardinality = cardinality;
		return this;
	}

	@Override
	public boolean add(GraphNode newValue) {
		if(super.addEntity(newValue)){
			newValue.with(this);
		}
		return true;
	}

	public GraphEdge getOther() {
		return other;
	}

	@Override
	public boolean remove(Object value) {
		return removeItemByObject((GraphNode)value)>=0;
	}
	
	public List<GraphNode> values(){
		return keys;
	}
}
