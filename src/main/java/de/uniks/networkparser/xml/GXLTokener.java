package de.uniks.networkparser.xml;

import java.util.Collection;
import java.util.Iterator;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class GXLTokener extends Tokener {
	public static final String GXL = "gxl";
	public static final String GRAPH = "graph";
	public static final String NODE = "node";
	public static final String EDGE = "edge";
	public static final String BOOL = "bool";
	public static final String INT = "int";
	public static final String FLOAT = "float";
	public static final String STRING = "string";
	public static final String BAG = "bag";

	public static final String EDGEIDS = "edgeids";
	public static final String EDGEMODE = "edgemode";
	public static final String HYPERGRAPH = "hypergraph";
	public static final String ISDIRECTED = "isdirected";
	public static final String ATTRIBUTE = "attr";
	public static final String NAME = "name";
	public static final String FROM = "from";
	public static final String TO = "to";

	@Override
	public BaseItem encode(Object entity, MapEntity map) {
		XMLEntity instance = new XMLEntity();
		instance.withType(GXL);
		instance.withKeyValue("xmlns:xlink", "http://www.w3.org/1999/xlink");
		XMLEntity graph = new XMLEntity().withType(GRAPH);
		graph.withKeyValue(IdMap.ID, "undirected-instance");
		graph.withKeyValue(EDGEIDS, false);
		graph.withKeyValue(EDGEMODE, "defaultdirected");
		graph.withKeyValue(HYPERGRAPH, false);
		instance.with(graph);
		if (entity != null) {
			String className = entity.getClass().getName();
			Grammar grammar = map.getGrammar();
			if (grammar != null) {
				SendableEntityCreator creator = grammar.getCreator(SendableEntityCreator.NEW, entity, map, className);
				encodeChildren(entity, graph, creator, map);
			}
		}
		return instance;
	}

	private boolean encodeChildren(Object item, XMLEntity root, SendableEntityCreator creator, MapEntity map) {
		if (map == null || map.contains(item)) {
			return false;
		}
		map.with(item);
		if (creator == null) {
			return false;
		}
		String id = this.map.getId(item, true);
		XMLEntity node = this.newInstance();
		node.withType(NODE);
		node.withKeyValue(IdMap.ID, id);
		root.with(node);
		for (String property : creator.getProperties()) {
			Object value = creator.getValue(item, property);
			parseValue(property, value, node, root, node, map);
		}
		return true;
	}

	private void parseValue(String property, Object value, XMLEntity parent, XMLEntity root, XMLEntity node,
			MapEntity map) {
		if (parent == null || this.map == null) {
			return;
		}
		if (value == null) {
			/* Null Value */
			XMLEntity attribute = this.newInstance();
			attribute.withType(ATTRIBUTE);
			attribute.withKeyValue(NAME, property);
			parent.with(attribute);
			return;
		}
		SendableEntityCreator childCreator = this.map.getCreator(value.getClass().getName(), true, true, null);
		if (childCreator != null) {
			if (encodeChildren(value, root, childCreator, map)) {
				XMLEntity edge = this.newInstance();
				edge.withType(EDGE);
				edge.withKeyValue(FROM, node.get(IdMap.ID));
				edge.withKeyValue(TO, this.map.getKey(value));
				edge.withKeyValue(ISDIRECTED, false);
				root.with(edge);
			}
			return;
		}
		XMLEntity attribute = this.newInstance();
		attribute.withType(ATTRIBUTE);
		attribute.withKeyValue(NAME, property);
		parent.with(attribute);
		if (value instanceof Collection<?>) {
			Collection<?> children = (Collection<?>) value;
			XMLEntity bag = this.newInstance();
			for (Iterator<?> i = children.iterator(); i.hasNext();) {
				parseValue(property, i.next(), bag, root, node, map);
			}
			if (bag.size() > 0) {
				bag.withType(BAG);
				attribute.with(bag);
			}
			return;
		}
		XMLEntity valueItem = this.newInstance();
		valueItem.withValue(value.toString());
		attribute.with(valueItem);
		if (value instanceof Boolean) {
			valueItem.withType(BOOL);
		} else if (value instanceof Integer || value instanceof Long) {
			valueItem.withType(INT);
		} else if (value instanceof Float || value instanceof Double) {
			valueItem.withType(FLOAT);
		} else {
			valueItem.withType(STRING);
		}
	}

	@Override
	public XMLEntity newInstance() {
		return new XMLEntity();
	}
}
