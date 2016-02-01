package de.uniks.networkparser.graph;

import de.uniks.networkparser.graph.util.AssociationSet;
import de.uniks.networkparser.graph.util.AttributeSet;
import de.uniks.networkparser.graph.util.ClazzSet;
import de.uniks.networkparser.graph.util.MethodSet;
import de.uniks.networkparser.interfaces.Condition;
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

public class Clazz extends GraphEntity {
	public static final StringFilter<Clazz> NAME = new StringFilter<Clazz>(GraphMember.PROPERTY_NAME);

	public enum ClazzType {CLAZZ, ENUMERATION, INTERFACE};
	private ClazzType type = ClazzType.CLAZZ;

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
	 *	   %srcCardinality%	   %tgtCardinality%
	 * Clazz --------------------------------------- %tgtClass%
	 *	   %srcRoleName%			 %tgtRoleName%
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
		tgtClass.with(assocTarget);

		// Source
		Association assocSource = new Association(this).with(srcCardinality).with(srcRoleName);

		assocSource.with(assocTarget);

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
		Association assocTarget = new Association(tgtClass).with(tgtCardinality);
		tgtClass.with(assocTarget);
		assocTarget.with(AssociationTypes.UNDIRECTIONAL).with(tgtRoleName);

		// Source
		Association assocSource = new Association(this);
		assocSource.with(AssociationTypes.EDGE);
		assocSource.with(assocTarget);

		this.with(assocSource);
		return this;
	}

	public Clazz getSuperClass() {
		if (associations == null) {
			return null;
		}
		ClazzSet clazzSet = getEdges(AssociationTypes.GENERALISATION, AssociationTypes.EDGE);
		return clazzSet.first();
	}
	/**
	 * Get All Interfaces
	 * @param transitive Get all Interfaces or direct Interfaces
	 * @return all Interfaces of a Clazz
	 *		 <pre>
	 *			  one					   many
	 * Clazz ----------------------------------- Clazz
	 *			  clazz				   Interfaces
	 *		 </pre>
	 */
	public ClazzSet getInterfaces(boolean transitive) {
		ClazzSet interfaces = new ClazzSet();
		if (associations == null) {
			return interfaces;
		}
		ClazzSet clazzSet = getEdges(AssociationTypes.EDGE, AssociationTypes.GENERALISATION);
		for (Clazz clazz : clazzSet) {
			if (GraphUtil.isInterface(clazz)) {
				interfaces.with(clazz);
			}
		}
		clazzSet = getEdges(AssociationTypes.EDGE, AssociationTypes.IMPLEMENTS);
		for (Clazz clazz : clazzSet) {
			if (GraphUtil.isInterface(clazz)) {
				interfaces.with(clazz);
			}
		}
		if(!transitive) {
			return interfaces;
		}
		int size = interfaces.size();
		for(int i=0;i<size;i++) {
			interfaces.withList(interfaces.get(i).getInterfaces(transitive));
		}
		return interfaces;
	}

	public Clazz withSuperClazz(Clazz... values) {
		if (values == null) {
			return this;
		}
		AssociationSet associations = getAssociations();
		for (Clazz item : values) {
			if (item != null) {
				boolean found=false;
				for (Association assoc : associations) {
					if(assoc.getType()==AssociationTypes.GENERALISATION) {
						if(assoc.contains(item, false, true) == false) {
							found = true;
							for(GraphMember member : assoc.getOther().getParents()) {
								if(member instanceof Clazz && member != item) {
									assoc.getOther().withoutParent(member);
								}
								
							}
							assoc.getOther().setParent(item);
							break;
						}
					}
				}
				if(found == false) {
					Association childAssoc = new Association(item).with(AssociationTypes.EDGE);
					Association superAssoc = new Association(this).with(AssociationTypes.GENERALISATION);
					childAssoc.with(superAssoc);
					this.with(superAssoc);
					item.with(childAssoc);
				}
			}
		}
		return this;
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
		ClazzSet collection = getEdges(AssociationTypes.GENERALISATION);
		if(!transitive) {
			return collection;
		}
		int size = collection.size();
		for(int i=0;i<size;i++) {
			collection.withList(collection.get(i).getSuperClazzes(transitive));
		}
		return collection;
	}

	/**
	 * get All KindClazzes
	 * @param transitive Get all KindClasses or direct KindClasses
	 * @return all KindClasses of a Clazz
	 *		 <pre>
	 *			  one					   many
	 * Clazz ----------------------------------- Clazz
	 *			  clazz				   kindClazzes
	 *		 </pre>
	 */
	public ClazzSet getKidClazzes(boolean transitive) {
		ClazzSet kidClazzes = getEdges(AssociationTypes.EDGE, AssociationTypes.GENERALISATION);
		if(!transitive) {
			return kidClazzes;
		}
		int size = kidClazzes.size();
		for(int i=0;i<size;i++) {
			kidClazzes.withList(kidClazzes.get(i).getKidClazzes(transitive));
		}
		return kidClazzes;
	}

	ClazzSet getEdges(AssociationTypes typ) {
		return getEdges(typ, null);
	}

	ClazzSet getEdges(AssociationTypes typ, AssociationTypes otherTyp) {
		ClazzSet kindClazzes = new ClazzSet();
		if (associations == null || typ == null) {
			return kindClazzes;
		}
		for (Association assoc : getEdges()) {
			if(typ != assoc.getType()) {
				continue;
			}
			Clazz clazz = assoc.getOtherClazz();
			if(otherTyp == null || assoc.getOtherTyp() == otherTyp) {
				if(GraphUtil.isInterface(clazz) == false) {
					kindClazzes.with(clazz);
				}
			}
		}
		return kindClazzes;
	}

	public Clazz withKidClazzes(Clazz... values) {
		if (values == null) {
			return this;
		}
		AssociationSet associations = getAssociations();
		for (Clazz item : values) {
			if (item != null) {
				boolean found=false;
				for (Association assoc : associations) {
					if(assoc.getOther().getType() == AssociationTypes.GENERALISATION) {
						if(assoc.contains(item, true, false)) {
							found = true;
							break;
						}
					}
				}
				if(found == false) {
					Association superAssoc = new Association(item).with(AssociationTypes.GENERALISATION);
					Association childAssoc = new Association(this).with(AssociationTypes.EDGE);
					childAssoc.with(superAssoc);
					this.with(childAssoc);
					item.with(superAssoc);
				}
			}
		}
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
		if (this.associations == null || values == null) {
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
		if (this.associations == null || values == null) {
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

	public Clazz with(ClazzImport... value) {
		super.withChildren(value);
		return this;
	}

	public SimpleSet<ClazzImport> getImports() {
		SimpleSet<ClazzImport> collection = new SimpleSet<ClazzImport>();
		if(this.children == null) {
			return collection;
		}
		if(this.children instanceof ClazzImport) {
			collection.add((ClazzImport)this.children);
			return collection;
		}
		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for(GraphMember item : list) {
				if(item instanceof ClazzImport) {
					collection.add((ClazzImport)item);
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