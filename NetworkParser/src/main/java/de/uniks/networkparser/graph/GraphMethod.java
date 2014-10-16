/*
   Copyright (c) 2012 zuendorf

   Permission is hereby granted, free of charge, to any person obtaining a copy of this software
   and associated documentation files (the "Software"), to deal in the Software without restriction,
   including without limitation the rights to use, copy, modify, merge, publish, distribute,
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:

   The above copyright notice and this permission notice shall be included in all copies or
   substantial portions of the Software.

   The Software shall be used for Good, not Evil.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.uniks.networkparser.graph;

import de.uniks.networkparser.AbstractList;

public class GraphMethod extends AbstractList<GraphParameter> implements GraphMember {
	public static final String PROPERTY_RETURNTYPE = "returnType";
	public static final String PROPERTY_PARAMETER = "parameter";
	public static final String PROPERTY_NODE = "node";
	public static final String PROPERTY_MODIFIER = "modifier";

	private GraphVisibility modifier = GraphVisibility.PUBLIC;
	private GraphNode node = null;
	private GraphDataType returnType = GraphDataType.VOID;
	private String name;

	public String getName() {
		return name;
	}

	public GraphMethod with(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public String getId() {
		return name;
	}

	public String getName(boolean includeName) {
		StringBuilder sb = new StringBuilder();

		sb.append(this.getName() + "(");
		boolean first = true;
		int i = 0;

		for (GraphParameter parameter : keys) {

			if (first) {
				sb.append(getParameterSignature(includeName, parameter, i));
				first = false;
			} else {
				sb.append(getParameterSignature(includeName, parameter, i));
			}

			if (i < keys.size() - 1) {
				if (includeName) {
					sb.append(", ");
				} else {
					sb.append(",");
				}
			}
			i++;
		}
		sb.append(")");
		return sb.toString();
	}

	private String getParameterSignature(boolean includeName,
			GraphParameter parameter, int i) {
		String param = parameter.getType().getValue();
		if (!includeName) {
			return param;
		}
		String name = "";
		if (parameter.getName() != null) {
			name = parameter.getName().trim();
		}
		if (name != "") {
			return param + " " + name;
		}
		return param + " p" + i;
	}

	public GraphMethod() {
	}

	public GraphMethod(String name, GraphDataType returnType, GraphParameter... parameters) {
		this.with(name);
		this.with(parameters);
		this.with(returnType);
	}

	public GraphMethod(String name, GraphParameter... parameters) {
		this.with(parameters);
		this.with(name);
	}

	public GraphMethod with(GraphParameter... value) {
		if (value == null) {
			return this;
		}
		for (GraphParameter item : value) {
			if (item != null) {
				this.addEntity(item);
			}
		}
		return this;
	}

	public GraphMethod withParameter(String paramName, GraphDataType dataType) {
		new GraphParameter(paramName, dataType).with(this);
		return this;
	}

	public GraphVisibility getModifier() {
		return this.modifier;
	}

	public GraphMethod with(GraphVisibility value) {
		this.modifier = value;
		return this;
	}

	public GraphDataType getReturnType() {
		return this.returnType;
	}

	public GraphMethod with(GraphDataType value) {
		this.returnType = value;
		return this;
	}

	public GraphParameter createParameter(GraphDataType type) {
		return new GraphParameter(type).with(this);
	}

	public GraphMethod with(GraphNode value) {
		if (this.node != value) {
			// GraphNode oldValue = this.clazz;
			if (this.node != null) {
				this.node = null;
				node.without(this);
			}
			this.node = value;
			if (value != null) {
				value.with(this);
			}
		}

		return this;
	}

	public GraphNode getNode() {
		return node;
	}

	@Override
	public GraphMethod getNewInstance() {
		return new GraphMethod();
	}

	@Override
	public GraphMethod with(Object... values) {
		if (values == null) {
			return this;
		}
		for (Object value : values) {
			if (value != null && value instanceof GraphParameter) {
				this.addEntity((GraphParameter) value);
			}
		}
		return this;
	}
	
	public String getParameterString(boolean shortName){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<size();i++) {
			if(i>0) {
				sb.append(", ");
			}
			if(get(i).getName() == null) {
				sb.append("p"+i+" : "+get(i).getType(shortName));
			}else{
				sb.append(get(i).getName()+" : "+get(i).getType(shortName));
			}
		}
		return sb.toString();
	}
}
