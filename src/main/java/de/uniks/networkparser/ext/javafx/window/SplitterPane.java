package de.uniks.networkparser.ext.javafx.window;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
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
