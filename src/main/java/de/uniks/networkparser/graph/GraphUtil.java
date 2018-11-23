package de.uniks.networkparser.graph;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;
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
 * @author Stefan Lindel
 */
public class GraphUtil {
	private static SimpleList<String> noneDictionary=new SimpleList<String>().with("aircraft","carp","deer", "salmon", "sheep", "trout");
	private static SimpleList<String> oesDictionary=new SimpleList<String>().with("hero", "potato", "torpedo");
	
	
	public static String getPlural(String singular) {
		// Zischlaut
		if(singular == null || singular.length()<1) {
			return null;
		}
		if(noneDictionary.contains(singular)) {
			return singular;
		}
		if(singular.endsWith("ch") || singular.endsWith("s") || singular.endsWith("sh") || singular.endsWith("x") || singular.endsWith("z") ) {
			return singular+"es";
		}
		// y Konsonant
		if(singular.endsWith("y")) {
			return singular.subSequence(0, singular.length()-1)+"ies";
		}
		// -f/-fe wird zu -ves im Plural
		if(singular.endsWith("f") || singular.endsWith("fe") ) {
			return singular.subSequence(0, singular.length()-1)+"ves";
		}
		// -o wird zu -oes im Plural
		if(singular.endsWith("o") ) {
			if(oesDictionary.contains(singular)) {
				return singular+"es";
			}
			return singular+"s";
		}
		return singular+"s";
	}
	
	public static boolean isPlural(String plural) {
		if(noneDictionary.contains(plural)) {
			return true;
		}
		if(plural == null || plural.length()<1) {
			return false;
		}
		if(plural.endsWith("s") == false) {
			return false;
		}
		return true;
	}

	public static double compareName(String source, String other) {
		int counter = 0;
		int caseDiff = 0;
		double score = 0;
		char sourceChar;
		char otherChar;
		while (counter < source.length() && counter < other.length()) {
			sourceChar = source.charAt(counter);
			otherChar = other.charAt(counter);
			if (sourceChar != otherChar) {
				if (Math.abs(sourceChar - otherChar) != 32) {
					score += 1;
				} else if (caseDiff < 100) {
					score += 0.01;
					caseDiff++;
				}
			}
			counter++;
		}
		score += Math.abs(source.length() - other.length());
		return score;
	}

	public static final boolean isGenerate(GraphMember member) {
		return member.isGenerate;
	}

	public static final void setGenerate(GraphMember member, boolean value) {
		if (member != null) {
			member.isGenerate = value;
		}
	}

	public static final boolean setRole(GraphMember member, ObjectCondition value) {
		if (member != null) {
			member.withRole(value);
			return true;
		}
		return false;
	}

	public static final ObjectCondition getRole(GraphMember member) {
		if (member != null) {
			return member.getRole();
		}
		return null;
	}

	public static final boolean withChildren(GraphMember member, GraphMember child) {
		if (member != null) {
			member.withChildren(child);
			return true;
		}
		return false;
	}

	public static double compareType(String sourceType, String otherType) {
		if (EntityUtil.isNumericType(sourceType) && EntityUtil.isNumericType(otherType)) {
			return 0;
		}
		if (EntityUtil.isPrimitiveType(sourceType) && EntityUtil.isPrimitiveType(otherType)) {
			return 0;
		}
		if (sourceType.equals(otherType)) {
			return 0;
		}
		return -1;
	}

	public static final String getPackage(Class<?> classObj) {
		if (classObj != null) {
			return getPackage(classObj.getName());
		}
		return "";
	}

	public static final String getPackage(String name) {
		if (name == null) {
			return "";
		}
		int pos = name.lastIndexOf(".");
		if (pos > 0) {
			return name.substring(0, pos);
		}
		return name;
	}

	public static final Clazz getByObject(GraphEntity item, String clazz, boolean fullName) {
		if (clazz == null) {
			return null;
		}
		return (Clazz) item.getByObject(clazz, fullName);
	}

	public static final GraphModel setGenPath(GraphModel model, String path) {
		model.genPath = path;
		return model;
	}

	public static final String getGenPath(GraphModel model) {
		return model.genPath;
	}

	public static final SimpleSet<Annotation> getAnnotations(GraphMember item) {
		if (item == null) {
			return null;
		}
		SimpleSet<Annotation> collection = new SimpleSet<Annotation>();
		Annotation annotation = null;
		if (item instanceof Clazz) {
			annotation = ((Clazz) item).getAnnotation();
		}
		if (item instanceof Attribute) {
			annotation = ((Attribute) item).getAnnotation();
		}
		if (item instanceof Annotation) {
			annotation = (Annotation) item;
		}
		if (annotation != null) {
			collection.add(annotation);
			while (annotation.hasNext()) {
				annotation = annotation.next();
				collection.add(annotation);
			}
		}
		return collection;
	}

	public static final void setAssociation(GraphEntity entry, Association assoc) {
		entry.with(assoc);
	}

	public static final void setGraphImage(Clazz clazz, GraphImage... images) {
		clazz.with(images);
	}

	public static final void setLiteral(Clazz clazz, Literal... literals) {
		clazz.with(literals);
	}

	public static final void setModifierEntry(Clazz clazz, ModifyEntry modifier) {
		clazz.with(modifier);
	}

	public static final void setClazzType(Clazz clazz, String clazzType) {
		clazz.withType(clazzType);
	}

	public static final void setImport(Clazz clazz, Import... importClazzes) {
		clazz.with(importClazzes);
	}

	public static boolean setId(GraphEntity graphEntity, String id) {
		return graphEntity.setId(id);
	}

	public static final boolean isWithNoObjects(Clazz clazz) {
		if (clazz == null) {
			return false;
		}
		return (clazz.getModifier().has(Modifier.ABSTRACT) || Clazz.TYPE_INTERFACE.equals(clazz.getType()));
	}
	

	public static boolean isAbstract(Clazz clazz) {
		if (clazz == null) {
			return false;
		}
		return clazz.getModifier().has(Modifier.ABSTRACT);
	}

	public static final boolean isInterface(Clazz clazz) {
		if (clazz == null) {
			return false;
		}
		return Clazz.TYPE_INTERFACE.equals(clazz.getType());
	}

	public static final boolean isEnumeration(Clazz clazz) {
		if (clazz == null) {
			return false;
		}
		return Clazz.TYPE_ENUMERATION.equals(clazz.getType());
	}

	public static final boolean isUndirectional(Association assoc) {
		if (assoc == null) {
			return false;
		}
		if ((assoc.getType() == AssociationTypes.ASSOCIATION || assoc.getType() == AssociationTypes.UNDIRECTIONAL)
				&& assoc.getOtherType() == AssociationTypes.EDGE) {
			return true;
		}
		return (assoc.getOtherType() == AssociationTypes.ASSOCIATION
				|| assoc.getOtherType() == AssociationTypes.UNDIRECTIONAL) && assoc.getType() == AssociationTypes.EDGE;
	}

	public static final boolean isAssociation(Association assoc) {
		if (assoc == null || assoc.getOther() == null) {
			return false;
		}
		if (isUndirectional(assoc)) {
			return true;
		}
		return (assoc.getType() == AssociationTypes.ASSOCIATION
				&& assoc.getOther().getType() == AssociationTypes.ASSOCIATION);
	}

	public static final boolean isInterfaceAssociation(Association assoc) {
		if (assoc == null) {
			return false;
		}
		if (assoc.getType() == AssociationTypes.IMPLEMENTS && assoc.getOtherType() == AssociationTypes.EDGE) {
			return true;
		}
		return assoc.getOtherType() == AssociationTypes.IMPLEMENTS && assoc.getType() == AssociationTypes.EDGE;
	}

	public static final CharacterBuffer getMethodParameters(Method method, boolean shortName) {
		return method.getParameterString(shortName, false, true);
	}

	public static final ModifierSet getModifier(GraphMember member) {
		ModifierSet set = new ModifierSet();
		Modifier modifier = member.getModifier();
		set.add(modifier);
		for (GraphMember child : modifier.getChildren()) {
			if (child instanceof Modifier) {
				set.add(child);
			}
		}
		return set;
	}

	public static final SimpleSet<Association> getOtherAssociations(Clazz clazz) {
		SimpleSet<Association> collection = new SimpleSet<Association>();
		for (Association assoc : clazz.getAssociations()) {
			collection.add(assoc.getOther());
		}
		return collection;
	}

	public static final Modifier getVisible(GraphMember member) {
		Modifier modifier = member.getModifier();
		if(modifier.equals(Modifier.PACKAGE) || modifier.equals(Modifier.PRIVATE) || modifier.equals(Modifier.PUBLIC)|| modifier.equals(Modifier.PROTECTED)) {
			return modifier;
		}
		for (GraphMember child : modifier.getChildren()) {
			if (child instanceof Modifier) {
				if(child.equals(Modifier.PACKAGE) || child.equals(Modifier.PRIVATE) || child.equals(Modifier.PUBLIC)|| child.equals(Modifier.PROTECTED)) {
					return modifier;
				}
			}
		}
		return Modifier.PACKAGE;
	}

	public static final GraphSimpleSet getChildren(GraphMember item) {
		return item.getChildren();
	}

	public static final GraphSimpleSet getGraphDiff(GraphSimpleSet owner, GraphMember item) {
		if (item == null) {
			return owner;
		}
		if (owner == null) {
			owner = new GraphSimpleSet();
		}
		GraphSimpleSet children = item.getChildren();
		for (GraphMember member : children) {
			if (member instanceof Match) {
				owner.add(member);
				continue;
			}
			getGraphDiff(owner, member);
		}
		return owner;
	}

	public static final String getSeperator(Association item) {
		return item.getSeperator();
	}

	public static final SimpleSet<GraphEntity> getNodes(GraphMember item) {
		return item.getNodes();
	}

	public static final Match getDifference(GraphMember item) {
		return item.getDiff();
	}

	public static final void removeYou(GraphMember value) {
		if (value == null) {
			return;
		}
		value.setParentNode(null);
		if (value instanceof Attribute) {
			Attribute attribute = (Attribute) value;
			Annotation annotation = attribute.getAnnotation();
			value.remove(annotation);
		}
		if (value instanceof Association) {
			Association assoc = (Association) value;
			assoc.withOther(null);
			assoc.remove(assoc.getClazz());
		}
		if (value instanceof Clazz) {
			Clazz clazz = (Clazz) value;
			GraphSimpleSet collection = clazz.getChildren();
			GraphMember[] list = collection.toArray(new GraphMember[collection.size()]);
			for (GraphMember item : list) {
				clazz.remove(item);
			}
		}
	}

	public static final boolean containsClazzAssociation(SimpleList<GraphMember> visited, Association assoc,
			Association other) {
		boolean foundAssoc = false;
		for (GraphMember checkItem : visited) {
			if (checkItem instanceof Association == false || checkItem.getName() == null) {
				continue;
			}
			Association assocA = (Association) checkItem;
			Association assocB = assocA.getOther();
			if (assocB.getName() == null) {
				continue;
			}
			if (assocA.getName().equals(assoc.getName())) {
				if (assocB.getName().equals(other.getName())) {
					// Found Link ??
					foundAssoc = true;
					if (assocA.getClazz() == assoc.getClazz()) {
						if (assocB.getClazz() == other.getClazz()) {
							// May be n-m
							assocA.with(Association.MANY);
							assocB.with(Association.MANY);
						} else {
							assocA.with(Association.MANY);
						}
					} else {
						assocB.with(Association.MANY);
					}
					break;
				}
			}
		}
		return foundAssoc;
	}

	public static final String getShortAssoc(Association assoc) {
		if (assoc == null) {
			return "";
		}
		CharacterBuffer sb = new CharacterBuffer();
		Clazz clazz = assoc.getClazz();
		if (clazz != null) {
			sb.with(clazz.getName(true));
		}
		sb.with(':');
		sb.with(assoc.getName());
		sb.with("_");
		sb.with(assoc.getCardinality());
		sb.with(assoc.getSeperator());
		assoc = assoc.getOther();
		if (assoc != null) {
			clazz = assoc.getClazz();
			if (clazz != null) {
				sb.with(clazz.getName(true));
			}
			sb.with(':');
			sb.with(assoc.getName());
			sb.with("_");
			sb.with(assoc.getCardinality());
		}
		return sb.toString();
	}

	public static final GraphModel getGraphModel(GraphMember member) {
		if (member instanceof GraphModel) {
			return (GraphModel) member;
		}
		Object parent = member.getParent();
		if (parent instanceof GraphMember) {
			return getGraphModel((GraphMember) parent);
		} else if (parent instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) parent;
			if (list.size() > 0) {
				return getGraphModel(list.first());
			}
		}
		return null;
	}

	public static final Clazz getParentClazz(GraphMember member) {
		if (member instanceof Clazz) {
			return (Clazz) member;
		}
		Object parent = member.getParent();
		if (parent instanceof GraphMember) {
			return getParentClazz((GraphMember) parent);
		} else if (parent instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) parent;
			if (list.size() > 0) {
				return getParentClazz(list.first());
			}
		}
		return null;
	}

	public static final String createType(String value) {
		if (value == null) {
			return Clazz.TYPE_CLASS;
		}
		String trim = value.trim().toLowerCase();
		if (trim.equals(Clazz.TYPE_ENUMERATION)) {
			return Clazz.TYPE_ENUMERATION;
		}
		if (trim.equals(Clazz.TYPE_INTERFACE)) {
			return Clazz.TYPE_INTERFACE;
		}
		if (trim.equals(Clazz.TYPE_CREATOR)) {
			return Clazz.TYPE_CREATOR;
		}
		if (trim.equals(Clazz.TYPE_SET)) {
			return Clazz.TYPE_SET;
		}
		if (trim.equals(Clazz.TYPE_PATTERNOBJECT)) {
			return Clazz.TYPE_PATTERNOBJECT;
		}
		return Clazz.TYPE_CLASS;
	}
	
	public static final Clazz createClazzById(GraphModel model, String id) {
		if(model == null || id == null) {
			return null;
		}
		ClazzSet clazzes = model.getClazzes();
		for(Clazz item : clazzes) {
			if(id.equals(item.getId())) {
				return item;
			}
		}
		Clazz clazz = new Clazz();
		clazz.setId(id);
		model.add(clazz);
		return clazz;
	}

	public static final GraphEntity setExternal(GraphEntity entity, boolean value) {
		entity.withExternal(value);
		return entity;
	}

	public static final boolean isExternal(GraphEntity entity) {
		return entity.isExternal();
	}

	public static final DataType setClazz(DataType type, Clazz value) {
		type.value = value;
		return type;
	}

	public static final int createCardinality(String value) {
		if ("one".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)) {
			return Association.ONE;
		}
		if ("many".equalsIgnoreCase(value) || "n".equalsIgnoreCase(value)) {
			return Association.MANY;
		}
		return 0;
	}

	public static final String getGraphPath(GraphModel value) {
		return value.genPath;
	}

	public static final void setGraphPath(GraphModel model, String value) {
		model.genPath = value;
	}

	public static final void remove(GraphMember model, GraphMember child) {
		if (model != null) {
			model.withChildren(child);
		}
	}

	public static final String getCardinaltiy(int value) {
		if (value == Association.ONE) {
			return "ONE";
		}
		return "MANY";
	}

}
