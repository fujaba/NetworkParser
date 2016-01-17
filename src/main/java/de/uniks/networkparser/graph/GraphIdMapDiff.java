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
	private HashSet<Clazz> toDoList=new HashSet<Clazz>();  
	private GraphList master;
	private GraphList slave;
//	private HashMap<GraphClazzDiff, HashSet<GraphEdgeDiff>> edges;
	
	public GraphIdMapDiff() {
		
	}

	public GraphIdMapDiff(IdMap map) {
		with(map);	
	}
	
	protected void initItem(GraphMember item) {
		item.withChildren(true, new GraphDiff());
		if(item instanceof Clazz) {
			if(this.getMaster() ==null) {
				this.toDoList.add((Clazz) item);
			}
		}
	}
	public void highlightModel(JsonArray clazzDiagram, GraphList objectDiagram) {
		GraphList list = new GraphConverter().convertGraphList(GraphIdMap.CLASS, clazzDiagram, true);
		this.highlightModel(list, objectDiagram);
	}
	public GraphList highlightModel(GraphList clazzDiagram, GraphList objectDiagram) {
		HashMap<String, Association> edges = new HashMap<String, Association>();
		HashMap<String, Clazz> clazzes = new HashMap<String, Clazz>();
		
		// Copy all Nodes
		for(Iterator<GraphMember> i = clazzDiagram.getChildren().iterator();i.hasNext();) {
			Clazz item = (Clazz) i.next();
			clazzes.put(item.getName(false), item);
		}
		// Copy all Edges
		for(Iterator<Association> i = clazzDiagram.getEdges().iterator();i.hasNext();) {
			Association item = i.next();
			Clazz node = (Clazz) item.getClazz();
			edges.put(node.getName(false)+":"+item.getName(), item);
		}
		
		// Check all Clazzes of the objectdiagram
		for(Iterator<GraphMember> i = objectDiagram.getChildren().iterator();i.hasNext();) {
			Clazz item = (Clazz) i.next();
			Clazz graphClazz = clazzes.get(item.getName(false));
			if(graphClazz != null) {
				GraphDiff diff = graphClazz.getDiff();
				diff.addCounter();
			}
		}
		// Copy all Edges
		for(Iterator<Association> i = objectDiagram.getEdges().iterator();i.hasNext();) {
			Association item = i.next();
			Clazz node = (Clazz) item.getClazz();
			String signature = node.getName(false)+":"+item.getName();
			Association graphEdge = edges.get(signature);
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
		
		Clazz masterFile = (Clazz) masterDiff.getMainFile();
		Clazz slaveFile = (Clazz) saveDiff.getMainFile();
		masterFile.getDiff().with(slaveFile);
		
		
		// create new map<key: Clazz without s, Value: Object with {attributes, items}> 
		// Search for single clazz
		// Search for clazz with master attributes
		// search to 1 assoc
		// try to find in to n assoc
		searchMatch(masterFile);
	}

	private void searchMatch(Clazz master) {
		master.getChildren().iterator();
	}

	public GraphList getMaster() {
		return master;
	}
	
	public GraphList getSlave() {
		return slave;
	}
}
