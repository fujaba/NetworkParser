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

import java.lang.annotation.Annotation;

import de.uniks.networkparser.list.SimpleSet;

public class GraphMethod extends GraphNode implements GraphMember {
	public static final String PROPERTY_RETURNTYPE = "returnType";
	public static final String PROPERTY_PARAMETER = "parameter";
	public static final String PROPERTY_NODE = "node";
	public static final String PROPERTY_MODIFIER = "modifier";
	public static final String PROPERTY_ANNOTATIONS = "annotations";

	private GraphModifier modifier = GraphModifier.PUBLIC;
	private GraphDataType returnType = GraphDataType.VOID;
	private String name;
	private String body;
	private SimpleSet<GraphAnnotation> annotations;
	private String throwsTags;
	private GraphNode parentNode;

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

		for (GraphMember item : children) {
			if((item instanceof GraphParameter) == false) {
				continue;
			}
			GraphParameter param = (GraphParameter) item; 
			if (first) {
				sb.append(getParameterSignature(includeName, param, i));
				first = false;
			} else {
				sb.append(getParameterSignature(includeName, param, i));
			}

			if (i < children.size() - 1) {
				if (includeName) {
					sb.append(", ");
				} else {
					sb.append(",");
				}
			}
			i++;
		}
		sb.append(")");
		if(returnType!=null && returnType!= GraphDataType.VOID){
			sb.append(" "+returnType.getValue());
		}
		return sb.toString();
	}

	private String getParameterSignature(boolean includeName,
			GraphParameter parameter, int i) {
		String param = parameter.getType().getValue();
		if (!includeName) {
			return param;
		}
		String name = "";
		if (parameter.getId() != null) {
			name = parameter.getId().trim();
		}
		if (name != "") {
			return param + " " + name;
		}
		return param + " p" + i;
	}

	public GraphMethod() {
	}

	public GraphMethod(String name) {
		this.with(name);
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

	public GraphMethod withParameter(String paramName, GraphDataType dataType) {
		new GraphParameter(paramName, dataType).with(this);
		return this;
	}

	public GraphModifier getModifier() {
		return this.modifier;
	}

	public GraphMethod with(GraphModifier value) {
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
		withParent(value);
		return this;
	}

	public String getParameterString(boolean shortName){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<children.size();i++) {
			if((children.get(i) instanceof GraphParameter)==false) {
				continue;
			}
			GraphParameter param = (GraphParameter) children.get(i); 
			if(i>0) {
				sb.append(", ");
			}
			if(param.getId() == null) {
				sb.append("p"+i+" : "+param.getType(shortName));
			}else{
				sb.append(children.get(i).getId()+" : "+param.getType(shortName));
			}
		}
		return sb.toString();
	}

	public String getBody() {
		return this.body;
	}

	public boolean setBody(String value) {
		if ((this.body == null && value != null) || (this.body != null && !this.body.equals(value))) {
			this.body = value;
			return true;
		}
		return false;
	}

	public GraphMethod withBody(String value) {
		setBody(value);
		return this;
	}
	
	public String getThrowsTags() {
		return this.throwsTags;
	}

	public boolean setThrowsTags(String value) {
		if ((this.throwsTags == null && value != null) || (this.throwsTags != null && !this.throwsTags.equals(value))) {
			this.throwsTags = value;
			return true;
		}
		return false;
	}

	public GraphMethod withThrowsTags(String value) {
		setThrowsTags(value);
		return this;
	}

	public SimpleSet<GraphAnnotation> getAnnotations() {
		if (this.annotations == null) {
			return new SimpleSet<GraphAnnotation>().addFlag(SimpleSet.READONLY);
		}
		return this.annotations;
	}

	public GraphMethod with(GraphAnnotation... value) {
		if (value == null) {
			return this;
		}
		for (GraphAnnotation item : value) {
			if (item != null) {
				if (this.annotations == null) {
					this.annotations = new SimpleSet<GraphAnnotation>();
				}
				if (this.annotations.add(item)) {
					item.withParent(this);
				}
			}
		}
		return this;
	}

	public GraphMethod without(Annotation... value) {
		for (Annotation item : value) {
			if ((this.annotations != null) && (item != null)) {
				this.annotations.remove(item);
			}
		}
		return this;
	}

	@Override
	public GraphMethod withParent(GraphNode value) {
		if (this.parentNode != value) {
			GraphNode oldValue = this.parentNode;
			if (this.parentNode != null) {
				this.parentNode = null;
				oldValue.without(this);
			}
			this.parentNode = value;
			if (value != null) {
				value.with(this);
			}
		}
		return this;
	}	
}
