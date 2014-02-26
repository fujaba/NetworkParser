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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import de.uniks.networkparser.interfaces.BaseEntity;
import de.uniks.networkparser.interfaces.BaseEntityList;

public class GraphList implements BaseEntityList {
	private LinkedHashMap<String, GraphNode> children = new LinkedHashMap<String, GraphNode>();
	private ArrayList<GraphEdge> cardinalities = new ArrayList<GraphEdge>();
	private String typ;

	@Override
	public BaseEntityList initWithMap(Collection<?> value) {
		for (Iterator<?> i = value.iterator(); i.hasNext();) {
			Object item = i.next();
			if (item instanceof GraphNode) {
				GraphNode entity = (GraphNode) item;
				children.put(entity.getId(), entity);
			}
		}
		return this;
	}

	@Override
	public BaseEntityList put(Object value) {
		if (value instanceof GraphNode) {
			GraphNode entity = (GraphNode) value;
			children.put(entity.getId(), entity);
		}
		return this;
	}

	@Override
	public int size() {
		return children.size();
	}

	@Override
	public boolean add(Object value) {
		if (value instanceof GraphNode) {
			GraphNode entity = (GraphNode) value;
			children.put(entity.getId(), entity);
			return true;
		}
		return false;
	}

	@Override
	public Object get(int z) {
		Iterator<Entry<String, GraphNode>> iterator = children.entrySet()
				.iterator();
		while (z > 0 && iterator.hasNext()) {
			iterator.next();
		}
		if (z == 0) {
			return iterator.next().getValue();
		}
		return null;
	}

	public GraphNode getById(String id) {
		return children.get(id);
	}

	@Override
	public BaseEntityList getNewArray() {
		return new GraphList();
	}

	@Override
	public BaseEntity getNewObject() {
		return new GraphNode();
	}

	@Override
	public String toString() {
		return toString(new YUMLConverter());
	}

	@Override
	public String toString(int indentFactor) {
		return toString();
	}

	@Override
	public String toString(int indentFactor, int intent) {
		return toString();
	}

	public String toString(Converter converter) {
		return converter.convert(this);
	}

	@Override
	public BaseEntity withVisible(boolean value) {
		return this;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	public String getTyp() {
		return typ;
	}

	public GraphList withTyp(String typ) {
		this.typ = typ;
		return this;
	}

	public boolean addCardinality(GraphEdge cardinality) {
		return this.cardinalities.add(cardinality);
	}

	public ArrayList<GraphEdge> getEdges() {
		return cardinalities;
	}
	
	public HashMap<String, HashSet<GraphEdge>> getLinks(){
		HashMap<String, HashSet<GraphEdge>> links = new HashMap<String, HashSet<GraphEdge>>();
		for (GraphEdge element : cardinalities) {
			for(GraphNode node : element.getSource().getItems()){
				String key = node.getTyp(typ);
				if (links.containsKey(key)) {
					links.get(key).add(element);
				} else {
					HashSet<GraphEdge> hashSet = new HashSet<GraphEdge>();
					hashSet.add(element);
					links.put(key, hashSet);
				}
			}
		}
		return links;
	}
	
	public Collection<GraphNode> getChildren() {
		return children.values();
	}
}
