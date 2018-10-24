package de.uniks.networkparser.graph;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

public class ClazzSet extends SimpleSet<Clazz> {
	public AttributeSet getAttributes() {
		AttributeSet collection = new AttributeSet();
		for (Clazz item : this) {
			collection.addAll(item.getAttributes());
		}
		return collection;
	}

	public AssociationSet getAssociations() {
		AssociationSet collection = new AssociationSet();
		for (Clazz item : this) {
			collection.addAll(item.getAssociations());
		}
		return collection;
	}

	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for (Clazz item : this) {
			collection.addAll(item.getMethods());
		}
		return collection;
	}

	public AnnotationSet getAnnotations() {
		AnnotationSet collection = new AnnotationSet();
		for (Clazz item : this) {
			collection.add(item.getAnnotation());
		}
		return collection;
	}

	public ModifierSet getModifiers() {
		ModifierSet collection = new ModifierSet();
		for (Clazz item : this) {
			collection.add(item.getModifier());
		}
		return collection;
	}

	public ClazzSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Clazz.PROPERTY_NAME, otherValue));
	}

	public String toString(String splitter) {
		if (size() == 0) {
			return null;
		}
		CharacterBuffer buffer = new CharacterBuffer();
		for (Clazz clazz : this) {
			if (buffer.length() > 0) {
				buffer.with(splitter);
			}
			buffer.with(clazz.getName());
		}
		return buffer.toString();
	}
	
	@Override
	public boolean contains(Object o) {
		if(o instanceof String && o != null) {
			for(Clazz item : this) {
				if (o.equals(item.getId())) {
					return true;
				}
			}
		}
		return super.contains(o);
	}

	public Clazz getClazz(String id) {
		if(id == null) {
			return null;
		}
		for(Clazz item : this) {
			if (id.equals(item.getName())) {
				return item;
			}
		}
		return null;
	}

	@Override
	public Clazz[] toArray() {
		return super.toArray(new Clazz[size()]);
	}
}
