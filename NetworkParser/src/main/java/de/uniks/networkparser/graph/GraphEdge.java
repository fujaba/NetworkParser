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

import de.uniks.networkparser.list.SimpleList;

public class GraphEdge extends GraphSimpleList<GraphNode> implements
		List<GraphNode> {
	public static final String PROPERTY_NODE = "node";
	public static final String PROPERTY_CARDINALITY = "cardinality";
	public static final String PROPERTY_PROPERTY = "property";
	private GraphCardinality cardinality;
	private GraphLabel property;
	private GraphLabel info;
	private GraphEdge other;
	private GraphEdgeTypes typ = GraphEdgeTypes.EDGE;
	private int count;

	public GraphEdge() {

	}

	public GraphEdge(GraphNode node, GraphCardinality cardinality, String property) {
		with(node);
		with(cardinality);
		with(property);
	}

	public GraphCardinality getCardinality() {
		if(cardinality != null) {
			return cardinality;
		}
		if(this.size() > 1){
			return GraphCardinality.MANY;
		}
		return GraphCardinality.ONE;
	}
	
	public String getCardinalityValue() {
		return cardinality.getValue();
	}
	
	public String getCardinalityText() {
		return property + "<br>0.." + this.cardinality;
	}

	public String getProperty() {
		if(property != null) {
			return property.getValue();
		}
		if(this.size() == 1) {
			GraphNode item = this.get(0);
			if(item instanceof GraphClazz) {
				return ((GraphClazz)item).getClassName(true).toLowerCase();	
			}
			
		}
		return null;
	}

	public GraphEdge with(String value) {
		this.property = GraphLabel.create(value);
		return this;
	}
	
	public GraphEdge withInfo(String value) {
		if(info != null) {
			this.info.withValue(value);
			return this;
		}
		this.info = GraphLabel.create(value);
		return this;
	}
	
	public GraphEdge withInfo(GraphLabel value) {
		this.info = value;
		return this;
	}
	
	public GraphEdge withStyle(String value) {
		if(info != null) {
			this.info.withStyle(value);
			return this;
		}
		this.info = new GraphLabel().withStyle(value);
		return this;
	}
	
	public GraphLabel getInfo() {
		return info;
	}


	@Override
	public SimpleList<GraphNode> getNewInstance() {
		return new GraphEdge();
	}

	@Override
	public GraphEdge with(Object... values) {
		if (values == null) {
			return this;
		}
		for (Object value : values) {
			if (value instanceof GraphNode) {
				add((GraphNode) value);
			}
			if (value instanceof GraphEdge) {
				with((GraphEdge) value);
			}
			if (value instanceof GraphCardinality) {
				with((GraphCardinality) value);
			}
		}
		return this;
	}

	public GraphEdge with(GraphEdge value) {
		if (this.getOther() == value) {
			return this;
		}
		this.other = value;
		getOther().with(this);
		return this;
	}

	public GraphEdge with(GraphCardinality cardinality) {
		this.cardinality = cardinality;
		return this;
	}

	@Override
	public boolean add(GraphNode newValue) {
		if (super.add(newValue)) {
			newValue.with(this);
		}
		return true;
	}

	public GraphEdge getOther() {
		return other;
	}

	@Override
	public boolean remove(Object value) {
		return removeItemByObject((GraphNode) value) >= 0;
	}

	public static GraphEdge create(GraphNode source, GraphNode target){
		GraphEdge edge = new GraphEdge().with(source);
		edge.with(new GraphEdge().with(target));
		return edge;
	}

	public GraphEdgeTypes getTyp() {
		return typ;
	}

	public GraphEdge withTyp(GraphEdgeTypes typ) {
		this.typ = typ;
		return this;
	}

	public int getCount() {
		return count;
	}

	public GraphEdge withCount(int count) {
		this.count = count;
		return this;
	}
	
	public GraphNode getNode() {
		if(size()>0) {
			return get(0);
		}
		return null;
	}

	public void addCounter() {
		this.count++;
	}
}
