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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import de.uniks.networkparser.ArrayEntryList;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BaseKeyValueEntity;
import de.uniks.networkparser.interfaces.BaseListEntity;

public class GraphList extends BaseKeyValueEntity {
	private LinkedHashMap<String, GraphNode> children = new LinkedHashMap<String, GraphNode>();
	private ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
	private String typ;


	public GraphList with(GraphNode... values) {
		for(Object value : values){
			if (value instanceof GraphNode) {
				GraphNode entity = (GraphNode) value;
				children.put(entity.getId(), entity);
			}
		}
		return this;
	}

	@Override
	public int size() {
		return children.size();
	}

	public boolean add(GraphNode value) {
		GraphNode entity = (GraphNode) value;
		children.put(entity.getId(), entity);
		return true;
	}

	@Override
	public GraphNode get(int z) {
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
	public String toString() {
		return toString(new YUMLConverter());
	}

	public String toString(Converter converter) {
		return converter.convert(this, false);
	}

	public String getTyp() {
		return typ;
	}

	public GraphList withTyp(String typ) {
		this.typ = typ;
		return this;
	}
	public GraphList withEdge(GraphEdge edge) {
		 addEdge(edge);
		 return this;
	}
	public GraphList withEdge(String sourceName, String targetName) {
		GraphEdge edge = new GraphEdge()
			.withSource(new GraphNode().withClassName(sourceName))
			.withTarget(new GraphNode().withClassName(targetName));
		addEdge(edge);
		return this;
	}
	
	public boolean addEdge(GraphEdge edge) {
		for(Iterator<GraphEdge> i = this.edges.iterator();i.hasNext();){
			GraphEdge item = i.next();
			if(item.getSource().has(edge.getTarget().getItems()) && item.getTarget().has(edge.getSource().getItems())){
				// Back again
				item.getSource().withCardinality(edge.getTarget().getCardinality());
				item.getSource().withProperty(edge.getTarget().getProperty());
				return false;
			}
		}
		return this.edges.add(edge);
	}

	public ArrayList<GraphEdge> getEdges() {
		return edges;
	}
	
	public ArrayEntryList getLinks(){
		ArrayEntryList links = new ArrayEntryList();
		for (GraphEdge element : edges) {
			for(GraphNode node : element.getSource().getItems()){
				String key = node.getTyp(typ, false);
				EdgeList value = (EdgeList)links.getValue(key);
				if(value!=null){
					value.with(element);
				}else{
					EdgeList simpleList = new EdgeList();
					simpleList.add(element);
					links.put(key, simpleList);
				}
			}
		}
		return links;
	}
	
	public Collection<GraphNode> getChildren() {
		return children.values();
	}

	@Override
	public BaseItem getNewObject() {
		return new GraphEdge();
	}

	@Override
	public GraphList getNewArray() {
		return new GraphList();
	}

	@Override
	public BaseListEntity with(Collection<?> values) {
		for(Iterator<?> i = values.iterator();i.hasNext();){
			with(i.next());
		}
		return this;
	}

	@Override
	public BaseListEntity with(Object value) {
		if(value instanceof GraphNode){
			this.add((GraphNode)value);
		}
		return this;
	}
}
