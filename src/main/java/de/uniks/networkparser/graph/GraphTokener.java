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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.list.SimpleKeyValueList;
/**
 * The Class YUMLIdParser.
 */

public class GraphTokener extends Tokener {
	/** The Constant for CLASS Diagramms. */
	public static final String CLASS = "classdiagram";

	public static final byte FLAG_CLASS = 0x01;
	public static final byte FLAG_CARDINALITY = 0x02;
	public static final byte FLAG_SHOWLINE = 0x04;
	public static final byte FLAG_ORDERD = 0x08;
	public static final byte FLAG_UNORDERD = 0x00;
//	public static final byte FLAG_OBJECT = 0x01;

	/** The Constant for OBJECT Diagramms. */
	public static final String OBJECT = "objectdiagram";

	private String getType(MapEntity map) {
		if(map.isTokenerFlag(FLAG_CLASS) ){
			return CLASS;
		}
		return OBJECT;
	}

	@Override
	public GraphList encode(Object object, MapEntity map) {
		GraphList newElement = new GraphList();

		newElement.withTyp(getType(map));
		Clazz main = parse(object, map, newElement, 0);
		GraphDiff diff = newElement.getDiff();
		if(diff != null) {
			diff.withMain(main);
		}
		return newElement;
	}

	/**
	 * Parses the.
	 *
	 * @param object			the object to Serialisation
	 * @param map				Runtime information OBJECT OR A CLASS diagram
	 * @param list				Target List
	 * @param deep				The Current Deep of Graph
	 * @return the Object as String
	 */
	private Clazz parse(Object object, MapEntity map,
			GraphList list, int deep) {
		if (object == null) {
			return null;
		}

		String mainKey = getId(object);
		GraphMember element = list.getByObject(mainKey, true);
		if (element != null && element instanceof Clazz) {
			return (Clazz)element;
		}

		SendableEntityCreator prototyp = getCreatorClass(object);
		String className = object.getClass().getName();
		className = className.substring(className.lastIndexOf('.') + 1);

		Clazz newElement = new Clazz();
		newElement.withId(mainKey);
		newElement.with(className);
		list.with(newElement);
		if (prototyp != null) {
			for (String property : prototyp.getProperties()) {
				Object value = prototyp.getValue(object, property);
				if (value == null) {
					continue;
				}
				if (value instanceof Collection<?>) {
					for (Object containee : ((Collection<?>) value)) {
						parsePropertyValue(object, map, list, deep, newElement,
								property, containee, Cardinality.MANY);
					}
				} else {
					parsePropertyValue(object, map, list, deep, newElement,
							property, value, Cardinality.ONE);
				}
			}
		}
		return newElement;
	}

	private void parsePropertyValue(Object entity, MapEntity map,
			GraphList list, int deep, Clazz element, String property,
			Object item, Cardinality cardinality) {
		if (item == null) {
			return;
		}
		map.add();
		if(map.isPropertyRegard(entity, getMap(), property, item) == false || map.isConvertable(entity, getMap(), property, item) == false) {
			map.minus();
			return;
		}
		SendableEntityCreator valueCreater = getCreatorClass(item);
		if (valueCreater != null) {
			Clazz subId = parse(item, map, list, deep + 1);
			Association edge = new Association(element);
			element.with(edge);
			Association target = new Association(subId).with(cardinality).with(property);
			subId.with(target);
			list.with(edge.with(target));
		} else {
			Attribute attribute = element.createAttribute(property, DataType.create(item.getClass()));
			attribute.withValue("" + item);
		}
		map.minus();
		return;
	}

	public void highlightModel(JsonArray clazzDiagram, GraphList objectDiagram) {
		GraphList list = new GraphConverter().convertGraphList(GraphTokener.CLASS, clazzDiagram);
		this.highlightModel(list, objectDiagram);
	}
	public GraphList highlightModel(GraphList clazzDiagram, GraphList objectDiagram) {
		HashMap<String, Association> edges = new HashMap<String, Association>();
		HashMap<String, Clazz> clazzes = new HashMap<String, Clazz>();

		// Copy all Nodes
		for(Iterator<GraphMember> i = clazzDiagram.getChildren().iterator();i.hasNext();) {
			Clazz item = (Clazz) i.next();
			clazzes.put(item.getName(false), item);
		}
		// Copy all Edges
		for(Iterator<Association> i = clazzDiagram.getAssociations().iterator();i.hasNext();) {
			Association item = i.next();
			Clazz node = (Clazz) item.getClazz();
			edges.put(node.getName(false)+":"+item.getName(), item);
		}

		// Check all Clazzes of the objectdiagram
		for(Iterator<GraphMember> i = objectDiagram.getChildren().iterator();i.hasNext();) {
			Clazz item = (Clazz) i.next();
			Clazz graphClazz = clazzes.get(item.getName(false));
			if(graphClazz != null) {
				GraphDiff diff = graphClazz.getDiff();
				diff.addCounter();
			}
		}
		// Copy all Edges
		for(Iterator<Association> i = objectDiagram.getAssociations().iterator();i.hasNext();) {
			Association item = i.next();
			Clazz node = (Clazz) item.getClazz();
			String signature = node.getName(false)+":"+item.getName();
			Association graphEdge = edges.get(signature);
			if(graphEdge != null) {
				GraphDiff diff = graphEdge.getDiff();
				diff.addCounter();
			}
		}
		return clazzDiagram;
	}

	public GraphPatternMatch diffModel(Object master, Object slave, MapEntity map) {
		if(map.add(master) == false) {
			return null;
		}
		GraphPatternMatch result = new GraphPatternMatch();
		if(master == null) {
			if(slave == null) {
				return result;
			}
			result.with(GraphPatternChange.createCreate(slave));
			return result;
		}
		if(master.equals(slave)) {
			return result;
		}
		if(slave == null) {
			result.with(GraphPatternChange.createDelete(master));
			return result;
		}

		SendableEntityCreator masterCreator = this.map.getCreatorClass(master);
		SendableEntityCreator slaveCreator = this.map.getCreatorClass(slave);

		if(masterCreator == null || slaveCreator == null) {
			result.with(GraphPatternChange.createChange(master, slave));
			// No Creator Found for both value check if th same instance
			return result;
		}
		String[] properties = masterCreator.getProperties();

// Check properties
// Step one use equals-Method
		SimpleKeyValueList<String, Collection<?>> assocMany = new SimpleKeyValueList<String, Collection<?>>();
		SimpleKeyValueList<String, Object> attributes = new SimpleKeyValueList<String, Object>();
		for(String property : properties) {
			Object masterValue = masterCreator.getValue(master, property);
			if(masterValue instanceof Collection<?>) {
				assocMany.add(property, (Collection<?>)masterValue);
			} else {
				attributes.add(property, masterValue);
			}
		}
		SimpleKeyValueList<Object, Object> matchMap=new SimpleKeyValueList<Object, Object>();
		if(map.isFlag(GraphTokener.FLAG_ORDERD)) {
// Step two: orderd
//				Primitive
//				Assoc to 1
//				Assoc to n
			for(Iterator<Entry<String, Object>> i = attributes.iterator();i.hasNext();){
				Entry<String, Object> item = i.next();
				Object value = item.getValue();
				Object slaveValue = slaveCreator.getValue(slave, item.getKey());
				if(value == null) {
					if(slaveValue == null) {
						continue;
					}
					if(slaveValue instanceof Collection<?>) {
						Collection<?> child = (Collection<?>) slaveValue;
						GraphPatternMatch match = GraphPatternMatch.create(item.getKey(), slaveValue);
						for(Iterator<?> childIterator = child.iterator();childIterator.hasNext();) {
							match.with(GraphPatternChange.createCreate(childIterator.next()));
						}
						if(match.size()>0) {
							result.with(match);
						}
					} else {
						result.with(GraphPatternChange.createCreate(item.getKey(), slaveValue));
					}
					continue;
				}
				if(value instanceof String || value instanceof Date || value instanceof Number) {
					if(value.equals(slaveValue) == false) {
						result.with(GraphPatternChange.createChange(item.getKey(), value, slaveValue));
					}
				} else {
					matchMap.add(value, slaveValue);
					if(this.map.getCreatorClass(value) != null ) {
						result.with(diffModel(value, slaveValue, map));
					} else if(value.equals(slaveValue) == false) {
						result.with(GraphPatternChange.createChange(item.getKey(), value, slaveValue));
					}
				}
			}
			// Now try to Many Assoc
			for(Iterator<Entry<String, Collection<?>>> i = assocMany.iterator();i.hasNext();){
				Entry<String, Collection<?>> item = i.next(); 
				Collection<?> masterCollection = item.getValue();
				GraphPatternMatch match = GraphPatternMatch.create(item.getKey(), masterCollection);
				Object slaveValue = slaveCreator.getValue(slave, item.getKey());
				if(slaveValue == null || slaveValue instanceof Collection<?> == false) {
					if(masterCollection.size()>0) {
						for(Iterator<?> childIterator = masterCollection.iterator();childIterator.hasNext();) {
							match.with(GraphPatternChange.createDelete(childIterator.next()));
						}
						result.with(match);
					}
					continue;
				}
				Iterator<?> masterIterator = masterCollection.iterator();
				Iterator<?> slaveIterator = ((Collection<?>)slaveValue).iterator();
				while(masterIterator.hasNext()) {
					Object masterChild = masterIterator.next();
					if(slaveIterator.hasNext()) {
						Object slaveChild = slaveIterator.next();
						match.with(diffModel(masterChild, slaveChild, map));
					} else {
						match.with(GraphPatternChange.createDelete(masterChild));
					}
				}
				while(slaveIterator.hasNext()) {
					match.with(GraphPatternChange.createCreate(slaveIterator.next()));
				}
				if(match.size()>0) {
					result.with(match);
				}
			}
		} else {
// Step two: unorderd
//				Assoc to n
//				Assoc to 1
//				Primitive ( try to find keyattributes use order: String, Date, Int, Object)
			for(String property : properties) {
				Object masterValue = masterCreator.getValue(master, property);
				Object slaveValue = slaveCreator.getValue(slave, property);
//				diffModel(value, slaveValue, map)
			}
		}
		return result;
	}

	@Override
	public GraphTokener withMap(IdMap map) {
		super.withMap(map);
		return this;
	}
}
