package de.uniks.networkparser.graph;

import de.uniks.networkparser.converter.YUMLConverter;
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
import de.uniks.networkparser.event.SimpleMapEntry;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleSet;

public class GraphList extends GraphModel implements BaseItem{
	private String typ=GraphIdMap.CLASS;
	private String style;
	private GraphOptions options;

	@Override
	public String toString() {
		return toString(new YUMLConverter());
	}

	public String toString(Converter converter) {
		if(converter == null) {
			return null;
		}
		return converter.encode(this);
	}
	
	@Override
	public String toString(int indentFactor) {
		return toString();
	}

	public String getTyp() {
		return typ;
	}

	public GraphList withTyp(String typ) {
		this.typ = typ;
		return this;
	}

	public GraphList withEdge(String sourceName, String targetName) {
		Association edge = new Association().with(sourceName).with(
				new Association().with(targetName));
		super.with(edge);
		return this;
	}

	public void initSubLinks() {
		for(GraphEntity node : getNodes()) {
			if(node instanceof Clazz == false) {
				continue;
			}
			Clazz graphClazz = (Clazz) node;
			SimpleSet<Association> childEdges = graphClazz.getAssociations();
			for(Association edge : childEdges) {
				SimpleSet<Association> associations = getAssociations();
				if(associations.contains(edge) == false && associations.contains(edge.getOther()) == false) {
					super.with(edge);
				}
			}
		}
	}

	public Clazz with(Clazz value) {
		if (value != null) {
			if(value.getName()==null){
				value.with(value.getName(false));
			}
			super.with(value);
		}
		return value;
	}

	public GraphList with(GraphList... values) {
		super.withChildren(values);
		return this;
	}

	public GraphPattern with(GraphPattern value) {
		super.withChildren(value);
		return value;
	}

	public GraphList withNode(GraphEntity... value) {
		super.withChildren(value);
		return this;
	}

	public GraphOptions getOptions() {
		return options;
	}

	public GraphList withOptions(GraphOptions options) {
		this.options = options;
		return this;
	}

	public String getStyle() {
		return style;
	}

	public GraphList withStyle(String style) {
		this.style = style;
		return this;
	}

	public Clazz getNode(String id) {
		if(id==null){
			return null;
		}
		for(GraphMember item : this.getChildren()) {
			if(item instanceof Clazz && id.equalsIgnoreCase(item.getFullId())){
				return (Clazz)item;
			}
		}
		return null;
	}


	public SimpleSet<GraphEntity> getNodes() {
		return super.getNodes();
	}

	public Association getEdge(GraphEntity node, String property) {
		for(Association edge : getAssociations()) {
			Association oEdge = edge.getOther();
			if(edge.getClazz()==node && property.equals(oEdge.getName())) {
				return edge;
			}else if(oEdge.getClazz()==node && property.equals(edge.getName())) {
				return oEdge;
			}
		}
		return null;
	}

	@Override
	public GraphList with(Object... values) {
		if (values == null) {
			return this;
		}
		for(Object item : values) {
			if(item instanceof GraphMember) {
				super.withChildren((GraphMember) item);
			}
		}
		return this;
	}

	public Object getValue(Object key) {
		if(this.children == null) {
			return null;
		}
		if(this.children instanceof GraphMember) {
			if(this.children == key) {
				return this.children;
			}
			return null;
		}
		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet collection = (GraphSimpleSet) this.children;
			return collection.getValue(key);
		}
		return null;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		if(keyValue) {
			return new SimpleMapEntry<String, GraphNode>();
		}
		return new GraphList();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

}
