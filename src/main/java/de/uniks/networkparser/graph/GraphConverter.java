package de.uniks.networkparser.graph;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;

public class GraphConverter implements Converter {
	public static final String TYP = "typ";
	public static final String VALUE = "value";
	public static final String NODE = "node";
	public static final String EDGE = "edge";
	public static final String ID = "id";
	public static final String ATTRIBUTES = "attributes";
	public static final String METHODS = "methods";
	public static final String NODES = "nodes";
	public static final String EDGES = "edges";
	public static final String SOURCE = "source";
	public static final String TARGET = "target";
	public static final String SOURCECARDINALITY = "sourcecardinality";
	public static final String TARGETCARDINALITY = "targetcardinality";
	public static final String SOURCEPROPERTY = "sourceproperty";
	public static final String TARGETPROPERTY = "targetproperty";
	public static final String HEADIMAGE = "headimage";

	@Override
	public String convert(GraphList root, boolean removePackage) {
		return convertToJson(root, removePackage).toString();

	}

	public JsonObject convertToJson(JsonArray list, boolean removePackage) {
		return convertToJson(GraphIdMap.OBJECT, list, removePackage);
	}

	public JsonObject convertToJson(String typ, JsonArray list,
			boolean removePackage) {
		GraphList root = new GraphList().withTyp(typ);
		HashMap<GraphNode, ArrayList<GraphAttribute>> attributes = new HashMap<GraphNode, ArrayList<GraphAttribute>>();
		for (Object item : list) {
			if (item instanceof JsonObject) {
				parseJsonObject(root, (JsonObject) item, attributes);
			}
		}

		// Iteration of all primitive Attributes
		for (Iterator<Entry<GraphNode, ArrayList<GraphAttribute>>> iterator = attributes
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<GraphNode, ArrayList<GraphAttribute>> node = iterator.next();
			for (GraphAttribute attribute : node.getValue()) {
				boolean addValue = true;
				for (GraphEdge edge : root.getEdges()) {

					if (edge.contains(node.getKey())) {
						if (attribute.getName().equals(edge.getProperty())) {
							addValue = false;
							break;
						}
					}
					if (edge.getOther().contains(node.getKey())) {
						if (attribute.getName().equals(
								edge.getOther().getProperty())) {
							addValue = false;
							break;
						}
					}
				}
				if (addValue) {
					node.getKey().add(attribute);
				}
			}
		}
		return convertToJson(root, removePackage);
	}

	public GraphNode parseJsonObject(GraphList root, JsonObject node,
			HashMap<GraphNode, ArrayList<GraphAttribute>> attributes) {
		String id = node.getString(JsonIdMap.ID);
		GraphClazz graphNode = (GraphClazz) root.get(id);
		if (graphNode == null) {
			graphNode = new GraphClazz().withId(id);
			root.add(graphNode);
		}
		
		if (node.containsKey(JsonIdMap.CLASS)) {
			graphNode.withClassName(node.getString(JsonIdMap.CLASS));
		}
		if (node.containsKey(HEADIMAGE)) {
			graphNode
					.with(new GraphNodeImage().with(node.getString(HEADIMAGE)));
		}

		if (node.containsKey(JsonIdMap.JSON_PROPS)) {
			JsonObject props = node.getJsonObject(JsonIdMap.JSON_PROPS);
			for (int i = 0; i < props.size(); i++) {
				if (props.getValue(i) instanceof JsonObject) {
					// Must be a Link to 1
					GraphNode newNode = parseJsonObject(root,
							(JsonObject) props.getValue(i), attributes);
					root.add(new GraphEdge().with(graphNode).with(
							new GraphEdge(newNode, GraphCardinality.ONE, props
									.get(i))));
				} else if (props.getValue(i) instanceof JsonArray) {
					// Must be a Link to n
					JsonArray array = (JsonArray) props.getValue(i);
					StringBuilder sb = new StringBuilder();
					for (Object entity : array) {
						if (entity instanceof JsonObject) {
							GraphNode newNode = parseJsonObject(root,
									(JsonObject) entity, attributes);
							root.add(new GraphEdge().with(graphNode).with(
									new GraphEdge(newNode, GraphCardinality.MANY,
											props.get(i))));
						} else {
							if (sb.length() > 0) {
								sb.append("," + entity.toString());
							} else {
								sb.append(entity.toString());
							}
						}
					}
					if (sb.length() > 0) {
						GraphAttribute attribute = new GraphAttribute()
								.with(props.get(i))
								.with(props.getValue(i).getClass().getName())
								.withValue(sb.toString());
						if (attributes.get(graphNode) == null) {
							attributes.put(graphNode,
									new ArrayList<GraphAttribute>());
						}
						attributes.get(graphNode).add(attribute);
					}
				} else {
					GraphAttribute attribute = new GraphAttribute().with(props.get(i));
					if (props.getValue(i) != null) {
						attribute.with(
								GraphDataType.ref(props.getValue(i).getClass()))
								.withValue(props.getValue(i).toString());
					}
					if (attributes.get(graphNode) == null) {
						attributes.put(graphNode, new ArrayList<GraphAttribute>());
					}
					attributes.get(graphNode).add(attribute);
				}

			}
		}
		return graphNode;
	}

	public JsonObject convertToJson(GraphList root, boolean removePackage) {
		String typ = root.getTyp();
		JsonObject jsonRoot = new JsonObject().withValue(TYP, typ);
		JsonObject value = new JsonObject();
		value.put(NODES, parseEntities(typ, root.values(), removePackage));
		value.put(EDGES, parseEdges(typ, root.getEdges(), removePackage));
		jsonRoot.put("value", value);
		return jsonRoot;
	}

	private Collection<?> parseEdges(String typ, ArrayList<GraphEdge> edges,
			boolean shortName) {
		JsonArray result = new JsonArray();
		ArrayList<String> ids = new ArrayList<String>();

		for (GraphEdge edge : edges) {
			for (GraphNode source : edge.values()) {
				GraphClazz sourceClazz = (GraphClazz) source;
				for (GraphNode target : edge.getOther().values()) {
					JsonObject child = new JsonObject().withValue(TYP, EDGE);
					child.put(SOURCECARDINALITY, edge.getCardinality());
					child.put(TARGETCARDINALITY, edge.getOther()
							.getCardinality());
					child.put(SOURCEPROPERTY, edge.getProperty());
					child.put(TARGETPROPERTY, edge.getOther().getProperty());
					
					GraphClazz targetClazz = (GraphClazz) target;
					if (typ.equals(GraphIdMap.OBJECT)) {
						child.put(
								SOURCE,
								source.getId() + " : "
										+ sourceClazz.getClassName(shortName));
						child.put(
								TARGET,
								target.getId() + " : "
										+ sourceClazz.getClassName(shortName));
						result.add(child);
					} else {
						String id = sourceClazz.getClassName(false) + ":"
								+ edge.getProperty()
								+ targetClazz.getClassName(false) + ":"
								+ edge.getOther().getProperty();
						if (!ids.contains(id)) {
							child.put(SOURCE, sourceClazz.getClassName(shortName));
							child.put(TARGET, targetClazz.getClassName(shortName));
							result.add(child);
							ids.add(id);
						}
					}
				}
			}
		}
		return result;
	}

	public JsonArray parseEntities(String typ, Collection<GraphNode> nodes,
			boolean shortName) {
		JsonArray result = new JsonArray();
		ArrayList<String> ids = new ArrayList<String>();
		for (GraphNode entity : nodes) {
			JsonObject item = parseEntity(typ, entity, shortName);
			if (item != null) {
				if (typ == GraphIdMap.CLASS) {
					String key = item.getString(ID);
					if (ids.contains(key)) {
						continue;
					}
					ids.add(key);
				}
				result.add(item);
			}
		}
		return result;
	}

	public JsonObject parseEntity(String typ, GraphNode entity,
			boolean shortName) {
		if (typ == null) {
			typ = GraphIdMap.OBJECT;
			if (entity.getId() == null) {
				typ = GraphIdMap.CLASS;
			}
		}
		JsonObject item = new JsonObject().withValue(TYP, NODE);
		GraphNodeImage nodeHeader = getNodeHeader(entity);
		if (nodeHeader != null) {
			item.put(HEADIMAGE, nodeHeader);
		}
		
		GraphClazz entityClazz = (GraphClazz) entity;
		if (typ == GraphIdMap.OBJECT) {
			item.put(ID,
					entity.getId() + " : " + entityClazz.getClassName(shortName));
		} else {
			item.put(ID, entityClazz.getClassName(shortName));
		}
		item.put(ATTRIBUTES, parseAttributes(typ, entity.values(), shortName));
		item.put(METHODS, parseMethods(entity.values(), shortName));
		return item;
	}

	public GraphNodeImage getNodeHeader(GraphNode entity) {
		for (GraphMember member : entity.values()) {
			if (member instanceof GraphNodeImage) {
				return (GraphNodeImage) member;
			}
		}
		return null;
	}

	private JsonArray parseAttributes(String typ, Collection<GraphMember> list,
			boolean shortName) {
		JsonArray result = new JsonArray();
		String splitter = "";
		if (typ.equals(GraphIdMap.OBJECT)) {
			splitter = "=";
		} else if (typ.equals(GraphIdMap.CLASS)) {
			splitter = ":";
		}
		for (GraphMember item : list) {
			if (!(item instanceof GraphAttribute)) {
				continue;
			}
			GraphAttribute attribute = (GraphAttribute) item;
			result.add(attribute.getName() + splitter
					+ attribute.getValue(typ, shortName));
		}
		return result;
	}
	
	private JsonArray parseMethods(Collection<GraphMember> list, boolean shortName) {
		JsonArray result = new JsonArray();
		for (GraphMember item : list) {
			if (!(item instanceof GraphMethod)) {
				continue;
			}
			GraphMethod method = (GraphMethod) item;
			result.add( method.getName() + "(" + method.getParameterString(shortName)+")");
		}
		return result;
	}
}
