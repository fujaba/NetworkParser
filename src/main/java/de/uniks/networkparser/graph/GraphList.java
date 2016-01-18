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
import de.uniks.networkparser.event.SimpleMapEntry;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
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
		return converter.convert(this, false);
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

	public SimpleKeyValueList<String, Object> getLinks() {
		SimpleKeyValueList<String, Object> links = new SimpleKeyValueList<String, Object>();
		for (Association element : associations) {
			for (GraphEntity node : element.getNodes()) {
				String key = node.getTyp(typ, false);
				SimpleList<?> value = (SimpleList<?>)links
						.getValueItem(key);
				if (value != null) {
					value.withAll(element);
				} else {
					SimpleList<Association> simpleList = new SimpleList<Association>();
					simpleList.add(element);
					links.put(key, simpleList);
				}
			}
		}
		return links;
	}
	
	public void initSubLinks() {
		for(GraphEntity node : getNodes()) {
			if(node instanceof Clazz == false) {
				continue;
			}
			Clazz graphClazz = (Clazz) node;
			SimpleSet<Association> childEdges = graphClazz.getAssociation();
			SimpleSet<Association> myAssocs = getEdges();
			for(Association edge : childEdges) {
				if(myAssocs.contains(edge) == false && myAssocs.contains(edge.getOther()) == false) {
					myAssocs.add(edge);
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
		super.withChildren(true, values);
		return this;
	}

	public GraphPattern with(GraphPattern value) {
		super.withChildren(true, value);
		return value;
	}

	public GraphList withNode(GraphEntity... value) {
		super.withChildren(true, value);
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
	
	public SimpleSet<Association> getEdges() {
		if(associations == null) {
			associations = new SimpleSet<Association>(); 
		}
		return associations;
	}

	public Association getEdge(GraphEntity node, String property) {
		for(Association edge : associations) {
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
	public GraphList withAll(Object... values) {
		if (values == null) {
			return this;
		}
		for(Object item : values) {
			if(item instanceof GraphMember) {
				super.withChildren(true, (GraphMember) item);
			}
		}
		return this;
	}

	@Override
	public Object getValueItem(Object key) {
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
			return collection.getValueItem(key);
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

	GraphList withoutAssoc(Association assoc) {
		if(this.associations != null) {
			this.associations.remove(assoc);
		}
		return this;
	}
}
