package de.uniks.networkparser.graph;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.util.AssociationSet;
import de.uniks.networkparser.graph.util.AttributeSet;
import de.uniks.networkparser.graph.util.ClazzSet;
import de.uniks.networkparser.graph.util.MethodSet;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class Clazz extends GraphEntity {
	public static final StringFilter<Clazz> NAME = new StringFilter<Clazz>(GraphMember.PROPERTY_NAME);
	public static final String PROPERTY_PACKAGENAME = "packageName";
	public static final String PROPERTY_FULLNAME = "fullName";
	public static final String PROPERTY_VISIBILITY = "visibility";
	public static final String PROPERTY_MODIFIERS = "modifiers";
	public static final String PROPERTY_TYPE = "type";
	
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
	protected String getFullId() {
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

		ClazzSet collection = getEdgeClazzes(type, null);
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
		ClazzSet collection = getEdgeClazzes(AssociationTypes.GENERALISATION, null);
		if(!transitive) {
			return collection;
		}
		int size = collection.size();
		for(int i=0;i<size;i++) {
			collection.withList(collection.get(i).getSuperClazzes(transitive));
		}
		return collection;
	}
	
	protected void repairAssociation(Association assoc) {
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
		ClazzSet kidClazzes = getEdgeClazzes(AssociationTypes.EDGE, AssociationTypes.GENERALISATION);
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
		ClazzSet kidClazzes = getEdgeClazzes(AssociationTypes.EDGE, AssociationTypes.IMPLEMENTS);
		return kidClazzes;
	}
	
	protected ClazzSet getEdgeClazzes(AssociationTypes typ, AssociationTypes otherTyp) {
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

	protected void createAssociation(AssociationTypes direction, AssociationTypes backDirection, Clazz... values) {
		if (values == null) {
			return;
		}
		AssociationSet associations = getAssociations();
		for (Clazz item : values) {
			if (item != null) {
				for (Association assoc : associations) {
					if(assoc.getType() == direction && assoc.getOtherType() == backDirection) {
						if(assoc.contains(item, true, false) == false) {
							assoc.getOther().setParentNode(item);
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
		return super.setParentNode(value);
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
		
		ClazzSet superClasses= new ClazzSet();
		if(this.children instanceof Attribute) {
			if(check((Attribute)this.children, filters)) {
				collection.add((Attribute)this.children);
			}
			return collection;
		} else if(this.children instanceof Association) {
			Association assoc = (Association) this.children;
			if(assoc.getType()==AssociationTypes.GENERALISATION || assoc.getType() == AssociationTypes.IMPLEMENTS) {
				superClasses.add(assoc.getOtherClazz());
			}
		}
		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for(GraphMember item : list) {
				if(item instanceof Attribute) {
					if(check(item, filters)) {
						collection.add((Attribute)item);
					}
				} else if(item instanceof Association) {
					Association assoc = (Association) item;
					if(assoc.getType()==AssociationTypes.GENERALISATION || assoc.getType() == AssociationTypes.IMPLEMENTS) {
						superClasses.add(assoc.getOtherClazz());
					}
				}
			}
		}
		boolean isInterface = getType() == ClazzType.INTERFACE;
		boolean isAbstract = getModifier().has(Modifier.ABSTRACT);
		if(isInterface || isAbstract) {
			return collection;
		}
		// ALL SUPERMETHODS
		AttributeSet newAttribute = new AttributeSet();
		AttributeSet foundAttribute = new AttributeSet();
		for(int i=0;i<superClasses.size();i++) {
			Clazz item = superClasses.get(i);
			item.parseSuperElements(superClasses, collection, newAttribute, foundAttribute, filters);
		}
		collection.addAll(foundAttribute);
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
		ClazzSet superClasses= new ClazzSet();
		if(this.children instanceof Method) {
			if(check((Method)this.children, filters)) {
				collection.add((Method)this.children);
			}
			return collection;
		} else if(this.children instanceof Association) {
			Association assoc = (Association) this.children;
			if(assoc.getType()==AssociationTypes.GENERALISATION || assoc.getType() == AssociationTypes.IMPLEMENTS) {
				superClasses.add(assoc.getOtherClazz());
			}
		}

		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for(GraphMember item : list) {
				if(item instanceof Method) {
					if(check(item, filters)) {
						collection.add((Method)item);
					}
				} else if(item instanceof Association) {
					Association assoc = (Association) item;
					if(assoc.getType()==AssociationTypes.GENERALISATION || assoc.getType() == AssociationTypes.IMPLEMENTS) {
						superClasses.add(assoc.getOtherClazz());
					}
				}
			}
		}
		boolean isInterface = getType() == ClazzType.INTERFACE;
		boolean isAbstract = getModifier().has(Modifier.ABSTRACT);
		if(isInterface || isAbstract) {
			return collection;
		}
		// ALL SUPERMETHODS
		MethodSet newMethods = new MethodSet();
		MethodSet foundMethods = new MethodSet();
		for(int i=0;i<superClasses.size();i++) {
			Clazz item = superClasses.get(i);
			item.parseSuperElements(superClasses, collection, newMethods, foundMethods, filters);
		}
		collection.addAll(foundMethods);
		
		return collection;
	}
	
	@Override
	public AssociationSet getAssociations(Condition<?>... filters) {
		AssociationSet collection = super.getAssociations(filters);
		boolean isInterface = getType() == ClazzType.INTERFACE;
		boolean isAbstract = getModifier().has(Modifier.ABSTRACT);
		if(isInterface || isAbstract) {
			return collection;
		}
		ClazzSet superClasses= new ClazzSet();
		for(Association assoc : collection) {
			if(assoc.getType()==AssociationTypes.GENERALISATION || assoc.getType() == AssociationTypes.IMPLEMENTS) {
				superClasses.add(assoc.getOtherClazz());
			}
		}
		AssociationSet newAssocs = new AssociationSet();
		AssociationSet foundAssocs = new AssociationSet();
		for(int i=0;i<superClasses.size();i++) {
			Clazz item = superClasses.get(i);
			item.parseSuperElements(superClasses, collection, newAssocs, foundAssocs, filters);
		}
		collection.addAll(foundAssocs);
		return collection;
	}
	
	/** get All Methods
	 * @param superClasses Set of all SuperClasses
	 * @param existsElements Set of Found Methods or new Attribute (Return Value)
	 * @param newExistElements Set of new Methods or new Attribute
	 * @param newElements new Methods or new Attribute
	 * @param filters Can Filter the List of Methods
	 *
	 *<pre>
	 * Clazz  --------------------- Methods
	 * one                          many
	 *</pre>
	 */
	protected void parseSuperElements(ClazzSet superClasses, SimpleSet<?> existsElements, SimpleSet<?> newExistElements, SimpleSet<?> newElements, Condition<?>... filters) {
		if(this.children == null) {
			return;
		}
		boolean isInterface = getType() == ClazzType.INTERFACE;
		boolean isAbstract = getModifier().has(Modifier.ABSTRACT);
		Class<?> checkClassType = existsElements.getTypClass();
		if(isInterface == false && isAbstract  == false ) {
			SimpleSet<?> collection = null;
			if(checkClassType == Method.class) {
				collection = getMethods(filters);
			}else if(checkClassType == Attribute.class) {
				collection = getAttributes(filters);
			}else if(checkClassType == Association.class) {
				collection = getAssociations(filters);
			}
			newElements.removeAll(collection);
			return;
		}
		
		GraphSimpleSet list = this.getChildren();
		for(GraphMember member : list) {
			if(member instanceof Association) {
				Association assoc = (Association) member;
				if(assoc.getType()==AssociationTypes.GENERALISATION || assoc.getType() == AssociationTypes.IMPLEMENTS) {
					superClasses.add(assoc.getOtherClazz());
					continue;
				}
				if(checkClassType != Association.class) {
					continue;
				}
				if(assoc.getOtherType()==AssociationTypes.GENERALISATION || assoc.getType() == AssociationTypes.IMPLEMENTS) {
					continue;
				}
			}
			if(checkClassType == Method.class && member instanceof Method == false) {
				continue;
			}else if(checkClassType == Attribute.class && member instanceof Attribute == false) {
				continue;
			}else if(checkClassType == Association.class && member instanceof Association == false) {
				continue;
			}
			if(existsElements.contains(member)) {
				continue;
			}
			if(isInterface) {
				if(member.getModifier() == null || member.getModifier().has(Modifier.DEFAULT) == false) {
					if(check(member, filters) && newExistElements.contains(member) == false) {
						newElements.add(member);
					}
				} else if(newExistElements.contains(member) == false){
					newExistElements.add(member);
					newElements.remove(member);
				}
			} else if(isAbstract && member.getModifier().has(Modifier.ABSTRACT)) {
				if(check(member, filters) && newExistElements.contains(member) == false) {
					newElements.add(member);
				} else if(newExistElements.contains(member) == false){
					newExistElements.add(member);
					newElements.remove(member);
				}
			}
		}
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
		method.setParentNode(this);
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
	
	public Clazz with(Clazz tgtClass, String tgtRoleName, Cardinality tgtCardinality, String srcRoleName, Cardinality srcCardinality) {
		this.withBidirectional(tgtClass, tgtRoleName, tgtCardinality, srcRoleName, srcCardinality);
		return this;
	}

	public Clazz with(String name, DataType type) {
		this.withAttribute(name, type);
		return this;
	}

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public Object getValue(String attribute) {
		if(PROPERTY_PACKAGENAME.equalsIgnoreCase(attribute)) {
			String fullName = this.getName(false);
			if(fullName == null) {
				return null;
			}
			int pos = fullName.lastIndexOf(".");
			if(pos < 0) {
				return "";
			}
			return fullName.substring(0, pos);
		}
		if (PROPERTY_FULLNAME.equalsIgnoreCase(attribute)) {
			return this.getName(false);
		}
		if(PROPERTY_VISIBILITY.equalsIgnoreCase(attribute)) {
			return this.getModifier().getName();
		}
		if(PROPERTY_MODIFIERS.equalsIgnoreCase(attribute)) {
			CharacterBuffer buffer = new CharacterBuffer();
			Modifier modifier = this.getModifier();
			if(modifier != null) {
				modifier = modifier.getModifier();
				while(modifier != null) {
					buffer.with(modifier.getName());
					modifier = modifier.getModifier();
					if(modifier != null) {
						buffer.with(' ');
					}
				}
			}
			return buffer.toString();
		}
		if(PROPERTY_TYPE.equalsIgnoreCase(attribute)) {
			return this.getType().getValue();
		}
//		parameters.put("name", clazz.getName(true));
//		parameters.put("superclasses", determineSuperClasses(clazz, superPropertyChangeEnabled));
//		parameters.put("propertyChange", propertyChange);

		return super.getValue(attribute);
	}
}
