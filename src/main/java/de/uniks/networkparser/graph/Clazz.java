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

public class Clazz extends GraphEntity {
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
			super.withChildren(true, modifier);
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
		super.withChildren(true, values);
		return this;
	}

	public Clazz with(Method... values) {
		super.withChildren(true, values);
		return this;
	}
	
	public Clazz with(Annotation value) {
		super.with(value);
		return this;
	}
	
	public Clazz with(GraphImage... values) {
		super.withChildren(true, values);
		return this;
	}
	public Clazz with(Literal... values) {
		super.withChildren(true, values);
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
		for (Association assoc : getAssociation()) {
			if(!AssociationTypes.GENERALISATION.equals(assoc.getType())) {
				continue;
			}
			Clazz otherClazz = assoc.getOtherClazz();
			if(GraphUtil.isInterface(otherClazz) == false) {
				return otherClazz;
			}
		}
		return null;
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
	public SimpleSet<Clazz> getInterfaces(boolean transitive) {
		SimpleSet<Clazz> interfaces = new SimpleSet<Clazz>();
		if (associations == null) {
			return interfaces;
		}
		for (Association assoc : getAssociation()) {
			Clazz clazz = assoc.getOtherClazz();
			if(assoc.getType()==AssociationTypes.GENERALISATION || assoc.getType()==AssociationTypes.IMPLEMENTS) {
				if (GraphUtil.isInterface(clazz)) {
					interfaces.with(clazz);
				}
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
		SimpleSet<Association> associations = getAssociation();
		for (Clazz item : values) {
			if (item != null) {
				boolean found=false;
				for (Association assoc : associations) {
					if(assoc.getType()==AssociationTypes.GENERALISATION) {
						if(assoc.contains(item, false, true) == false) {
							found = true;
							assoc.getOther().setParent(item);
							break;
						}
					}
				}
				if(found == false) {
					Association child = new Association(item).with(AssociationTypes.EDGE);
					child.with(new Association(this).with(AssociationTypes.GENERALISATION));
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
	public SimpleSet<Clazz> getSuperClazzes(boolean transitive) {
		SimpleSet<Clazz> collection = getEdges(AssociationTypes.GENERALISATION);
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
	public SimpleSet<Clazz> getKidClazzes(boolean transitive) {
		SimpleSet<Clazz> kidClazzes = getEdges(AssociationTypes.EDGE, AssociationTypes.GENERALISATION);
		if(!transitive) {
			return kidClazzes;
		}
		int size = kidClazzes.size();
		for(int i=0;i<size;i++) {
			kidClazzes.withList(kidClazzes.get(i).getKidClazzes(transitive));
		}
		return kidClazzes;
	}
	
	SimpleSet<Clazz> getEdges(AssociationTypes typ) {
		return getEdges(typ, null);
	}
	
	SimpleSet<Clazz> getEdges(AssociationTypes typ, AssociationTypes otherTyp) {
		SimpleSet<Clazz> kindClazzes = new SimpleSet<Clazz>();
		if (associations == null || typ == null) {
			return kindClazzes;
		}
		for (Association assoc : getAssociation()) {
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
		SimpleSet<Association> associations = getAssociation();
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
					Association child = new Association(item).with(AssociationTypes.GENERALISATION);
					child.with(new Association(this).with(AssociationTypes.EDGE));
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
	
	public Clazz with(GraphModel value) {
		setClassModel(value);
		return this;
	}

	/**
	 * get All GraphAttributes
	 * 
	 * @return all GraphAttributes of a GraphNode
	 * 
	 *		 <pre>
	 *			  one					   many
	 * GraphModel ----------------------------------- GraphAttributes
	 *			  parent				   clazz
	 *		 </pre>
	 */
	public SimpleSet<Attribute> getAttributes() {
		SimpleSet<Attribute> collection = new SimpleSet<Attribute>();
		if(this.children == null) {
			return collection;
		}
		if(this.children instanceof Attribute) {
			collection.add((Attribute)this.children);
			return collection;
		}
		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for(GraphMember item : list) {
				if(item instanceof Attribute) {
					collection.add((Attribute)item);	
				}
			}
		}
		return collection;
	}

	/**
	 * get All GraphMethods
	 * 
	 * @return all GraphMethods of a GraphNode
	 * 
	 *		 <pre>
	 *			  one					   many
	 * GraphModel ----------------------------------- GraphMethods
	 *			  parent				   clazz
	 *		 </pre>
	 */
	public SimpleSet<Method> getMethods() {
		SimpleSet<Method> collection = new SimpleSet<Method>();
		if(this.children == null) {
			return collection;
		}
		if(this.children instanceof Method) {
			collection.add((Method)this.children);
			return collection;
		}
		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for(GraphMember item : list) {
				if(item instanceof Method) {
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
				for (Association assoc : getAssociation()) {
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
				for (Association assoc : getAssociation()) {
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
		super.withChildren(true, value);
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

	public boolean hasModifier(Modifier value) {
		if (value == null) {
			return true;
		}
		if (this.children == null) {
			return false;
		}
		if (this.children instanceof Modifier) {
			Modifier modifier = value;
			return modifier.getName().equals(modifier.getName());
		}
		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet items = (GraphSimpleSet) this.children; 
			for (GraphMember item : items) {
				if((item instanceof Modifier) == false) {
					continue;
				}
				Modifier modifier = value;
				if(modifier.getName().equals(modifier.getName())) {
					return true;
				}
			}
		}
		return false;
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