package de.uniks.networkparser.graph;

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
		return (Clazz) item.getByObject(clazz, fullName);
	}
	public static SimpleSet<Annotation> getAnnotations(GraphMember item) {
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
		return (clazz.hasModifier(Modifier.ABSTRACT) || clazz.getType() == ClazzType.INTERFACE);
	}
	public static boolean isInterface(Clazz clazz) {
		return clazz.getType() == ClazzType.INTERFACE;
	}
	public static boolean isEnumeration(Clazz clazz) {
		return clazz.getType() == ClazzType.ENUMERATION;
	}
	public static SimpleSet<Association> getOtherAssociations(Clazz clazz) {
		SimpleSet<Association> collection = new SimpleSet<Association>();
		for(Association assoc : clazz.getAssociation()) {
			collection.add(assoc.getOther());
		}
		return collection;
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
			assoc.withOtherEdge(null);
			assoc.without(assoc.getClazz());
		}
		if(value instanceof Clazz) {
			Clazz clazz = (Clazz) value;
			clazz.without(clazz.getChildren().toArray(new GraphMember[clazz.getChildren().size()]));	
		}
	}
}
