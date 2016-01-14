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
	
	Association(GraphEntity node) {
		with(node);
	}
	
	public Association(Clazz node, Cardinality cardinality) {
		with(node);
		with(cardinality);
	}

	public Cardinality getCardinality() {
		if(cardinality != null) {
			return cardinality;
		}
		if(children != null && children.size() > 1){
			return Cardinality.MANY;
		}
		return Cardinality.ONE;
	}
	
	public String getCardinalityText() {
		return name + "<br>0.." + this.cardinality;
	}

	@Override
	public String getName() {
		if(name != null) {
			return name;
		}
		if(children != null && children.size() == 1) {
			GraphMember item = children.get(0);
			if(item instanceof Clazz) {
				String className = ((Clazz)item).getName(true);
				if(className != null) {
					return className.toLowerCase();
				}
			}
			
		}
		return null;
	}

	
	public Association with(GraphLabel label) {
		super.with(label);
		return this;
	}
	
	public GraphLabel getInfo() {
		if (children == null && this.other.getChildren() == null) {
			return null;
		}
		for (GraphMember child : children) {
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
		if (values == null) {
			return this;
		}
		if(values.length==1) {
			if(this.parentNode == null) {
				this.parentNode = values[0];
				values[0].with(this);
				return this;
			}else if(this.parentNode == values[0]) {
				return this;
			}
		}
		for (GraphEntity value : values) {
			if(getChildren().add(value) ) {
				value.with(this);
			}
		}
		return this;
	}
	
	public Association with(String name) {
		super.with(name);
		return this;
	}

	Association withOtherEdge(Association value) {
		this.other = value;
		return this;
	}

	public Association with(Association value) {
		if (this.getOther() == value) {
			return this;
		}
		if(this.other != null) {
			this.other.withOtherEdge(null);
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

	public AssociationTypes getTyp() {
		return type;
	}
	
	String getSeperator() {
		if (getTyp() == AssociationTypes.GENERALISATION) {
			return "-|>";
		}
		if (getOther().getTyp() == AssociationTypes.GENERALISATION) {
			return "<|-";
		}
		if (getTyp() == AssociationTypes.EDGE) {
			return "->";
		}
		if (getOther().getTyp() == AssociationTypes.EDGE) {
			return "<-";
		}
		return "-";
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
			return other.getTyp();
		}
		return null;
	}

	public Clazz getClazz() {
		if(parentNode instanceof Clazz) {
			return (Clazz) parentNode;
		}
		if(getChildren().size()>0) {
			GraphMember item = children.get(0);
			if(item instanceof Clazz) {
				return (Clazz)item;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getIds()+getSeperator()+getOther().getIds();
	}
	
	GraphSimpleSet<GraphEntity> getNodes() {
		GraphSimpleSet<GraphEntity> values = new GraphSimpleSet<GraphEntity>(); 
		for(GraphMember item : getChildren()) {
			if(item instanceof GraphEntity) {
				values.add((GraphEntity)item);
			}
		}
		return values;
	}

	String getIds() {
		StringBuilder sb=new StringBuilder();
		if(parentNode != null){
			sb.append(parentNode.getName());
		} else if(children == null){
			sb.append("[]");
		}else if(children.size()>1) {
			sb.append("[");
			sb.append(children.get(0).getName());
			for(int i=1;i<children.size();i++) {
				sb.append(","+children.get(1).getName());
			}
			sb.append("]");
		}else if(children.size()>0) {
			sb.append(children.get(0).getName());
		}
		return sb.toString();
	}

	boolean contains(GraphEntity key, boolean self, boolean other) {
		boolean contains = false;
		if (self) {
			if(children == null) {
				contains = this.parentNode == key;
			} else {
				contains = children.contains(key);
			}
		}
		if (other && contains == false) {
			contains = this.other.contains(key, true, false);
		}
		return contains;
	}
	
	boolean containsAll(Association others, boolean both) {
		if(! children.containsAll(others.getChildren()) ) {
			return false;
		}
		if(getOther()!= null && both) {
			return getOther().containsAll(others.getOther(), false);
		}
		return true;
	}
}
