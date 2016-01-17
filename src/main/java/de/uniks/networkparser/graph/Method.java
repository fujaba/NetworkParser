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

public class Method extends GraphMember {
	public static final String PROPERTY_RETURNTYPE = "returnType";
	public static final String PROPERTY_PARAMETER = "parameter";
	public static final String PROPERTY_NODE = "node";
	public static final String PROPERTY_MODIFIER = "modifier";
	public static final String PROPERTY_ANNOTATIONS = "annotations";

	private Modifier modifier = Modifier.PUBLIC;
	private DataType returnType = DataType.VOID;
	private String body;

	@Override
	public Method with(String name) {
		super.with(name);
		return this;
	}
	
	public String getName(boolean includeName) {
		StringBuilder sb = new StringBuilder();

		sb.append(super.getName() + "(");
		int i = 0;
		if(children != null) {
			GraphSimpleSet collection = this.getChildren();
			for (GraphMember item : collection) {
				if((item instanceof Parameter) == false) {
					continue;
				}
				Parameter param = (Parameter) item;
				sb.append(param.getType(false));
				if (includeName) {
					String name = "";
					if (param.getName() != null) {
						name = param.getName().trim();
					}
					if (name != "") {
						sb.append(" " + name);
					} else {
						sb.append(" p" + (i++));
					}
				}
	
				if (i < collection.size() - 1) {
					if (includeName) {
						sb.append(", ");
					} else {
						sb.append(",");
					}
				}
			}
		}
		sb.append(")");
		if(returnType!=null && returnType!= DataType.VOID){
			sb.append(" "+returnType.getName(false));
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

	public Method withParameter(String paramName, Clazz dataType) {
		new Parameter().with(paramName).with(DataType.ref(dataType)).withParent(this);
		return this;
	}

	public Modifier getModifier() {
		return this.modifier;
	}

	public Method with(Modifier value) {
		this.modifier = value;
		return this;
	}

	public DataType getReturnType() {
		return this.returnType;
	}
	
	public Clazz getClazz() {
		return (Clazz) parentNode;
	}

	public Parameter createParameter(DataType type) {
		return new Parameter().with(type).withParent(this);
	}
	
	public Parameter createParameter(Clazz type) {
		return new Parameter().with(DataType.ref(type)).withParent(this);
	}

	public Method withParent(Clazz value) {
		super.setParent(value);
		return this;
	}

	public String getParameterString(boolean shortName){
		StringBuilder sb=new StringBuilder();
		GraphSimpleSet collection = this.getChildren();
		for(int i=0;i<collection.size();i++) {
			if((collection.get(i) instanceof Parameter)==false) {
				continue;
			}
			Parameter param = (Parameter) collection.get(i); 
			if(i>0) {
				sb.append(", ");
			}
			if(param.getName() == null) {
				sb.append("p"+i+" : "+param.getType(shortName));
			}else{
				sb.append(collection.get(i).getName()+" : "+param.getType(shortName));
			}
		}
		return sb.toString();
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
		if( children instanceof Throws) {
			collection.add((Throws) children);
		}else if (children instanceof GraphSimpleSet) {
			GraphSimpleSet items = (GraphSimpleSet) children;
			for (GraphMember child : items) {
				if (child instanceof Throws)  {
					collection.add((Throws) child);
				}
			}
		}
		return collection;
	}
	
	public SimpleSet<Parameter> getParameter() {
		SimpleSet<Parameter> collection = new SimpleSet<Parameter>();
		if (children == null) {
			return collection;
		}
		if( children instanceof Parameter) {
			collection.add((Parameter) children);
		}else if (children instanceof GraphSimpleSet) {
			GraphSimpleSet items = (GraphSimpleSet) children;
			for (GraphMember child : items) {
				if (child instanceof Parameter)  {
					collection.add((Parameter) child);
				}
			}
		}
		return collection;
	}
	

	public Method with(Throws... values) {
		super.withChildren(true, values);
		return this;
	}
	
	public Method with(Parameter... values) {
		super.withChildren(true, values);
		return this;
	}
	
	public Method with(DataType returnType) {
		this.returnType = returnType;
		return this;
	}
	
	public Method with(Clazz returnType) {
		this.returnType = DataType.ref(returnType);
		return this;
	}
	
	public Method without(Parameter... values) {
		super.without(values);
		return this;
	}
	
	public Method without(Annotation... values) {
		super.without(values);
		return this;
	}

	public Annotation getAnnotation() {
		return super.getAnnotation();
	}

	public Method with(Annotation value) {
		if(this.children != null) {
			if (this.children instanceof Annotation) {
				this.children = null;
			} else if(this.children instanceof GraphSimpleSet) {
				GraphSimpleSet collection = (GraphSimpleSet) this.children;
				for(int i=collection.size();i>=0;i--) {
					if(collection.get(i) instanceof Annotation) {
						collection.remove(i);
					}
				}
			}
		}
		super.withChildren(true, value);
		return this;
	}
}
