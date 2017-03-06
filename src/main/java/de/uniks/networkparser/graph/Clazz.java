package de.uniks.networkparser.graph;

import de.uniks.networkparser.graph.util.AssociationSet;
import de.uniks.networkparser.graph.util.AttributeSet;
import de.uniks.networkparser.graph.util.ClazzSet;
import de.uniks.networkparser.graph.util.MethodSet;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class Clazz extends GraphEntity {
	public static final StringFilter<Clazz> NAME = new StringFilter<Clazz>(GraphMember.PROPERTY_NAME);

	public enum ClazzType {CLAZZ, ENUMERATION, INTERFACE};
	private ClazzType type = ClazzType.CLAZZ;

	Clazz() {
		
	}
	
	/**
	 * Constructor with Name of Clazz
	 * @param name Name of Clazz
	 */
	public Clazz(String name) {
		this.with(name);
	}
	public Clazz(Class<?> name) {
		if(name != null) {
			with(name.getName().replace("$", "."));
		}
	}
	
	@Override
	public Clazz with(String name) {
		super.with(name);
		return this;
	}
	
	@Override
	public Clazz withId(String id) {
		super.withId(id);
		return this;
	}

	public Clazz with(ClazzType clazzType) {
		this.type = clazzType;
		return this;
	}

	public Clazz enableInterface() {
		this.with(ClazzType.INTERFACE);
		return this;
	}

	public Clazz enableEnumeration() {
		this.with(ClazzType.ENUMERATION);
		return this;
	}

	public Clazz enableEnumeration(String... literals) {
		this.with(ClazzType.ENUMERATION);
		if(literals == null) {
			return this;
		}
		for(String item : literals) {
			this.with(new Literal(item));
		}
		return this;
	}

	public Clazz enableEnumeration(Literal... literals) {
		this.with(ClazzType.ENUMERATION);
		if(literals == null) {
			return this;
		}
		for(Literal item : literals) {
			this.with(item);
		}
		return this;
	}

	public ClazzType getType() {
		return type;
	}

	@Override
	String getFullId() {
		if(this.getId() != null) {
			return this.getId();
		}
		return super.getFullId();
	}

	@Override
	public Clazz withExternal(boolean value) {
		super.withExternal(value);
		return this;
	}

	@Override
	public Modifier getModifier() {
		Modifier modifier = super.getModifier();
		if(modifier == null) {
			modifier = new Modifier(Modifier.PUBLIC.getName());
			super.withChildren(modifier);
		}
		return modifier;
	}

	public Clazz with(Association... values) {
		super.with(values);
		return this;
	}

	public Clazz with(Modifier... values) {
		super.withModifier(values);
		return this;
	}

	public Clazz with(Attribute... values) {
		super.withChildren(values);
		return this;
	}

	public Clazz with(Method... values) {
		super.withChildren(values);
		return this;
	}

	public Clazz with(Annotation value) {
		super.with(value);
		return this;
	}

	public Clazz with(GraphImage... values) {
		super.withChildren(values);
		return this;
	}
	public Clazz with(Literal... values) {
		super.withChildren(values);
		return this;
	}

	public SimpleSet<Literal> getValues() {
		SimpleSet<Literal> collection = new SimpleSet<Literal>();
		if(this.children == null) {
			return collection;
		}
		if(this.children instanceof Literal) {
			collection.add((Literal)this.children);
			return collection;
		}
		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for(GraphMember item : list) {
				if(item instanceof Literal) {
					collection.add((Literal)item);
				}
			}
		}
		return collection;
	}

	public Clazz without(Association... values) {
		super.without(values);
		return this;
	}

	public Clazz without(Modifier... values) {
		super.without(values);
		return this;
	}

	public Clazz without(Attribute... values) {
		super.without(values);
		return this;
	}

	public Clazz without(Method... values) {
		super.without(values);
		return this;
	}

	public Clazz without(Annotation value) {
		super.without(value);
		return this;
	}

	public Clazz without(GraphImage... values) {
		super.without(values);
		return this;
	}

	public Clazz without(Literal... values) {
		super.without(values);
		return this;
	}

	/**
	 * ********************************************************************
	 * <pre>
	 *		%srcCardinality%		%tgtCardinality%
	 * Clazz -------------------------------------- %tgtClass%
	 *		%srcRoleName%			%tgtRoleName%
	 * </pre>
	 *
	 * create a Bidirectional Association
	 *
	 * @param tgtClass				 The target Clazz
	 * @param tgtRoleName			 The Targetrolename
	 * @param tgtCardinality		The Targetcardinality
	 * @param srcRoleName			The sourcerolename
	 * @param srcCardinality		The sourcecardinality
	 * @return The Clazz Instance
	 */
	public Clazz withBidirectional(Clazz tgtClass, String tgtRoleName, Cardinality tgtCardinality, String srcRoleName, Cardinality srcCardinality) {
		// Target
		Association assocTarget = new Association(tgtClass).with(tgtCardinality).with(tgtRoleName);

		// Source
		Association assocSource = new Association(this).with(srcCardinality).with(srcRoleName);
		assocSource.with(assocTarget);

		tgtClass.with(assocTarget);
		this.with(assocSource);
		return this;
	}

	/**
	 * ********************************************************************
	 * <pre>
	 *								 %tgtCardinality%
	 * Clazz ----------------------------------- %tgtClass%
	 *									%tgtRoleName%
	 * </pre>
	 *
	 * create a Undirectional Association
	 *
	 * @param tgtClass			The target Clazz
	 * @param tgtRoleName		The Targetrolename
	 * @param tgtCardinality	The Targetcardinality
	 * @return The Clazz Instance
	 */
	public Clazz withUniDirectional(Clazz tgtClass, String tgtRoleName, Cardinality tgtCardinality) {
		// Target
		Association assocTarget = new Association(tgtClass).with(tgtCardinality).with(AssociationTypes.UNDIRECTIONAL).with(tgtRoleName);

		// Source
		Association assocSource = new Association(this).with(AssociationTypes.EDGE).with(assocTarget);

		tgtClass.with(assocTarget);
		this.with(assocSource);
		return this;
	}

	/**
	 * Get All Interfaces
	 * @param transitive Get all Interfaces or direct Interfaces
	 * @return all Interfaces of a Clazz
	 *		 <pre>
	 *			one						many
	 * Clazz ----------------------------------- Clazz
	 *			clazz					Interfaces
	 *		 </pre>
	 */
	public ClazzSet getInterfaces(boolean transitive) {
		repairAssociations();
		AssociationTypes type = AssociationTypes.IMPLEMENTS;
		if(this.getType()==ClazzType.INTERFACE) {
			type = AssociationTypes.GENERALISATION;
		}

		ClazzSet collection = getEdgesByTypes(type, null);
		if(!transitive) {
			return collection;
		}
		int size = collection.size();
		for(int i=0;i<size;i++) {
			collection.withList(collection.get(i).getInterfaces(transitive));
		}
		return collection;
	}
	
	/**
	 * Get All SuperClazzes
	 * @param transitive Get all SuperClasses or direct SuperClasses
	 * @return all SuperClasses of a Clazz
	 *		 <pre>
	 *			  one					   many
	 * Clazz ----------------------------------- Clazz
	 *			  clazz				   superClazzes
	 *		 </pre>
	 */
	public ClazzSet getSuperClazzes(boolean transitive) {
		repairAssociations();
		ClazzSet collection = getEdgesByTypes(AssociationTypes.GENERALISATION, null);
		if(!transitive) {
			return collection;
		}
		int size = collection.size();
		for(int i=0;i<size;i++) {
			collection.withList(collection.get(i).getSuperClazzes(transitive));
		}
		return collection;
	}
	
	void repairAssociation(Association assoc) {
		if(AssociationTypes.IMPLEMENTS.equals(assoc.getType()) == false && AssociationTypes.GENERALISATION.equals(assoc.getType()) == false) {
			// Wrong way try another round
			assoc = assoc.getOther();
		}
		if(AssociationTypes.IMPLEMENTS.equals(assoc.getType()) == false  && AssociationTypes.GENERALISATION.equals(assoc.getType()) == false) {
			// Ignore
			return;
		}
		if(assoc.getClazz().getType().equals(ClazzType.INTERFACE)) {
			if(AssociationTypes.GENERALISATION.equals(assoc.getType()) ==false) {
				assoc.with(AssociationTypes.GENERALISATION);
			}
		} else {
			// Its a Class
			if(assoc.getOtherClazz().getType().equals(ClazzType.INTERFACE)) {
				// Must be an Implements
				if(AssociationTypes.IMPLEMENTS.equals(assoc.getType())==false) {
					assoc.with(AssociationTypes.IMPLEMENTS);
				}	
			} else {
				// Must be an Genralization
				if(AssociationTypes.GENERALISATION.equals(assoc.getType())==false) {
					assoc.with(AssociationTypes.GENERALISATION);
				}
			}

		}
		
	}
	private void repairAssociations() {
		if (this.children == null ) {
			return;
		}
		if(this.children instanceof Association) {
			// Is is easy only one Assoc
			repairAssociation((Association) this.children);
		}else if(children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			AssociationSet generalizations = new AssociationSet();
			for (GraphMember item : list) {
				if(item instanceof Association) {
					Association assoc = (Association) item;
					repairAssociation(assoc);
					if(AssociationTypes.GENERALISATION.equals(assoc.getType())) {
						generalizations.add(assoc);
					}
				}
			}
			if(generalizations.size() > 1) {
				// Repair only valid last generalization
				for(int i=0;i<generalizations.size() - 1;i++) {
					this.without(generalizations.get(i));
				}
			}
		}
	}

	public Clazz withSuperClazz(Clazz... values) {
		createAssociation(AssociationTypes.GENERALISATION, AssociationTypes.EDGE, values);
		return this;
	}

	/**
	 * get All KidClazzes
	 * @param transitive Get all KidClasses or direct KidClasses
	 * @return all KidClasses of a Clazz
	 *		 <pre>
	 *			  one					   many
	 * Clazz ----------------------------------- Clazz
	 *			  superClass		   kidClazzes
	 *		 </pre>
	 */
	public ClazzSet getKidClazzes(boolean transitive) {
		ClazzSet kidClazzes = getEdgesByTypes(AssociationTypes.EDGE, AssociationTypes.GENERALISATION);
		if(!transitive) {
			return kidClazzes;
		}
		int size = kidClazzes.size();
		for(int i=0;i<size;i++) {
			kidClazzes.withList(kidClazzes.get(i).getKidClazzes(transitive));
		}
		return kidClazzes;
	}
	
	/**
	 * get All Implements Clazz
	 * @return all implements of a Clazz
	 *		 <pre>
	 *			  one					   many
	 * Clazz ----------------------------------- Clazz
	 *			  superClass		   kidClazzes
	 *		 </pre>
	 */
	public ClazzSet getImplements() {
		ClazzSet kidClazzes = getEdgesByTypes(AssociationTypes.EDGE, AssociationTypes.IMPLEMENTS);
		return kidClazzes;
	}
	
	ClazzSet getEdgesByTypes(AssociationTypes typ, AssociationTypes otherTyp) {
		ClazzSet kidClazzes = new ClazzSet();
		if (this.children == null || typ == null) {
			return kidClazzes;
		}
		for (Association assoc : super.getEdges(AssociationTypes.EDGE)) {
			if(typ != assoc.getType()) {
				continue;
			}
			if(otherTyp == null || assoc.getOtherType() == otherTyp) {
				Clazz clazz = assoc.getOtherClazz();
				kidClazzes.with(clazz);
			}
		}
		return kidClazzes;
	}

	void createAssociation(AssociationTypes direction, AssociationTypes backDirection, Clazz... values) {
		if (values == null) {
			return;
		}
		AssociationSet associations = getAssociations();
		for (Clazz item : values) {
			if (item != null) {
				for (Association assoc : associations) {
					if(assoc.getType() == direction && assoc.getOtherType() == backDirection) {
						if(assoc.contains(item, true, false) == false) {
							assoc.getOther().setParent(item);
							break;
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
	}

	public Clazz withKidClazzes(Clazz... values) {
		createAssociation(AssociationTypes.EDGE, AssociationTypes.GENERALISATION, values);
		return this;
	}

	public GraphModel getClassModel() {
		return (GraphModel) this.parentNode;
	}

	public boolean setClassModel(GraphModel value) {
		return super.setParent(value);
	}

	/** get All Attributes
	 * @param filters Can Filter the List of Attributes
	 * @return all Attributes of a Clazz
	 *
	 *<pre>
	 * Clazz  --------------------- Attributes
	 * one                          many
	 *</pre>
	 */
	public AttributeSet getAttributes(Condition<?>... filters) {
		AttributeSet collection = new AttributeSet();
		if(this.children == null) {
			return collection;
		}
		if(this.children instanceof Attribute) {
			if(check((Attribute)this.children, filters)) {
				collection.add((Attribute)this.children);
			}
			return collection;
		}
		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for(GraphMember item : list) {
				if(item instanceof Attribute && check(item, filters) ) {
					collection.add((Attribute)item);
				}
			}
		}
		return collection;
	}

	/** get All Methods
	 * @param filters Can Filter the List of Methods
	 * @return all Methods of a Clazz
	 *
	 *<pre>
	 * Clazz  --------------------- Methods
	 * one                          many
	 *</pre>
	 */
	public MethodSet getMethods(Condition<?>... filters) {
		MethodSet collection = new MethodSet();
		if(this.children == null) {
			return collection;
		}
		if(this.children instanceof Method) {
			if(check((Method)this.children, filters)) {
				collection.add((Method)this.children);
			}
			return collection;
		}
		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for(GraphMember item : list) {
				if(item instanceof Method && check(item, filters) ) {
					collection.add((Method)item);
				}
			}
		}
		return collection;
	}

	public Clazz withoutKidClazz(Clazz... values) {
		if (this.children == null || values == null) {
			return this;
		}
		for (Clazz item : values) {
			if (item != null) {
				for (Association assoc : getAssociations()) {
					if(assoc.getOther().getType() == AssociationTypes.GENERALISATION) {
						if(assoc.contains(item, false, true)) {
							super.without(assoc);
							break;
						}
					}
				}
			}
		}
		return this;
	}

	public Clazz withoutSuperClazz(Clazz... values) {
		if (this.children == null || values == null) {
			return this;
		}
		for (Clazz item : values) {
			if (item != null) {
				for (Association assoc : getAssociations()) {
					if(assoc.getType() == AssociationTypes.GENERALISATION) {
						if(assoc.contains(item, true, false)) {
							this.without(assoc);
							break;
						}
					}
				}
			}
		}
		return this;
	}

	public Clazz with(Import... value) {
		super.withChildren(value);
		return this;
	}

	public SimpleSet<Import> getImports() {
		SimpleSet<Import> collection = new SimpleSet<Import>();
		if(this.children == null) {
			return collection;
		}
		if(this.children instanceof Import) {
			collection.add((Import)this.children);
			return collection;
		}
		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for(GraphMember item : list) {
				if(item instanceof Import) {
					collection.add((Import)item);
				}
			}
		}
		return collection;
	}

	public Method createMethod(String name, Parameter... parameters) {
		Method method = new Method().with(name);
		method.with(parameters);
		method.setParent(this);
		return method;
	}

	public Attribute createAttribute(String name, DataType type) {
		Attribute attribute = new Attribute(name, type);
		with(attribute);
		return attribute;
	}
	public Clazz withAttribute(String name, DataType type) {
		Attribute attribute = new Attribute(name, type);
		with(attribute);
		return this;
	}
	public Clazz withMethod(String name, DataType returnType, Parameter... parameters) {
		Method method = this.createMethod(name, parameters);
		method.with(returnType);
		return this;
	}

	@Override
	public String toString() {
		return getName();
	}
}
