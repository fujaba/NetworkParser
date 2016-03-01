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
import java.util.HashMap;
import java.util.Iterator;

import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonArray;
/**
 * The Class YUMLIdParser.
 */

public class GraphTokener extends Tokener {
	public static final byte FLAG_OBJECT = 0x01;
	public static final byte FLAG_CLASS = 0x02;
	public static final byte FLAG_CARDINALITY = 0x04;
	public static final byte FLAG_SHOWLINE = 0x08;
	
	/** The Constant for CLASS Diagramms. */
	public static final String CLASS = "classdiagram";

	/** The Constant for OBJECT Diagramms. */
	public static final String OBJECT = "objectdiagram";
	
	private String getType(MapEntity map) {
		if(map.isFlag(FLAG_OBJECT)){
			return OBJECT;
		}
		return CLASS;
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
	 * @param object
	 *			the object to Serialisation
	 * @param typ
	 *			Is it a OBJECT OR A CLASS diagram
	 * @param filter
	 *			Filter for Serialisation
	 * @param showCardinality
	 *			the show cardinality
	 * @return the Object as String
	 */
	private Clazz parse(Object object, MapEntity map,
			GraphList list, int deep) {
		if (object == null) {
			return null;
		}

		String mainKey = map.getId(object);
		GraphMember element = list.getByObject(mainKey, true);
		if (element != null && element instanceof Clazz) {
			return (Clazz)element;
		}

		SendableEntityCreator prototyp = map.getCreatorClass(object);
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
		if(map.isPropertyRegard(entity, property, item) == false || map.isConvertable(entity, property, item) == false) {
			map.minus();
			return;
		}
		SendableEntityCreator valueCreater = map.getCreatorClass(item);
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
	
	public GraphList diffModel(GraphList master, GraphList slave) {
		GraphDiff masterDiff = master.getDiff();
		GraphDiff saveDiff = slave.getDiff();
		Clazz masterFile = (Clazz) masterDiff.getMainFile();
		Clazz slaveFile = (Clazz) saveDiff.getMainFile();
		masterFile.getDiff().with(slaveFile);

		// create new map<key: Clazz without s, Value: Object with {attributes, items}>
		// Search for single clazz
		// Search for clazz with master attributes
		// search to 1 assoc
		// try to find in to n assoc
		searchMatch(masterFile);
		return master;
	}
	
	private void searchMatch(Clazz master) {
		master.getChildren().iterator();
	}
	
//	protected void initItem(GraphMember item) {
//		item.withChildren(new GraphDiff());
//		if(item instanceof Clazz) {
//			if(this.getMaster() ==null) {
//				this.toDoList.add((Clazz) item);
//			}
//		}
//	}
	public void highlightModel(JsonArray clazzDiagram, GraphList objectDiagram) {
		GraphList list = new GraphConverter().convertGraphList(GraphTokener.CLASS, clazzDiagram, true);
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
}
