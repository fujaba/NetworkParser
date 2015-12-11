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
import de.uniks.networkparser.list.SimpleSet;

public abstract class GraphEntity extends GraphMember {
	protected SimpleSet<Association> associations = new SimpleSet<Association>();
	private boolean external;
	private String id;
	
	public String getName(boolean shortName) {
		if (this.name == null) {
			return null;
		}
		if (!shortName || name.lastIndexOf(".") < 0) {
			return name;
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
	
	GraphMember getByObject(String clazz, boolean fullName) {
		if(clazz == null){
			return null;
		}
		String sub = clazz;
		if(clazz.lastIndexOf(".")>=0) {
			sub = clazz.substring(clazz.lastIndexOf(".")+1);
		}
		String id;
		for(GraphMember item : children) {
			id = item.getFullId();
			if(clazz.equalsIgnoreCase(id) || sub.equalsIgnoreCase(id)){
				return item;
			}
		}
		if(fullName || clazz.lastIndexOf(".") < 0) {
			return null;
		}
		sub = "."+clazz.substring(clazz.lastIndexOf(".")+1);
		for(GraphMember item : children) {
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

	public boolean setExternal(boolean value) {
		if (this.external != value) {
			this.external = value;
			return true;
		}
		return false;
	}

	public GraphEntity withExternal(boolean value) {
		setExternal(value);
		return this;
	}

	protected GraphEntity with(Association... values) {
		if (values != null) {
			for (Association value : values) {
				if(value != null) {
					this.associations.add(value);
					value.with(this);
				}
			}
		}
		return this;
	}
	
	public Annotation getAnnotations() {
		if(this.children == null) {
			return null;
		}
		for(GraphMember item : this.children) {
			if(item instanceof Annotation) {
				return (Annotation) item;
			}
		}
		return null;
	}

	public GraphEntity with(Annotation value) {
		// Remove Old GraphAnnotation
		if(this.children != null) {
			for(int i=this.children.size();i>=0;i--) {
				if(this.children.get(i) instanceof Annotation) {
					this.children.remove(i);
				}
			}
		}
		super.with(value);
		return this;
	}
}
