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
	public static final String ID = "id";
	
	public static final String NODE = "node";
	public static final String PATTERN = "patternobject";
	public static final String SUBGRAPH = "subgraph";

	public static final String ATTRIBUTES = "attributes";
	public static final String METHODS = "methods";
	public static final String NODES = "nodes";
	public static final String EDGES = "edges";
	public static final String SOURCE = "source";
	public static final String TARGET = "target";
	public static final String CARDINALITY = "cardinality";
	public static final String PROPERTY = "property";
	public static final String HEADIMAGE = "headimage";
	public static final String OPTIONS = "options";
	private static final String STYLE = "style";
	private static final String GRAPH = "graph";
	private static final String INFO = "info";

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
		GraphClazz graphNode = (GraphClazz) root.getByObject(id);
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
		
		if(root.getOptions() != null) {
			jsonRoot.add(OPTIONS, root.getOptions().getJson());
		}
		
		jsonRoot.put(NODES, parseEntities(typ, root.values(), removePackage));
		jsonRoot.put(EDGES, parseEdges(typ, root.getEdges(), removePackage));
		return jsonRoot;
	}

	private Collection<?> parseEdges(String typ, ArrayList<GraphEdge> edges,
			boolean shortName) {
		JsonArray result = new JsonArray();
		ArrayList<String> ids = new ArrayList<String>();

		for (GraphEdge edge : edges) {
			for (GraphNode source : edge.values()) {
				for (GraphNode target : edge.getOther().values()) {
					JsonObject child =  parseEdge(typ, source, target, edge, shortName, ids);
					if(child != null) {
						result.add(child);
					}
				}
			}
		}
		if(result.size()<1){
			return null;
		}
		return result;
	}
	
	private JsonObject parseEdge(String typ, GraphNode source, GraphNode target, GraphEdge edge, boolean shortName, ArrayList<String> ids) {
		if(source instanceof GraphClazz && target instanceof GraphClazz ) {
			return parseEdge(typ, (GraphClazz) source, (GraphClazz) target, edge, shortName, ids);
		}
		if(source instanceof GraphPattern && target instanceof GraphPattern ) {
			return parseEdge(typ, (GraphPattern) source, (GraphPattern) target, edge, shortName, ids);
		}
		return null;
	}

	private JsonObject parseEdge(String typ, GraphClazz source, GraphClazz target, GraphEdge edge, boolean shortName, ArrayList<String> ids) {
		JsonObject child = new JsonObject().withValue(TYP, edge.getTyp());
		if (typ.equals(GraphIdMap.OBJECT)) {
			child.put(SOURCE, addInfo(edge, true).withValue(ID, source.getId() + " : "
					+ source.getClassName(shortName)));
			child.put(TARGET, addInfo(edge.getOther(), true).withValue(ID, target.getId() + " : "
					+ target.getClassName(shortName)));
			return child;
		}else{
			String id = source.getClassName(false) + ":"
					+ edge.getProperty()
					+ target.getClassName(false) + ":"
					+ edge.getOther().getProperty();
			if (!ids.contains(id)) {
				child.put(SOURCE, addInfo(edge, true).withValue(ID, source.getClassName(shortName)));
				child.put(TARGET, addInfo(edge.getOther(), true).withValue(ID, target.getClassName(shortName)));
				return child;
			}
		}
		return null;
	}
	
	private JsonObject parseEdge(String typ, GraphPattern source, GraphPattern target, GraphEdge edge, boolean shortName, ArrayList<String> ids) {
		JsonObject child = new JsonObject().withValue(TYP, edge.getTyp());
		child.put(SOURCE, addInfo(edge, false).withValue(ID, source.getId()));
		child.put(TARGET, addInfo(edge.getOther(), false).withValue(ID, target.getId()));
		GraphLabel info = edge.getInfo();
		if(info != null) {
			child.put(INFO, info.getValue());
			child.put(STYLE, info.getStyle());
		}
		return child;
	}
	
	private JsonObject addInfo(GraphEdge edge, boolean cardinality) {
		if(cardinality) {
			return new JsonObject().withValue(CARDINALITY, edge.getCardinality()).withValue(PROPERTY, edge.getProperty());
		}
		return new JsonObject().withValue(PROPERTY, edge.getProperty());
	}

	public JsonArray parseEntities(String typ, Collection<GraphMember> nodes,
			boolean shortName) {
		JsonArray result = new JsonArray();
		ArrayList<String> ids = new ArrayList<String>();
		
		for (GraphMember entity : nodes) {
			JsonObject item = parseEntity(typ, entity, shortName);
			if (item != null) {
				if (typ == GraphIdMap.CLASS && item.has(ID)) {
					String key = item.getString(ID);
					if (ids.contains(key)) {
						continue;
					}
					ids.add(key);
				}
				result.add(item);
			}
		}
		if(result.size()<1){
			return null;
		}
		return result;
	}

	public JsonObject parseEntity(String typ, GraphMember entity,
			boolean shortName) {
		if (typ == null) {
			typ = GraphIdMap.OBJECT;
			if (entity.getId() == null) {
				typ = GraphIdMap.CLASS;
			}
		}
		JsonObject item = new JsonObject();

		if(entity instanceof GraphClazz) {
			item.put(TYP, NODE);
			GraphClazz element = (GraphClazz) entity;
			if (typ == GraphIdMap.OBJECT) {
				item.put(ID,
						entity.getId() + " : " + element.getClassName(shortName));
			} else {
				item.put(ID, element.getClassName(shortName));
			}
		}else if(entity instanceof GraphPattern) {
			item.put(TYP, PATTERN);
			item.put(STYLE, ((GraphPattern) entity).getBounds() );
			item.put(ID, entity.getId());
		}else if(entity instanceof GraphList) {
			item.put(TYP, SUBGRAPH);
			item.put(GRAPH , convertToJson((GraphList) entity, shortName));
			item.put(ID, entity.getId());
			return item;
		}
		if(!(entity instanceof GraphNode)) {
			return null;
		}
		
		GraphNode element = (GraphNode) entity;
		
		GraphNodeImage nodeHeader = getNodeHeader(element);
		if (nodeHeader != null) {
			item.put(HEADIMAGE, nodeHeader);
		}
		JsonArray items = parseAttributes(typ, element.values(), shortName);
		if(items.size()>0){
			item.put(ATTRIBUTES, items);
		}
		items = parseMethods(element.values(), shortName);
		if(items.size()>0){
			item.put(METHODS, items);
		}
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
