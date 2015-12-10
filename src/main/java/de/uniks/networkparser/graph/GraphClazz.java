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

public class GraphClazz extends GraphEntity {
	private boolean interfaze = false;
	private boolean isEnum = false;
	private SimpleSet<String> imports = new SimpleSet<String>();

	@Override
	public GraphClazz with(String name) {
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
	public GraphClazz withExternal(boolean value) {
		super.withExternal(value);
		return this;
	}

	public GraphClazz with(GraphAssociation... values) {
		super.with(values);
		return this;
	}
	
	public GraphClazz with(GraphModifier... values) {
		super.with(values);
		return this;
	}
	
	public GraphClazz with(GraphAttribute... values) {
		super.with(values);
		return this;
	}

	public GraphClazz with(GraphMethod... values) {
		super.with(values);
		return this;
	}
	
	@Override
	public GraphClazz with(GraphAnnotation value) {
		super.with(value);
		return this;
	}
	
	public GraphClazz with(GraphImage... values) {
		super.with(values);
		return this;
	}
	
	public GraphClazz with(GraphLiteral... values) {
		super.with(values);
		return this;
	}

	public boolean isInterface() {
		return this.interfaze;
	}

	public boolean setInterface(boolean value) {
		if (this.interfaze != value) {
			this.interfaze = value;
			return true;
		}
		return false;
	}

	public GraphClazz withInterface(boolean value) {
		setInterface(value);
		return this;
	}
	
	/**
	 * create a Bidirectional Association 
	 * 
	 * @param tgtClass The target Clazz
	 * @param tgtRoleName The Targetrolename
	 * @param tgtCard	The Targetcardinality
	 * @param srcRoleName	The sourcerolename
	 * @param srcCard		The sourcecardinality
	 * @return The GraphCard Instance
	 */
	public GraphClazz withAssoc(GraphClazz tgtClass, String tgtRoleName, GraphCardinality tgtCard, String srcRoleName, GraphCardinality srcCard) {
		// Target
		GraphAssociation assocTarget = new GraphAssociation();
		assocTarget.with(tgtClass, tgtCard, tgtRoleName);

		// Source
		GraphAssociation assocSource = new GraphAssociation();
		assocSource.with(this, srcCard, srcRoleName);
		
		assocSource.with(assocTarget);

		this.with(assocSource);
		return this;
	}

	/**
	 * create a Undirectional Association
	 * @param tgtClass The target Clazz
	 * @param tgtRoleName The Targetrolename
	 * @param tgtCard	The Targetcardinality
	 * @return The GraphCard Instance
	 */
	public GraphClazz withAssoc(GraphClazz tgtClass, String tgtRoleName, GraphCardinality tgtCard) {
		// Target
		GraphAssociation assocTarget = new GraphAssociation();
		assocTarget.withTyp(GraphEdgeTypes.UNDIRECTIONAL);
		assocTarget.with(tgtClass, tgtCard, tgtRoleName);

		// Source
		GraphAssociation assocSource = new GraphAssociation();
		assocSource.withTyp(GraphEdgeTypes.EDGE);
		assocSource.with(assocTarget);

		this.with(assocSource);
		return this;
	}

	public GraphClazz getSuperClass() {
		if (associations == null) {
			return null;
		}
		for (GraphEdge assoc : associations) {
			if(!GraphEdgeTypes.GENERALISATION.equals(assoc.getTyp())) {
				continue;
			}
			GraphClazz otherClazz = assoc.getOtherClazz();
			if(!otherClazz.isInterface()) {
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
	public SimpleSet<GraphClazz> getInterfaces(boolean transitive) {
		SimpleSet<GraphClazz> interfaces = new SimpleSet<GraphClazz>();
		if (associations == null) {
			return interfaces;
		}
		for (GraphEdge assoc : associations) {
			GraphClazz clazz = assoc.getOtherClazz();
			if (!clazz.isInterface()) {
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

	public GraphClazz withSuperClazz(GraphClazz... values) {
		if (values == null) {
			return this;
		}
		if (this.associations == null) {
			this.associations = new SimpleSet<GraphEdge>();
		}
		for (GraphClazz item : values) {
			if (item != null) {
				boolean found=false;
				for (GraphEdge assoc : associations) {
					if(assoc instanceof GraphGeneralization) {
						if(assoc.containsOther(item)) {
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
	public SimpleSet<GraphClazz> getSuperClazzes(boolean transitive) {
		SimpleSet<GraphClazz> collection = getEdges(GraphEdgeTypes.GENERALISATION);
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
	public SimpleSet<GraphClazz> getKidClazzes(boolean transitive) {
		SimpleSet<GraphClazz> kidClazzes = getEdges(GraphEdgeTypes.CHILD);
		if(!transitive) {
			return kidClazzes;
		}
		int size = kidClazzes.size();
		for(int i=0;i<size;i++) {
			kidClazzes.withList(kidClazzes.get(i).getKidClazzes(transitive));
		}
		return kidClazzes;
	}
	
	public SimpleSet<GraphEdge> getAllEdges() {
		SimpleSet<GraphEdge> allEdges = new SimpleSet<GraphEdge>();
		if (associations == null ) {
			return allEdges;
		}
		for (GraphEdge assoc : associations) {
			if(GraphEdgeTypes.isEdge(assoc.getTyp().getValue())) {
				allEdges.add(assoc);
			}
		}
		return allEdges;
	}
	
	SimpleSet<GraphClazz> getEdges(GraphEdgeTypes typ) {
		SimpleSet<GraphClazz> kindClazzes = new SimpleSet<GraphClazz>();
		if (associations == null || typ == null) {
			return kindClazzes;
		}
		String typValue = typ.getValue(); 
		for (GraphEdge assoc : associations) {
			if(!typValue.equals(assoc.getTyp().getValue())) {
				continue;
			}
			GraphClazz clazz = assoc.getOtherClazz();
			if(!clazz.isInterface()) {
				kindClazzes.with(clazz);
			}
		}
		return kindClazzes;
	}

	public GraphClazz withKidClazzes(GraphClazz... values) {
		if (values == null) {
			return this;
		}
		if (this.associations == null) {
			this.associations = new SimpleSet<GraphEdge>();
		}
		for (GraphClazz item : values) {
			if (item != null) {
				boolean found=false;
				for (GraphEdge assoc : associations) {
					if(assoc instanceof GraphGeneralization) {
						if(assoc.contains(item)) {
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
	
	public GraphClazz withClassModel(GraphModel value) {
		setClassModel(value);
		return this;
	}

	public GraphClazz with(GraphModel value) {
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
	public SimpleSet<GraphAttribute> getAttributes() {
		SimpleSet<GraphAttribute> collection = new SimpleSet<GraphAttribute>();
		if (children == null) {
			return collection;
		}
		for (GraphMember child : children) {
			if (child instanceof GraphAttribute)  {
				collection.add((GraphAttribute) child);
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
	public SimpleSet<GraphMethod> getMethods() {
		SimpleSet<GraphMethod> collection = new SimpleSet<GraphMethod>();
		if (children == null) {
			return collection;
		}
		for (GraphMember child : children) {
			if (child instanceof GraphMethod)  {
				collection.add((GraphMethod) child);
			}
		}
		return collection;
	}

	public GraphClazz withEnum(boolean value) {
		this.isEnum = value;
		return this;
	}
	
	public boolean isEnum() {
		return isEnum;
	}
	
	public GraphClazz without(GraphAttribute... value) {
		without(value);
		return this;
	}
	
	public GraphClazz without(GraphMethod... value) {
		without(value);
		return this;
	}

	public GraphClazz withoutKidClazz(GraphClazz... values) {
		if (this.associations == null || values == null) {
			return this;
		}
		for (GraphClazz item : values) {
			if (item != null) {
				for (GraphEdge assoc : associations) {
					if(assoc instanceof GraphGeneralization) {
						if(assoc.containsOther(item)) {
							this.associations.remove(assoc);
							break;
						}
					}
				}
			}
		}
		return this;
	}

	public GraphClazz withoutSuperClazz(GraphClazz... values) {
		if (this.associations == null || values == null) {
			return this;
		}
		for (GraphClazz item : values) {
			if (item != null) {
				for (GraphEdge assoc : associations) {
					if(assoc instanceof GraphGeneralization) {
						if(assoc.contains(item)) {
							this.associations.remove(assoc);
							break;
						}
					}
				}
			}
		}
		return this;
	}

	public GraphClazz withImport(String value) {
		this.imports.add(value);
		return this;
	}

	public SimpleSet<String> getImports() {
		return imports;
	}

	public boolean hasModifier(GraphModifier value) {
		if (value == null) {
			return true;
		}
		if (this.children == null) {
			return false;
		}
		for (GraphMember item : children) {
			if((item instanceof GraphModifier) == false) {
				continue;
			}
			GraphModifier modifier = value;
			if(modifier.getName().equals(modifier.getName())) {
				return true;
			}
		}
		return false;
	}

	public SimpleSet<GraphModifier> getModifiers() {
		SimpleSet<GraphModifier> collection = new SimpleSet<GraphModifier>();
		if (children == null ) {
			return collection;
		}
		for (GraphMember child : children) {
			if((child instanceof GraphModifier) == false) {
				continue;
			}
			collection.add((GraphModifier) child);
		}
		return collection;
	}
	
	public GraphMethod createMethod(String name, GraphParameter... parameters) {
		GraphMethod method = new GraphMethod().with(name);
		method.with(parameters);
		method.setParent(this);
		return method;
	}

	public GraphAttribute createAttribute(String name, GraphType type) {
		GraphAttribute attribute = new GraphAttribute(name, type);
		with(attribute);
		return attribute;
	}	
}