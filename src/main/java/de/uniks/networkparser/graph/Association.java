package de.uniks.networkparser.graph;

import de.uniks.networkparser.buffer.CharacterBuffer;

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

public class Association extends GraphMember {
	public static final String PROPERTY_NODE = "node";
	public static final String PROPERTY_CARDINALITY = "cardinality";
	public static final String PROPERTY_PROPERTY = "property";
	private Cardinality cardinality;
	// The Complete Edge Info
//	private GraphLabel info;
	private Association other;
	private AssociationTypes type = AssociationTypes.ASSOCIATION;

	Association() {
	}
	
	public Association(GraphEntity node) {
		with(node);
	}
	
	public Cardinality getCardinality() {
		if(cardinality != null) {
			return cardinality;
		}
		if(children != null) {
			GraphSimpleSet collection = getChildren();
			int count=0;
			for(GraphMember item : collection) {
				if(item instanceof Clazz) {
					count++;
					if(count>1) {
						return Cardinality.MANY;
					}
				}
			}
		}
		return Cardinality.ONE;
	}
	
	@Override
	public String getName() {
		if(name != null) {
			return name;
		}
		if(children != null) {
			GraphSimpleSet collection = super.getChildren();
			if(collection.size() == 1) {
				GraphMember item = collection.first();
				if(item instanceof Clazz) {
					String className = ((Clazz)item).getName(true);
					if(className != null) {
						return className.toLowerCase();
					}
				}
				
			}
		}
		return null;
	}
	String name() {
		return name;
	}

	public Association with(GraphLabel label) {
		super.withChildren(true, label);
		return this;
	}
	
	public GraphLabel getInfo() {
		if (children == null && this.other.getChildren() == null) {
			return null;
		}
		for (GraphMember child : getChildren()) {
			if (child instanceof GraphLabel)  {
				return (GraphLabel) child;
			}
		}
		for (GraphMember child : this.other.getChildren()) {
			if (child instanceof GraphLabel)  {
				return (GraphLabel) child;
			}
		}
		return null;
	}

	public Association with(GraphEntity... values) {
		super.withChildren(false, values);
		return this;
	}
	
	public Association with(String name) {
		super.with(name);
		return this;
	}

	Association withOther(Association value) {
		this.other = value;
		return this;
	}

	public Association with(Association value) {
		if (this.getOther() == value) {
			return this;
		}
		if(this.other != null) {
			this.other.withOther(null);
		}
		this.other = value;
		getOther().with(this);
		return this;
	}

	public Association with(Cardinality cardinality) {
		this.cardinality = cardinality;
		return this;
	}
	
	public Association getOther() {
		return other;
	}
	
	public static Association create(GraphEntity source, GraphEntity target){
		Association edge = new Association(source);
		edge.with(new Association(target));
		return edge;
	}

	public AssociationTypes getType() {
		return type;
	}
	
	String getSeperator() {
		if (getType() == AssociationTypes.GENERALISATION) {
			return "-|>";
		}
		if (getOtherTyp() == AssociationTypes.GENERALISATION) {
			return "<|-";
		}
		if (getType() == AssociationTypes.EDGE) {
			return "->";
		}
		if (getOtherTyp() == AssociationTypes.EDGE) {
			return "<-";
		}
		return "-";
	}
	
	String getCardinalityText() {
		return name + "<br>0.." + this.cardinality;
	}

	public Association with(AssociationTypes typ) {
		this.type = typ;
		return this;
	}
	
	public Clazz getOtherClazz() {
		if(other != null && other.getClazz() instanceof Clazz) {
			return other.getClazz();
		}
		return null;
	}
	AssociationTypes getOtherTyp() {
		if(other != null ) {
			return other.getType();
		}
		return null;
	}

	public Clazz getClazz() {
		GraphSimpleSet collection =  getChildren();
		if(collection.size()>0) {
			GraphMember item = collection.get(0);
			if(item instanceof Clazz) {
				return (Clazz)item;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		CharacterBuffer charList = new CharacterBuffer();
		addIds(charList);
		charList.with(getSeperator());
		if(getOther() != null) {
			getOther().addIds(charList);
		}
		return charList.toString();
	}
	

	void addIds(CharacterBuffer sb) {
		if (children == null) {
			sb.with("[]");
		} else if (children instanceof GraphMember) {
			sb.with(((GraphMember) children).getName());
		} else if (children instanceof GraphSimpleSet) {
			GraphSimpleSet collection = (GraphSimpleSet) children;
			if (collection.size() < 1) {
				return;
			}
			if (collection.size() == 1) {
				sb.with(collection.get(0).getName());
				return;
			}
			sb.with("[");
			sb.with(collection.get(0).getName());
			for (int i = 1; i < collection.size(); i++) {
				sb.with("," + collection.get(1).getName());
			}
			sb.with("]");
		}
	}

	boolean contains(GraphEntity key, boolean self, boolean other) {
		boolean contains = false;
		if (self) {
			if(children == null) {
				contains = false;
			} else if(children instanceof GraphMember) {
				contains = children == key;
			} else if(children instanceof GraphSimpleSet) {
				contains = ((GraphSimpleSet)children).contains(key);
			}
		}
		if (other && contains == false) {
			contains = this.other.contains(key, true, false);
		}
		return contains;
	}
	
	boolean containsAll(Association others, boolean both) {
		GraphSimpleSet otherChildren = others.getChildren();
		if(children == null) {
			return false;
		}
		if(children instanceof GraphMember) {
			if(otherChildren.size()!=1 || children != otherChildren.first()) {
				return false;
			}
		}
		if(children instanceof GraphSimpleSet ) {
			if (((GraphSimpleSet)children).containsAll(otherChildren) ) {
				return false;
			}
		}
		
		if(getOther()!= null && both) {
			return getOther().containsAll(others.getOther(), false);
		}
		return true;
	}
	public Association with(Annotation value) {
		withAnnotaion(value);
		return this;
	}
	public Association without(Annotation value) {
		super.without(value);
		return this;
	}
	boolean isSame(Association other) {
		if(this.name == null ) {
			if(other.name() == null) {
				return true;
			}
			return false;
		}
		return this.name().equals(other.name());
	}
}
