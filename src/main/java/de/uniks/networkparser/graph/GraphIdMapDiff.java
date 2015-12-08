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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.json.JsonArray;

public class GraphIdMapDiff extends GraphIdMap{
	private HashSet<GraphClazzDiff> toDoList=new HashSet<GraphClazzDiff>();  
	private GraphListDiff master;
	private GraphListDiff slave;
//	private HashMap<GraphClazzDiff, HashSet<GraphEdgeDiff>> edges;
	
	public GraphIdMapDiff() {
		
	}

	public GraphIdMapDiff(IdMap map) {
		withCreator(map);	
	}
	
	
	@Override
	public GraphEdge createEdge() {
		return new GraphEdgeDiff();
	}
	
	@Override
	public GraphEdgeDiff createEdge(GraphNode node, GraphCardinality cardinality, String property) {
		GraphEdgeDiff edge = new GraphEdgeDiff();
		edge.with(node, cardinality, property);
		return edge;
	}
	
	@Override
	public GraphAttribute createAttribute() {
		return new GraphAttributeDiff();
	}
	
	@Override
	public GraphList createList() {
		return new GraphListDiff();
	}
	
	@Override
	public GraphClazz createClazz() {
		GraphClazzDiff clazz = new GraphClazzDiff();
		if(this.getMaster() ==null) {
			this.toDoList.add(clazz);
		}
		return clazz;
	}

	public void highlightModel(JsonArray clazzDiagram, GraphList objectDiagram) {
		GraphList list = new GraphConverter().convertGraphList(GraphIdMap.CLASS, clazzDiagram, true);
		this.highlightModel(list, objectDiagram);
	}
	public GraphList highlightModel(GraphList clazzDiagram, GraphList objectDiagram) {
		HashMap<String, GraphEdge> edges = new HashMap<String, GraphEdge>();
		HashMap<String, GraphClazz> clazzes = new HashMap<String, GraphClazz>();
		
		// Copy all Nodes
		for(Iterator<GraphMember> i = clazzDiagram.getChildren().iterator();i.hasNext();) {
			GraphClazz item = (GraphClazz) i.next();
			clazzes.put(item.getName(false), item);
		}
		// Copy all Edges
		for(Iterator<GraphEdge> i = clazzDiagram.getEdges().iterator();i.hasNext();) {
			GraphEdge item = i.next();
			GraphClazz node = (GraphClazz) item.getNode();
			edges.put(node.getName(false)+":"+item.getProperty(), item);
		}
		
		// Check all Clazzes of the objectdiagram
		for(Iterator<GraphMember> i = objectDiagram.getChildren().iterator();i.hasNext();) {
			GraphClazz item = (GraphClazz) i.next();
			GraphClazz graphClazz = clazzes.get(item.getName(false));
			if(graphClazz != null) {
				graphClazz.addCounter();
			}
		}
		// Copy all Edges
		for(Iterator<GraphEdge> i = objectDiagram.getEdges().iterator();i.hasNext();) {
			GraphEdge item = i.next();
			GraphClazz node = (GraphClazz) item.getNode();
			String signature = node.getName(false)+":"+item.getProperty();
			GraphEdge graphEdge = edges.get(signature);
			if(graphEdge != null) {
				graphEdge.addCounter();
			}
		}
		return clazzDiagram;
	}

	
	public void diffModel(Object master, Object slave) {
		this.master = (GraphListDiff)this.parsingObject(master);
		this.slave = (GraphListDiff)this.parsingObject(slave);
		GraphClazzDiff masterFile = (GraphClazzDiff) this.getMaster().getMainFile();
		GraphClazzDiff slaveFile = (GraphClazzDiff) this.slave.getMainFile();
		masterFile.withMatch(slaveFile);
		
		
		// create new map<key: Clazz without s, Value: Object with {attributes, items}> 
		// Search for single clazz
		// Search for clazz with master attributes
		// search to 1 assoc
		// try to find in to n assoc
		searchMatch(masterFile);
	}

	private void searchMatch(GraphClazzDiff master) {
		master.getChildren().iterator();
	}

	public GraphListDiff getMaster() {
		return master;
	}
	
	public GraphListDiff getSlave() {
		return slave;
	}
}
