package de.uniks.networkparser.graph;

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
	public GraphEdge createEdge(GraphNode node, GraphCardinality cardinality, String property) {
		return new GraphEdgeDiff(node, cardinality, property);
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
		for(Iterator<GraphMember> i = clazzDiagram.iterator();i.hasNext();) {
			GraphClazz item = (GraphClazz) i.next();
			clazzes.put(item.getClassName(), item);
		}
		// Copy all Edges
		for(Iterator<GraphEdge> i = clazzDiagram.getEdges().iterator();i.hasNext();) {
			GraphEdge item = i.next();
			GraphClazz node = (GraphClazz) item.getNode();
			edges.put(node.getClassName()+":"+item.getProperty(), item);
		}
		
		// Check all Clazzes of the objectdiagram
		for(Iterator<GraphMember> i = objectDiagram.iterator();i.hasNext();) {
			GraphClazz item = (GraphClazz) i.next();
			GraphClazz graphClazz = clazzes.get(item.getClassName());
			if(graphClazz != null) {
				graphClazz.addCounter();
			}
		}
		// Copy all Edges
		for(Iterator<GraphEdge> i = objectDiagram.getEdges().iterator();i.hasNext();) {
			GraphEdge item = i.next();
			GraphClazz node = (GraphClazz) item.getNode();
			String signature = node.getClassName()+":"+item.getProperty();
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
		master.iterator();
	}

	public GraphListDiff getMaster() {
		return master;
	}
	
	public GraphListDiff getSlave() {
		return slave;
	}
}
