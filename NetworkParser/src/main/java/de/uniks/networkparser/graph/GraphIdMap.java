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
import java.util.Collection;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * The Class YUMLIdParser.
 */

public class GraphIdMap extends IdMapEncoder {
	/** The Constant for CLASS Diagramms. */
	public static final String CLASS = "class";

	/** The Constant for OBJECT Diagramms. */
	public static final String OBJECT = "object";

	private GraphIdMapFilter filter = new GraphIdMapFilter()
			.withShowCardinality(true).withTyp(CLASS);

	/**
	 * Parses the object.
	 *
	 * @param object
	 *            the object
	 * @return the string
	 */
	public String parseObject(Object object) {
		return parse(object,
				filter.clone(new GraphIdMapFilter()).withTyp(OBJECT));
	}

	/**
	 * Parses the class.
	 *
	 * @param object
	 *            the object
	 * @return the string
	 */
	public String parseClass(Object object) {
		return parse(object, filter.clone(new GraphIdMapFilter())
				.withTyp(CLASS));
	}

	public String parse(Object object, GraphIdMapFilter filter) {
		GraphList list = new GraphList().withTyp(filter.getTyp());
		parse(object, filter, list, 0);
		return list.toString();
	}

	/**
	 * Parses the.
	 *
	 * @param object
	 *            the object to Serialisation
	 * @param typ
	 *            Is it a OBJECT OR A CLASS diagram
	 * @param filter
	 *            Filter for Serialisation
	 * @param showCardinality
	 *            the show cardinality
	 * @return the Object as String
	 */
	private GraphNode parse(Object object, GraphIdMapFilter filter,
			GraphList list, int deep) {
		if (object == null) {
			return null;
		}

		String mainKey = getId(object);
		GraphMember element = list.getByObject(mainKey);
		if (element != null && element instanceof GraphNode) {
			return (GraphNode)element;
		}

		SendableEntityCreator prototyp = getCreatorClass(object);
		String className = object.getClass().getName();
		className = className.substring(className.lastIndexOf('.') + 1);

		GraphClazz newElement = new GraphClazz();
		newElement.withId(mainKey);
		newElement.withClassName(className);
		list.add(newElement);
		if (prototyp != null) {
			for (String property : prototyp.getProperties()) {
				Object value = prototyp.getValue(object, property);
				if (value == null) {
					continue;
				}
				if (value instanceof Collection<?>) {
					for (Object containee : ((Collection<?>) value)) {
						parsePropertyValue(object, filter, list, deep, newElement,
								property, containee, GraphCardinality.MANY);
					}
				} else {
					parsePropertyValue(object, filter, list, deep, newElement,
							property, value, GraphCardinality.ONE);
				}
			}
		}
		return newElement;
	}

	private void parsePropertyValue(Object entity, GraphIdMapFilter filter,
			GraphList list, int deep, GraphNode element, String property,
			Object item, GraphCardinality cardinality) {
		if (item == null) {
			return;
		}
		if (!filter.isPropertyRegard(this, entity, property, item, true,
				deep + 1)) {
			return;
		}
		if (!filter.isConvertable(this, entity, property, item, true, deep + 1)) {
			return;
		}
		SendableEntityCreator valueCreater = getCreatorClass(item);
		if (valueCreater != null) {
			GraphNode subId = parse(item, filter, list, deep + 1);
			list.add(new GraphEdge().with(element).with(
					new GraphEdge(subId, cardinality, property)));
		} else {
			element.addValue(property, GraphDataType.ref(item.getClass()), "" + item);
		}
		return;
	}

	@Override
	public BaseItem encode(Object value) {
		GraphList list = new GraphList();
		parse(value, this.filter.clone(new GraphIdMapFilter()), list, 0);
		return list;
	}

	@Override
	public BaseItem encode(Object value, Filter filter) {
		GraphList list = new GraphList();
		if (filter instanceof GraphIdMapFilter) {
			GraphIdMapFilter yumlFilter = (GraphIdMapFilter) filter;
			list.withTyp(yumlFilter.getTyp());
			parse(value, yumlFilter, list, 0);
		}
		return list;
	}

	/**
	 * Gets the class name.
	 *
	 * @param object
	 *            the object
	 * @return the class name
	 */
	public String getClassName(Object object) {
		if (object instanceof String) {
			object = getObject((String) object);
		}
		String className = object.getClass().getName();
		return className.substring(className.lastIndexOf('.') + 1);
	}

	@Override
	public BaseItem getPrototyp() {
		return new GraphList();
	}
}
