package de.uniks.networkparser.graph;

import java.util.Collection;

import de.uniks.networkparser.buffer.CharacterBuffer;
/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.TemplateItem;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public abstract class GraphMember implements TemplateItem {
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_CLASSNAME = "className";
	public static final String PROPERTY_PARENT = "parent";
	public static final String PROPERTY_CHILD = "child";
	public static final String PROPERTY_CHILDTRANSITIVE = "childtransitive";
	public static final String PROPERTY_LITERAL = "literal";
	public static final String PROPERTY_VISIBILITY = "visibility";
	public static final String PROPERTY_MODIFIERS = "modifiers";
	public static final String PROPERTY_THIS = "this";
	public static final String PROPERTY_PATH = "path";
	public static final String PROPERTY_ANNOTATION = "annotation";
	public static final String PROPERTY_FILETYPE = "filetype";

	protected String name;
	protected Object children;
	protected Object parentNode;
	private ObjectCondition role;
	protected boolean isGenerate;

	GraphMember withRole(ObjectCondition condition) {
		this.role = condition;
		return this;
	}

	ObjectCondition getRole() {
		return role;
	}

	public Object getValue(String attribute) {
		if (attribute == null) {
			return null;
		}
		if (PROPERTY_PATH.equalsIgnoreCase(attribute)) {
			return getName().replaceAll("\\.", "/");
		}
		if (PROPERTY_VISIBILITY.equalsIgnoreCase(attribute)) {
			Modifier modifier = this.getModifier();
			if (modifier == null) {
				return Modifier.PRIVATE.getName();
			}
			return modifier.getName();
		}
		if (PROPERTY_MODIFIERS.equalsIgnoreCase(attribute)) {
			CharacterBuffer buffer = new CharacterBuffer();
			Modifier modifier = this.getModifier();
			if (modifier != null) {
				modifier = modifier.getModifier();
				while (modifier != null) {
					buffer.with(modifier.getName());
					modifier = modifier.getModifier();
					if (modifier != null) {
						buffer.with(' ');
					}
				}
			}
			return buffer.toString();
		}
		if (PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return this.name;
		}
		if (PROPERTY_CLASSNAME.equalsIgnoreCase(attribute)) {
			return this.getClass().getName();
		}
		int pos = attribute.indexOf('.');
		String attrName;
		if (pos > 0) {
			attrName = attribute.substring(0, pos);
		} else {
			attrName = attribute;
		}
		if (PROPERTY_PARENT.equalsIgnoreCase(attrName)) {
			if (pos > 0) {
				if (parentNode instanceof GraphMember) {
					GraphMember item = (GraphMember) this.getParent();
					return item.getValue(attribute.substring(pos + 1));
				}
				return null;
			}
			return this.parentNode;
		}
		if (PROPERTY_CHILD.equalsIgnoreCase(attrName)) {
			if (pos > 0) {
				GraphSimpleSet item = this.getChildren();
				return item.getValue(attribute.substring(pos + 1));
			}
			return this.children;
		}
		if (PROPERTY_CHILDTRANSITIVE.equalsIgnoreCase(attrName)) {
			GraphSimpleSet children = new GraphSimpleSet();
			GraphSimpleSet items = this.getChildren();
			for (GraphMember item : items) {
				if (item instanceof Association) {
					Association assoc = (Association) item;
					if (assoc.getType() == AssociationTypes.GENERALISATION) {
						/* Add all SuperAttributes */
						children.withList((Collection<?>) assoc.getOtherClazz().getValue(PROPERTY_CHILDTRANSITIVE));
					} else if (assoc.getOtherType() == AssociationTypes.GENERALISATION) {
						/* IGNORE */
					} else {
						children.add(item);
					}
				} else {
					children.add(item);
				}
			}
			if (pos > 0) {
				return children.getValue(attribute.substring(pos + 1));
			}
			return children;
		}

		if (PROPERTY_LITERAL.equalsIgnoreCase(attrName)) {
			GraphSimpleSet items = this.getChildren();
			GraphSimpleSet literals = new GraphSimpleSet();
			for (GraphMember child : items) {
				if (child instanceof Literal) {
					literals.add(child);
				}
			}
			if (pos > 0) {
				return literals.getValue(attribute.substring(pos + 1));
			}
			return literals;
		}
		if (PROPERTY_THIS.equalsIgnoreCase(attrName)) {
			/* Check if Static or not */
			Modifier modifier = this.getModifier();
			if (modifier != null && modifier.has(Modifier.STATIC)) {
				return getValue(PROPERTY_PARENT);
			}
			return PROPERTY_THIS;
		}
		if (PROPERTY_ANNOTATION.equalsIgnoreCase(attrName)) {
			Annotation annotation = getAnnotation();
			if (annotation != null) {
				return annotation;
			}
			return "";
		}
		if (PROPERTY_FILETYPE.equalsIgnoreCase(attrName)) {
			return getClass().getSimpleName().toLowerCase();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected boolean check(GraphMember element, Condition<?>... filters) {
		if (filters == null) {
			return element != null;
		}
		boolean result = true;
		for (Condition<?> item : filters) {
			Condition<Object> filter = (Condition<Object>) item;
			if (filter != null && result) {
				result = filter.update(element);
			}
		}
		return result;
	}

	public Clazz getClazz() {
		if (this instanceof Clazz) {
			return (Clazz) this;
		}
		if (parentNode instanceof Clazz) {
			return (Clazz) parentNode;
		}
		if (parentNode instanceof GraphMember) {
			return ((GraphMember) parentNode).getClazz();
		}
		return null;

	}

	protected String getFullId() {
		return name;
	}

	/* PACKAGE VISIBILITY */
	protected GraphSimpleSet getChildren() {
		if (this.children instanceof GraphSimpleSet) {
			return (GraphSimpleSet) this.children;
		}
		GraphSimpleSet collection = new GraphSimpleSet();
		if (this.children == null) {
			return collection;
		}
		if (this.children instanceof GraphMember) {
			collection.with(this.children);
		} else if (children instanceof SimpleList<?>) {
			SimpleList<?> list = (SimpleList<?>) children;
			collection.withList(list);
		}
		return collection;
	}

	protected SimpleSet<GraphEntity> getNodes() {
		SimpleSet<GraphEntity> collection = new SimpleSet<GraphEntity>();
		if (this.children == null) {
			return collection;
		}
		if (this.children instanceof GraphEntity) {
			collection.add((GraphEntity) this.children);
			return collection;
		}
		if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for (GraphMember item : list) {
				if (item instanceof GraphEntity) {
					collection.add((GraphEntity) item);
				}
			}
		}
		return collection;
	}

	/**
	 * Set the name of Element
	 * 
	 * @param name The Name of Element
	 * @return The Instance
	 */
	public GraphMember with(String name) {
		setName(name);
		return this;
	}

	protected boolean setName(String value) {
		if ((value != null && value.equals(this.name) == false) || (value == null && this.name != null)) {
			this.name = value;
			return true;
		}
		return false;
	}

	protected boolean setParentNode(GraphMember value) {
		if (this.parentNode != value) {
			GraphMember oldValue = (GraphMember) this.parentNode;
			if (this.parentNode != null) {
				this.parentNode = null;
				oldValue.remove(this);
			}
			this.parentNode = value;
			if (value != null) {
				value.withChildren(this);
			}
			return true;
		}
		return false;
	}

	protected Object getParent() {
		return parentNode;
	}

	GraphMember withChildren(GraphMember... values) {
		/* Do Nothing */
		if (values == null || (values.length == 1 && (this.children == values[0]))) {
			return this;
		}
		if (this.children == null) {
			if (values.length == 1) {
				this.children = values[0];
				((GraphMember) values[0]).setParentNode(this);
				return this;
			}
		}
		GraphSimpleSet list;
		if (this.children instanceof GraphSimpleSet) {
			list = (GraphSimpleSet) this.children;
		} else {
			list = new GraphSimpleSet();
			list.with((GraphMember) this.children);
			this.children = list;
		}
		for (GraphMember value : values) {
			if (value != null) {
				if (list.add(value)) {
					value.setParentNode(this);
				}
			}
		}
		return this;
	}

	protected Match getDiff() {
		if (this.children == null) {
			Match graphDiff = new Match();
			this.withChildren(graphDiff);
			return graphDiff;
		}
		for (GraphMember item : getChildren()) {
			if (item instanceof Match) {
				return (Match) item;
			}
		}
		Match graphDiff = new Match();
		this.withChildren(graphDiff);
		return graphDiff;
	}

	public String getName() {
		return this.name;
	}

	protected GraphMember withAnnotation(Annotation value) {
		/* Remove Old GraphAnnotation */
		if (this.children != null) {
			if (this.children instanceof GraphMember) {
				if (this.children instanceof Annotation) {
					((Annotation) this.children).setParentNode(null);
					this.children = null;
				}
			}
			if (this.children instanceof GraphSimpleSet) {
				GraphSimpleSet collection = (GraphSimpleSet) this.children;
				for (int i = collection.size(); i >= 0; i--) {
					if (collection.get(i) instanceof Annotation) {
						GraphMember oldValue = collection.remove(i);
						if (oldValue != null) {
							oldValue.setParentNode(null);
						}
					}
				}
			}
		}
		withChildren(value);
		return this;
	}

	protected Annotation getAnnotation() {
		if (this.children == null) {
			return null;
		}
		if (this.children instanceof Annotation) {
			return (Annotation) this.children;
		} else if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet collection = (GraphSimpleSet) this.children;
			for (GraphMember item : collection) {
				if (item instanceof Annotation) {
					return (Annotation) item;
				}
			}
		}
		return null;
	}

	public Modifier getModifier() {
		if (this.children == null) {
			return null;
		}
		if (this.children instanceof Modifier) {
			return (Modifier) this.children;
		} else if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet collection = (GraphSimpleSet) this.children;
			for (GraphMember item : collection) {
				if (item instanceof Modifier) {
					return (Modifier) item;
				}
			}
		}
		return null;
	}

	protected GraphMember withModifier(Modifier... values) {
		if (values == null) {
			return this;
		}
		Modifier rootModifier = getModifier();
		if (rootModifier == null && this instanceof Modifier) {
			rootModifier = (Modifier) this;
		}
		if (rootModifier == null) {
			return this;
		}
		for (Modifier item : values) {
			if(item == null) {
				continue;
			}
			if (item.has(Modifier.PUBLIC) || item.has(Modifier.PACKAGE) || item.has(Modifier.PROTECTED)
					|| item.has(Modifier.PRIVATE)) {
				rootModifier.with(item.getName());
				continue;
			}
			rootModifier.withChildren(new Modifier(item.getName()));
		}
		return this;
	}

	public boolean remove(GraphMember member) {
		if (member == null) {
			return true;
		}
		if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			if (member instanceof Association) {
				((Association) member).withoutParent(this);
			} else {
				member.setParentNode(null);
			}
			return list.remove(member);
		}
		if (this.children == member) {
			this.children = null;
			if (member instanceof Association) {
				((Association) member).withoutParent(this);
			} else {
				member.setParentNode(null);
			}
			return true;
		}
		return false;
	}
}
