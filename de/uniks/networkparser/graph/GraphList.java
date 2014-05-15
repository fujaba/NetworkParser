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
import java.util.Iterator;

import de.uniks.networkparser.AbstractKeyValueEntry;
import de.uniks.networkparser.AbstractKeyValueList;
import de.uniks.networkparser.AbstractList;
import de.uniks.networkparser.ArrayEntryList;
import de.uniks.networkparser.event.SimpleMapEntry;

public class GraphList extends AbstractKeyValueList<String, GraphNode> {
	private ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
	private String typ;

	public boolean add(GraphNode value) {
		put(value.getId(), value);
		return true;
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
			if(item.getSource().containsAll(edge.getTarget().values()) && item.getTarget().containsAll(edge.getSource().values())){
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
			for(GraphNode node : element.getSource().values()){
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

	@Override
	public AbstractKeyValueEntry<String, GraphNode> getNewEntity() {
		return new SimpleMapEntry<String, GraphNode>();
	}

	@Override
	public AbstractList<AbstractKeyValueEntry<String, GraphNode>> getNewInstance() {
		return new GraphList();
	}
	
	@Override
	public GraphList with(Object... values){
		if(values != null){
			for(Object value : values){
				if(value instanceof GraphNode){
					this.add((GraphNode)value);
				}
			}
		}
		return this;
	}
}
