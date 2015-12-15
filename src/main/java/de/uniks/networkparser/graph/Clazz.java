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
	@Override
	public Clazz with(String name) {
		super.with(name);
		return this;
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

	public Clazz with(Association... values) {
		super.with(values);
		return this;
	}
	
	public Clazz with(Modifier... values) {
		super.with(values);
		return this;
	}
	
	public Clazz with(Attribute... values) {
		super.with(values);
		return this;
	}

	public Clazz with(Method... values) {
		super.with(values);
		return this;
	}
	
	public Clazz with(Annotation value) {
		super.with(value);
		return this;
	}
	
	public Clazz with(GraphImage... values) {
		super.with(values);
		return this;
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
	
	public Clazz without(GraphLiteral... values) {
		super.without(values);
		return this;
	}

	/**
	 * ********************************************************************
	 * <pre>
	 *       %srcCardinality%       %tgtCardinality%
	 * Clazz --------------------------------------- %tgtClass%
	 *       %srcRoleName%             %tgtRoleName%
	 * </pre>
	 *
	 * create a Bidirectional Association 
	 * 
	 * @param tgtClass 				The target Clazz
	 * @param tgtRoleName 			The Targetrolename
	 * @param tgtCardinality		The Targetcardinality
	 * @param srcRoleName			The sourcerolename
	 * @param srcCardinality		The sourcecardinality
	 * @return The Clazz Instance
	 */
	public Clazz withBidirectional(Clazz tgtClass, String tgtRoleName, Cardinality tgtCardinality, String srcRoleName, Cardinality srcCardinality) {
		// Target
		Association assocTarget = new Association();
		assocTarget.with(tgtClass, tgtCardinality, tgtRoleName);

		// Source
		Association assocSource = new Association();
		assocSource.with(this, srcCardinality, srcRoleName);
		
		assocSource.with(assocTarget);

		this.with(assocSource);
		return this;
	}

	/**
	 * ********************************************************************
	 * <pre>
	 *                                 %tgtCardinality%
	 * Clazz ----------------------------------- %tgtClass%
	 *                                    %tgtRoleName%
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
		Association assocTarget = new Association();
		assocTarget.withTyp(GraphEdgeTypes.UNDIRECTIONAL);
		assocTarget.with(tgtClass, tgtCardinality, tgtRoleName);

		// Source
		Association assocSource = new Association();
		assocSource.withTyp(GraphEdgeTypes.EDGE);
		assocSource.with(assocTarget);

		this.with(assocSource);
		return this;
	}

	public Clazz getSuperClass() {
		if (associations == null) {
			return null;
		}
		for (Association assoc : associations) {
			if(!GraphEdgeTypes.GENERALISATION.equals(assoc.getTyp())) {
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
	 *         <pre>
	 *              one                       many
	 * Clazz ----------------------------------- Clazz
	 *              clazz                   Interfaces
	 *         </pre>
	 */
	public SimpleSet<Clazz> getInterfaces(boolean transitive) {
		SimpleSet<Clazz> interfaces = new SimpleSet<Clazz>();
		if (associations == null) {
			return interfaces;
		}
		for (Association assoc : associations) {
			Clazz clazz = assoc.getOtherClazz();
			if (GraphUtil.isInterface(clazz) == false) {
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
		if (this.associations == null) {
			this.associations = new SimpleSet<Association>();
		}
		for (Clazz item : values) {
			if (item != null) {
				boolean found=false;
				for (Association assoc : associations) {
					if(assoc instanceof GraphGeneralization) {
						if(assoc.contains(item, false, true)) {
							found = true;
							break;
						}
					}
				}
				if(found == false) {
					this.associations.add(new GraphGeneralization().with(this, item));
				}
			}
		}
		return this;
	}

	/**
	 * Get All SuperClazzes
	 * @param transitive Get all SuperClasses or direct SuperClasses
	 * @return all SuperClasses of a Clazz
	 *         <pre>
	 *              one                       many
	 * Clazz ----------------------------------- Clazz
	 *              clazz                   superClazzes
	 *         </pre>
	 */
	public SimpleSet<Clazz> getSuperClazzes(boolean transitive) {
		SimpleSet<Clazz> collection = getEdges(GraphEdgeTypes.GENERALISATION);
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
	 *         <pre>
	 *              one                       many
	 * Clazz ----------------------------------- Clazz
	 *              clazz                   kindClazzes
	 *         </pre>
	 */
	public SimpleSet<Clazz> getKidClazzes(boolean transitive) {
		SimpleSet<Clazz> kidClazzes = getEdges(GraphEdgeTypes.CHILD);
		if(!transitive) {
			return kidClazzes;
		}
		int size = kidClazzes.size();
		for(int i=0;i<size;i++) {
			kidClazzes.withList(kidClazzes.get(i).getKidClazzes(transitive));
		}
		return kidClazzes;
	}
	
	public SimpleSet<Association> getAssociation() {
		SimpleSet<Association> allEdges = new SimpleSet<Association>();
		if (associations == null ) {
			return allEdges;
		}
		for (Association assoc : associations) {
			if(GraphEdgeTypes.isEdge(assoc.getTyp().getValue())) {
				allEdges.add(assoc);
			}
		}
		return allEdges;
	}
	
	SimpleSet<Clazz> getEdges(GraphEdgeTypes typ) {
		SimpleSet<Clazz> kindClazzes = new SimpleSet<Clazz>();
		if (associations == null || typ == null) {
			return kindClazzes;
		}
		String typValue = typ.getValue(); 
		for (Association assoc : associations) {
			if(!typValue.equals(assoc.getTyp().getValue())) {
				continue;
			}
			Clazz clazz = assoc.getOtherClazz();
			if(GraphUtil.isInterface(clazz) == false) {
				kindClazzes.with(clazz);
			}
		}
		return kindClazzes;
	}

	public Clazz withKidClazzes(Clazz... values) {
		if (values == null) {
			return this;
		}
		if (this.associations == null) {
			this.associations = new SimpleSet<Association>();
		}
		for (Clazz item : values) {
			if (item != null) {
				boolean found=false;
				for (Association assoc : associations) {
					if(assoc instanceof GraphGeneralization) {
						if(assoc.contains(item, true, false)) {
							found = true;
							break;
						}
					}
				}
				if(found == false) {
					this.associations.add(new GraphGeneralization().with(item, this));
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
	 *         <pre>
	 *              one                       many
	 * GraphModel ----------------------------------- GraphAttributes
	 *              parent                   clazz
	 *         </pre>
	 */
	public SimpleSet<Attribute> getAttributes() {
		SimpleSet<Attribute> collection = new SimpleSet<Attribute>();
		if (children == null) {
			return collection;
		}
		for (GraphMember child : children) {
			if (child instanceof Attribute)  {
				collection.add((Attribute) child);
			}
		}
		return collection;
	}

	/**
	 * get All GraphMethods
	 * 
	 * @return all GraphMethods of a GraphNode
	 * 
	 *         <pre>
	 *              one                       many
	 * GraphModel ----------------------------------- GraphMethods
	 *              parent                   clazz
	 *         </pre>
	 */
	public SimpleSet<Method> getMethods() {
		SimpleSet<Method> collection = new SimpleSet<Method>();
		if (children == null) {
			return collection;
		}
		for (GraphMember child : children) {
			if (child instanceof Method)  {
				collection.add((Method) child);
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
				for (Association assoc : associations) {
					if(assoc instanceof GraphGeneralization) {
						if(assoc.contains(item, false, true)) {
							this.associations.remove(assoc);
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
				for (Association assoc : associations) {
					if(assoc instanceof GraphGeneralization) {
						if(assoc.contains(item, true, false)) {
							this.associations.remove(assoc);
							break;
						}
					}
				}
			}
		}
		return this;
	}

	public Clazz with(GraphImport... value) {
		super.with(value);
		return this;
	}

	public SimpleSet<GraphImport> getImports() {
		SimpleSet<GraphImport> collection = new SimpleSet<GraphImport>();
		if (children == null) {
			return collection;
		}
		for (GraphMember child : children) {
			if (child instanceof GraphImport)  {
				collection.add((GraphImport) child);
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
		for (GraphMember item : children) {
			if((item instanceof Modifier) == false) {
				continue;
			}
			Modifier modifier = value;
			if(modifier.getName().equals(modifier.getName())) {
				return true;
			}
		}
		return false;
	}

	public SimpleSet<Modifier> getModifiers() {
		SimpleSet<Modifier> collection = new SimpleSet<Modifier>();
		if (children == null ) {
			return collection;
		}
		for (GraphMember child : children) {
			if((child instanceof Modifier) == false) {
				continue;
			}
			collection.add((Modifier) child);
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
	public Attribute createAttribute(String name, Clazz type) {
		Attribute attribute = new Attribute(name, DataType.ref(type));
		with(attribute);
		return attribute;
	}
	public Clazz withAttribute(String name, Clazz type) {
		Attribute attribute = new Attribute(name, DataType.ref(type));
		with(attribute);
		return this;
	}
	
}