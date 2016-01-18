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
import java.util.Iterator;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class YUMLConverter implements Converter {
	/** The Constant URL. */
	public static final String URL = "http://yuml.me/diagram/class/";

	@Override
	public String convert(GraphList root, boolean removePackage) {
		String typ = root.getTyp();
		GraphSimpleSet collection = root.getChildren();
		if (collection.size() > 0) {
			StringBuilder sb = new StringBuilder();
			Iterator<GraphMember> i = collection.iterator();

			SimpleList<GraphEntity> visitedObj = new SimpleList<GraphEntity>();
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
			SimpleList<GraphEntity> visited, SimpleKeyValueList<String, Object> links, boolean shortName) {
		if(item instanceof GraphEntity) {
			parse(typ, (GraphEntity) item, sb, visited, links, shortName);
		}
	}
	public void parse(String typ, GraphEntity item, StringBuilder sb,
			SimpleList<GraphEntity> visited, SimpleKeyValueList<String, Object> links, boolean shortName) {
		String key = item.getTyp(typ, shortName);
		SimpleList<?> showedLinks = (SimpleList<?>) links
				.getValueItem(key);
		if (showedLinks == null) {
			if(visited.contains(item) == false) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(parseEntity(item, visited, typ, shortName));
			}
			return;
		}
		Iterator<?> iterator = showedLinks.iterator();
		while (iterator.hasNext()) {
			Object entry = iterator.next();
			if (entry instanceof Association == false) {
				continue;
			}
			Association element = (Association) entry;
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(parseEntity(item, visited, typ, shortName));
			sb.append(element.getSeperator());
//				("->");
//			}else{
//				sbC.append("-");
//			}

			SimpleSet<GraphEntity> targetCollection = element.getOther().getNodes();
			Iterator<GraphEntity> targetIterator = targetCollection.iterator();
			GraphEntity target = targetIterator.next();
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
	public String parseEntity(GraphEntity entity, SimpleList<GraphEntity> visited,
			boolean shortName) {
		return parseEntity(entity, visited, null, shortName);
	}

	public String parseEntity(GraphEntity entity, SimpleList<GraphEntity> visited,
			String typ, boolean shortName) {
		if(!(entity instanceof Clazz)){
			return "";
		}
		Clazz clazzEntity = (Clazz) entity;
		
		boolean shortString = visited.contains(clazzEntity);
		if (!shortString) {
			visited.add(clazzEntity);
		}
		if (typ == null) {
			typ = GraphIdMap.OBJECT;
			if (clazzEntity.getName(false) == null) {
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
					+ clazzEntity.getName(shortName)
					+ parseEntityValues(clazzEntity, typ, shortString) + "]";
		}
		return "[" + clazzEntity.getName(shortName)
				+ parseEntityValues(clazzEntity, typ, shortString) + "]";
	}

	public String parseEntityValues(GraphEntity entity, String typ,
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
			Attribute attribute;
			if (element instanceof Attribute) {
				attribute = (Attribute) element;
				sb.append(attribute.getName() + splitter
						+ attribute.getValue(typ, shortName)); // / without Typ
			}

			while (i.hasNext()) {
				element = i.next();
				if (!(element instanceof Attribute)) {
					continue;
				}
				attribute = (Attribute) element;

				sb.append(";");
				sb.append(attribute.getName() + splitter
						+ attribute.getValue(typ, shortName));
			}
		}
		return sb.toString();
	}
}
