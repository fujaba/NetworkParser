package de.uniks.networkparser.graph;
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
import de.uniks.networkparser.graph.util.AssociationSet;
import de.uniks.networkparser.interfaces.Condition;

public abstract class GraphEntity extends GraphMember {
	private boolean external;
	private String id;

	public String getName(boolean shortName) {
		if (this.name == null) {
			return null;
		}
		if (!shortName) {
			if (name.indexOf('.') < 0 && this.parentNode != null) {
				String parentName = ((GraphMember)this.parentNode).getName();
				if(parentName != null) {
					return parentName + "." + name.replace("$", ".");
				}
			}
			return name.replace("$", ".");
		}
		if (name.endsWith("..."))
		{
		   String realName = name.substring(0, name.length()-3);
		   int pos = realName.lastIndexOf(".");
		   return name.substring(pos+1);
		}
		return name.substring(name.lastIndexOf(".") + 1);
	}

	public GraphEntity withId(String id) {
		this.id = id;
		return this;
	}

	public String getId() {
		return id;
	}

	protected String getTyp(String typ, boolean shortName) {
		if (typ.equals(GraphTokener.OBJECT)) {
			return getId();
		} else if (typ.equals(GraphTokener.CLASS)) {
			return getName(shortName);
		}
		return "";
	}

	/** get All Edges
	 * @param type Association types Edge for all Association for only Assocs
	 * @param otherType Other Association type
	 * @param filters Can Filter the List of Associations
	 * @return all Associations of a Clazz
	 *
	 *<pre>
	 * Clazz  --------------------- Associations
	 * one                          many
	 *</pre>
	 */
	AssociationSet getEdges(AssociationTypes type, Condition<?>... filters) {
		if (this.children == null ) {
			return AssociationSet.EMPTY_SET;
		}
		AssociationSet collection = new AssociationSet();
		if(this.children instanceof Association) {
			if(check((Association)this.children, filters)) {
				collection.add((Association)this.children);
			}
		}else if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for (GraphMember item : list) {
				if(item instanceof Association) {
					Association assoc = (Association) item;
					if(check(assoc, filters) ) {
						if(type==AssociationTypes.EDGE) {
							collection.add(assoc);
						}else if(type==AssociationTypes.ASSOCIATION) {
							if(AssociationTypes.isEdge(assoc.getType())) {
								collection.add(assoc);
							}
						}
					}
				}
			}
		}
		return collection;
	}

	protected GraphMember getByObject(String clazz, boolean fullName) {
		if(clazz == null || children == null){
			return null;
		}
		String sub = clazz;
		if(clazz.lastIndexOf(".")>=0) {
			sub = clazz.substring(clazz.lastIndexOf(".")+1);
		}
		String id;
		GraphSimpleSet collection = this.getChildren();
		for(GraphMember item : collection) {
			id = item.getFullId();
			if(clazz.equalsIgnoreCase(id) || sub.equalsIgnoreCase(id)){
				return item;
			}
		}
		if(fullName || clazz.lastIndexOf(".") < 0) {
			return null;
		}
		for(GraphMember item : collection) {
			if(item instanceof Clazz) {
				id = ((Clazz)item).getId();
			} else {
				id = item.getName();
			}
			if(id.endsWith(clazz)){
				return item;
			}
		}
		return null;
	}

	public boolean isExternal() {
		return this.external;
	}

	public GraphEntity withExternal(boolean value) {
		if (this.external != value) {
			this.external = value;
		}
		return this;
	}

	protected GraphEntity with(Association... values) {
		if (values != null) {
			boolean add;
			AssociationSet allAssoc = this.getEdges(AssociationTypes.ASSOCIATION);
 			for (Association assoc : values) {
				// Do Nothing
				if (assoc == null || assoc.getOther() == null) {
					continue;
				}
				add = true;
					
				// If Nessesarry to search
				// Assoc_Own - Otherclazz_Property

				Association assocOther = assoc.getOther();
				boolean mergeFlag = (assoc.getType()==AssociationTypes.ASSOCIATION && assocOther.getType() == AssociationTypes.EDGE) ||
						(assoc.getType()==AssociationTypes.EDGE && assocOther.getType() == AssociationTypes.ASSOCIATION);
				for(Association item : allAssoc) {
					if(item == assoc || item.getOther() == assoc) {
						// I Know the Assoc
						add = false;
						break;
					}
					// Implements new Search for Association Only Search for duplicate
					Association itemOther = item.getOther();
					String name = itemOther.name();
					
					if(name != null && name.equals(assocOther.name()) && itemOther.getClazz() == assocOther.getClazz()) {
						add = false;
						break;
					}
					// Check for Merge Association
					if(mergeFlag) {
						if(itemOther.getClazz() == assocOther.getClazz() && item.getClazz() == assoc.getClazz()) {
							add = false;
							if(assocOther.name() != null && assoc.name() == null) {
								if(itemOther.getType()==AssociationTypes.EDGE && item.getType()==AssociationTypes.ASSOCIATION) {
									itemOther.with(AssociationTypes.ASSOCIATION);
									itemOther.with(assocOther.getName());
									itemOther.with(assocOther.getCardinality());
									GraphMember attribute = itemOther.getClazz().getChildByName(assocOther.getName(), Attribute.class);
									if(attribute != null) {
										itemOther.getClazz().without(attribute);
									}
								}
								
							}else if(item.getType()==AssociationTypes.EDGE && itemOther.getType()==AssociationTypes.ASSOCIATION) {
								// Cool its Bidirectional but remove Attributes
								item.with(AssociationTypes.ASSOCIATION);
								item.with(assoc.getName());
								item.with(assoc.getCardinality());
								
								GraphMember attribute = item.getClazz().getChildByName(assoc.getName(), Attribute.class);
								if(attribute != null) {
									item.getClazz().without(attribute);
								}
							}
							break;
						}
					}
				}
				if(add) {
					// ADD TO PARENT MAY BE LIST
					if(this.parentNode!= null) {
						if(this.parentNode instanceof GraphModel) {
							((GraphModel)this.parentNode).with(assoc);
						}
					}
					if(this.children == null) {
						this.children = assoc;
					} else {
						GraphSimpleSet list;
						if( this.children instanceof GraphSimpleSet) {
							list = (GraphSimpleSet) this.children;
							list.add(assoc);
						}else {
							list = new GraphSimpleSet();
							list.with((GraphMember) this.children);
							this.children = list;
							list.add(assoc);
						}
					}
				}
			}
		}
		return this;
	}

	public GraphEntity without(Association... values) {
		super.without(values);
		return this;
	}
	
	public GraphMember getChildByName(String name, Class<?> subClass) {
		if(this.children == null) {
			return null;
		}
		GraphSimpleSet children = this.getChildren();
		String itemName;
		for(GraphMember item : children) {
			if(item instanceof Association) {
				Association assoc = (Association) item;
				itemName = assoc.getOther().name();
			} else {
				itemName = item.getName();
			}
			if(itemName != null && itemName.equals(name)) {
				if(subClass != null && subClass == item.getClass()) {
					return item;
				}
			}
		}
		return null;
	}

	/** get all Associations
    * @param filters Can Filter the List of Attributes
    * @return all Attributes of a Clazz
    *
    *<pre>
    * Clazz  --------------------- Association
    * one                          many
    *</pre>
    */
	public AssociationSet getAssociations(Condition<?>... filters) {
		return getEdges(AssociationTypes.EDGE, filters);
	}
	public Annotation getAnnotation() {
		return super.getAnnotation();
	}

	public GraphEntity with(Annotation value) {
		withAnnotaion(value);
		return this;
	}
}
