package de.uniks.networkparser.graph;

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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class Method extends GraphMember {
	public static final String PROPERTY_RETURNTYPE = "returnType";
	public static final String PROPERTY_PARAMETER = "parameter";
	public static final String PROPERTY_PARAMETERNAME = "parameterName";
	public static final String PROPERTY_NODE = "node";
	public static final String PROPERTY_ANNOTATIONS = "annotations";
	public static final String PROPERTY_BODY = "body";

	private DataType returnType = DataType.VOID;
	private String body;

	@Override
	public Method with(String name) {
		if (name == null) {
			return this;
		}
		int pos = name.indexOf("(");
		if (pos > 0) {
			name = name.substring(0, pos);
		}
		super.with(name);
		return this;
	}

	public String getName(boolean shortName, boolean removeParameterNames) {
		StringBuilder sb = new StringBuilder();

		sb.append(super.getName());
		if (children != null) {
			sb.append(getParameterString(shortName, removeParameterNames, true));
		} else {
			sb.append("()");
		}
		if (returnType != null && returnType.equals(DataType.VOID) == false) {
			sb.append(" " + returnType.getName(shortName));
		}
		return sb.toString();
	}

	public Method() {
	}

	public Method(String name) {
		this.with(name);
	}

	public Method(String name, DataType returnType, Parameter... parameters) {
		this.with(name);
		this.with(parameters);
		this.with(returnType);
	}

	public Method(String name, Parameter... parameters) {
		this.with(parameters);
		this.with(name);
	}

	public Method withParameter(String paramName, DataType dataType) {
		new Parameter().with(paramName).with(dataType).withParent(this);
		return this;
	}

	@Override
	public Modifier getModifier() {
		Modifier modifier = super.getModifier();
		if (modifier == null) {
			modifier = new Modifier(Modifier.PUBLIC.getName());
			super.withChildren(modifier);
		}
		return modifier;
	}

	public Method with(Modifier... modifiers) {
		super.withModifier(modifiers);
		return this;
	}

	public DataType getReturnType() {
		return this.returnType;
	}

	public Parameter create(DataType type) {
		return new Parameter().with(type).withParent(this);
	}

	public Method withParent(Clazz value) {
		setParentNode(value);
		return this;
	}

	CharacterBuffer getParameterString(boolean shortName, boolean removeParameterNames, boolean optimizeArray) {
		CharacterBuffer sb = new CharacterBuffer().with("(");
		GraphSimpleSet collection = this.getChildren();
		for (int i = 0; i < collection.size(); i++) {
			if ((collection.get(i) instanceof Parameter) == false) {
				continue;
			}
			Parameter param = (Parameter) collection.get(i);
			if (sb.length() > 1) {
				sb.with(", ");
			}
			sb.with(param.getType().getName(shortName));
			if (param.isArray()) {
				if (optimizeArray && i == collection.size() - 1) {
					/* Check for array at least */
					sb.with("...");
				} else {
					sb.with("[]");
				}
			}

			if (param.getName() == null || removeParameterNames) {
				sb.with(" p" + i);
			} else {
				sb.with(" " + collection.get(i).getName());
			}
		}
		sb.with(")");
		return sb;
	}

	public String getBody() {
		return this.body;
	}

	public Method withBody(String value) {
		this.body = value;
		return this;
	}

	public SimpleSet<Throws> getThrows() {
		SimpleSet<Throws> collection = new SimpleSet<Throws>();
		if (children == null) {
			return collection;
		}
		if (children instanceof Throws) {
			collection.add((Throws) children);
		} else if (children instanceof GraphSimpleSet) {
			GraphSimpleSet items = (GraphSimpleSet) children;
			for (GraphMember child : items) {
				if (child instanceof Throws) {
					collection.add((Throws) child);
				}
			}
		}
		return collection;
	}

	/**
	 * get All Parameter
	 * 
	 * @param filters Can Filter the List of Parameter
	 * @return all Parameter of a Method
	 *
	 *         <pre>
	 * Method  --------------------- Parameter
	 * one                          many
	 *         </pre>
	 */
	public ParameterSet getParameters(Condition<?>... filters) {
		ParameterSet collection = new ParameterSet();
		if (children == null) {
			return collection;
		}
		if (children instanceof Parameter) {
			if (check((Parameter) this.children, filters)) {
				collection.add((Parameter) this.children);
			}
		} else if (children instanceof GraphSimpleSet) {
			GraphSimpleSet items = (GraphSimpleSet) children;
			for (GraphMember item : items) {
				if (item instanceof Parameter && check(item, filters)) {
					collection.add((Parameter) item);
				}
			}
		}
		return collection;
	}

	public Method with(Throws... values) {
		super.withChildren(values);
		return this;
	}

	public Method with(Parameter... values) {
		super.withChildren(values);
		return this;
	}

	public Method with(DataType returnType) {
		this.returnType = returnType;
		return this;
	}

	public Method with(Clazz returnType) {
		this.returnType = DataType.create(returnType);
		return this;
	}

	public Annotation getAnnotation() {
		return super.getAnnotation();
	}

	public Method with(Annotation value) {
		if (this.children != null) {
			if (this.children instanceof Annotation) {
				this.children = null;
			} else if (this.children instanceof GraphSimpleSet) {
				GraphSimpleSet collection = (GraphSimpleSet) this.children;
				for (int i = collection.size(); i >= 0; i--) {
					if (collection.get(i) instanceof Annotation) {
						collection.remove(i);
					}
				}
			}
		}
		super.withChildren(value);
		return this;
	}

	@Override
	public String toString() {
		return getName(true, false);
	}

	@Override
	public Object getValue(String attribute) {
		if (PROPERTY_RETURNTYPE.equalsIgnoreCase(attribute)) {
			return this.getReturnType();
		}
		if (PROPERTY_PARAMETER.equalsIgnoreCase(attribute)) {
			return this.getParameters();
		}
		if (PROPERTY_PARAMETERNAME.equalsIgnoreCase(attribute)) {
			return this.getParameterString(true, false, true);
		}
		if (PROPERTY_ANNOTATIONS.equalsIgnoreCase(attribute)) {
			return this.getAnnotation();
		}
		if (PROPERTY_NODE.equalsIgnoreCase(attribute)) {
			return this.getNodes();
		}
		if (PROPERTY_BODY.equalsIgnoreCase(attribute)) {
			return this.getBody();
		}
		return super.getValue(attribute);
	}

	public boolean isValidReturn() {
		if (getReturnType() == null || DataType.VOID.equals(getReturnType())) {
			return true;
		}
		if (this.body == null) {
			return false;
		}
		String[] lines = this.body.split("\n");
		if (lines.length < 1) {
			return false;
		}

		for (String l : lines) {
			if (l.trim().startsWith("return")) {
				return true;
			}
		}
		return false;
	}
}
