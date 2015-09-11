package de.uniks.networkparser.graph;

import java.util.Iterator;

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
		for (Iterator<GraphEdge> i = this.associations.iterator(); i.hasNext();) {
			GraphEdge item = i.next();
			if (edge.getOther()!= null && item.containsAll(edge.getOther(), true)) {
				// Back again
				if(edge.getOther() != null ) {	
					item.with(edge.getOther().getCardinality());
					item.with(edge.getOther().getProperty());
				}
				return false;
			}
		}
		return this.associations.add(edge);
	}


	public SimpleKeyValueList<String, Object> getLinks() {
		SimpleKeyValueList<String, Object> links = new SimpleKeyValueList<String, Object>();
		for (GraphEdge element : associations) {
			for (GraphNode node : element.getNodes()) {
				String key = node.getTyp(typ, false);
				SimpleList<?> value = (SimpleList<?>)links
						.getValueItem(key);
				if (value != null) {
					value.withAll(element);
				} else {
					SimpleList<GraphEdge> simpleList = new SimpleList<GraphEdge>();
					simpleList.add(element);
					links.put(key, simpleList);
				}
			}
		}
		return links;
	}
	
	public void initSubLinks() {
		for(GraphNode node : getNodes()) {
			if(node instanceof GraphClazz == false) {
				continue;
			}
			GraphClazz graphClazz = (GraphClazz) node;
			SimpleSet<GraphEdge> childEdges = graphClazz.getAllEdges();
			for(GraphEdge edge : childEdges) {
				if(associations.contains(edge) == false && associations.contains(edge.getOther()) == false) {
					associations.add(edge);
//				} else if(allEdges.get(edge) != graphClazz) {
//					allEdges.put(edge, graphClazz);
				}
			}
		}
	}

	
	public GraphClazz with(GraphClazz value) {
		if (value != null) {
			if(value.getId()==null){
				value.withId(value.getClassName());
			}
			super.with(value);
		}
		return value;
	}

	public GraphPattern with(GraphPattern value) {
		super.with(value);
		return value;
	}

	public GraphEdge with(GraphEdge value) {
		add(value);
		return value;
	}
	
	public GraphList withNode(GraphNode... value) {
		super.with(value);
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

	public GraphList withMain(GraphNode parse) {
		return this;
	}

	public GraphNode getNode(String id) {
		if(id==null){
			return null;
		}
		for(GraphMember item : this.getChildren()) {
			if(item instanceof GraphNode && id.equalsIgnoreCase(item.getId())){
				return (GraphNode)item;
			}
		}
		return null;
	}
	
	public SimpleSet<GraphNode> getNodes() {
		SimpleSet<GraphNode> nodes = new SimpleSet<GraphNode>();
		for(GraphMember item : this.getChildren()) {
			if(item instanceof GraphNode ){
				nodes.add((GraphNode)item);
			}
		}
		return nodes;
	}
	
	public SimpleSet<GraphEdge> getEdges() {
		return associations;
	}

	public GraphEdge getEdge(GraphNode node, String property) {
		for(GraphEdge edge : associations) {
			GraphEdge oEdge = edge.getOther();
			if(edge.getNode()==node && property.equals(oEdge.getProperty())) {
				return edge;
			}else if(oEdge.getNode()==node && property.equals(edge.getProperty())) {
				return oEdge;
			}
		}
		return null;
	}

	@Override
	public BaseItem withAll(Object... values) {
		this.children.withAll(values);
        return this;
    }

	@Override
	public Object getValueItem(Object key) {
		return this.children.getValueItem(key);
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
        if(keyValue) {
            return new SimpleMapEntry<String, GraphNode>();
        }
        return new GraphList();
	}
}
