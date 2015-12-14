package de.uniks.networkparser.graph;

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
	public static SimpleSet<Annotation> getAnnotations(Clazz item) {
		return getAnnotations(item.getAnnotation());
	}
	public static SimpleSet<Annotation> getAnnotations(Method item) {
		return getAnnotations(item.getAnnotation());
	}
	public static SimpleSet<Annotation> getAnnotations(Attribute item) {
		return getAnnotations(item.getAnnotation());
	}
	public static SimpleSet<Annotation> getAnnotations(Annotation item) {
		SimpleSet<Annotation> collection = new SimpleSet<Annotation>();
		collection.add(item);
		if(item != null) {
			while(item.hasNext()) {
				item = item.next();
				collection.add(item);
			}
		}
		return collection;
 	}
	public static boolean isWithNoObjects(Clazz clazz) {
		return (clazz.hasModifier(Modifier.ABSTRACT) || clazz instanceof Interfaze);
	}
	public static boolean isInterface(Clazz clazz) {
		return clazz instanceof Interfaze;
	}
	public static boolean isEnumeration(Clazz clazz) {
		return clazz instanceof Enumeration;
	}
	public static SimpleSet<Association> getOtherAssociations(Clazz clazz) {
		SimpleSet<Association> collection = new SimpleSet<Association>();
		for(Association assoc : clazz.getAssociation()) {
			collection.add(assoc.getOther());
		}
		return collection;
	}
	public static void removeYou(Attribute attribute) {
		attribute.withParent(null);
		de.uniks.networkparser.graph.Annotation annotation = attribute.getAnnotation();
		attribute.without(annotation);
	}

	public static void removeYou(Association assoc) {
		assoc.setParent(null);
		assoc.withOtherEdge(null);
		assoc.without(assoc.getClazz());
	}
	

	public static void removeYou(Clazz clazz) {
		clazz.setParent(null);
//		clazz.without(clazz.getAssociation().toArray(new Association[clazz.getAssociation().size()]));
//		clazz.without(clazz.getAttributes().toArray(new Attribute[clazz.getAssociation().size()]));
		clazz.without(clazz.getChildren().toArray(new GraphMember[clazz.getChildren().size()]));
	}
}
