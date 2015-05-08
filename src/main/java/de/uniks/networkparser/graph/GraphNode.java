package de.uniks.networkparser.graph;

import de.uniks.networkparser.interfaces.BaseItem;

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

public class GraphNode extends GraphSimpleList<GraphMember> implements GraphMember{
	private String id;
	private int count;
	private GraphNode parentNode;

	// GETTER AND SETTER
	public String getId() {
		return id;
	}

	public String getTyp(String typ, boolean shortName) {
		return getId();
	}

	public GraphNode withTyp(String typ, String value) {
		withId(value);
		return this;
	}

	public GraphNode withId(String id) {
		this.id = id;
		return this;
	}

	public void addValue(String property, GraphDataType clazz, String value) {
		add(new GraphAttribute().withValue(value).with(property).with(clazz));
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new GraphNode();
	}

	@Override
	public GraphNode withAll(Object... values) {
		if (values != null) {
			for (Object value : values) {
				if (value instanceof GraphAttribute) {
					add((GraphAttribute) value);
				}else if (value instanceof GraphMethod) {
					add((GraphMethod) value);
				}else  if (value instanceof GraphClazz) {
					GraphClazz child = (GraphClazz) value;
					add(child);
					child.withParent(this);
				}
			}
		}
		return this;
	}
	
	public GraphMember getByObject(String clazz, boolean fullName) {
		if(clazz == null){
			return null;
		}
		String sub = clazz;
		if(clazz.lastIndexOf(".")>=0) {
			sub = clazz.substring(clazz.lastIndexOf(".")+1);
		}
		for(GraphMember item : this) {
			if(clazz.equalsIgnoreCase(item.getId()) || sub.equalsIgnoreCase(item.getId())){
				return item;
			}
		}
		if(fullName || clazz.lastIndexOf(".") < 0) {
			return null;
		}
		sub = "."+clazz.substring(clazz.lastIndexOf(".")+1);
		for(GraphMember item : this) {
			if(item.getId().endsWith(clazz)){
				return item;
			}
		}
		return null;
	}
	
	public GraphNode withParent(GraphNode value) {
		if (this.parentNode != value) {
			GraphNode oldValue = this.parentNode;
			if (this.parentNode != null) {
				this.parentNode = null;
				oldValue.without(this);
			}
			this.parentNode = value;
			if (value != null) {
				value.withAll(this);
			}
		}
		return this;
	}
	
	public GraphNode getParent(GraphNode value) {
		return parentNode;
	}

	@Override
	public boolean remove(Object value) {
		return removeItemByObject((GraphMember) value) >= 0;
	}
	
	int getCount() {
		return count;
	}
	void addCounter() {
		this.count++;
	}
	GraphNode withCount(int count) {
		this.count = count;
		return this;
	}
}
