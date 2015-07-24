package de.uniks.networkparser.graph;

public class GraphEdge {
	public static final String PROPERTY_NODE = "node";
	public static final String PROPERTY_CARDINALITY = "cardinality";
	public static final String PROPERTY_PROPERTY = "property";
	private GraphCardinality cardinality;
	private GraphLabel property;
	private GraphLabel info;
	private GraphEdge other;
	private GraphEdgeTypes typ = GraphEdgeTypes.EDGE;
	private int count;
	private GraphSimpleSet<GraphNode> nodes = new GraphSimpleSet<GraphNode>(); 

	public GraphEdge() {

	}

	public GraphEdge(GraphNode node, GraphCardinality cardinality, String property) {
		with(node);
		with(cardinality);
		with(property);
	}

	public GraphCardinality getCardinality() {
		if(cardinality != null) {
			return cardinality;
		}
		if(nodes.size() > 1){
			return GraphCardinality.MANY;
		}
		return GraphCardinality.ONE;
	}
	
	public String getCardinalityValue() {
		return cardinality.getValue();
	}
	
	public String getCardinalityText() {
		return property + "<br>0.." + this.cardinality;
	}

	public String getProperty() {
		if(property != null) {
			return property.getId();
		}
		if(nodes.size() == 1) {
			GraphNode item = nodes.get(0);
			if(item instanceof GraphClazz) {
				String className = ((GraphClazz)item).getClassName(true);
				if(className != null) {
					return className.toLowerCase();
				}
			}
			
		}
		return null;
	}

	public GraphEdge with(String value) {
		this.property = GraphLabel.create(value);
		return this;
	}
	
	public GraphEdge withInfo(String value) {
		if(info != null) {
			this.info.withId(value);
			return this;
		}
		this.info = GraphLabel.create(value);
		return this;
	}
	
	public GraphEdge withInfo(GraphLabel value) {
		this.info = value;
		return this;
	}
	
	public GraphEdge withStyle(String value) {
		if(info != null) {
			this.info.withStyle(value);
			return this;
		}
		this.info = new GraphLabel().withStyle(value);
		return this;
	}
	
	public GraphLabel getInfo() {
		return info;
	}

	public GraphEdge with(GraphNode... values) {
		if (values == null) {
			return this;
		}
		for (GraphNode value : values) {
			if(this.nodes.add(value) ) {
				value.with(this);
			}
		}
		return this;
	}

	GraphEdge withOtherEdge(GraphEdge value) {
		this.other = value;
		return this;
	}

	public GraphEdge with(GraphEdge value) {
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

	public GraphEdge with(GraphCardinality cardinality) {
		this.cardinality = cardinality;
		return this;
	}
	
	public GraphEdge getOther() {
		return other;
	}
	
	public GraphClazz getOtherClazz() {
		if(other.getNode() instanceof GraphClazz) {
			return (GraphClazz) other.getNode();
		}
		return null;
	}

	public static GraphEdge create(GraphNode source, GraphNode target){
		GraphEdge edge = new GraphEdge().with(source);
		edge.with(new GraphEdge().with(target));
		return edge;
	}

	public GraphEdgeTypes getTyp() {
		return typ;
	}

	public GraphEdge withTyp(GraphEdgeTypes typ) {
		this.typ = typ;
		return this;
	}

	public int getCount() {
		return count;
	}

	public GraphEdge withCount(int count) {
		this.count = count;
		return this;
	}
	
	public GraphNode getNode() {
		if(nodes.size()>0) {
			return nodes.get(0);
		}
		return null;
	}
	
	public GraphSimpleSet<GraphNode> getNodes() {
		return nodes;
	}

	public void addCounter() {
		this.count++;
	}
	
	@Override
	public String toString() {
		return getIds()+"-"+getOther().getIds();
	}
	public String getIds() {
		StringBuilder sb=new StringBuilder();
		if(nodes.size()>1) {
			sb.append("[");
			sb.append(nodes.get(0).getId());
			for(int i=1;i<nodes.size();i++) {
				sb.append(","+nodes.get(1).getId());
			}
			sb.append("]");
		}else if(nodes.size()>0) {
			sb.append(nodes.get(0).getId());
		}else{
			sb.append("[]");
		}
		
		return sb.toString();
	}

	public boolean contains(GraphNode key) {
		return nodes.contains(key);
	}
	
	public boolean containsOther(GraphNode key) {
		if(other != null) {
			return other.getNodes().contains(key);
		}
		return false;
	}

	public boolean containsAll(GraphEdge others, boolean both) {
		if(! nodes.containsAll(others.getNodes()) ) {
			return false;
		}
		if(getOther()!= null && both) {
			return getOther().containsAll(others.getOther(), false);
		}
		return true;
	}
}
