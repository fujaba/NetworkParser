package de.uniks.networkparser.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
	public static final String CARDINALITY="cardinality";
	public static final String LABEL="label";
	
	
	@Override
	public String convert(GraphList root) {
		return convertToJson(root).toString();

	}
	public JsonObject convertToJson(JsonArray list){
		return convertToJson(GaphIdMap.OBJECT, list);
	}
	public JsonObject convertToJson(String typ, JsonArray list){
		GraphList root=new GraphList().withTyp(typ);
		for(Object item  : list){
			if(item instanceof JsonObject){
				JsonObject node = (JsonObject) item;
				GraphNode graphNode = new GraphNode().withId(node.getString(JsonIdMap.ID));
				JsonObject props = node.getJsonObject(JsonIdMap.JSON_PROPS);
				for(Iterator<String> keys = props.keys();keys.hasNext();){
					String key = keys.next();
					
				}
				root.add(graphNode);
			}
		}
		return convertToJson(root);
	}
	
	public JsonObject convertToJson(GraphList root){
		String typ = root.getTyp();
		JsonObject jsonRoot=new JsonObject().withValue(TYP, typ);
		jsonRoot.put(NODES, parseEntities(typ, root.getChildren()));
		jsonRoot.put(EDGES, parseEdges(typ, root.getEdges()));
		return jsonRoot;
	}
	
	private Collection<?> parseEdges(String typ, ArrayList<GraphEdge> edges) {
		JsonArray result=new JsonArray();

		for(GraphEdge edge : edges){
			if(typ.equals(GaphIdMap.OBJECT)) {
				for(GraphNode source : edge.getSource().getItems()){
					for(GraphNode target : edge.getTarget().getItems()){
						result.add(new JsonObject().withValue(TYP, EDGE, SOURCE, source.getId(), TARGET, target.getId()));
					}
				}
			}else{
				for(GraphNode source : edge.getSource().getItems()){
					for(GraphNode target : edge.getTarget().getItems()){
						result.add(new JsonObject().withValue(TYP, EDGE, SOURCE, source.getId(), TARGET, target.getId(), CARDINALITY, edge.getTarget().getCardinality(), LABEL, edge.getTarget().getProperty()));
					}
				}	
			}
		}
		return result;
	}
	public JsonArray parseEntities(String typ, Collection<GraphNode> nodes) {
		JsonArray result=new JsonArray();
		for(GraphNode entity : nodes){
			JsonObject item = parseEntity(typ, entity);
			if(item!=null){
				result.add(item);
			}
		}
		return result;
	}
	
	public JsonObject parseEntity(String typ, GraphNode entity) {
		if(typ==null){
			typ = GaphIdMap.OBJECT;
			if(entity.getId()==null){
				typ = GaphIdMap.CLASS;
			}
		}
		if (!entity.isVisible()) {
			return null;
		}
		JsonObject item = new JsonObject().withValue(TYP, NODE);
		if (typ == GaphIdMap.OBJECT) {
			item.add(ID, entity.getId() + " : " + entity.getClassName());
		}else{
			item.add(ID, entity.getClassName());
		}
		item.add(ATTRIBUTES, parseAttributes(typ, entity.getAttributes()));
		return item;
	}
	private JsonArray parseAttributes(String typ, ArrayList<Attribute> attributes) {
		JsonArray result=new JsonArray();
		String splitter = "";
		if (typ.equals(GaphIdMap.OBJECT)) {
			splitter = "=";
		} else if (typ.equals(GaphIdMap.CLASS)) {
			splitter = ":";
		}
		for(Attribute attribute : attributes){
			result.put(attribute.getKey() + splitter + attribute.getValue(typ));
		}
		return result;
	}
}
