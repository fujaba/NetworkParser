package de.uniks.networkparser.graph;

/*
 NetworkParser
 Copyright (c) 2011 - 2014, Stefan Lindel
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

import de.uniks.networkparser.AbstractEntityList;
import de.uniks.networkparser.ArrayEntityList;
import de.uniks.networkparser.ArraySimpleList;
import de.uniks.networkparser.event.SimpleMapEntry;
import de.uniks.networkparser.interfaces.Converter;

public class GraphList extends AbstractEntityList<GraphMember> implements GraphMember{
	private ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
	private String typ=GraphIdMap.CLASS;
	private String style;
	private String id;
	private GraphOptions options;

	public boolean add(GraphMember value) {
		with(value);
		return true;
	}
	
	@Override
	protected boolean checkValue(Object a, Object b) {
		String idA = ((GraphMember)a).getId();
		String idB;
		if(b instanceof String) {
			idB = (String)b;
		}else {
			idB = ((GraphMember)b).getId();
		}
		return idA.equalsIgnoreCase(idB);
	}

	@Override
	public boolean remove(Object o) {
		int index = super.getIndex(o);
		if(index>=0) {
			return super.remove(index) != null;
		}
		return false;
	}

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

	public GraphList withEdge(GraphEdge edge) {
		add(edge);
		return this;
	}

	public GraphList withEdge(String sourceName, String targetName) {
		GraphEdge edge = new GraphEdge().with(sourceName).with(
				new GraphEdge().with(targetName));
		add(edge);
		return this;
	}

	public boolean add(GraphEdge edge) {
		for (Iterator<GraphEdge> i = this.edges.iterator(); i.hasNext();) {
			GraphEdge item = i.next();
			if (item.contains(edge.getOther().values())
					&& item.getOther().containsAll(edge.values())) {
				// Back again
				item.with(edge.getOther().getCardinality());
				item.with(edge.getOther().getProperty());
				return false;
			}
		}
		return this.edges.add(edge);
	}

	public ArrayList<GraphEdge> getEdges() {
		return edges;
	}

	public ArrayEntityList<String, Object> getLinks() {
		ArrayEntityList<String, Object> links = new ArrayEntityList<String, Object>();
		for (GraphEdge element : edges) {
			for (GraphNode node : element.values()) {
				String key = node.getTyp(typ, false);
				ArraySimpleList<?> value = (ArraySimpleList<?>) links
						.getValueItem(key);
				if (value != null) {
					value.with(element);
				} else {
					ArraySimpleList<GraphEdge> simpleList = new ArraySimpleList<GraphEdge>();
					simpleList.add(element);
					links.put(key, simpleList);
				}
			}
		}
		return links;
	}

	public SimpleMapEntry<String, GraphNode> getNewEntity() {
		return new SimpleMapEntry<String, GraphNode>();
	}

	@Override
	public GraphList getNewInstance() {
		return new GraphList();
	}

	@Override
	public GraphList with(Object... values) {
		super.with(values);
		return this;
	}
	
	public GraphClazz with(GraphClazz value) {
		if (value != null) {
			if(value.getId()==null){
				value.withId(value.getClassName());
			}
			add(value);
		}
		return value;
	}

	public GraphPattern with(GraphPattern value) {
		add(value);
		return value;
	}

	public GraphEdge with(GraphEdge value) {
		add(value);
		return value;
	}
	
	public GraphOptions getOptions() {
		return options;
	}

	public GraphList withOptions(GraphOptions options) {
		this.options = options;
		return this;
	}

	public String getId() {
		return id;
	}

	public GraphList withId(String value) {
		this.id = value;
		return this;
	}

	public String getStyle() {
		return style;
	}

	public GraphList withStyle(String style) {
		this.style = style;
		return this;
	}
}
