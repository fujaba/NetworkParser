package de.uniks.networkparser.graph;

import de.uniks.networkparser.StringUtil;
/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class Clazz.
 *
 * @author Stefan
 */
public class Clazz extends GraphEntity {
	
	/** The Constant TYPE_CLASS. */
	public static final String TYPE_CLASS = "class";
	
	/** The Constant TYPE_ENUMERATION. */
	public static final String TYPE_ENUMERATION = "enum";
	
	/** The Constant TYPE_INTERFACE. */
	public static final String TYPE_INTERFACE = "interface";
	
	/** The Constant TYPE_CREATOR. */
	public static final String TYPE_CREATOR = "creator";
	
	/** The Constant TYPE_SET. */
	public static final String TYPE_SET = "set";
	
	/** The Constant TYPE_PATTERNOBJECT. */
	public static final String TYPE_PATTERNOBJECT = "pattern";

	/** The Constant PROPERTY_FULLNAME. */
	public static final String PROPERTY_FULLNAME = "fullName";
	
	/** The Constant PROPERTY_VISIBILITY. */
	public static final String PROPERTY_VISIBILITY = "visibility";
	
	/** The Constant PROPERTY_MODIFIERS. */
	public static final String PROPERTY_MODIFIERS = "modifiers";
	
	/** The Constant PROPERTY_TYPE. */
	public static final String PROPERTY_TYPE = "type";
	
	/** The Constant PROPERTY_SUPERCLAZZ. */
	public static final String PROPERTY_SUPERCLAZZ = "superclazz";
	
	/** The Constant PROPERTY_IMPLEMENTS. */
	public static final String PROPERTY_IMPLEMENTS = "implements";
	
	/** The Constant PROPERTY_ATTRIBUTE. */
	public static final String PROPERTY_ATTRIBUTE = "attribute";
	
	/** The Constant PROPERTY_ASSOCIATION. */
	public static final String PROPERTY_ASSOCIATION = "association";
	
	/** The Constant PROPERTY_METHOD. */
	public static final String PROPERTY_METHOD = "method";
	
	/** The Constant ONE. */
	public static final int ONE = 1;
	
	/** The Constant MANY. */
	public static final int MANY = 42;

	private String type = TYPE_CLASS;

	Clazz() {

	}

	/**
	 * Constructor with Name of Clazz.
	 *
	 * @param name Name of Clazz
	 */
	public Clazz(String name) {
		this.with(name);
	}

	/**
	 * Instantiates a new clazz.
	 *
	 * @param name the name
	 */
	public Clazz(Class<?> name) {
		if (name != null) {
			with(name.getName().replace("$", "."));
		}
	}

	/**
	 * With.
	 *
	 * @param name the name
	 * @return the clazz
	 */
	@Override
	public Clazz with(String name) {
		super.with(name);
		return this;
	}

	protected Clazz withType(String clazzType) {
		this.type = clazzType;
		return this;
	}

	/**
	 * Enable interface.
	 *
	 * @return the clazz
	 */
	public Clazz enableInterface() {
		this.withType(TYPE_INTERFACE);
		return this;
	}

	/**
	 * Enable enumeration.
	 *
	 * @param literals the literals
	 * @return the clazz
	 */
	public Clazz enableEnumeration(Object... literals) {
		this.withType(TYPE_ENUMERATION);
		if (literals == null) {
			return this;
		}
		for (Object item : literals) {
			if (item == null) {
				continue;
			}
			if (item instanceof Literal) {
				this.with((Literal) item);
			} else {
				this.with(new Literal(item.toString()));
			}
		}
		return this;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	@Override
	protected String getFullId() {
		if (this.getId() != null) {
			return this.getId();
		}
		return super.getFullId();
	}

	/**
	 * With external.
	 *
	 * @param value the value
	 * @return the clazz
	 */
	@Override
	public Clazz withExternal(boolean value) {
		super.withExternal(value);
		return this;
	}

	/**
	 * Gets the modifier.
	 *
	 * @return the modifier
	 */
	@Override
	public Modifier getModifier() {
		Modifier modifier = super.getModifier();
		if (modifier == null) {
			modifier = new Modifier(Modifier.PUBLIC);
			super.withChildren(modifier);
		}
		return modifier;
	}

	/**
	 * With.
	 *
	 * @param values the values
	 * @return the clazz
	 */
	public Clazz with(Modifier... values) {
		super.withModifier(values);
		return this;
	}

	protected Clazz with(Attribute... values) {
		super.withChildren(values);
		return this;
	}

	protected Clazz with(Method... values) {
		super.withChildren(values);
		return this;
	}

	/**
	 * With.
	 *
	 * @param value the value
	 * @return the clazz
	 */
	public Clazz with(Annotation value) {
		super.with(value);
		return this;
	}

	protected Clazz with(GraphCustomItem... values) {
		super.withChildren(values);
		return this;
	}

	protected Clazz with(Literal... values) {
		super.withChildren(values);
		return this;
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public SimpleSet<Literal> getValues() {
		SimpleSet<Literal> collection = new SimpleSet<Literal>();
		if (this.children == null) {
			return collection;
		}
		if (this.children instanceof Literal) {
			collection.add((Literal) this.children);
			return collection;
		}
		if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for (GraphMember item : list) {
				if (item instanceof Literal) {
					collection.add((Literal) item);
				}
			}
		}
		return collection;
	}

	/**
	 * ********************************************************************
	 * 
	 * <pre>
	 * 		%srcCardinality%		%tgtCardinality%
	 * Clazz -------------------------------------- %tgtClass%
	 * 		%srcRoleName%			%tgtRoleName%
	 * </pre>
	 * 
	 * create a Bidirectional Association.
	 *
	 * @param tgtClass       The target Clazz
	 * @param tgtRoleName    The Targetrolename
	 * @param tgtCardinality The Targetcardinality
	 * @param srcRoleName    The sourcerolename
	 * @param srcCardinality The sourcecardinality
	 * @return The Clazz Instance
	 */
	public Clazz withBidirectional(Clazz tgtClass, String tgtRoleName, int tgtCardinality, String srcRoleName,
			int srcCardinality) {
		/* Target */
		Association assocTarget = new Association(tgtClass).with(tgtCardinality).with(tgtRoleName);

		/* Source */
		Association assocSource = new Association(this).with(srcCardinality).with(srcRoleName);
		assocSource.with(assocTarget);

		if (tgtClass != null) {
			tgtClass.with(assocTarget);
		}
		this.with(assocSource);
		return this;
	}

	/**
	 * <pre>
	 *    %srcCardinality%     %tgtCardinality%
	 * Clazz -------------------------------------- %tgtClass%
	 *    %srcRoleName%        %tgtRoleName%
	 * </pre>
	 * 
	 * create a Bidirectional Association.
	 *
	 * @param tgtClass       The target Clazz
	 * @param tgtRoleName    The Targetrolename
	 * @param tgtCardinality The Targetcardinality
	 * @param srcRoleName    The sourcerolename
	 * @param srcCardinality The sourcecardinality
	 * @return The Association Instance
	 */
	public Association createBidirectional(Clazz tgtClass, String tgtRoleName, int tgtCardinality, String srcRoleName,
			int srcCardinality) {
		/* Target */
		if (tgtCardinality < 1 || srcCardinality < 1 || tgtClass == null) {
			return null;
		}
		Association assocTarget = new Association(tgtClass).with(tgtCardinality).with(tgtRoleName);

		/* Source */
		Association assocSource = new Association(this).with(srcCardinality).with(srcRoleName);
		assocSource.with(assocTarget);

		tgtClass.with(assocTarget);
		this.with(assocSource);
		return assocSource;
	}

	/**
	 * With assoc.
	 *
	 * @param tgtClass the tgt class
	 * @param tgtRoleName the tgt role name
	 * @param tgtCardinality the tgt cardinality
	 * @param srcRoleName the src role name
	 * @param srcCardinality the src cardinality
	 * @return the clazz
	 */
	public Clazz withAssoc(Clazz tgtClass, String tgtRoleName, int tgtCardinality, String srcRoleName,
			int srcCardinality) {
		createBidirectional(tgtClass, tgtRoleName, tgtCardinality, srcRoleName, srcCardinality);
		return this;
	}

	/**
	 * With assoc.
	 *
	 * @param tgtClass    The Target Class
	 * @param cardinality The Cardinality default is [1,1], May Be
	 *                    [[1,1],[1,42],[42,1],[42,42],[1,0], [42,0]]
	 * @return ThisComponent
	 */
	public Clazz withAssoc(Clazz tgtClass, int... cardinality) {
		if (tgtClass == null || tgtClass.getName() == null) {
			return this;
		}
		int tgtCardinality = 1;
		int srcCardinality = 1;
		if (cardinality != null && cardinality.length > 0) {
			if (cardinality[0] > 0) {
				tgtCardinality = cardinality[0];
			}
			if (cardinality.length > 1 && cardinality[1] >= 0) {
				srcCardinality = cardinality[1];
			}
		}
		String srcRoleName = null, tgtRoleName = tgtClass.getName();

		/* Now Check dupplicate Naming */
		for (Association assoc : getAssociations()) {
			if (tgtRoleName.equals(assoc.getName())) {
				return this;
			}
		}
		if (srcCardinality > 0) {
			srcRoleName = this.getName();
			for (Association assoc : tgtClass.getAssociations()) {
				if (srcRoleName.equals(assoc.getName())) {
					return this;
				}
			}
		}
		/* Set MANY Name */
		if (tgtCardinality > 1) {
			tgtRoleName = GraphUtil.getPlural(tgtRoleName);
		}
		if (srcCardinality > 1) {
			srcRoleName = GraphUtil.getPlural(srcRoleName);
		}

		/* So SourceRoleName and TargetRoleName is Set now create Asssoc */
		Association assocTarget = new Association(tgtClass).with(tgtCardinality).with(tgtRoleName);
		/* Source */
		Association assocSource = new Association(this).with(assocTarget);

		if (srcCardinality > 0) {
			assocSource.with(srcCardinality).with(srcRoleName);
		} else {
			assocTarget.with(AssociationTypes.UNDIRECTIONAL);
			assocSource.with(AssociationTypes.EDGE);
		}
		tgtClass.with(assocTarget);
		this.with(assocSource);
		return this;
	}

	/**
	 * ********************************************************************
	 * 
	 * <pre>
	 * 								 %tgtCardinality%
	 * Clazz ----------------------------------- %tgtClass%
	 * 									%tgtRoleName%
	 * </pre>
	 * 
	 * create a Undirectional Association.
	 *
	 * @param tgtClass       The target Clazz
	 * @param tgtRoleName    The Targetrolename
	 * @param tgtCardinality The Targetcardinality
	 * @return The Clazz Instance
	 */
	public Clazz withUniDirectional(Clazz tgtClass, String tgtRoleName, int tgtCardinality) {
		/* Target */
		Association assocTarget = new Association(tgtClass).with(tgtCardinality).with(AssociationTypes.UNDIRECTIONAL)
				.with(tgtRoleName);

		/* Source */
		Association assocSource = new Association(this).with(AssociationTypes.EDGE).with(assocTarget);

		tgtClass.with(assocTarget);
		this.with(assocSource);
		return this;
	}

	/**
	 * ********************************************************************
	 * 
	 * <pre>
	 *                       %tgtCardinality%
	 * Clazz ----------------------------------- %tgtClass%
	 *                         %tgtRoleName%
	 * </pre>
	 * 
	 * create a Undirectional Association.
	 *
	 * @param tgtClass       The target Clazz
	 * @param tgtRoleName    The Targetrolename
	 * @param tgtCardinality The Targetcardinality
	 * @return The Association Instance
	 */
	public Association createUniDirectional(Clazz tgtClass, String tgtRoleName, int tgtCardinality) {
		/* Target */
		if (tgtCardinality < 1) {
			return null;
		}
		Association assocTarget = new Association(tgtClass).with(tgtCardinality).with(AssociationTypes.UNDIRECTIONAL)
				.with(tgtRoleName);

		/* Source */
		Association assocSource = new Association(this).with(AssociationTypes.EDGE).with(assocTarget);
		if (tgtClass != null) {
			tgtClass.with(assocTarget);
		}
		this.with(assocSource);
		return assocSource;
	}

	/**
	 * Get All Interfaces.
	 *
	 * @param transitive Get all Interfaces or direct Interfaces
	 * @return all Interfaces of a Clazz
	 * 
	 *         <pre>
	 * 			one						many
	 * Clazz ----------------------------------- Clazz
	 * 			clazz					Interfaces
	 *         </pre>
	 */
	public ClazzSet getInterfaces(boolean transitive) {
		repairAssociations();
		AssociationTypes type = AssociationTypes.IMPLEMENTS;
		if (TYPE_INTERFACE.equals(this.getType())) {
			type = AssociationTypes.GENERALISATION;
		}

		ClazzSet collection = getEdgeClazzes(type, null);
		if (!transitive) {
			return collection;
		}
		int size = collection.size();
		for (int i = 0; i < size; i++) {
			collection.withList(collection.get(i).getInterfaces(transitive));
		}
		return collection;
	}

	/**
	 * Get All SuperClazzes.
	 *
	 * @param transitive Get all SuperClasses or direct SuperClasses
	 * @return all SuperClasses of a Clazz
	 * 
	 *         <pre>
	 * 			  one					   many
	 * Clazz ----------------------------------- Clazz
	 * 			  clazz				   superClazzes
	 *         </pre>
	 */
	public ClazzSet getSuperClazzes(boolean transitive) {
		repairAssociations();
		ClazzSet collection = getEdgeClazzes(AssociationTypes.GENERALISATION, null);
		if (!transitive) {
			return collection;
		}
		int size = collection.size();
		for (int i = 0; i < size; i++) {
			collection.withList(collection.get(i).getSuperClazzes(transitive));
		}
		return collection;
	}

	protected boolean repairAssociation(Association assoc, boolean renameName) {
		if (assoc == null || assoc.getOther() == null) {
			return false;
		}
		if (!AssociationTypes.IMPLEMENTS.equals(assoc.getType())
				&& !AssociationTypes.GENERALISATION.equals(assoc.getType())) {
			/* Wrong way try another round */
			assoc = assoc.getOther();
		}
		Association otherAssoc = assoc.getOther();
		if (!AssociationTypes.IMPLEMENTS.equals(assoc.getType())
				&& !AssociationTypes.GENERALISATION.equals(assoc.getType())) {
			/* Check Cardinality */
			if (assoc.getClazz() == otherAssoc.getClazz() && assoc.getName() != null
					&& assoc.getName().equals(otherAssoc.getName())) {
				if (assoc.getCardinality() != otherAssoc.getCardinality()) {
					/* Self assoc with same RoleName but other Cardinality */
					return false;
				}
			}
			/* Check Name */
			if (renameName) {
				String name2 = assoc.getName();
				if (name2 != null && name2.length() > 0) {
					if (name2.equals(name2.toUpperCase())) {
						assoc.setName(name2.toLowerCase());
					} else {
						char no = name2.charAt(0);
						if (no < 'a' || no > 'z') {
							assoc.setName(StringUtil.downFirstChar(name2));
						}
					}
				}
			}
			/* Check for duplicate */
			AssociationSet associations = otherAssoc.getClazz().getAssociations();
			for (Association checkAssoc : associations) {
				if (checkAssoc == otherAssoc || checkAssoc.getType() == AssociationTypes.GENERALISATION
						|| checkAssoc.getOtherType() == AssociationTypes.GENERALISATION) {
					continue;
				}
				if (checkAssoc.getName() != null && checkAssoc.getName().equalsIgnoreCase(otherAssoc.getName())
						&& checkAssoc.getOther().getName() == null) {
					/* Create UnDirectional Association */
					checkAssoc.getOther().with(AssociationTypes.EDGE);
					checkAssoc.with(AssociationTypes.UNDIRECTIONAL);
					assoc.with(AssociationTypes.EDGE);
					otherAssoc.with(AssociationTypes.UNDIRECTIONAL);
					break;
				}
			}
			/* Ignore */
			return true;
		}
		/* REPAIR CLAZZES */
		GraphSimpleSet items = otherAssoc.getParents();
		ClazzSet interfaces = new ClazzSet();
		ClazzSet generalizations = new ClazzSet();
		for (GraphMember child : items) {
			if (child != null && child instanceof Clazz) {
				Clazz clazzChild = (Clazz) child;
				if (TYPE_INTERFACE.equals(clazzChild.getType())) {
					interfaces.add(child);
				} else {
					generalizations.add(child);
				}
			}
		}

		/* CHECK FOR WRONG TYPE */
		if (AssociationTypes.GENERALISATION.equals(assoc.getType())) {
			if (generalizations.size() < 1) {
				assoc.with(AssociationTypes.IMPLEMENTS);
			} else if (interfaces.size() > 0) {
				/* BOTH */
				for (Clazz item : interfaces) {
					item.remove(otherAssoc);
				}
				createAssociation(AssociationTypes.IMPLEMENTS, AssociationTypes.EDGE, interfaces.toArray());
			}
			return true;
		}
		if (interfaces.size() < 1) {
			assoc.with(AssociationTypes.GENERALISATION);
		} else if (generalizations.size() > 0) {
			/* BOTH */
			for (Clazz item : interfaces) {
				item.remove(otherAssoc);
			}
			createAssociation(AssociationTypes.GENERALISATION, AssociationTypes.EDGE, generalizations.toArray());
		}
		return true;
	}

	private void repairAssociations() {
		if (this.children == null) {
			return;
		}
		if (this.children instanceof Association) {
			/* Is is easy only one Assoc */
			repairAssociation((Association) this.children, true);
		} else if (children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			int size = list.size();
			AssociationSet generalizations = new AssociationSet();
			for (int i = 0; i < size; i++) {
				GraphMember item = list.get(i);
				if (item instanceof Association) {
					Association assoc = (Association) item;
					repairAssociation(assoc, true);
					if (AssociationTypes.GENERALISATION.equals(assoc.getType())) {
						generalizations.add(assoc);
					}
				}
			}

			if (generalizations.size() > 1) {
				/* Repair only valid last generalization */
				for (int i = 0; i < generalizations.size() - 1; i++) {
					this.remove(generalizations.get(i));
				}
			}
		}
	}

	/**
	 * With super clazz.
	 *
	 * @param values the values
	 * @return the clazz
	 */
	public Clazz withSuperClazz(Clazz... values) {
		AssociationTypes type = AssociationTypes.GENERALISATION;

		if (values != null) {
			if (values.length == 1) {
				Clazz item = values[0];
				if (item != null && TYPE_INTERFACE.equals(item.getType())) {
					type = AssociationTypes.IMPLEMENTS;
				}
			} else {
				/* COMPLEX */
				ClazzSet interfaces = new ClazzSet();
				ClazzSet generalizations = new ClazzSet();
				for (Clazz item : values) {
					if (item != null) {
						if (TYPE_INTERFACE.equals(item.getType())) {
							interfaces.add(item);
						}
						if (TYPE_CLASS.equals(item.getType())) {
							generalizations.add(item);
						}
					}
				}
				if (generalizations.size() > 0) {
					createAssociation(AssociationTypes.GENERALISATION, AssociationTypes.EDGE,
							generalizations.toArray());
				}
				if (interfaces.size() > 0) {
					createAssociation(AssociationTypes.IMPLEMENTS, AssociationTypes.EDGE, interfaces.toArray());
				}
				return this;
			}
		}
		createAssociation(type, AssociationTypes.EDGE, values);
		return this;
	}

	/**
	 * get All KidClazzes.
	 *
	 * @param transitive Get all KidClasses or direct KidClasses
	 * @return all KidClasses of a Clazz
	 * 
	 *         <pre>
	 * 			  one					   many
	 * Clazz ----------------------------------- Clazz
	 * 			  superClass		   kidClazzes
	 *         </pre>
	 */
	public ClazzSet getKidClazzes(boolean transitive) {
		ClazzSet kidClazzes = getEdgeClazzes(AssociationTypes.EDGE, AssociationTypes.GENERALISATION);
		if (!transitive) {
			return kidClazzes;
		}
		int size = kidClazzes.size();
		for (int i = 0; i < size; i++) {
			kidClazzes.withList(kidClazzes.get(i).getKidClazzes(transitive));
		}
		return kidClazzes;
	}

	/**
	 * get All Implements Clazz.
	 *
	 * @return all implements of a Clazz
	 * 
	 *         <pre>
	 * 			  one					   many
	 * Clazz ----------------------------------- Clazz
	 * 			  superClass		   kidClazzes
	 *         </pre>
	 */
	public ClazzSet getImplements() {
		ClazzSet kidClazzes = getEdgeClazzes(AssociationTypes.IMPLEMENTS, AssociationTypes.EDGE);
		return kidClazzes;
	}

	protected ClazzSet getEdgeClazzes(AssociationTypes typ, AssociationTypes otherTyp) {
		ClazzSet kidClazzes = new ClazzSet();
		if (this.children == null || typ == null) {
			return kidClazzes;
		}
		for (Association assoc : super.getEdges(AssociationTypes.EDGE)) {
			if (typ != assoc.getType()) {
				continue;
			}
			if (otherTyp == null || assoc.getOtherType() == otherTyp) {
				GraphSimpleSet parents = assoc.getOther().getParents();
				kidClazzes.withList(parents);
			}
		}
		return kidClazzes;
	}

	protected boolean createAssociation(AssociationTypes direction, AssociationTypes backDirection, Clazz... values) {
		if (values == null) {
			return false;
		}
		AssociationSet associations = getAssociations();
		for (Clazz item : values) {
			if (item != null) {
				if (!AssociationTypes.GENERALISATION.equals(backDirection)
						&& !AssociationTypes.GENERALISATION.equals(direction)) {
					for (Association assoc : associations) {
						if ((assoc.getType() == direction && assoc.getOtherType() == backDirection) &&
							(!assoc.contains(item, true, false))) {
								assoc.getOther().setParentNode(item);
								return true;
						}
					}
				}
				Association childAssoc = new Association(this).with(direction);
				Association superAssoc = new Association(item).with(backDirection);
				childAssoc.with(superAssoc);
				this.with(childAssoc);
				item.with(superAssoc);
			}
		}
		return true;
	}

	/**
	 * With kid clazzes.
	 *
	 * @param values the values
	 * @return the clazz
	 */
	public Clazz withKidClazzes(Clazz... values) {
		createAssociation(AssociationTypes.EDGE, AssociationTypes.GENERALISATION, values);
		return this;
	}

	/**
	 * Gets the class model.
	 *
	 * @return the class model
	 */
	public GraphModel getClassModel() {
		return (GraphModel) this.parentNode;
	}

	/**
	 * Sets the class model.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setClassModel(GraphModel value) {
		return super.setParentNode(value);
	}

	/**
	 * get All Attributes.
	 *
	 * @param filters Can Filter the List of Attributes
	 * @return all Attributes of a Clazz
	 * 
	 *         <pre>
	 * Clazz  --------------------- Attributes
	 * one                          many
	 *         </pre>
	 */
	public AttributeSet getAttributes(Condition<?>... filters) {
		AttributeSet collection = new AttributeSet();
		if (this.children == null) {
			return collection;
		}

		ClazzSet superClasses = new ClazzSet();
		if (this.children instanceof Attribute) {
			if (check((Attribute) this.children, filters)) {
				collection.add((Attribute) this.children);
			}
			return collection;
		} else if (this.children instanceof Association) {
			Association assoc = (Association) this.children;
			if (assoc.getType() == AssociationTypes.GENERALISATION || assoc.getType() == AssociationTypes.IMPLEMENTS) {
				superClasses.add(assoc.getOtherClazz());
			}
		}
		if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for (GraphMember item : list) {
				if (item instanceof Attribute) {
					if (check(item, filters)) {
						collection.add((Attribute) item);
					}
				} else if (item instanceof Association) {
					Association assoc = (Association) item;
					if (assoc.getType() == AssociationTypes.GENERALISATION
							|| assoc.getType() == AssociationTypes.IMPLEMENTS) {
						superClasses.add(assoc.getOtherClazz());
					}
				}
			}
		}
		boolean isInterface = TYPE_INTERFACE.equals(getType());
		boolean isAbstract = getModifier().has(Modifier.ABSTRACT);
		if (isInterface || isAbstract) {
			return collection;
		}
		/* ALL SUPERMETHODS */
		AttributeSet newAttribute = new AttributeSet();
		AttributeSet foundAttribute = new AttributeSet();
		for (int i = 0; i < superClasses.size(); i++) {
			Clazz item = superClasses.get(i);
			item.parseSuperElements(superClasses, collection, newAttribute, foundAttribute, filters);
		}
		collection.addAll(foundAttribute);
		return collection;
	}

	/**
	 * get All Methods.
	 *
	 * @param filters Can Filter the List of Methods
	 * @return all Methods of a Clazz
	 * 
	 *         <pre>
	 * Clazz  --------------------- Methods
	 * one                          many
	 *         </pre>
	 */
	public MethodSet getMethods(Condition<?>... filters) {
		MethodSet collection = new MethodSet();
		if (this.children == null) {
			return collection;
		}
		ClazzSet superClasses = new ClazzSet();
		if (this.children instanceof Method) {
			if (check((Method) this.children, filters)) {
				collection.add((Method) this.children);
			}
			return collection;
		} else if (this.children instanceof Association) {
			Association assoc = (Association) this.children;
			if (assoc.getType() == AssociationTypes.GENERALISATION || assoc.getType() == AssociationTypes.IMPLEMENTS) {
				superClasses.add(assoc.getOtherClazz());
			}
		}

		if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for (GraphMember item : list) {
				if (item instanceof Method) {
					if (check(item, filters)) {
						collection.add((Method) item);
					}
				} else if (item instanceof Association) {
					Association assoc = (Association) item;
					if (assoc.getType() == AssociationTypes.GENERALISATION
							|| assoc.getType() == AssociationTypes.IMPLEMENTS) {
						superClasses.add(assoc.getOtherClazz());
					}
				}
			}
		}
		boolean isInterface = TYPE_INTERFACE.equals(getType());
		boolean isAbstract = getModifier().has(Modifier.ABSTRACT);
		if (isInterface || isAbstract) {
			return collection;
		}
		/* ALL SUPERMETHODS */
		MethodSet newMethods = new MethodSet();
		MethodSet foundMethods = new MethodSet();
		for (int i = 0; i < superClasses.size(); i++) {
			Clazz item = superClasses.get(i);
			item.parseSuperElements(superClasses, collection, newMethods, foundMethods, filters);
		}
		collection.addAll(foundMethods);

		return collection;
	}

	/**
	 * Gets the associations.
	 *
	 * @param filters the filters
	 * @return the associations
	 */
	@Override
	public AssociationSet getAssociations(Condition<?>... filters) {
		AssociationSet collection = super.getAssociations(filters);
		boolean isInterface = TYPE_INTERFACE.equals(getType());
		boolean isAbstract = getModifier().has(Modifier.ABSTRACT);
		if (isInterface || isAbstract) {
			return collection;
		}
		ClazzSet superClasses = new ClazzSet();
		for (Association assoc : collection) {
			if (assoc.getType() == AssociationTypes.GENERALISATION || assoc.getType() == AssociationTypes.IMPLEMENTS) {
				superClasses.add(assoc.getOtherClazz());
			}
		}
		AssociationSet newAssocs = new AssociationSet();
		AssociationSet foundAssocs = new AssociationSet();
		for (int i = 0; i < superClasses.size(); i++) {
			Clazz item = superClasses.get(i);
			item.parseSuperElements(superClasses, collection, newAssocs, foundAssocs, filters);
		}
		collection.addAll(foundAssocs);
		return collection;
	}

	/**
	 * get All Methods
	 * 
	 * @param superClasses     Set of all SuperClasses
	 * @param existsElements   Set of Found Methods or new Attribute (Return Value)
	 * @param newExistElements Set of new Methods or new Attribute
	 * @param newElements      new Methods or new Attribute
	 * @param filters          Can Filter the List of Methods
	 *
	 *                         <pre>
	 * Clazz  --------------------- Methods
	 * one                          many
	 *                         </pre>
	 */
	protected void parseSuperElements(ClazzSet superClasses, SimpleSet<?> existsElements, SimpleSet<?> newExistElements,
			SimpleSet<?> newElements, Condition<?>... filters) {
		if (this.children == null || existsElements == null) {
			return;
		}
		boolean isInterface = TYPE_INTERFACE.equals(getType());
		boolean isAbstract = getModifier().has(Modifier.ABSTRACT);
		Class<?> checkClassType = existsElements.getTypClass();
		if (!isInterface && !isAbstract) {
			SimpleSet<?> collection = null;
			if (checkClassType == Method.class) {
				collection = getMethods(filters);
			} else if (checkClassType == Attribute.class) {
				collection = getAttributes(filters);
			} else if (checkClassType == Association.class) {
				collection = getAssociations(filters);
			}
			newElements.removeAll(collection);
			return;
		}

		GraphSimpleSet list = this.getChildren();
		for (GraphMember member : list) {
			if (member instanceof Association) {
				Association assoc = (Association) member;
				if (assoc.getType() == AssociationTypes.GENERALISATION
						|| assoc.getType() == AssociationTypes.IMPLEMENTS) {
					superClasses.add(assoc.getOtherClazz());
					continue;
				}
				if (checkClassType != Association.class) {
					continue;
				}
				if (assoc.getOtherType() == AssociationTypes.GENERALISATION
						|| assoc.getType() == AssociationTypes.IMPLEMENTS) {
					continue;
				}
			}
			if (checkClassType == Method.class && !(member instanceof Method)) {
				continue;
			} else if (checkClassType == Attribute.class && !(member instanceof Attribute)) {
				continue;
			} else if (checkClassType == Association.class && !(member instanceof Association)) {
				continue;
			}
			if (existsElements.contains(member)) {
				continue;
			}
			Modifier modifier = member.getModifier();
			if (isInterface) {
				if (modifier == null || !modifier.has(Modifier.DEFAULT)) {
					if (check(member, filters) && !newExistElements.contains(member)) {
						newElements.add(member);
					}
				} else if (!newExistElements.contains(member)) {
					newExistElements.add(member);
					newElements.remove(member);
				}
			} else if (isAbstract && modifier != null && modifier.has(Modifier.ABSTRACT)) {
				if (check(member, filters) && !newExistElements.contains(member)) {
					newElements.add(member);
				} else if (!newExistElements.contains(member)) {
					newExistElements.add(member);
					newElements.remove(member);
				}
			}
		}
	}

	/**
	 * Removes the.
	 *
	 * @param member the member
	 * @return true, if successful
	 */
	@Override
	public boolean remove(GraphMember member) {
		if (this.children == null || member == null) {
			return false;
		}
		if (member instanceof Clazz) {
			Clazz clazz = (Clazz) member;
			for (Association assoc : getAssociations()) {
				if (assoc.getType() == AssociationTypes.GENERALISATION
						|| assoc.getType() == AssociationTypes.IMPLEMENTS) {
					if (assoc.getOther().contains(clazz, true, true)) {
						if (assoc.getOther().getParents().size() == 1) {
							this.remove(assoc);
						} else {
							assoc.getOther().withoutParent(clazz);
						}
						break;
					}
				}
			}
		}
		return super.remove(member);
	}

	protected Clazz with(Import... value) {
		super.withChildren(value);
		return this;
	}

	/**
	 * Gets the imports.
	 *
	 * @return the imports
	 */
	public SimpleSet<Import> getImports() {
		SimpleSet<Import> collection = new SimpleSet<Import>();
		if (this.children == null) {
			return collection;
		}
		if (this.children instanceof Import) {
			collection.add((Import) this.children);
			return collection;
		}
		if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for (GraphMember item : list) {
				if (item instanceof Import) {
					collection.add((Import) item);
				}
			}
		}
		return collection;
	}

	/**
	 * Creates the method.
	 *
	 * @param name the name
	 * @param returnValue the return value
	 * @param parameters the parameters
	 * @return the method
	 */
	public Method createMethod(String name, DataType returnValue, Parameter... parameters) {
		Method method = createMethod(name, parameters);
		method.with(returnValue);
		return method;
	}

	/**
	 * Creates the method.
	 *
	 * @param name the name
	 * @param parameters the parameters
	 * @return the method
	 */
	public Method createMethod(String name, Parameter... parameters) {
		Method method = new Method().with(name);
		method.with(parameters);
		method.setParentNode(this);
		return method;
	}

	/**
	 * Creates the attribute.
	 *
	 * @param name the name
	 * @param type the type
	 * @return the attribute
	 */
	public Attribute createAttribute(String name, DataType type) {
		Attribute attribute = new Attribute(name, type);
		with(attribute);
		return attribute;
	}

	/**
	 * With attribute.
	 *
	 * @param name the name
	 * @param type the type
	 * @return the clazz
	 */
	public Clazz withAttribute(String name, DataType type) {
		Attribute attribute = new Attribute(name, type);
		with(attribute);
		return this;
	}

	/**
	 * With method.
	 *
	 * @param name the name
	 * @param returnType the return type
	 * @param parameters the parameters
	 * @return the clazz
	 */
	public Clazz withMethod(String name, DataType returnType, Parameter... parameters) {
		Method method = this.createMethod(name, parameters);
		method.with(returnType);
		return this;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		if (this.id != null) {
			return this.id + ":" + this.getName();
		}
		return getName();
	}

	/**
	 * Gets the value.
	 *
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(String attribute) {
		if (attribute == null) {
			return null;
		}
		if (PROPERTY_PACKAGENAME.equalsIgnoreCase(attribute)) {
			String fullName = this.getName(false);
			if (fullName == null) {
				return null;
			}
			int pos = fullName.lastIndexOf(".");
			if (pos < 0) {
				return "";
			}
			return fullName.substring(0, pos);
		}
		if (PROPERTY_FULLNAME.equalsIgnoreCase(attribute)) {
			return this.getName(false);
		}
		if (PROPERTY_MODIFIERS.equalsIgnoreCase(attribute)) {
			CharacterBuffer buffer = new CharacterBuffer();
			Modifier modifier = this.getModifier();
			if (modifier != null) {
				modifier = modifier.getModifier();
				while (modifier != null) {
					buffer.with(modifier.getName());
					modifier = modifier.getModifier();
					if (modifier != null) {
						buffer.with(' ');
					}
				}
			}
			return buffer.toString();
		}
		if (PROPERTY_TYPE.equalsIgnoreCase(attribute)) {
			return this.getType();
		}
		if (PROPERTY_SUPERCLAZZ.equalsIgnoreCase(attribute)) {
			ClazzSet clazzes;
			if (TYPE_ENUMERATION.equals(this.getType()) || TYPE_INTERFACE.equals(this.getType())) {
				clazzes = getImplements();
			} else {
				clazzes = getSuperClazzes(false);
			}
			return clazzes.toString(", ");
		}
		if (PROPERTY_IMPLEMENTS.equalsIgnoreCase(attribute)) {
			if (TYPE_ENUMERATION.equals(this.getType()) || TYPE_INTERFACE.equals(this.getType())) {
				return null;
			}

			ClazzSet implementsClazz = getImplements();
			return implementsClazz.toString(", ");
		}
		int pos = attribute.indexOf('.');
		String attrName;
		if (pos > 0) {
			attrName = attribute.substring(0, pos);
		} else {
			attrName = attribute;
		}
		if (PROPERTY_ATTRIBUTE.equalsIgnoreCase(attrName)) {
			AttributeSet attributes = this.getAttributes();
			if (pos > 0) {
				return attributes.getValue(attribute.substring(pos + 1));
			}
			return attributes;
		}
		return super.getValue(attribute);
	}
	
	/**
	 * To data type.
	 *
	 * @return the data type
	 */
	public DataType toDataType() {
		return DataType.create(this);
	}
}
