package de.uniks.networkparser.graph;
import java.util.Iterator;

import de.uniks.networkparser.graph.util.AssociationSet;
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
import de.uniks.networkparser.list.SimpleSet;

public abstract class GraphEntity extends GraphMember {
	protected Object associations;
	private boolean external;
	private String id;
	
	public String getName(boolean shortName) {
		if (this.name == null) {
			return null;
		}
		if (!shortName) {
			if (name.indexOf('.') < 0 && this.parentNode != null && this.parentNode.getName() != null) {
				return this.parentNode.getName() + "." + name.replace("$", ".");
			}
			return name.replace("$", ".");
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
	
	String getTyp(String typ, boolean shortName) {
		if (typ.equals(GraphIdMap.OBJECT)) {
			return getId();
		} else if (typ.equals(GraphIdMap.CLASS)) {
			return getName(shortName);
		}
		return "";
	}
	
	public AssociationSet getAssociations() {
	   AssociationSet allEdges = new AssociationSet();
		if (associations == null ) {
			return allEdges;
		}
		if(associations instanceof Association) {
			allEdges.add((Association) this.associations);
		}else if(associations instanceof GraphSimpleSet) {
			GraphSimpleSet collection = (GraphSimpleSet) this.associations;
			for (GraphMember item : collection) {
				if(item instanceof Association) {
					Association assoc = (Association) item;
					if(AssociationTypes.isEdge(assoc.getType().getValue())) {
						allEdges.add(assoc);
					}
				}
			}
		}
		return allEdges;
	}
	
	GraphMember getByObject(String clazz, boolean fullName) {
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
		sub = "."+clazz.substring(clazz.lastIndexOf(".")+1);
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
			for (Association value : values) {
				addAssoc(value);
			}
		}
		return this;
	}
	
	public GraphEntity without(Association... values) {
		if (values == null || this.associations == null) {
			return this;
		}
		if(this.associations instanceof GraphMember) {
			for (GraphMember value : values) {
				if(this.associations == value) {
					this.associations = null;
				}
			}
			return this;
		}
		GraphSimpleSet collection = (GraphSimpleSet) this.associations;
		for (GraphMember value : values) {
			if(value != null) {
				collection.remove(value);
			}
		}
		return this;
	}
	
	boolean addAssoc(Association assoc) {
		// Do Nothing
		if (assoc == null || (this.associations == assoc)) {
			return false;
		}
		boolean add=true;

		if(assoc.getOther() != null && this.associations != null) {
			for (Iterator<Association> i = this.getAssociations().iterator(); i.hasNext();) {
				Association item = i.next();
				if(has(item, assoc.getOther()) && has(item.getOther(), assoc)) {
					if(item.isSame(assoc.getOther()) && item.getOther().isSame(assoc)) {
						if(GraphUtil.isUndirectional(item)) {
							item.getOther().with(AssociationTypes.ASSOCIATION);
							item.with(AssociationTypes.ASSOCIATION);
						}
						add=false;
						break;
					}else if (item.containsAll(assoc.getOther(), false) && item.getOther().name() == null
							&& assoc.name() != null) {
						item.getOther().with(assoc.getCardinality());
						item.getOther().with(assoc.getName());
						item.getOther().with(AssociationTypes.ASSOCIATION);
						item.with(AssociationTypes.ASSOCIATION);
						add=false;
						break;
					}
				}
			}
		}
		if(add) {
			if(this.associations == null) {
				this.associations = assoc;
			} else {
				GraphSimpleSet list;
				if( this.associations  instanceof GraphSimpleSet) {
					list = (GraphSimpleSet) this.associations;
					add = list.add(assoc);
				}else {
					list = new GraphSimpleSet().withAllowDuplicate(true);
					list.with((GraphMember) this.associations);
					this.associations = list;
					add = list.add(assoc);
				}
			}
		}
		return add;
	}
		
	private boolean has(Association o1, Association o2) {
		return (o1.getClazz() == o2.getClazz());
	}
	
	public Annotation getAnnotation() {
		return super.getAnnotation();
	}

	public GraphEntity with(Annotation value) {
		withAnnotaion(value);
		return this;
	}
}
