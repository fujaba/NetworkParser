package de.uniks.networkparser.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import de.uniks.networkparser.AbstractKeyValueEntry;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;


public class GraphConverter implements Converter {
	public static final String TYP="typ";
	public static final String VALUE="value";
	public static final String NODE="node";
	public static final String EDGE = "edge";
	public static final String ID="id";
	public static final String ATTRIBUTES = "attributes";
	public static final String NODES = "nodes";
	public static final String EDGES = "edges";
	public static final String SOURCE= "source";
	public static final String TARGET= "target";
	public static final String SOURCECARDINALITY= "sourcecardinality";
	public static final String TARGETCARDINALITY= "targetcardinality";
	public static final String SOURCEPROPERTY= "sourceproperty";
	public static final String TARGETPROPERTY= "targetproperty";
	public static final String HEADIMAGE= "headimage";
	
	@Override
	public String convert(GraphList root, boolean removePackage) {
		return convertToJson(root, removePackage).toString();

	}
	public JsonObject convertToJson(JsonArray list, boolean removePackage){
		return convertToJson(GraphIdMap.OBJECT, list, removePackage);
	}
	public JsonObject convertToJson(String typ, JsonArray list, boolean removePackage){
		GraphList root = new GraphList().withTyp(typ);
		HashMap<GraphNode, ArrayList<Attribute>> attributes = new HashMap<GraphNode, ArrayList<Attribute>>();
		for(Object item  : list){
			if(item instanceof JsonObject){
				parseJsonObject(root, (JsonObject) item, attributes);
			}
		}
		
		// Iteration of all primitive Attributes
		for(Iterator<Entry<GraphNode, ArrayList<Attribute>>> iterator = attributes.entrySet().iterator();iterator.hasNext();){
			Entry<GraphNode, ArrayList<Attribute>> node = iterator.next();
			for(Attribute attribute : node.getValue()){
				boolean addValue=true;
				for(GraphEdge edge : root.getEdges()){
					if(edge.getSource().has(node.getKey())){
						if(attribute.getKey().equals(edge.getSource().getProperty())){
							addValue=false;
							break;
						}
					}
					if(edge.getTarget().has(node.getKey())){
						if(attribute.getKey().equals(edge.getTarget().getProperty())){
							addValue=false;
							break;
						}
					}
				}
				if(addValue){
					node.getKey().add(attribute);
				}
			}
		}
		return convertToJson(root, removePackage);
	}
	
	public GraphNode parseJsonObject(GraphList root, JsonObject node, HashMap<GraphNode, ArrayList<Attribute>> attributes){
		String id = node.getString(JsonIdMap.ID);
		GraphNode graphNode = root.get(id);
		if(graphNode==null){
			graphNode = new GraphNode().withId(id);
			root.add(graphNode);
		}
		if(node.containsKey(JsonIdMap.CLASS)){
			graphNode.withClassName(node.getString(JsonIdMap.CLASS));
		}
		if(node.containsKey(HEADIMAGE)){
			graphNode.withHeadImage(node.getString(HEADIMAGE));
		}
		
		if(node.containsKey(JsonIdMap.JSON_PROPS)){
			JsonObject props = node.getJsonObject(JsonIdMap.JSON_PROPS);
			for(Iterator<AbstractKeyValueEntry<String, Object>> i = props.iterator();i.hasNext();){
				AbstractKeyValueEntry<String, Object> item = i.next();
				if(item.getValue() instanceof JsonObject) {
					// Must be a Link to 1
					GraphNode newNode = parseJsonObject(root, (JsonObject)item.getValue(), attributes);
					root.addEdge(new GraphEdge().withSource(graphNode).withTarget(newNode, GraphIdMap.ONE, item.getKey()));
				}else if(item.getValue() instanceof JsonArray) {
					// Must be a Link to n
					JsonArray array = (JsonArray) item.getValue();
					StringBuilder sb = new StringBuilder();
					for(Object entity : array){
						if(entity instanceof JsonObject){
							GraphNode newNode = parseJsonObject(root, (JsonObject)entity, attributes);
							root.addEdge(new GraphEdge().withSource(graphNode).withTarget(newNode, GraphIdMap.MANY, item.getKey()));							
						}else{
							if(sb.length()>0){
								sb.append(","+entity.toString());
							}else{
								sb.append(entity.toString());
							}
						}
					}
					if(sb.length()>0){
						Attribute attribute = new Attribute().withKey(item.getKey()).withClazz(item.getValue().getClass().getName()).withValue(sb.toString());
						if(attributes.get(graphNode)==null){
							attributes.put(graphNode, new ArrayList<Attribute>());
						}
						attributes.get(graphNode).add(attribute);
					}
				}else{
					Attribute attribute = new Attribute().withKey(item.getKey()).withClazz(item.getValue().getClass().getName()).withValue(item.getValue().toString());
					if(attributes.get(graphNode)==null){
						attributes.put(graphNode, new ArrayList<Attribute>());
					}
					attributes.get(graphNode).add(attribute);
				}
				
			}
		}
		return graphNode;
	}
	
	public JsonObject convertToJson(GraphList root, boolean removePackage){
		String typ = root.getTyp();
		JsonObject jsonRoot=new JsonObject().withValue(TYP, typ);
		JsonObject value=new JsonObject();
		value.put(NODES, parseEntities(typ, root.values(), removePackage));
		value.put(EDGES, parseEdges(typ, root.getEdges(), removePackage));
		jsonRoot.put("value", value);
		return jsonRoot;
	}
	
	private Collection<?> parseEdges(String typ, ArrayList<GraphEdge> edges, boolean shortName) {
		JsonArray result=new JsonArray();
		ArrayList<String> ids=new ArrayList<String>();

		for(GraphEdge edge : edges){
			for(GraphNode source : edge.getSource().getItems()){
				for(GraphNode target : edge.getTarget().getItems()){
					JsonObject child = new JsonObject().withValue(TYP, EDGE);
					child.put(SOURCECARDINALITY,  edge.getSource().getCardinality());
					child.put(TARGETCARDINALITY,  edge.getTarget().getCardinality());
					child.put(SOURCEPROPERTY,  edge.getSource().getProperty());
					child.put(TARGETPROPERTY,  edge.getTarget().getProperty());
					if(typ.equals(GraphIdMap.OBJECT)) {
						child.put(SOURCE, source.getId() + " : " + source.getClassName(shortName));
						child.put(TARGET, target.getId() + " : " + target.getClassName(shortName));
						result.add(child);
					}else{
						String id = source.getClassName(false)+":"+edge.getSource().getProperty()+target.getClassName(false)+":"+edge.getTarget().getProperty();
						if(!ids.contains(id)){
							child.put(SOURCE, source.getClassName(shortName));
							child.put(TARGET, target.getClassName(shortName));
							result.add(child);
							ids.add(id);
						}
					}
				}
			}
		}
		return result;
	}
	
	public JsonArray parseEntities(String typ, Collection<GraphNode> nodes, boolean shortName) {
		JsonArray result=new JsonArray();
		ArrayList<String> ids=new ArrayList<String>();
		for(GraphNode entity : nodes){
			JsonObject item = parseEntity(typ, entity, shortName);
			if(item!=null){
				if (typ == GraphIdMap.CLASS) {
					String key = item.getString(ID);
					if(ids.contains(key)){
						continue;
					}
					ids.add(key);
				}
				result.add(item);
			}
		}
		return result;
	}
	
	public JsonObject parseEntity(String typ, GraphNode entity, boolean shortName) {
		if(typ==null){
			typ = GraphIdMap.OBJECT;
			if(entity.getId()==null){
				typ = GraphIdMap.CLASS;
			}
		}
		JsonObject item = new JsonObject().withValue(TYP, NODE);
		if (entity.getHeadImage()!=null) {
			item.put(HEADIMAGE, entity.getHeadImage());
		}
		if (typ == GraphIdMap.OBJECT) {
			item.put(ID, entity.getId() + " : " + entity.getClassName(shortName));
		}else{
			item.put(ID, entity.getClassName(shortName));
		}
		item.put(ATTRIBUTES, parseAttributes(typ, entity.getAttributes(), shortName));
		return item;
	}
	private JsonArray parseAttributes(String typ, List<Attribute> attributes, boolean shortName) {
		JsonArray result=new JsonArray();
		String splitter = "";
		if (typ.equals(GraphIdMap.OBJECT)) {
			splitter = "=";
		} else if (typ.equals(GraphIdMap.CLASS)) {
			splitter = ":";
		}
		for(Attribute attribute : attributes){
			result.add(attribute.getKey() + splitter + attribute.getValue(typ, shortName));
		}
		return result;
	}
}
