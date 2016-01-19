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
	private DataType returnType = DataType.VOID;
	private String body;

	@Override
	public Method with(String name) {
		super.with(name);
		return this;
	}
	
	public String getName(boolean shortName) {
		StringBuilder sb = new StringBuilder();

		sb.append(super.getName() + "(");
		if(children != null) {
			sb.append(getParameterString(shortName));
		}
		sb.append(")");
		if(returnType!=null && returnType!= DataType.VOID){
			sb.append(" "+returnType.getName(shortName));
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
		new Parameter().with(paramName).with(DataType.create(dataType)).withParent(this);
		return this;
	}
	
	public Modifier getModifier() {
		Modifier modifier = super.getModifiers();
		if(modifier == null) {
			modifier = new Modifier(Modifier.PUBLIC.getName());
			super.withChildren(true, modifier);
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
	
	public Clazz getClazz() {
		return (Clazz) parentNode;
	}

	public Parameter create(DataType type) {
		return new Parameter().with(type).withParent(this);
	}
	
	public Parameter create(Clazz type) {
		return new Parameter().with(DataType.create(type)).withParent(this);
	}

	public Method withParent(Clazz value) {
		super.setParent(value);
		return this;
	}

	String getParameterString(boolean shortName){
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
				sb.append(param.getType(shortName)+ " p"+i);
			}else{
				sb.append(param.getType(shortName)+" "+collection.get(i).getName());
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
		this.returnType = DataType.create(returnType);
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
