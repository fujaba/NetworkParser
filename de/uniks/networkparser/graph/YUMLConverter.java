package de.uniks.networkparser.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import de.uniks.networkparser.ArrayEntryList;

public class YUMLConverter implements Converter {
	/** The Constant URL. */
	public static final String URL = "http://yuml.me/diagram/class/";

	@Override
	public String convert(GraphList root, boolean removePackage){
		String typ = root.getTyp();
		Collection<GraphNode> children = root.getChildren(); 
		if (children.size() > 0) {
			StringBuilder sb = new StringBuilder();
			Iterator<GraphNode> i = children.iterator();

			ArrayList<GraphNode> visitedObj = new ArrayList<GraphNode>();
			ArrayEntryList links = root.getLinks();
			parse(typ, i.next(), sb, visitedObj, links, removePackage);
			while (i.hasNext()) {
				parse(typ, i.next(), sb, visitedObj, links, removePackage);
			}
			return sb.toString();
		}
		return null;
	}

	public void parse(String typ, GraphNode item, StringBuilder sb,
			ArrayList<GraphNode> visited,
			ArrayEntryList links, boolean shortName) {
		String key = item.getTyp(typ, shortName);
		EdgeList showedLinks = (EdgeList) links.get(key);
		if (showedLinks == null) {
			if(sb.length()<1){
				sb.append(parseEntity(item, visited, typ, shortName));
			}
			return;
		}
		Iterator<GraphEdge> iterator = showedLinks.iterator();
		while (iterator.hasNext() ) {
			GraphEdge entry = iterator.next();
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(parseEntity(item, visited, typ, shortName));
			sb.append("-");
			
			Iterator<GraphNode> targetIterator = entry.getTarget().iterator();
			GraphNode target = targetIterator.next();
			sb.append(parseEntity(target, visited, typ, shortName));
			
			while(targetIterator.hasNext()){
				sb.append(parseEntity(item, visited, typ, shortName));
				sb.append("-");
				target = targetIterator.next();
				sb.append(parseEntity(target, visited, typ, shortName));
			}
		}
	}

	
	// ##################################### Entity
	public String parseEntity(GraphNode entity, ArrayList<GraphNode> visited, boolean shortName) {
		return parseEntity(entity, visited, null, shortName);
	}
	public String parseEntity(GraphNode entity, ArrayList<GraphNode> visited, String typ, boolean shortName) {
		boolean shortString = visited.contains(entity);
		if(!shortString){
			visited.add(entity);
		}
		if(typ==null){
			typ = GraphIdMap.OBJECT;
			if(entity.getId()==null){
				typ = GraphIdMap.CLASS;
			}
		}
		if (typ == GraphIdMap.OBJECT) {
//				String text = entity.getId() + " : " + entity.getClassName();
//				return "["
//						+ text
//						+ "\\n"
//						+ new String(new char[text.length()]).replace("\0",	"&oline;") + "]";
			return "[" + entity.getId() + " : " + entity.getClassName(shortName) + parseEntityValues(entity,typ, shortString) + "]";
		}
		return "[" + entity.getClassName(shortName) + parseEntityValues(entity, typ, shortString) + "]";
	}

	public String parseEntityValues(GraphNode entity, String typ, boolean shortName) {
		if (shortName) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		ArrayList<Attribute> attributes = entity.getAttributes();
		if (attributes.size() > 0) {
			String splitter = "";
			Iterator<Attribute> i = attributes.iterator();
			if (typ.equals(GraphIdMap.OBJECT)) {
				splitter = "=";
			} else if (typ.equals(GraphIdMap.CLASS)) {
				splitter = ":";

			}
			sb.append("|");
			Attribute attribute = i.next();
			sb.append(attribute.getKey() + splitter + attribute.getValue(typ, shortName));

			while (i.hasNext()) {
				attribute = i.next();
				sb.append(";");
				sb.append(attribute.getKey() + splitter + attribute.getValue(typ, shortName));
			}
		}
		return sb.toString();
	}
}
