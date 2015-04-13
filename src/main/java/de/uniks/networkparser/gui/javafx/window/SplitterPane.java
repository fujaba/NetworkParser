package de.uniks.networkparser.gui.javafx.window;

import java.util.ArrayList;
import java.util.Iterator;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class SplitterPane extends SplitPane{
	private ArrayList<Node> nodes=new ArrayList<Node>(); 
	public void maximize(Node node){
		for(Iterator<Node> i = this.getItems().iterator();i.hasNext();){
			if(i.next() != node) {
				i.remove();
			}
		}
		setDividerPositions(1);
	}
	
	public SplitterPane withDividor(double pos){
		this.getItems().clear();
		for(Iterator<Node> i = nodes.iterator();i.hasNext();){
			this.getItems().add(i.next());
		}
		
		if(pos<=1) {
			this.setDividerPositions(pos);
		}else {
			this.setDividerPositions(this.getHeight() / pos);
		}
		
		return this;
	}
	
	public SplitterPane withNodes(Node... nodes) {
		if(nodes == null) {
			return this;
		}
		for(Node node : nodes) {
			this.nodes.add(node);
			this.getItems().add(node);
		}
		return this;
	}
}
