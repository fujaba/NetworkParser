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
import java.util.Iterator;

import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class YUMLConverter implements Converter {
	/** The Constant URL. */
	public static final String URL = "http://yuml.me/diagram/class/";

	@Override
	public String convert(GraphList root, boolean removePackage) {
		String typ = root.getTyp();
		if (root.getChildren().size() > 0) {
			StringBuilder sb = new StringBuilder();
			Iterator<GraphMember> i = root.getChildren().iterator();

			ArrayList<GraphNode> visitedObj = new ArrayList<GraphNode>();
			root.initSubLinks();
			SimpleKeyValueList <String, Object> links = root.getLinks();
			parse(typ, i.next(), sb, visitedObj, links, removePackage);
			while (i.hasNext()) {
				parse(typ, i.next(), sb, visitedObj, links, removePackage);
			}
			return sb.toString();
		}
		return null;
	}

	public void parse(String typ, GraphMember item, StringBuilder sb,
			ArrayList<GraphNode> visited,
			SimpleKeyValueList<String, Object> links, boolean shortName) {
		if(item instanceof GraphNode) {
			parse(typ, (GraphNode) item, sb, visited, links, shortName);
		}
	}
	public void parse(String typ, GraphNode item, StringBuilder sb,
			ArrayList<GraphNode> visited,
			SimpleKeyValueList<String, Object> links, boolean shortName) {
		String key = item.getTyp(typ, shortName);
		SimpleList<?> showedLinks = (SimpleList<?>) links
				.getValueItem(key);
		if (showedLinks == null) {
			if (sb.length() < 1) {
				sb.append(parseEntity(item, visited, typ, shortName));
			}
			return;
		}
		Iterator<?> iterator = showedLinks.iterator();
		while (iterator.hasNext()) {
			Object entry = iterator.next();
			if (!(entry instanceof GraphEdge)) {
				continue;
			}
			GraphEdge element = (GraphEdge) entry;
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(parseEntity(item, visited, typ, shortName));
			sb.append("-");

			Iterator<GraphNode> targetIterator = element.getOther().getNodes().iterator();
			GraphNode target = targetIterator.next();
			sb.append(parseEntity(target, visited, typ, shortName));

			while (targetIterator.hasNext()) {
				sb.append(parseEntity(item, visited, typ, shortName));
				sb.append("-");
				target = targetIterator.next();
				sb.append(parseEntity(target, visited, typ, shortName));
			}
		}
	}

	// ##################################### Entity
	public String parseEntity(GraphNode entity, ArrayList<GraphNode> visited,
			boolean shortName) {
		return parseEntity(entity, visited, null, shortName);
	}

	public String parseEntity(GraphNode entity, ArrayList<GraphNode> visited,
			String typ, boolean shortName) {
		if(!(entity instanceof GraphClazz)){
			return "";
		}
		GraphClazz clazzEntity = (GraphClazz) entity;
		
		boolean shortString = visited.contains(clazzEntity);
		if (!shortString) {
			visited.add(clazzEntity);
		}
		if (typ == null) {
			typ = GraphIdMap.OBJECT;
			if (clazzEntity.getId() == null) {
				typ = GraphIdMap.CLASS;
			}
		}
		if (typ == GraphIdMap.OBJECT) {
			// String text = entity.getId() + " : " + entity.getClassName();
			// return "["
			// + text
			// + "\\n"
			// + new String(new char[text.length()]).replace("\0", "&oline;") +
			// "]";
			return "[" + clazzEntity.getId() + " : "
					+ clazzEntity.getClassName(shortName)
					+ parseEntityValues(clazzEntity, typ, shortString) + "]";
		}
		return "[" + clazzEntity.getClassName(shortName)
				+ parseEntityValues(clazzEntity, typ, shortString) + "]";
	}

	public String parseEntityValues(GraphNode entity, String typ,
			boolean shortName) {
		if (shortName) {
			return "";
		}
		StringBuilder sb = new StringBuilder();

		Iterator<GraphMember> i = entity.getChildren().iterator();
		if (i.hasNext()) {
			String splitter = "";
			if (typ.equals(GraphIdMap.OBJECT)) {
				splitter = "=";
			} else if (typ.equals(GraphIdMap.CLASS)) {
				splitter = ":";

			}
			sb.append("|");
			Object element = i.next();
			GraphAttribute attribute;
			if (element instanceof GraphAttribute) {
				attribute = (GraphAttribute) element;
				sb.append(attribute.getId() + splitter
						+ attribute.getValue(typ, shortName)); // / without Typ
			}

			while (i.hasNext()) {
				element = i.next();
				if (!(element instanceof GraphAttribute)) {
					continue;
				}
				attribute = (GraphAttribute) element;

				sb.append(";");
				sb.append(attribute.getId() + splitter
						+ attribute.getValue(typ, shortName));
			}
		}
		return sb.toString();
	}
}
