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

	public GraphList withEdge(String sourceName, String targetName) {
		Association edge = new Association().with(sourceName).with(
				new Association().with(targetName));
		add(edge);
		return this;
	}

	public boolean add(Association edge) {
		for (Iterator<Association> i = this.associations.iterator(); i.hasNext();) {
			Association item = i.next();
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
			SimpleSet<Association> childEdges = graphClazz.getAllEdges();
			for(Association edge : childEdges) {
				if(associations.contains(edge) == false && associations.contains(edge.getOther()) == false) {
					associations.add(edge);
//				} else if(allEdges.get(edge) != graphClazz) {
//					allEdges.put(edge, graphClazz);
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
		super.with(values);
		return this;
	}

	public GraphPattern with(GraphPattern value) {
		super.with(value);
		return value;
	}

	public Association with(Association value) {
		add(value);
		return value;
	}
	
	public GraphList withNode(GraphEntity... value) {
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
		SimpleSet<GraphEntity> nodes = new SimpleSet<GraphEntity>();
		for(GraphMember item : this.getChildren()) {
			if(item instanceof GraphEntity){
				nodes.add((GraphEntity)item);
			}
		}
		return nodes;
	}
	
	public SimpleSet<Association> getEdges() {
		return associations;
	}

	public Association getEdge(GraphEntity node, String property) {
		for(Association edge : associations) {
			Association oEdge = edge.getOther();
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
