package de.uniks.networkparser.graph;

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

public class GraphDiff extends GraphMember{
	private int count;
	private GraphMember match;
	private GraphEntity mainFile;
	private Object oldValue;
	private Object newValue;
	private String type;

	public GraphDiff withMain(GraphEntity node) {
		this.mainFile = node;
		return this;
	}

	public GraphEntity getMainFile() {
		return mainFile;
	}

	public GraphMember getMatch() {
		return match;
	}

	public GraphDiff with(GraphMember value) {
		if (this.match != value) {
			GraphMember oldValue = this.match;
			if (oldValue != null) {
				this.match = null;
				oldValue.without(this);
			}
			this.match = value;
			if (value != null) {
				value.withChildren(this);
			}
		}
		return this;
	}

	public int getCount() {
		return count;
	}
	protected void addCounter() {
		this.count++;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public boolean setOldValue(Object oldValue) {
		if(oldValue != this.oldValue) {
			this.oldValue = oldValue;
			return true;
		}
		return false;
	}
	
	public GraphDiff withOldValue(Object oldValue) {
		setOldValue(oldValue);
		return this;
	}

	public Object getNewValue() {
		return newValue;
	}

	public boolean setNewValue(Object newValue) {
		if(newValue != this.newValue) {
			this.newValue = newValue;
			return true;
		}
		return false;
	}
	
	public GraphDiff withNewValue(Object newValue) {
		setNewValue(newValue);
		return this;
	}
	
	public GraphDiff withType(String value) {
		this.type = value;
		return this;
	}
	
	public String getType() {
		return this.type;
	}

}
