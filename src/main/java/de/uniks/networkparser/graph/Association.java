package de.uniks.networkparser.graph;

public class Association extends GraphMember {
	public static final String PROPERTY_NODE = "node";
	public static final String PROPERTY_CARDINALITY = "cardinality";
	public static final String PROPERTY_PROPERTY = "property";
	private Cardinality cardinality;
	// The Complete Edge Info
//	private GraphLabel info;
	private Association other;
	private AssociationTypes typ = AssociationTypes.ASSOCIATION;

	public Association with(Clazz node, Cardinality cardinality, String property) {
		with(node);
		with(cardinality);
		with(property);
		return this;
	}

	public Cardinality getCardinality() {
		if(cardinality != null) {
			return cardinality;
		}
		if(children.size() > 1){
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
		if(children.size() == 1) {
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
		for (GraphEntity value : values) {
			if(this.children.add(value) ) {
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
		Association edge = new Association().with(source);
		edge.with(new Association().with(target));
		return edge;
	}

	public AssociationTypes getTyp() {
		return typ;
	}
	
	String getSeperator() {
		if (getTyp() == AssociationTypes.CHILD) {
			return "-|>";
		}
		if (getOther().getTyp() == AssociationTypes.EDGE) {
			return "->";
		}
		return "-";
	}

	public Association with(AssociationTypes typ) {
		this.typ = typ;
		return this;
	}
	
	public Clazz getOtherClazz() {
		if(other.getClazz() instanceof Clazz) {
			return other.getClazz();
		}
		return null;
	}

	public Clazz getClazz() {
		if(children.size()>0) {
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
		for(GraphMember item : children) {
			if(item instanceof GraphEntity) {
				values.add((GraphEntity)item);
			}
		}
		return values;
	}

	public String getIds() {
		StringBuilder sb=new StringBuilder();
		if(children.size()>1) {
			sb.append("[");
			sb.append(children.get(0).getName());
			for(int i=1;i<children.size();i++) {
				sb.append(","+children.get(1).getName());
			}
			sb.append("]");
		}else if(children.size()>0) {
			sb.append(children.get(0).getName());
		}else{
			sb.append("[]");
		}
		
		return sb.toString();
	}

	public boolean contains(GraphEntity key, boolean self, boolean other) {
		boolean contains = false;
		if (self) {
			contains = children.contains(key);
		}
		if (other && contains == false) {
			contains = children.contains(key);
		}
		return contains;
	}
	
	public boolean containsAll(Association others, boolean both) {
		if(! children.containsAll(others.getChildren()) ) {
			return false;
		}
		if(getOther()!= null && both) {
			return getOther().containsAll(others.getOther(), false);
		}
		return true;
	}
}
