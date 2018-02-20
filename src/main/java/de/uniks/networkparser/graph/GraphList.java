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
import de.uniks.networkparser.converter.YUMLConverter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleMapEntry;
import de.uniks.networkparser.list.SimpleSet;

public class GraphList extends GraphModel {
	private String typ = GraphTokener.CLASS;
	private String style;
	private GraphOptions options;

	@Override
	public String toString() {
		return toString(new YUMLConverter());
	}
	public String toString(boolean removePackage) {
		YUMLConverter converter = new YUMLConverter();
		converter.defaultShowPackage = removePackage;
		return toString(converter);
	}

	public String getType() {
		return typ;
	}

	public GraphList withType(String typ) {
		this.typ = typ;
		return this;
	}

	public GraphList withEdge(String sourceName, String targetName) {
		Association edge = new Association().with(sourceName).with(new Association().with(targetName));
		super.with(edge);
		return this;
	}

	public void initSubLinks() {
		for (GraphEntity node : getNodes()) {
			if (node instanceof Clazz == false) {
				continue;
			}
			Clazz graphClazz = (Clazz) node;
			SimpleSet<Association> childEdges = graphClazz.getAssociations();
			for (Association edge : childEdges) {
				SimpleSet<Association> associations = getAssociations();
				if (associations.contains(edge) == false && associations.contains(edge.getOther()) == false) {
					super.with(edge);
				}
			}
		}
	}

	public Clazz with(Clazz value) {
		if (value != null) {
			if (value.getName() == null) {
				value.with(value.getName(false));
			}
			super.withChildren(value);
		}
		return value;
	}

	public GraphList with(GraphList... values) {
		super.withChildren(values);
		return this;
	}

	public GraphPattern with(GraphPattern value) {
		super.withChildren(value);
		return value;
	}

	public GraphList withNode(GraphEntity... value) {
		super.withChildren(value);
		return this;
	}

	public GraphList withNode(GraphNode... value) {
		super.withChildren(value);
		return this;
	}

	public GraphOptions getOptions() {
		return options;
	}

	public GraphList withOptions(GraphOptions options) {
		this.options = options;
		return this;
	}

	public String getStyle() {
		return style;
	}

	public GraphList withStyle(String style) {
		this.style = style;
		return this;
	}

	public Clazz getNode(String id) {
		if (id == null) {
			return null;
		}
		for (GraphMember item : this.getChildren()) {
			if (item instanceof Clazz && id.equalsIgnoreCase(item.getFullId())) {
				return (Clazz) item;
			}
		}
		return null;
	}

	public SimpleSet<GraphEntity> getNodes() {
		return super.getNodes();
	}

	public Association getEdge(GraphEntity node, String property) {
		for (Association edge : getAssociations()) {
			Association oEdge = edge.getOther();
			if (edge.getClazz() == node && property.equals(oEdge.getName())) {
				return edge;
			} else if (oEdge.getClazz() == node && property.equals(edge.getName())) {
				return oEdge;
			}
		}
		return null;
	}

	@Override
	public boolean add(Object... values) {
		if (values == null) {
			return false;
		}
		for (Object item : values) {
			if (item instanceof GraphMember) {
				super.withChildren((GraphMember) item);
			}
		}
		return true;
	}

	public Object getValue(Object key) {
		if (this.children == null) {
			return null;
		}
		if (this.children instanceof GraphMember) {
			if (this.children == key) {
				return this.children;
			}
			return null;
		}
		if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet collection = (GraphSimpleSet) this.children;
			return collection.getValue(key);
		}
		return null;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		if (keyValue) {
			return new SimpleMapEntry<String, GraphNode>();
		}
		return new GraphList();
	}

	@Override
	public GraphList with(String name) {
		super.with(name);
		return this;
	}
}
