package de.uniks.networkparser.graph;

import java.util.Collection;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleMapEntry;
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class GraphList.
 *
 * @author Stefan
 */
public class GraphList extends GraphModel {
	private String type = GraphTokener.CLASSDIAGRAM;
	private String style;
	private GraphOptions options;

	/**
	 * To string.
	 *
	 * @param removePackage the remove package
	 * @return the string
	 */
	public String toString(boolean removePackage) {
		YUMLConverter converter = new YUMLConverter();
		converter.defaultShowPackage = removePackage;
		return toString(converter);
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * With type.
	 *
	 * @param typ the typ
	 * @return the graph list
	 */
	public GraphList withType(String typ) {
		this.type = typ;
		return this;
	}

	/**
	 * With edge.
	 *
	 * @param sourceName the source name
	 * @param targetName the target name
	 * @return the graph list
	 */
	public GraphList withEdge(String sourceName, String targetName) {
		Association edge = new Association().with(sourceName).with(new Association().with(targetName));
		super.with(edge);
		return this;
	}

	/**
	 * Inits the sub links.
	 */
	public void initSubLinks() {
		for (GraphEntity node : getNodes()) {
			if (node instanceof Clazz == false) {
				continue;
			}
			Clazz graphClazz = (Clazz) node;
			SimpleSet<Association> childEdges = graphClazz.getAssociations();
			for (Association edge : childEdges) {
				SimpleSet<Association> associations = getAssociations();
				if (associations.contains(edge) == false && associations.contains(edge.getOther()) == false) {
					super.with(edge);
				}
			}
		}
	}

	/**
	 * With.
	 *
	 * @param value the value
	 * @return the clazz
	 */
	public Clazz with(Clazz value) {
		if (value != null) {
			if (value.getName() == null) {
				value.with(value.getName(false));
			}
			super.withChildren(value);
		}
		return value;
	}

	/**
	 * With.
	 *
	 * @param values the values
	 * @return the graph list
	 */
	public GraphList with(GraphList... values) {
		super.withChildren(values);
		return this;
	}

	/**
	 * With.
	 *
	 * @param value the value
	 * @return the graph pattern
	 */
	public GraphPattern with(GraphPattern value) {
		super.withChildren(value);
		return value;
	}

	/**
	 * With node.
	 *
	 * @param value the value
	 * @return the graph list
	 */
	public GraphList withNode(GraphEntity... value) {
		super.withChildren(value);
		return this;
	}

	/**
	 * With node.
	 *
	 * @param value the value
	 * @return the graph list
	 */
	public GraphList withNode(GraphNode... value) {
		super.withChildren(value);
		return this;
	}

	/**
	 * Gets the options.
	 *
	 * @return the options
	 */
	public GraphOptions getOptions() {
		return options;
	}

	/**
	 * With options.
	 *
	 * @param options the options
	 * @return the graph list
	 */
	public GraphList withOptions(GraphOptions options) {
		this.options = options;
		return this;
	}

	/**
	 * Gets the style.
	 *
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * With style.
	 *
	 * @param style the style
	 * @return the graph list
	 */
	public GraphList withStyle(String style) {
		this.style = style;
		return this;
	}

	/**
	 * Gets the node.
	 *
	 * @param id the id
	 * @return the node
	 */
	public Clazz getNode(String id) {
		if (id == null) {
			return null;
		}
		for (GraphMember item : this.getChildren()) {
			if (item instanceof Clazz && id.equalsIgnoreCase(item.getFullId())) {
				return (Clazz) item;
			}
		}
		return null;
	}

	/**
	 * Gets the nodes.
	 *
	 * @return the nodes
	 */
	public SimpleSet<GraphEntity> getNodes() {
		return super.getNodes();
	}

	/**
	 * Gets the edge.
	 *
	 * @param node the node
	 * @param property the property
	 * @return the edge
	 */
	public Association getEdge(GraphEntity node, String property) {
		if (property == null || node == null) {
			return null;
		}
		for (Association edge : getAssociations()) {
			Association oEdge = edge.getOther();
			if (edge.getClazz() == node && property.equals(oEdge.getName())) {
				return edge;
			} else if (oEdge.getClazz() == node && property.equals(edge.getName())) {
				return oEdge;
			}
		}
		return null;
	}

	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	@Override
	public boolean add(Object... values) {
		if (values == null) {
			return false;
		}
		for (Object item : values) {
			if (item instanceof Collection<?>) {
				Collection<?> items = (Collection<?>) item;
				for (Object i : items) {
					add(i);
				}
			} else if (item instanceof GraphMember) {
				super.withChildren((GraphMember) item);
			}
		}
		return true;
	}

	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 */
	public Object getValue(Object key) {
		if (this.children == null) {
			return null;
		}
		if (this.children instanceof GraphMember) {
			if (this.children == key) {
				return this.children;
			}
			return null;
		}
		if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet collection = (GraphSimpleSet) this.children;
			return collection.getValue(key);
		}
		return null;
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		if (keyValue) {
			return new SimpleMapEntry<String, GraphNode>();
		}
		return new GraphList();
	}

	/**
	 * With.
	 *
	 * @param name the name
	 * @return the graph list
	 */
	@Override
	public GraphList with(String name) {
		super.with(name);
		return this;
	}
}
