package de.uniks.networkparser.graph.util;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.list.SimpleSet;

public class ClazzSet extends SimpleSet<Clazz>{
	public AttributeSet getAttributes() {
		AttributeSet collection = new AttributeSet();
		for(Clazz item : this) {
			collection.addAll(item.getAttributes());
		}
		return collection;
	}
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for(Clazz item : this) {
			collection.addAll(item.getMethods());
		}
		return collection;
	}

	public AnnotationSet getAnnotations() {
		AnnotationSet collection = new AnnotationSet();
		for(Clazz item : this) {
			collection.add(item.getAnnotation());
		}
		return collection;
	}
	public ModifierSet getModifiers() {
		ModifierSet collection = new ModifierSet();
		for(Clazz item : this) {
			collection.add(item.getModifier());
		}
		return collection;
	}


	//FIXME	de.uniks.networkparser.graph.Clazz.getClassModel()
//	de.uniks.networkparser.graph.Clazz.getImports()
//	de.uniks.networkparser.graph.Clazz.getInterfaces(boolean)
//	de.uniks.networkparser.graph.Clazz.getKidClazzes(boolean)
//	de.uniks.networkparser.graph.Clazz.getSuperClass()
//	de.uniks.networkparser.graph.Clazz.getSuperClazzes(boolean)
//	de.uniks.networkparser.graph.Clazz.getType()
//	de.uniks.networkparser.graph.Clazz.getValues()
}
