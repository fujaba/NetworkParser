package de.uniks.networkparser.converter;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.util.ArrayList;
import java.util.Collection;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphDiff;
import de.uniks.networkparser.graph.GraphEntity;
import de.uniks.networkparser.graph.GraphImage;
import de.uniks.networkparser.graph.GraphLabel;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphOptions;
import de.uniks.networkparser.graph.GraphPattern;
import de.uniks.networkparser.graph.GraphSimpleSet;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.util.AssociationSet;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleSet;

public class GraphConverter implements Converter{
	public static final String TYPE = "type";
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


	public GraphList convertGraphList(String type, JsonArray list) {
		GraphList root = new GraphList().withType(type);

		// Parse all Object to Object-Diagram
		for (Object item : list) {
			if (item instanceof JsonObject) {
				parseJsonObject(root, (JsonObject) item);
			}
		}
		return root;
	}

	public JsonObject convertToJson(String type, JsonArray list,
			boolean removePackage) {
		GraphList root = convertGraphList(type, list);
		return convertToJson(root, removePackage, false);
	}

	public Clazz parseJsonObject(GraphList root, JsonObject node) {
		String id = node.getString(IdMap.ID);
		String typeId = id;
		boolean isClassDiagram = GraphTokener.CLASS.equalsIgnoreCase(root.getType()); 
		
		if(isClassDiagram) {
			typeId = node.getString(IdMap.CLASS);
			id = null;
		}
		Clazz graphNode = GraphUtil.getByObject(root, typeId, true);
		if (graphNode == null) {
			graphNode = new Clazz(node.getString(IdMap.CLASS));
			if(id != null) {
				graphNode.withId(id);
			}
			root.with(graphNode);
		}

		if (node.containsKey(HEAD)) {
			graphNode.with(new GraphImage().with(node.getString(HEAD)));
		}

		if (node.containsKey(JsonTokener.PROPS)) {
			JsonObject props = node.getJsonObject(JsonTokener.PROPS);
			Association assoc, assocOther;
			for (int i = 0; i < props.size(); i++) {
				Object value = props.getValueByIndex(i);
				if (value instanceof JsonObject) {
					assocOther = new Association(graphNode).with(Cardinality.ONE).with(AssociationTypes.EDGE);
					// Must be a Link to 1
					Clazz newNode = parseJsonObject(root, (JsonObject) value);
					
					assoc = new Association(newNode).with(Cardinality.ONE).with(props.getKeyByIndex(i)).with(AssociationTypes.ASSOCIATION);
					assoc.with(assocOther);

					newNode.with(assoc);
					graphNode.with(assocOther);
				} else if (value instanceof JsonArray) {
					// Must be a Link to n
					JsonArray array = (JsonArray) value;
					Attribute attribute = null;

					for (Object entity : array) {
						if (entity instanceof JsonObject) {
							assocOther = new Association(graphNode).with(Cardinality.ONE).with(AssociationTypes.EDGE);
							Clazz newNode = parseJsonObject(root, (JsonObject) entity);
							assoc = new Association(newNode).with(Cardinality.MANY).with(props.getKeyByIndex(i)).with(AssociationTypes.ASSOCIATION);
							assoc.with(assocOther);
							
							newNode.with(assoc);
							graphNode.with(assocOther);
							if(isClassDiagram) {
								break;
							}
						} else {
							if(attribute == null) {
								//FIXME FOR ASSOC -- ATTRIBUTE
								String name = props.getKeyByIndex(i);
								DataType type = DataType.create(value.getClass().getName());
								attribute = new Attribute(name, type);
								attribute.withValue(entity.toString());
							} else {
								attribute.withValue(attribute.getValue() + "," + entity.toString());
							}
						}
					}
				}else {
					String name = props.getKeyByIndex(i);
					Attribute attribute;
					AssociationSet associations = graphNode.getAssociations();
					for(Association childAssoc : associations) {
						 if(name.equals(childAssoc.getName()) || name.equals(childAssoc.getOther().getName())) {
							 name = null;
							 break;
						 }
					}
					if(name == null) {
						continue;
					}
					if (value != null) {
						attribute = new Attribute(name, DataType.create(value.getClass()));
						if(isClassDiagram == false) {
							attribute.withValue(value.toString());
						}
					} else {
						attribute = new Attribute(name, null);
					}
					graphNode.with(attribute);
				}
			}
		}
		return graphNode;
	}

	public JsonObject convertToJson(JsonArray list, boolean removePackage) {
		return convertToJson(GraphTokener.OBJECT, list, removePackage);
	}
	public JsonObject convertToJson(GraphModel root, boolean removePackage, boolean removeParameterNames) {
		String type = GraphTokener.CLASS;
		String style = null;
		GraphOptions options = null;
		if(root instanceof GraphList) {
			GraphList graphList = (GraphList) root;
			type = graphList.getType();
			style = graphList.getStyle();
			options = graphList.getOptions();
		}
		JsonObject jsonRoot = new JsonObject().withValue(TYPE, type).withValue(ID, root.getName());

		if(options != null) {
			jsonRoot.add(OPTIONS, options.getJson());
		}
		if(style!=null) {
			jsonRoot.put(STYLE, style);
		}
		jsonRoot.put(NODES, parseEntities(type, root, removePackage, removeParameterNames));
		jsonRoot.withKeyValue(EDGES, parseEdges(type, root.getAssociations(), removePackage));
		return jsonRoot;
	}

	private Collection<?> parseEdges(String type, SimpleSet<Association> edges,
			boolean shortName) {
		JsonArray result = new JsonArray();
		ArrayList<String> ids = new ArrayList<String>();

		for (Association edge : edges) {
			SimpleSet<GraphEntity> edgeNodes = GraphUtil.getNodes(edge);
			for (GraphEntity source : edgeNodes) {
				SimpleSet<GraphEntity> edgeOtherNodes = GraphUtil.getNodes(edge.getOther());
				for (GraphEntity target : edgeOtherNodes) {
					JsonObject child = parseEdge(type, source, target, edge, shortName, ids);
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

	private JsonObject parseEdge(String type, GraphEntity source, GraphEntity target, Association edge, boolean shortName,
			ArrayList<String> ids) {
		if (source instanceof Clazz && target instanceof Clazz) {
			return parseEdge(type, (Clazz) source, (Clazz) target, edge, shortName, ids);
		}
		if (source instanceof GraphPattern && target instanceof GraphPattern) {
			return parseEdge(type, (GraphPattern) source, (GraphPattern) target, edge, shortName, ids);
		}
		return null;
	}

	private JsonObject parseEdge(String type, Clazz source, Clazz target, Association edge, boolean shortName, ArrayList<String> ids) {
		JsonObject child = new JsonObject().withKeyValue(TYPE, edge.getOther().getType());
		if (type.equals(GraphTokener.OBJECT)) {
			child.put(SOURCE, addInfo(edge, true).withValue(ID, source.getId() + " : "
					+ source.getName(shortName)));
			child.put(TARGET, addInfo(edge.getOther(), true).withValue(ID, target.getId() + " : "
					+ target.getName(shortName)));
			return child;
		}else{
			String id = new CharacterBuffer()
					.with(source.getName(false), ":", edge.getName(),
							"-",
					target.getName(false), ":",edge.getOther().getName()).toString();
			if (!ids.contains(id)) {
				GraphDiff diff = GraphUtil.getDifference(edge);
				if(diff != null && diff.getCount()>0) {
					child.put(COUNTER, diff.getCount());
				}
				child.put(SOURCE, addInfo(edge, true).withValue(ID, source.getName(shortName)));
				child.put(TARGET, addInfo(edge.getOther(), true).withValue(ID, target.getName(shortName)));
				ids.add(id);
				return child;
			}
		}
		return null;
	}

	private JsonObject parseEdge(String type, GraphPattern source, GraphPattern target, Association edge, boolean shortName, ArrayList<String> ids) {
		JsonObject child = new JsonObject().withKeyValue(TYPE, edge.getType());
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

	public JsonArray parseEntities(String type, GraphEntity nodes,
			boolean shortName, boolean removeParameterNames) {
		JsonArray result = new JsonArray();
		ArrayList<String> ids = new ArrayList<String>();
		GraphSimpleSet children = GraphUtil.getChildren(nodes);
		for (GraphMember entity : children) {
			JsonObject item = parseEntity(type, entity, shortName, removeParameterNames);
			if (item != null) {
				if (GraphTokener.CLASS.equals(type) && item.has(ID)) {
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

	public JsonObject parseEntity(String type, GraphMember entity,
			boolean shortName, boolean removeParameterNames) {
		if (type == null) {
			type = GraphTokener.OBJECT;
			if (entity.getName() == null) {
				type = GraphTokener.CLASS;
			}
		}
		JsonObject item = new JsonObject();

		if(entity instanceof Clazz) {
			item.put(TYPE, CLAZZ);
			Clazz element = (Clazz) entity;
			if (type == GraphTokener.OBJECT) {
				item.put(ID,
						element.getId() + " : " + element.getName(shortName));
			} else {
				item.put(ID, element.getName(shortName));
			}
		}else if(entity instanceof GraphPattern) {
			item.put(TYPE, PATTERN);
			String bounds = ((GraphPattern) entity).getBounds();
			if(bounds != null) {
				item.put(STYLE, bounds);
			}
			item.put(ID, entity.getName());
		}else if(entity instanceof GraphList) {
			return convertToJson((GraphList) entity, shortName, false);
		} else {
			item.put(TYPE, NODE);
		}
		if(entity instanceof GraphEntity == false) {
			return null;
		}

		GraphEntity element = (GraphEntity) entity;

		GraphImage nodeHeader = getNodeHeader(element);
		if (nodeHeader != null) {
			item.put(HEAD, new JsonObject().withKeyValue(SRC, nodeHeader));
		}
		JsonArray items = parseAttributes(type, element, shortName);
		if(items.size()>0){
			item.put(ATTRIBUTES, items);
		}
		items = parseMethods(element, shortName, removeParameterNames);
		if(items.size()>0){
			item.put(METHODS, items);
		}
		GraphDiff diff = GraphUtil.getDifference(element);
		if(diff != null && diff.getCount()>0) {
			item.put(COUNTER, diff.getCount());
		}
		return item;
	}

	public GraphImage getNodeHeader(GraphEntity entity) {
		GraphSimpleSet children = GraphUtil.getChildren(entity);
		for (GraphMember member : children) {
			if (member instanceof GraphImage) {
				return (GraphImage) member;
			}
		}
		return null;
	}

	private JsonArray parseAttributes(String type, GraphEntity list,
			boolean shortName) {
		JsonArray result = new JsonArray();
		String splitter = "";
		if (type.equals(GraphTokener.OBJECT)) {
			splitter = "=";
		} else if (type.equals(GraphTokener.CLASS)) {
			splitter = ":";
		}
		GraphSimpleSet children = GraphUtil.getChildren(list);
		for (GraphMember item : children) {
			if (!(item instanceof Attribute)) {
				continue;
			}
			Attribute attribute = (Attribute) item;
			result.add(attribute.getName() + splitter
					+ attribute.getValue(type, shortName));
		}
		return result;
	}

	private JsonArray parseMethods(GraphEntity list, boolean shortName, boolean removeParameterNames) {
		JsonArray result = new JsonArray();
		GraphSimpleSet children = GraphUtil.getChildren(list);
		for (GraphMember item : children) {
			if (!(item instanceof Method)) {
				continue;
			}
			Method method = (Method) item;
			result.add( method.getName(false, removeParameterNames));
		}
		return result;
	}

	@Override
	public String encode(BaseItem entity) {
		if(entity instanceof GraphList) {
			return this.convertToJson((GraphModel)entity, false, false).toString();
		}
		return null;
	}
	public static JsonObject convertModel(GraphModel model) {
		GraphConverter converter = new GraphConverter();
		return converter.convertToJson(model, false, true);
	}
}
