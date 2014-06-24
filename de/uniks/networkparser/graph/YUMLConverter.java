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
import java.util.Iterator;

import de.uniks.networkparser.ArrayEntryList;
import de.uniks.networkparser.SimpleArrayList;

public class YUMLConverter implements Converter {
	/** The Constant URL. */
	public static final String URL = "http://yuml.me/diagram/class/";

	@Override
	public String convert(GraphList root, boolean removePackage){
		String typ = root.getTyp();
		Collection<GraphNode> children = root.values(); 
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
		SimpleArrayList<?> showedLinks = (SimpleArrayList<?>) links.getValue(key);
		if (showedLinks == null) {
			if(sb.length()<1){
				sb.append(parseEntity(item, visited, typ, shortName));
			}
			return;
		}
		Iterator<?> iterator = showedLinks.iterator();
		while (iterator.hasNext() ) {
			Object entry = iterator.next();
			if(!(entry instanceof GraphEdge)){
				continue;
			}
			GraphEdge element = (GraphEdge) entry; 
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(parseEntity(item, visited, typ, shortName));
			sb.append("-");
			
			Iterator<GraphNode> targetIterator = element.getOther().iterator();
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
		
		Iterator<GraphMember> i =  entity.iterator();
		if(i.hasNext()){
			String splitter = "";
			if (typ.equals(GraphIdMap.OBJECT)) {
				splitter = "=";
			} else if (typ.equals(GraphIdMap.CLASS)) {
				splitter = ":";
	
			}
			sb.append("|");
			Object element = i.next();
			Attribute attribute;
			if(element instanceof Attribute){
				attribute =(Attribute) element; 
				sb.append(attribute.getName() + splitter + attribute.getType(shortName));	/// without Typ
			}
	
			while (i.hasNext()) {
				element = i.next();
				if(!(element instanceof Attribute)){
					continue;
				}
				attribute =(Attribute) element; 
				
				
				sb.append(";");
				sb.append(attribute.getName() + splitter + attribute.getType(shortName));
			}
		}
		return sb.toString();
	}
}
