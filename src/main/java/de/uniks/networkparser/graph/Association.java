package de.uniks.networkparser.graph;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleSet;

public class Association extends GraphMember {
	public static final StringFilter<Association> NAME = new StringFilter<Association>(GraphMember.PROPERTY_NAME);

	public static final String PROPERTY_NODE = "node";
	public static final String PROPERTY_CARDINALITY = "cardinality";
	public static final String PROPERTY_PROPERTY = "property";
	public static final String PROPERTY_OTHER = "other";
	public static final String PROPERTY_CLAZZ = "clazz";
	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_ISEDGE="isEdge";
	public static final String PROPERTY_ISIMPLEMENTS = "isImplements";
	private int cardinality;
	// The Complete Edge Info
//	private GraphLabel info;
	private Association other;
	private AssociationTypes type = AssociationTypes.ASSOCIATION;
	
	public static final int ONE=1;
	public static final int MANY=42;

	Association() {
	}

	public Association(GraphEntity node) {
		this.parentNode = node;
	}
	@Override
	public Object getValue(String attribute) {
		int pos = attribute.indexOf('.');
		String attrName;
		if (pos > 0) {
			attrName = attribute.substring(0, pos);
		} else {
			attrName = attribute;
		}
		if (PROPERTY_OTHER.equals(attrName)) {
			if (pos > 0) {
				Association other = this.getOther();
				return other.getValue(attribute.substring(pos + 1));
			}
			return this.getOther();
		}
		if (PROPERTY_CARDINALITY.equals(attrName)) {
			return this.getCardinality();
		}
		if (PROPERTY_CLAZZ.equals(attrName)) {
			if (pos > 0) {
				Clazz clazz = this.getClazz();
				return clazz.getValue(attribute.substring(pos + 1));
			}
			return this.getClazz();
		}
		if (PROPERTY_TYPE.equalsIgnoreCase(attrName)) {
			return this.getType();
		}
		if (PROPERTY_ISEDGE.equalsIgnoreCase(attrName)) {
			return AssociationTypes.isEdge(getType());
		}
		if (PROPERTY_ISIMPLEMENTS.equalsIgnoreCase(attrName)) {
			return AssociationTypes.isImplements(getType());
		}

		return super.getValue(attribute);
	}

	public boolean isSelfAssoc() {
		GraphSimpleSet collection = getParents();
		if(collection.size()>0) {
			GraphSimpleSet otherParents = getOther().getParents();
			for(GraphMember parent : collection) {
				if(parent instanceof Clazz) {
					if(otherParents.contains(parent)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public int getCardinality() {
		if(cardinality > 0) {
			return cardinality;
		}
		if(children != null) {
			GraphSimpleSet collection = getParents();
			int count=0;
			for(GraphMember item : collection) {
				if(item instanceof Clazz) {
					count++;
					if(count>1) {
						return MANY;
					}
				}
			}
		}
		return ONE;
	}

	@Override
	public String getName() {
		if(name != null) {
			return name;
		}
		if(parentNode != null) {
			GraphSimpleSet collection = getParents();
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
	protected String name() {
		return name;
	}

	/* Override the Default Method for setting Parent Node
	 * The Association can hav many Clazz-Instance as Parents (Objectdiagram) and a ClazzModel
	 * @see de.uniks.networkparser.graph.GraphMember#setParentNode(de.uniks.networkparser.graph.GraphMember)
	 */
	@Override
	protected boolean setParentNode(GraphMember value) {
		// Do Nothing
		if (value == this.parentNode ) {
			return false;
		}
		if(this.parentNode == null) {
			this.parentNode = value;
			if(value != null) {
				((GraphMember)value).withChildren(this);
			}
			return true;
		}
		GraphSimpleSet list;
		if( this.parentNode instanceof GraphSimpleSet) {
			list = (GraphSimpleSet) this.parentNode;
		}else {
			list = new GraphSimpleSet();
			list.with((GraphMember) this.parentNode);
			this.parentNode = list;
		}
		if(list.add(value)) {
			if(value != null) {
				value.withChildren(this);
			}
		}
		return true;
	}

	Association withoutParent(GraphMember member) {
		if(parentNode == null) {
			return this;
		}
		if(this.parentNode == member) {
			this.parentNode = null;
		}
		if( this.parentNode instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.parentNode;
			list.remove(member);
		}
		return this;
	}

	@Override
	protected SimpleSet<GraphEntity> getNodes() {
		SimpleSet<GraphEntity> collection = new SimpleSet<GraphEntity>();
		if(this.parentNode == null) {
			return collection;
		}
		if(this.parentNode instanceof GraphEntity) {
			collection.add((GraphEntity)this.parentNode);
			return collection;
		}
		if(this.parentNode instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.parentNode;
			for(GraphMember item : list) {
				if(item instanceof GraphEntity) {
					collection.add((GraphEntity)item);
				}
			}
		}
		return collection;
	}

	protected GraphSimpleSet getParents() {
		GraphSimpleSet parents = new GraphSimpleSet();
		if(this.parentNode == null) {
			return parents;
		}
		if( this.parentNode instanceof GraphMember) {
			parents.with((GraphMember)this.parentNode);
			return parents;
		}
		parents.withList((GraphSimpleSet)this.parentNode);
		return parents;
	}

	public Association with(GraphLabel label) {
		super.withChildren(label);
		return this;
	}

	public GraphLabel getInfo() {
		if (children == null && this.other.getChildren() == null) {
			return null;
		}
		for (GraphMember child : getChildren()) {
			if (child instanceof GraphLabel) {
				return (GraphLabel) child;
			}
		}
		for (GraphMember child : this.other.getChildren()) {
			if (child instanceof GraphLabel) {
				return (GraphLabel) child;
			}
		}
		return null;
	}

	public Association with(GraphEntity value) {
		super.setParentNode(value);
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

	public Association with(int cardinality) {
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

	protected String getSeperator() {
		if (getType() == AssociationTypes.GENERALISATION) {
			return "-^";
		}
		if (getOtherType() == AssociationTypes.GENERALISATION) {
			return "^-";
		}
		if (getType() == AssociationTypes.IMPLEMENTS) {
			return "-.-^";
		}
		if (getOtherType() == AssociationTypes.IMPLEMENTS) {
			return "^-.-";
		}
		if (getType() == AssociationTypes.EDGE) {
			return "->";
		}
		if (getOtherType() == AssociationTypes.EDGE) {
			return "<-";
		}
		return "-";
	}

	protected String getCardinalityText() {
		return name + "<br>0.." + this.cardinality;
	}

	public Association with(AssociationTypes typ) {
		this.type = typ;
		return this;
	}

	public Clazz getOtherClazz() {
		if(other != null) {
			return other.getClazz();
		}
		return null;
	}
	protected AssociationTypes getOtherType() {
		if(other != null ) {
			return other.getType();
		}
		return null;
	}

	@Override
	public Clazz getClazz() {
		GraphSimpleSet collection = getParents();
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

	protected void addIds(CharacterBuffer sb) {
		if (parentNode == null) {
			sb.with("[]");
		} else if (parentNode instanceof GraphMember) {
			sb.with(((GraphMember) parentNode).getName());
		} else if (parentNode instanceof GraphSimpleSet) {
			GraphSimpleSet collection = (GraphSimpleSet) parentNode;
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

	protected boolean contains(GraphEntity key, boolean self, boolean other) {
		boolean contains = false;
		if (self) {
			if(parentNode == null) {
				contains = false;
			} else if(parentNode instanceof GraphMember) {
				contains = parentNode == key;
			} else if(parentNode instanceof GraphSimpleSet) {
				contains = ((GraphSimpleSet)parentNode).contains(key);
			}
		}
		if (other && contains == false) {
			contains = this.other.contains(key, true, false);
		}
		return contains;
	}


	protected boolean containsAll(Association others, boolean both) {
		if(parentNode == null) {
			return false;
		}
		GraphSimpleSet otherChildren = others.getParents();
		if(parentNode instanceof GraphMember) {
			if(otherChildren.size()!=1 || parentNode != otherChildren.first()) {
				return false;
			}
		}
		if(parentNode instanceof GraphSimpleSet ) {
			if (((GraphSimpleSet)parentNode).containsAll(otherChildren) ) {
				return false;
			}
		}

		if(getOther()!= null && both) {
			return getOther().containsAll(others.getOther(), false);
		}
		return true;
	}

	public Association with(Annotation value) {
		withAnnotation(value);
		return this;
	}

	@Override
	public Association without(GraphMember... values) {
		super.without(values);
		return this;
	}
}
