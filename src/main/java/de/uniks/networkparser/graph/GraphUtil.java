package de.uniks.networkparser.graph;

import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.TemplateItem;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

/**
 * Special Util for package Method.
 *
 * @author Stefan Lindel
 */
public class GraphUtil {
	private static SimpleList<String> noneDictionary = new SimpleList<String>().with("aircraft", "carp", "deer",
			"salmon", "sheep", "trout");
	private static SimpleList<String> oesDictionary = new SimpleList<String>().with("hero", "potato", "torpedo");

	/**
	 * Gets the plural.
	 *
	 * @param singular the singular
	 * @return the plural
	 */
	public static String getPlural(String singular) {
		/* Zischlaut */
		if (singular == null || singular.length() < 1) {
			return null;
		}
		if (noneDictionary.contains(singular)) {
			return singular;
		}
		if (singular.endsWith("ch") || singular.endsWith("s") || singular.endsWith("sh") || singular.endsWith("x")
				|| singular.endsWith("z")) {
			return singular + "es";
		}
		/* y Konsonant */
		if (singular.endsWith("y")) {
			return singular.subSequence(0, singular.length() - 1) + "ies";
		}
		/* -f/-fe wird zu -ves im Plural */
		if (singular.endsWith("f") || singular.endsWith("fe")) {
			return singular.subSequence(0, singular.length() - 1) + "ves";
		}
		/* -o wird zu -oes im Plural */
		if (singular.endsWith("o")) {
			if (oesDictionary.contains(singular)) {
				return singular + "es";
			}
			return singular + "s";
		}
		return singular + "s";
	}

	/**
	 * Checks if is plural.
	 *
	 * @param plural the plural
	 * @return true, if is plural
	 */
	public static boolean isPlural(String plural) {
		if (noneDictionary.contains(plural)) {
			return true;
		}
		if (plural == null || plural.length() < 1) {
			return false;
		}
		if (!plural.endsWith("s")) {
			return false;
		}
		return true;
	}

	/**
	 * Compare name.
	 *
	 * @param source the source
	 * @param other the other
	 * @return the double
	 */
	public static double compareName(String source, String other) {
		if (source == null || other == null) {
			return 0;
		}
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

	/**
	 * Checks if is generate.
	 *
	 * @param member the member
	 * @return true, if is generate
	 */
	public static final boolean isGenerate(GraphMember member) {
		if (member == null) {
			return false;
		}
		return member.isGenerate;
	}

	/**
	 * Sets the generate.
	 *
	 * @param member the member
	 * @param value the value
	 * @return true, if successful
	 */
	public static final boolean setGenerate(GraphMember member, boolean value) {
		if (member != null) {
			member.isGenerate = value;
			return true;
		}
		return false;
	}

	/**
	 * Sets the role.
	 *
	 * @param member the member
	 * @param value the value
	 * @return true, if successful
	 */
	public static final boolean setRole(GraphMember member, ObjectCondition value) {
		if (member != null) {
			member.withRole(value);
			return true;
		}
		return false;
	}

	/**
	 * Gets the role.
	 *
	 * @param member the member
	 * @return the role
	 */
	public static final ObjectCondition getRole(TemplateItem member) {
		if (member != null && member instanceof GraphMember) {
			return ((GraphMember) member).getRole();
		}
		return null;
	}

	/**
	 * Compare type.
	 *
	 * @param sourceType the source type
	 * @param otherType the other type
	 * @return the double
	 */
	public static double compareType(String sourceType, String otherType) {
		if (sourceType == null || otherType == null) {
			return 1;
		}
		if (StringUtil.isNumericType(sourceType) && StringUtil.isNumericType(otherType)) {
			return 0;
		}
		if (StringUtil.isPrimitiveType(sourceType) && StringUtil.isPrimitiveType(otherType)) {
			return 0;
		}
		if (sourceType.equals(otherType)) {
			return 0;
		}
		return -1;
	}

	/**
	 * Gets the package.
	 *
	 * @param classObj the class obj
	 * @return the package
	 */
	public static final String getPackage(Class<?> classObj) {
		if (classObj != null) {
			return getPackage(classObj.getName());
		}
		return "";
	}

	/**
	 * Gets the package.
	 *
	 * @param name the name
	 * @return the package
	 */
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

	/**
	 * Gets the by object.
	 *
	 * @param item the item
	 * @param clazz the clazz
	 * @param fullName the full name
	 * @return the by object
	 */
	public static final Clazz getByObject(GraphEntity item, String clazz, boolean fullName) {
		if (clazz == null || item == null) {
			return null;
		}
		return (Clazz) item.getByObject(clazz, fullName);
	}

	/**
	 * Sets the gen path.
	 *
	 * @param model the model
	 * @param path the path
	 * @return the graph model
	 */
	public static final GraphModel setGenPath(GraphModel model, String path) {
		if (model != null) {
			model.genPath = path;
		}
		return model;
	}

	/**
	 * Gets the gen path.
	 *
	 * @param model the model
	 * @return the gen path
	 */
	public static final String getGenPath(GraphModel model) {
		if (model != null) {
			return model.genPath;
		}
		return "";
	}

	/**
	 * Gets the annotations.
	 *
	 * @param item the item
	 * @return the annotations
	 */
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

	/**
	 * Sets the association.
	 *
	 * @param entry the entry
	 * @param assoc the assoc
	 * @return true, if successful
	 */
	public static final boolean setAssociation(GraphEntity entry, Association assoc) {
		if (entry != null) {
			entry.with(assoc);
			return true;
		}
		return false;
	}

	/**
	 * Sets the literal.
	 *
	 * @param clazz the clazz
	 * @param literals the literals
	 * @return true, if successful
	 */
	public static final boolean setLiteral(Clazz clazz, Literal... literals) {
		if (clazz != null) {
			clazz.with(literals);
			return true;
		}
		return false;
	}

	/**
	 * Sets the modifier entry.
	 *
	 * @param clazz the clazz
	 * @param modifier the modifier
	 * @return true, if successful
	 */
	public static final boolean setModifierEntry(Clazz clazz, ModifyEntry modifier) {
		if (clazz != null) {
			clazz.with(modifier);
			return true;
		}
		return false;
	}

	/**
	 * Sets the clazz type.
	 *
	 * @param clazz the clazz
	 * @param clazzType the clazz type
	 * @return true, if successful
	 */
	public static final boolean setClazzType(Clazz clazz, String clazzType) {
		if (clazz != null) {
			clazz.withType(clazzType);
			return true;
		}
		return false;
	}

	/**
	 * Sets the import.
	 *
	 * @param clazz the clazz
	 * @param importClazzes the import clazzes
	 * @return true, if successful
	 */
	public static final boolean setImport(Clazz clazz, Import... importClazzes) {
		if (clazz != null) {
			clazz.with(importClazzes);
			return true;
		}
		return false;
	}

	/**
	 * Sets the id.
	 *
	 * @param graphEntity the graph entity
	 * @param id the id
	 * @return true, if successful
	 */
	public static boolean setId(GraphEntity graphEntity, String id) {
		if (graphEntity != null) {
			return graphEntity.setId(id);
		}
		return false;
	}

	/**
	 * Checks if is with no objects.
	 *
	 * @param clazz the clazz
	 * @return true, if is with no objects
	 */
	public static final boolean isWithNoObjects(Clazz clazz) {
		if (clazz == null) {
			return false;
		}
		return (clazz.getModifier().has(Modifier.ABSTRACT) || Clazz.TYPE_INTERFACE.equals(clazz.getType()));
	}

	/**
	 * Checks if is abstract.
	 *
	 * @param clazz the clazz
	 * @return true, if is abstract
	 */
	public static boolean isAbstract(Clazz clazz) {
		if (clazz == null) {
			return false;
		}
		return clazz.getModifier().has(Modifier.ABSTRACT);
	}

	/**
	 * Checks if is interface.
	 *
	 * @param clazz the clazz
	 * @return true, if is interface
	 */
	public static final boolean isInterface(Clazz clazz) {
		if (clazz == null) {
			return false;
		}
		return Clazz.TYPE_INTERFACE.equals(clazz.getType());
	}

	/**
	 * Checks if is enumeration.
	 *
	 * @param clazz the clazz
	 * @return true, if is enumeration
	 */
	public static final boolean isEnumeration(Clazz clazz) {
		if (clazz == null) {
			return false;
		}
		return Clazz.TYPE_ENUMERATION.equals(clazz.getType());
	}

	/**
	 * Checks if is undirectional.
	 *
	 * @param assoc the assoc
	 * @return true, if is undirectional
	 */
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

	/**
	 * Checks if is association.
	 *
	 * @param assoc the assoc
	 * @return true, if is association
	 */
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

	/**
	 * Checks if is interface association.
	 *
	 * @param assoc the assoc
	 * @return true, if is interface association
	 */
	public static final boolean isInterfaceAssociation(Association assoc) {
		if (assoc == null) {
			return false;
		}
		if (assoc.getType() == AssociationTypes.IMPLEMENTS && assoc.getOtherType() == AssociationTypes.EDGE) {
			return true;
		}
		return assoc.getOtherType() == AssociationTypes.IMPLEMENTS && assoc.getType() == AssociationTypes.EDGE;
	}

	/**
	 * Gets the method parameters.
	 *
	 * @param method the method
	 * @param shortName the short name
	 * @return the method parameters
	 */
	public static final CharacterBuffer getMethodParameters(Method method, boolean shortName) {
		if (method == null) {
			return null;
		}
		return method.getParameterString(shortName, false, true);
	}

	/**
	 * Gets the modifier.
	 *
	 * @param member the member
	 * @return the modifier
	 */
	public static final ModifierSet getModifier(GraphMember member) {
		ModifierSet set = new ModifierSet();
		if (member == null) {
			return set;
		}
		Modifier modifier = member.getModifier();
		set.add(modifier);
		for (GraphMember child : modifier.getChildren()) {
			if (child instanceof Modifier) {
				set.add(child);
			}
		}
		return set;
	}

	/**
	 * Gets the other associations.
	 *
	 * @param clazz the clazz
	 * @return the other associations
	 */
	public static final SimpleSet<Association> getOtherAssociations(Clazz clazz) {
		SimpleSet<Association> collection = new SimpleSet<Association>();
		if (clazz == null) {
			return collection;
		}
		for (Association assoc : clazz.getAssociations()) {
			collection.add(assoc.getOther());
		}
		return collection;
	}

	/**
	 * Gets the visible.
	 *
	 * @param member the member
	 * @return the visible
	 */
	public static final Modifier getVisible(GraphMember member) {
		if (member == null) {
			return Modifier.PACKAGE;
		}
		Modifier modifier = member.getModifier();
		if (modifier.equals(Modifier.PACKAGE) || modifier.equals(Modifier.PRIVATE) || modifier.equals(Modifier.PUBLIC)
				|| modifier.equals(Modifier.PROTECTED)) {
			return modifier;
		}
		for (GraphMember child : modifier.getChildren()) {
			if (child instanceof Modifier) {
				if (child.equals(Modifier.PACKAGE) || child.equals(Modifier.PRIVATE) || child.equals(Modifier.PUBLIC)
						|| child.equals(Modifier.PROTECTED)) {
					return modifier;
				}
			}
		}
		return Modifier.PACKAGE;
	}

	/**
	 * Gets the children.
	 *
	 * @param item the item
	 * @return the children
	 */
	public static final GraphSimpleSet getChildren(TemplateItem item) {
		if (item instanceof GraphMember) {
			return ((GraphMember) item).getChildren();
		}
		return null;
	}

	/**
	 * Gets the graph diff.
	 *
	 * @param owner the owner
	 * @param item the item
	 * @return the graph diff
	 */
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

	/**
	 * Gets the seperator.
	 *
	 * @param item the item
	 * @return the seperator
	 */
	public static final String getSeperator(Association item) {
		if (item != null) {
			return item.getSeperator();
		}
		return "";
	}

	/**
	 * Gets the nodes.
	 *
	 * @param item the item
	 * @return the nodes
	 */
	public static final SimpleSet<GraphEntity> getNodes(GraphMember item) {
		if (item != null) {
			return item.getNodes();
		}
		return null;
	}

	/**
	 * Gets the difference.
	 *
	 * @param item the item
	 * @return the difference
	 */
	public static final Match getDifference(GraphMember item) {
		if (item != null) {
			return item.getDiff();
		}
		return null;
	}

	/**
	 * Removes the you.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public static final boolean removeYou(GraphMember value) {
		if (value == null) {
			return true;
		}
		value.setParentNode(null);
		if (value instanceof Attribute) {
			Attribute attribute = (Attribute) value;
			Annotation annotation = attribute.getAnnotation();
			return value.remove(annotation);
		}
		if (value instanceof Association) {
			Association assoc = (Association) value;
			assoc.withOther(null);
			return assoc.remove(assoc.getClazz());
		}
		if (value instanceof Clazz) {
			Clazz clazz = (Clazz) value;
			GraphSimpleSet collection = clazz.getChildren();
			GraphMember[] list = collection.toArray(new GraphMember[collection.size()]);
			for (GraphMember item : list) {
				clazz.remove(item);
			}
			return true;
		}
		return false;
	}

	/**
	 * Contains clazz association.
	 *
	 * @param visited the visited
	 * @param assoc the assoc
	 * @param other the other
	 * @return true, if successful
	 */
	public static final boolean containsClazzAssociation(SimpleList<GraphMember> visited, Association assoc,
			Association other) {
		boolean foundAssoc = false;
		for (GraphMember checkItem : visited) {
			if (!(checkItem instanceof Association) || checkItem.getName() == null) {
				continue;
			}
			Association assocA = (Association) checkItem;
			Association assocB = assocA.getOther();
			if (assocB.getName() == null) {
				continue;
			}
			if (assocA.getName().equals(assoc.getName())) {
				if (assocB.getName().equals(other.getName())) {
					/* Found Link ?? */
					foundAssoc = true;
					if (assocA.getClazz() == assoc.getClazz()) {
						if (assocB.getClazz() == other.getClazz()) {
							/* May be n-m */
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

	/**
	 * Gets the short assoc.
	 *
	 * @param assoc the assoc
	 * @return the short assoc
	 */
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

	/**
	 * Gets the graph model.
	 *
	 * @param member the member
	 * @return the graph model
	 */
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

	/**
	 * Gets the parent clazz.
	 *
	 * @param member the member
	 * @return the parent clazz
	 */
	public static final Clazz getParentClazz(GraphMember member) {
		if (member == null) {
			return null;
		}
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

	/**
	 * Creates the type.
	 *
	 * @param value the value
	 * @return the string
	 */
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

	/**
	 * Creates the clazz by id.
	 *
	 * @param model the model
	 * @param id the id
	 * @return the clazz
	 */
	public static final Clazz createClazzById(GraphModel model, String id) {
		if (model == null || id == null) {
			return null;
		}
		ClazzSet clazzes = model.getClazzes();
		for (Clazz item : clazzes) {
			if (id.equals(item.getId())) {
				return item;
			}
		}
		Clazz clazz = new Clazz();
		clazz.setId(id);
		model.add(clazz);
		return clazz;
	}

	/**
	 * Sets the external.
	 *
	 * @param entity the entity
	 * @param value the value
	 * @return the graph entity
	 */
	public static final GraphEntity setExternal(GraphEntity entity, boolean value) {
		if (entity == null) {
			return null;
		}
		entity.withExternal(value);
		return entity;
	}

	/**
	 * Checks if is external.
	 *
	 * @param entity the entity
	 * @return true, if is external
	 */
	public static final boolean isExternal(GraphEntity entity) {
		if (entity == null) {
			return false;
		}
		return entity.isExternal();
	}

	/**
	 * Sets the clazz.
	 *
	 * @param type the type
	 * @param value the value
	 * @return the data type
	 */
	public static final DataType setClazz(DataType type, Clazz value) {
		type.value = value;
		return type;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param entity the entity
	 * @param name the name
	 * @return the string
	 */
	public static final String setName(GraphMember entity, String name) {
		if(entity.setName(name)) {
			return name;
		}
		return null;
	}
	
	/**
	 * Creates the feature.
	 *
	 * @param key the key
	 * @return the feature
	 */
	public static final Feature createFeature(String key) {
	  Feature item= new Feature((String) key);
	  return item;
	}

	/**
	 * Creates the cardinality.
	 *
	 * @param value the value
	 * @return the int
	 */
	public static final int createCardinality(String value) {
		if ("one".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)) {
			return Association.ONE;
		}
		if ("many".equalsIgnoreCase(value) || "n".equalsIgnoreCase(value)) {
			return Association.MANY;
		}
		return 0;
	}

	/**
	 * Gets the graph path.
	 *
	 * @param value the value
	 * @return the graph path
	 */
	public static final String getGraphPath(GraphModel value) {
		if (value != null) {
			return value.genPath;
		}
		return null;
	}

	/**
	 * Sets the graph path.
	 *
	 * @param model the model
	 * @param value the value
	 * @return true, if successful
	 */
	public static final boolean setGraphPath(GraphModel model, String value) {
		if (model != null) {
			model.genPath = value;
			return true;
		}
		return false;
	}

	/**
	 * Removes the.
	 *
	 * @param model the model
	 * @param child the child
	 */
	public static final void remove(GraphMember model, GraphMember child) {
		if (model != null) {
			model.withChildren(child);
		}
	}

	/**
	 * Gets the cardinaltiy.
	 *
	 * @param value the value
	 * @return the cardinaltiy
	 */
	public static final String getCardinaltiy(int value) {
		if (value == Association.ONE) {
			return "ONE";
		}
		return "MANY";
	}
	
	/**
	 * Gets the value.
	 *
	 * @param type the type
	 * @param value the value
	 * @return the value
	 */
	public static Object getValue(DataType type, String value) {
		if(value == null || type == null) {
			return value;
		}
		if(DataType.INT.equals(type)) {
			return Integer.valueOf(value);
		}else if(DataType.DOUBLE.equals(type)) {
			return Double.valueOf(value);
		}else if(DataType.BOOLEAN.equals(type)) {
			return Boolean.valueOf(value);
		}
		return value;
	}

	/**
	 * Sets the children.
	 *
	 * @param graphMember the graph member
	 * @param children the children
	 * @return true, if successful
	 */
	public static boolean setChildren(GraphMember graphMember, Object children) {
		if (graphMember != null ) {
			if(children instanceof GraphSimpleSet) {
				GraphSimpleSet set = (GraphSimpleSet) children;
				if(graphMember.children != set) {
					graphMember.children = set;
					return true;
				}
			}else if(children instanceof GraphMember) {
				graphMember.withChildren((GraphMember)children);
				return true;
			}
		}
		return false;
	}
}
