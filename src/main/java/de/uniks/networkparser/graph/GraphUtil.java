package de.uniks.networkparser.graph;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Clazz.ClazzType;
import de.uniks.networkparser.list.SimpleSet;

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
/**
 * Special Util for package Method
 *
 */
public class GraphUtil {
	public static Clazz getByObject(GraphEntity item, String clazz, boolean fullName) {
		if(clazz == null) {
			return null;
		}
		return (Clazz) item.getByObject(clazz, fullName);
	}
	public static SimpleSet<Annotation> getAnnotations(GraphMember item) {
		if(item == null) {
			return null;
		}
		SimpleSet<Annotation> collection = new SimpleSet<Annotation>();
		Annotation annotation = null;
		if(item instanceof Clazz) {
			annotation = ((Clazz)item).getAnnotation();
		}
		if(item instanceof Attribute) {
			annotation = ((Attribute)item).getAnnotation();
		}
		if(item instanceof Annotation) {
			annotation = (Annotation) item;
		}
		if(annotation != null) {
			collection.add(annotation);
			while(annotation.hasNext()) {
				annotation = annotation.next();
				collection.add(annotation);
			}
		}
		return collection;
	}

	public static boolean isWithNoObjects(Clazz clazz) {
		if(clazz == null) {
			return false;
		}
		return (clazz.getModifier().has(Modifier.ABSTRACT) || clazz.getType() == ClazzType.INTERFACE);
	}
	public static boolean isInterface(Clazz clazz) {
		if(clazz == null) {
			return false;
		}
		return clazz.getType() == ClazzType.INTERFACE;
	}
	public static boolean isEnumeration(Clazz clazz) {
		if(clazz == null) {
			return false;
		}
		return clazz.getType() == ClazzType.ENUMERATION;
	}
	public static boolean isUndirectional(Association assoc) {
		if(assoc == null) {
			return false;
		}
		if(assoc.getType()==AssociationTypes.ASSOCIATION && assoc.getOtherType()==AssociationTypes.EDGE) {
			return true;
		}
		return assoc.getOtherType()==AssociationTypes.ASSOCIATION && assoc.getType()==AssociationTypes.EDGE;
	}
	
	public static boolean isInterfaceAssociation(Association assoc) {
		if(assoc == null) {
			return false;
		}
		if(assoc.getType()==AssociationTypes.IMPLEMENTS && assoc.getOtherType()==AssociationTypes.EDGE) {
			return true;
		}
		return assoc.getOtherType()==AssociationTypes.IMPLEMENTS && assoc.getType()==AssociationTypes.EDGE;
	}
	
	public static CharacterBuffer getMethodParameters(Method method, boolean shortName) {
		return method.getParameterString(shortName);
	}

	public static SimpleSet<Association> getOtherAssociations(Clazz clazz) {
		SimpleSet<Association> collection = new SimpleSet<Association>();
		for(Association assoc : clazz.getAssociations()) {
			collection.add(assoc.getOther());
		}
		return collection;
	}
	
	public static GraphSimpleSet getChildren(GraphMember item) {
		return item.getChildren();
	}
	public static String getSeperator(Association item) {
		return item.getSeperator();
	}
	public static SimpleSet<GraphEntity> getNodes(GraphMember item) {
		return item.getNodes();
	}
	public static GraphDiff getDifference(GraphMember item) {
		return item.getDiff();
	}
	
	public static boolean addAccoc(GraphList list, Association assoc) {
		return list.addAssoc(assoc);
	}
	
	public static Attribute createAttribute() {
		return new Attribute();
	}
	
	public static void removeYou(GraphMember value) {
		value.setParent(null);
		if(value instanceof Attribute) {
			Attribute attribute = (Attribute) value;
			Annotation annotation = attribute.getAnnotation();
			value.without(annotation);
		}
		if(value instanceof Association) {
			Association assoc = (Association) value;
			assoc.withOther(null);
			assoc.without(assoc.getClazz());
		}
		if(value instanceof Clazz) {
			Clazz clazz = (Clazz) value;
			GraphSimpleSet collection = clazz.getChildren();
			clazz.without(collection.toArray(new GraphMember[collection.size()]));
		}
	}
}
