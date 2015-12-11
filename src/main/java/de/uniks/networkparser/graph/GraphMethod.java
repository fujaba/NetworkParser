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

import de.uniks.networkparser.list.SimpleSet;

public class GraphMethod extends GraphMember {
	public static final String PROPERTY_RETURNTYPE = "returnType";
	public static final String PROPERTY_PARAMETER = "parameter";
	public static final String PROPERTY_NODE = "node";
	public static final String PROPERTY_MODIFIER = "modifier";
	public static final String PROPERTY_ANNOTATIONS = "annotations";

	private GraphModifier modifier = GraphModifier.PUBLIC;
	private GraphDataType returnType = GraphDataType.VOID;
	private String body;

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
		String param = parameter.getType(false);
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
		new GraphParameter(paramName, dataType).withParent(this);
		return this;
	}

	public GraphMethod withParameter(String paramName, GraphClazz dataType) {
		new GraphParameter(paramName, GraphDataType.ref(dataType)).withParent(this);
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

	public GraphMethod with(GraphDataType returnType) {
		this.returnType = returnType;
		return this;
	}
	
	public GraphMethod with(GraphClazz returnType) {
		this.returnType = GraphDataType.ref(returnType);
		return this;
	}

	public GraphParameter createParameter(GraphDataType type) {
		return new GraphParameter(type).withParent(this);
	}
	
	public GraphParameter createParameter(GraphClazz type) {
		return new GraphParameter(GraphDataType.ref(type)).withParent(this);
	}

	public GraphMethod withParent(GraphClazz value) {
		super.setParent(value);
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

	public GraphMethod withBody(String value) {
		this.body = value;
		return this;
	}
	
	
	public SimpleSet<GraphThrows> getThrows() {
		SimpleSet<GraphThrows> collection = new SimpleSet<GraphThrows>();
		if (children == null) {
			return collection;
		}
		for (GraphMember child : children) {
			if (child instanceof GraphThrows)  {
				collection.add((GraphThrows) child);
			}
		}
		return collection;
	}

	public GraphMethod with(GraphThrows... values) {
		super.with(values);
		return this;
	}

	public GraphAnnotation getAnnotations() {
		if(this.children == null) {
			return null;
		}
		for(GraphMember item : this.children) {
			if(item instanceof GraphAnnotation) {
				return (GraphAnnotation) item;
			}
		}
		return null;
	}

	public GraphMethod with(GraphAnnotation value) {
		// Remove Old GraphAnnotation
		if(this.children != null) {
			for(int i=this.children.size();i>=0;i--) {
				if(this.children.get(i) instanceof GraphAnnotation) {
					this.children.remove(i);
				}
			}
		}
		super.with(value);
		return this;
	}
}
