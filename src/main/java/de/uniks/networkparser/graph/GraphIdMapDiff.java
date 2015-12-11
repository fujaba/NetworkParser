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
	private HashSet<GraphClazz> toDoList=new HashSet<GraphClazz>();  
	private GraphList master;
	private GraphList slave;
//	private HashMap<GraphClazzDiff, HashSet<GraphEdgeDiff>> edges;
	
	public GraphIdMapDiff() {
		
	}

	public GraphIdMapDiff(IdMap map) {
		withCreator(map);	
	}
	
	protected void initItem(GraphMember item) {
		item.with(new GraphDiff());
		if(item instanceof GraphClazz) {
			if(this.getMaster() ==null) {
				this.toDoList.add((GraphClazz) item);
			}
		}
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
				GraphDiff diff = graphClazz.getDiff();
				diff.addCounter();
			}
		}
		// Copy all Edges
		for(Iterator<GraphEdge> i = objectDiagram.getEdges().iterator();i.hasNext();) {
			GraphEdge item = i.next();
			GraphClazz node = (GraphClazz) item.getNode();
			String signature = node.getName(false)+":"+item.getProperty();
			GraphEdge graphEdge = edges.get(signature);
			if(graphEdge != null) {
				GraphDiff diff = graphEdge.getDiff();
				diff.addCounter();
			}
		}
		return clazzDiagram;
	}

	
	public void diffModel(Object master, Object slave) {
		this.master = (GraphList)this.parsingObject(master);
		this.slave = (GraphList)this.parsingObject(slave);
		GraphDiff masterDiff = this.getMaster().getDiff();
		GraphDiff saveDiff = this.getSlave().getDiff();
		
		GraphClazz masterFile = (GraphClazz) masterDiff.getMainFile();
		GraphClazz slaveFile = (GraphClazz) saveDiff.getMainFile();
		masterFile.getDiff().with(slaveFile);
		
		
		// create new map<key: Clazz without s, Value: Object with {attributes, items}> 
		// Search for single clazz
		// Search for clazz with master attributes
		// search to 1 assoc
		// try to find in to n assoc
		searchMatch(masterFile);
	}

	private void searchMatch(GraphClazz master) {
		master.getChildren().iterator();
	}

	public GraphList getMaster() {
		return master;
	}
	
	public GraphList getSlave() {
		return slave;
	}
}
