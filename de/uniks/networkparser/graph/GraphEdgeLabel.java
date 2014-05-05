package de.uniks.networkparser.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class GraphEdgeLabel {
	private ArrayList<GraphNode> items = new ArrayList<GraphNode>();
	private String cardinality;
	private String property;
	
	public ArrayList<GraphNode> getItems() {
		return items;
	}
	public GraphEdgeLabel withItem(GraphNode item) {
		this.items.add(item);
		return this;
	}
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
	public Iterator<GraphNode> iterator(){
		return items.iterator();
	}
	
	public String getInfo(){
		return property+"<br>0.."+this.cardinality;
	}
	
	public boolean has(GraphNode node){
		return items.contains(node);
	}
	
	public boolean has(Collection<GraphNode> nodes){
		for(GraphNode node : nodes){
			if(!items.contains(node)){
				return false;
			}
		}
		return true;
	}
}
