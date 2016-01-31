package de.uniks.networkparser.gui.javafx.window;

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
