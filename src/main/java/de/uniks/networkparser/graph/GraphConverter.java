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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleSet;

public class GraphConverter implements Converter {
	public static final String TYP = "typ";
	public static final String ID = "id";
	
	public static final String NODE = "node";
	public static final String CLAZZ = "clazz";
	public static final String PATTERN = "pattern";
	public static final String SUBGRAPH = "subgraph";

	public static final String ATTRIBUTES = "attributes";
	public static final String METHODS = "methods";
	public static final String NODES = "nodes";
	public static final String EDGES = "edges";
	public static final String SOURCE = "source";
	public static final String TARGET = "target";
	public static final String CARDINALITY = "cardinality";
	public static final String PROPERTY = "property";
	public static final String HEAD = "head";
	public static final String SRC = "src";
	public static final String OPTIONS = "options";
	private static final String STYLE = "style";
	private static final String INFO = "info";
	private static final String COUNTER = "counter";

	@Override
	public String convert(GraphList root, boolean removePackage) {
		return convertToJson(root, removePackage).toString();

	}

	public JsonObject convertToJson(JsonArray list, boolean removePackage) {
		return convertToJson(GraphIdMap.OBJECT, list, removePackage);
	}

	public GraphList convertGraphList(String typ, JsonArray list,
			boolean removePackage) {
		GraphList root = new GraphList().withTyp(typ);
		HashMap<GraphEntity, ArrayList<Attribute>> attributes = new HashMap<GraphEntity, ArrayList<Attribute>>();
		for (Object item : list) {
			if (item instanceof JsonObject) {
				parseJsonObject(root, (JsonObject) item, attributes);
			}
		}

		// Iteration of all primitive Attributes
		for (Iterator<Entry<GraphEntity, ArrayList<Attribute>>> iterator = attributes
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<GraphEntity, ArrayList<Attribute>> node = iterator.next();
			for (Attribute attribute : node.getValue()) {
				boolean addValue = true;
				for (Association edge : root.getEdges()) {
					if (edge.contains(node.getKey(), true, false)) {
						if (attribute.getName().equals(edge.getName())) {
							addValue = false;
							break;
						}
					}
					if (edge.getOther().contains(node.getKey(), true, false)) {
						if (attribute.getName().equals(
								edge.getOther().getName())) {
							addValue = false;
							break;
						}
					}
				}
				if (addValue) {
					node.getKey().with(attribute);
				}
			}
		}
		return root;
	}
	
	
	public JsonObject convertToJson(String typ, JsonArray list,
			boolean removePackage) {
		GraphList root = convertGraphList(typ, list, removePackage);
		return convertToJson(root, removePackage);
	}

	public Clazz parseJsonObject(GraphList root, JsonObject node,
			HashMap<GraphEntity, ArrayList<Attribute>> attributes) {
		String id = node.getString(JsonIdMap.ID);
		Clazz graphNode = (Clazz) root.getByObject(id, true);
		if (graphNode == null) {
			graphNode = new Clazz();
			graphNode.withId(id);
			root.with(graphNode);
		}
		
		if (node.containsKey(JsonIdMap.CLASS)) {
			graphNode.with(node.getString(JsonIdMap.CLASS));
		}
		if (node.containsKey(HEAD)) {
			graphNode
					.with(new GraphImage().with(node.getString(HEAD)));
		}

		if (node.containsKey(JsonIdMap.JSON_PROPS)) {
			JsonObject props = node.getJsonObject(JsonIdMap.JSON_PROPS);
			for (int i = 0; i < props.size(); i++) {
				if (props.getValueByIndex(i) instanceof JsonObject) {
					// Must be a Link to 1
					Clazz newNode = parseJsonObject(root,
							(JsonObject) props.getValueByIndex(i), attributes);
					root.add(new Association().with(graphNode).with(
							new Association().with(newNode, Cardinality.ONE, props
									.getKeyByIndex(i))));
				} else if (props.getValueByIndex(i) instanceof JsonArray) {
					// Must be a Link to n
					JsonArray array = (JsonArray) props.getValueByIndex(i);
					StringBuilder sb = new StringBuilder();
					for (Object entity : array) {
						if (entity instanceof JsonObject) {
							Clazz newNode = parseJsonObject(root,
									(JsonObject) entity, attributes);
							root.add(new Association().with(graphNode).with( 
									new Association().with(newNode, Cardinality.MANY,
											props.getKeyByIndex(i))));
						} else {
							if (sb.length() > 0) {
								sb.append("," + entity.toString());
							} else {
								sb.append(entity.toString());
							}
						}
					}
					if (sb.length() > 0) {
						Attribute attribute = new Attribute()
								.with(props.getKeyByIndex(i))
								.with(props.getValueByIndex(i).getClass().getName())
								.withValue(sb.toString());
						if (attributes.get(graphNode) == null) {
							attributes.put(graphNode,
									new ArrayList<Attribute>());
						}
						attributes.get(graphNode).add(attribute);
					}
				} else {
					Attribute attribute = new Attribute().with(props.getKeyByIndex(i));
					if (props.getValueByIndex(i) != null) {
						attribute.with(
								DataType.ref(props.getValueByIndex(i).getClass()))
								.withValue(props.getValueByIndex(i).toString());
				   if (attributes.get(graphNode) == null) {
					  attributes.put(graphNode, new ArrayList<Attribute>());
				   }
				   attributes.get(graphNode).add(attribute);
					}
				}
			}
		}
		return graphNode;
	}

	public JsonObject convertToJson(GraphList root, boolean removePackage) {
		String typ = root.getTyp();
		JsonObject jsonRoot = new JsonObject().withValue(TYP, typ).withValue(ID, root.getName());
		
		if(root.getOptions() != null) {
			jsonRoot.add(OPTIONS, root.getOptions().getJson());
		}
		jsonRoot.put(STYLE, root.getStyle());
		jsonRoot.put(NODES, parseEntities(typ, root, removePackage));
		jsonRoot.withKeyValue(EDGES, parseEdges(typ, root.getEdges(), removePackage));
		return jsonRoot;
	}

	private Collection<?> parseEdges(String typ, SimpleSet<Association> edges,
			boolean shortName) {
		JsonArray result = new JsonArray();
		ArrayList<String> ids = new ArrayList<String>();

		for (Association edge : edges) {
			for (GraphEntity source : edge.getNodes()) {
				for (GraphEntity target : edge.getOther().getNodes()) {
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
	
	private JsonObject parseEdge(String typ, GraphEntity source, GraphEntity target, Association edge, boolean shortName,
			ArrayList<String> ids) {
		if (source instanceof Clazz && target instanceof Clazz) {
			return parseEdge(typ, (Clazz) source, (Clazz) target, edge, shortName, ids);
		}
		if (source instanceof GraphPattern && target instanceof GraphPattern) {
			return parseEdge(typ, (GraphPattern) source, (GraphPattern) target, edge, shortName, ids);
		}
		return null;
	}
	
	private JsonObject parseEdge(String typ, Clazz source, Clazz target, Association edge, boolean shortName, ArrayList<String> ids) {
		JsonObject child = new JsonObject().withKeyValue(TYP, edge.getTyp());
		if (typ.equals(GraphIdMap.OBJECT)) {
			child.put(SOURCE, addInfo(edge, true).withValue(ID, source.getId() + " : "
					+ source.getName(shortName)));
			child.put(TARGET, addInfo(edge.getOther(), true).withValue(ID, target.getId() + " : "
					+ target.getName(shortName)));
			return child;
		}else{
			String id = source.getName(false) + ":"
					+ edge.getName()
					+ target.getName(false) + ":"
					+ edge.getOther().getName();
			if (!ids.contains(id)) {
				GraphDiff diff = edge.getDiff();
				if(diff != null && diff.getCount()>0) {
					child.put(COUNTER, diff.getCount());
				}
				child.put(SOURCE, addInfo(edge, true).withValue(ID, source.getName(shortName)));
				child.put(TARGET, addInfo(edge.getOther(), true).withValue(ID, target.getName(shortName)));
				return child;
			}
		}
		return null;
	}
	
	private JsonObject parseEdge(String typ, GraphPattern source, GraphPattern target, Association edge, boolean shortName, ArrayList<String> ids) {
		JsonObject child = new JsonObject().withKeyValue(TYP, edge.getTyp());
		child.put(SOURCE, addInfo(edge, false).withValue(ID, source.getId()));
		child.put(TARGET, addInfo(edge.getOther(), false).withValue(ID, target.getId()));
		
		GraphLabel info = edge.getInfo();
		if(info != null) {
			child.put(INFO, info.getName());
			child.put(STYLE, info.getStyle());
		}
		return child;
	}
	
	private JsonObject addInfo(Association edge, boolean cardinality) {
		if(cardinality) {
			return new JsonObject().withKeyValue(CARDINALITY, edge.getCardinality()).withValue(PROPERTY, edge.getName());
		}
		return new JsonObject().withValue(PROPERTY, edge.getName());
	}

	public JsonArray parseEntities(String typ, GraphEntity nodes,
			boolean shortName) {
		JsonArray result = new JsonArray();
		ArrayList<String> ids = new ArrayList<String>();
		
		for (GraphMember entity : nodes.getChildren()) {
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
			if (entity.getName() == null) {
				typ = GraphIdMap.CLASS;
			}
		}
		JsonObject item = new JsonObject();

		if(entity instanceof Clazz) {
			item.put(TYP, CLAZZ);
			Clazz element = (Clazz) entity;
			if (typ == GraphIdMap.OBJECT) {
				item.put(ID,
						element.getId() + " : " + element.getName(shortName));
			} else {
				item.put(ID, element.getName(shortName));
			}
		}else if(entity instanceof GraphPattern) {
			item.put(TYP, PATTERN);
			String bounds = ((GraphPattern) entity).getBounds();
			if(bounds != null) {
				item.put(STYLE,  bounds);
			}
			item.put(ID, entity.getName());
		}else if(entity instanceof GraphList) {
			return convertToJson((GraphList) entity, shortName);
		} else {
			item.put(TYP, NODE);
		}
		if(entity instanceof GraphEntity == false) {
			return null;
		}
		
		GraphEntity element = (GraphEntity) entity;
		
		GraphImage nodeHeader = getNodeHeader(element);
		if (nodeHeader != null) {
			item.put(HEAD, new JsonObject().withKeyValue(SRC, nodeHeader));
		}
		JsonArray items = parseAttributes(typ, element, shortName);
		if(items.size()>0){
			item.put(ATTRIBUTES, items);
		}
		items = parseMethods(element, shortName);
		if(items.size()>0){
			item.put(METHODS, items);
		}
		GraphDiff diff = element.getDiff();
		if(diff != null && diff.getCount()>0) {
			item.put(COUNTER, diff.getCount());
		}
		return item;
	}
	
	public GraphImage getNodeHeader(GraphEntity entity) {
		for (GraphMember member : entity.getChildren()) {
			if (member instanceof GraphImage) {
				return (GraphImage) member;
			}
		}
		return null;
	}

	private JsonArray parseAttributes(String typ, GraphEntity list,
			boolean shortName) {
		JsonArray result = new JsonArray();
		String splitter = "";
		if (typ.equals(GraphIdMap.OBJECT)) {
			splitter = "=";
		} else if (typ.equals(GraphIdMap.CLASS)) {
			splitter = ":";
		}
		for (GraphMember item : list.getChildren()) {
			if (!(item instanceof Attribute)) {
				continue;
			}
			Attribute attribute = (Attribute) item;
			result.add(attribute.getName() + splitter
					+ attribute.getValue(typ, shortName));
		}
		return result;
	}
	
	private JsonArray parseMethods(GraphEntity list, boolean shortName) {
		JsonArray result = new JsonArray();
		for (GraphMember item : list.getChildren()) {
			if (!(item instanceof Method)) {
				continue;
			}
			Method method = (Method) item;
			result.add( method.getName(false));
		}
		return result;
	}
}
