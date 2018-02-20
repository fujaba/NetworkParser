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
import java.util.Iterator;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphEntity;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphSimpleSet;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class YUMLConverter implements Converter{
	/** The Constant URL. */
	public static final String URL = "http://yuml.me/diagram/class/";
	public boolean defaultShowPackage;

	public String convert(GraphList root, boolean removePackage) {
		String type = root.getType();
		GraphSimpleSet collection = GraphUtil.getChildren(root);
		if(collection.size()>0) {
			StringBuilder sb = new StringBuilder();
			SimpleList<GraphMember> visitedObj = new SimpleList<GraphMember>();
			root.initSubLinks();
			for(GraphMember item : collection) {
				parse(type, item, sb, visitedObj, removePackage);
			}
			return sb.toString();
		}
		return null;
	}

	public void parse(String type, GraphMember item, StringBuilder sb,
			SimpleList<GraphMember> visited, boolean shortName) {
		if(item instanceof GraphEntity) {
			parse(type, (GraphEntity) item, sb, visited, shortName);
		}
	}
	public void parse(String type, GraphEntity item, StringBuilder sb,
			SimpleList<GraphMember> visited, boolean shortName) {
		SimpleSet<Association> association = item.getAssociations();
		if (association.size()==0) {
			if(visited.contains(item) == false) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(parseEntity(item, visited, type, shortName));
			}
			return;
		}
		if (type == null) {
			type = GraphTokener.OBJECT;
		}
		Iterator<?> iterator = association.iterator();
		while (iterator.hasNext()) {
			Object entry = iterator.next();
			if (entry instanceof Association == false) {
				continue;
			}
			Association element = (Association) entry;
			Association other = element.getOther();
			if(GraphTokener.CLASS.equals(type)) {
				if ( GraphUtil.containsClazzAssociation(visited, element, other)) {
					continue;
				}
			}
			if(visited.contains(element)) {
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
	}

	// ##################################### Entity
	public String parseEntity(GraphEntity entity, SimpleList<GraphMember> visited,
			boolean shortName) {
		return parseEntity(entity, visited, null, shortName);
	}

	public String parseEntity(GraphEntity entity, SimpleList<GraphMember> visited,
			String type, boolean shortName) {
		if(!(entity instanceof Clazz)){
			return "";
		}
		Clazz clazzEntity = (Clazz) entity;
		boolean shortString = visited.contains(clazzEntity);
		if(shortString == false) {
			visited.add(clazzEntity);
		}
		if (type == null) {
			type = GraphTokener.OBJECT;
			if (clazzEntity.getName(false) == null) {
				type = GraphTokener.CLASS;
			}
		}

		StringBuilder sb = new StringBuilder("[");
		if (type == GraphTokener.OBJECT) {
			sb.append(clazzEntity.getId());
			sb.append(" : ");
		}
		sb.append(clazzEntity.getName(shortName));
		if(shortString == false) {
			sb.append(parseEntityValues(clazzEntity, type, shortName));
		}
		sb.append("]");
		return sb.toString();
	}

	public String parseEntityValues(GraphEntity entity, String type,
			boolean shortName) {
		StringBuilder sb = new StringBuilder();

		Iterator<GraphMember> i = GraphUtil.getChildren(entity).iterator();
		boolean second=false;
		if (i.hasNext()) {
			String splitter = "";
			if (type.equals(GraphTokener.OBJECT)) {
				splitter = "=";
			} else if (type.equals(GraphTokener.CLASS)) {
				splitter = ":";
			}

			Object element;
			Attribute attribute;
//			if (element instanceof Attribute) {
//				attribute = (Attribute) element;
//				sb.append(attribute.getName() + splitter
//						+ attribute.getValue(type, shortName)); // / without Type
//			}

			while (i.hasNext()) {
				element = i.next();
				if (!(element instanceof Attribute)) {
					continue;
				}
				attribute = (Attribute) element;
				if(second) {
					sb.append(";");
				}
				second = true;
				sb.append(attribute.getName() + splitter
						+ attribute.getValue(type, shortName));
			}
		}
		if(sb.length()>0) {
			return "|" + sb.toString();
		}
		return sb.toString();
	}

	@Override
	public String encode(BaseItem entity) {
		if(entity instanceof GraphList) {
			return convert((GraphList) entity, defaultShowPackage);
		}
		return null;
	}
}
