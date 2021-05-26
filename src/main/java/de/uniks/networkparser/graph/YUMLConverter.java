package de.uniks.networkparser.graph;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.util.Iterator;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class YUMLConverter implements Converter {
	/** The Constant URL. */
	public static final String URL = "http://www.yuml.me/diagram/class/";
	public boolean defaultShowPackage;

	public String convert(GraphModel root, boolean removePackage) {
		String type = GraphTokener.CLASSDIAGRAM;
		if (root instanceof GraphList) {
			type = ((GraphList) root).getType();
		}
		GraphSimpleSet collection = GraphUtil.getChildren(root);
		if (collection == null) {
			return null;
		}
		if (collection.size() > 0) {
			StringBuilder sb = new StringBuilder();
			SimpleList<GraphMember> visitedObj = new SimpleList<GraphMember>();
			if (root instanceof GraphList) {
				((GraphList) root).initSubLinks();
			}
			for (GraphMember item : collection) {
				parse(type, item, sb, visitedObj, removePackage);
			}
			return sb.toString();
		}
		return null;
	}

	public void parse(String type, GraphMember item, StringBuilder sb, SimpleList<GraphMember> visited,
			boolean shortName) {
		if (item instanceof GraphEntity) {
			parse(type, (GraphEntity) item, sb, visited, shortName);
		}
	}

	public boolean parse(String type, GraphEntity item, StringBuilder sb, SimpleList<GraphMember> visited,
			boolean shortName) {
		if (item == null) {
			return false;
		}
		SimpleSet<Association> association = item.getAssociations();
		if (association.size() == 0) {
			if (visited.contains(item) == false) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(parseEntity(item, visited, type, shortName));
			}
			return true;
		}
		if (type == null) {
			type = GraphTokener.OBJECTDIAGRAM;
		}
		Iterator<?> iterator = association.iterator();
		while (iterator.hasNext()) {
			Object entry = iterator.next();
			if (entry instanceof Association == false) {
				continue;
			}
			Association element = (Association) entry;
			Association other = element.getOther();
			if (GraphTokener.CLASSDIAGRAM.equals(type)) {
				if (GraphUtil.containsClazzAssociation(visited, element, other)) {
					continue;
				}
			}
			if (visited.contains(element)) {
				continue;
			}
			visited.add(element);
			visited.add(other);
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(parseEntity(item, visited, type, shortName));
			String seperator = GraphUtil.getSeperator(element);
			sb.append(seperator);

			SimpleSet<GraphEntity> targetCollection = GraphUtil.getNodes(other);
			Iterator<GraphEntity> targetIterator = targetCollection.iterator();
			GraphEntity target = targetIterator.next();
			sb.append(parseEntity(target, visited, type, shortName));

			while (targetIterator.hasNext()) {
				sb.append(parseEntity(item, visited, type, shortName));
				sb.append(seperator);
				target = targetIterator.next();
				sb.append(parseEntity(target, visited, type, shortName));
			}
		}
		return true;
	}

	/**
	 * Method for Parsing Entity
	 * 
	 * @param entity    Entity to Parse
	 * @param visited   Visited elements
	 * @param shortName ShortName
	 * @return ResultString
	 */
	public String parseEntity(GraphEntity entity, SimpleList<GraphMember> visited, boolean shortName) {
		return parseEntity(entity, visited, null, shortName);
	}

	public String parseEntity(GraphEntity entity, SimpleList<GraphMember> visited, String type, boolean shortName) {
		if (entity instanceof Clazz == false) {
			return "";
		}
		Clazz clazzEntity = (Clazz) entity;
		boolean shortString = visited.contains(clazzEntity);
		if (shortString == false) {
			visited.add(clazzEntity);
		}
		if (type == null) {
			type = GraphTokener.OBJECTDIAGRAM;
			if (clazzEntity.getName(false) == null) {
				type = GraphTokener.CLASSDIAGRAM;
			}
		}

		StringBuilder sb = new StringBuilder("[");
		if (type == GraphTokener.OBJECTDIAGRAM) {
			sb.append(clazzEntity.getId());
			sb.append(" : ");
		}
		sb.append(clazzEntity.getName(shortName));
		if (shortString == false) {
			sb.append(parseEntityValues(clazzEntity, type, shortName));
		}
		sb.append("]");
		return sb.toString();
	}

	public String parseEntityValues(GraphEntity entity, String type, boolean shortName) {
		StringBuilder sb = new StringBuilder();
		GraphSimpleSet children = GraphUtil.getChildren(entity);
		if (children == null) {
			return null;
		}
		Iterator<GraphMember> i = children.iterator();
		boolean second = false;
		if (i.hasNext()) {
			String splitter = "";
			if (type.equals(GraphTokener.OBJECTDIAGRAM)) {
				splitter = "=";
			} else if (type.equals(GraphTokener.CLASSDIAGRAM)) {
				splitter = ":";
			}

			Object element;
			Attribute attribute;

			while (i.hasNext()) {
				element = i.next();
				if (element instanceof Attribute == false) {
					continue;
				}
				attribute = (Attribute) element;
				if (second) {
					sb.append(";");
				}
				second = true;
				sb.append(attribute.getName() + splitter + attribute.getValue(type, shortName));
			}
		}
		if (sb.length() > 0) {
			return "|" + sb.toString();
		}
		return sb.toString();
	}

	@Override
	public String encode(BaseItem entity) {
		if (entity instanceof GraphModel) {
			return convert((GraphModel) entity, defaultShowPackage);
		}
		return null;
	}
}
