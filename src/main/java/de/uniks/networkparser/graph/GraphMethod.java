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

package de.uniks.networkparser.graph;

import java.lang.annotation.Annotation;

import de.uniks.networkparser.list.SimpleSet;

public class GraphMethod extends GraphNode {
	public static final String PROPERTY_RETURNTYPE = "returnType";
	public static final String PROPERTY_PARAMETER = "parameter";
	public static final String PROPERTY_NODE = "node";
	public static final String PROPERTY_MODIFIER = "modifier";
	public static final String PROPERTY_ANNOTATIONS = "annotations";

	private GraphModifier modifier = GraphModifier.PUBLIC;
	private GraphType returnType = GraphDataType.VOID;
	private String body;
	private SimpleSet<GraphAnnotation> annotations;
	private String throwsTags;

	@Override
	public GraphMethod with(String name) {
		super.with(name);
		return this;
	}
	
	public String getName(boolean includeName) {
		StringBuilder sb = new StringBuilder();

		sb.append(super.getName() + "(");
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
			sb.append(" "+returnType.getName(false));
		}
		return sb.toString();
	}

	private String getParameterSignature(boolean includeName,
			GraphParameter parameter, int i) {
		String param = parameter.getType().getName(false);
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

	public GraphMethod(String name) {
		this.with(name);
	}
	
	public GraphMethod(String name, GraphType returnType, GraphParameter... parameters) {
		this.with(name);
		this.with(parameters);
		this.with(returnType);
	}

	public GraphMethod(String name, GraphParameter... parameters) {
		this.with(parameters);
		this.with(name);
	}

	public GraphMethod withParameter(String paramName, GraphType dataType) {
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

	public GraphType getReturnType() {
		return this.returnType;
	}

	public GraphMethod with(GraphType returnType) {
		this.returnType = returnType;
		return this;
	}

	public GraphParameter createParameter(GraphType type) {
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
			if(param.getName() == null) {
				sb.append("p"+i+" : "+param.getType(shortName));
			}else{
				sb.append(children.get(i).getName()+" : "+param.getType(shortName));
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
			return new SimpleSet<GraphAnnotation>().withFlag(SimpleSet.READONLY);
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
					item.setParent(this);
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
}
