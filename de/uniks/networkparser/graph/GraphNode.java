package de.uniks.networkparser.graph;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
import java.util.ArrayList;
import java.util.Collection;

import de.uniks.networkparser.interfaces.BaseListEntity;

public class GraphNode implements BaseListEntity {
	private String className;
	private String id;
	private String headImage;
	private ArrayList<Attribute> values = new ArrayList<Attribute>();

	// GETTER AND SETTER
	public String getClassName(boolean shortName) {
		if(!shortName || className==null || className.lastIndexOf(".")<0){
			return className;
		}
		return className.substring(className.lastIndexOf(".") + 1);
	}

	public GraphNode withClassName(String className) {
		this.className = className;
		return this;
	}

	public String getClassName(){
		return className;
	}
	
	public String getId() {
		return id;
	}
	
	public String getTyp(String typ, boolean shortName){
		if(typ.equals(GraphIdMap.OBJECT)){
			return getId();
		}else if(typ.equals(GraphIdMap.CLASS)){
			return getClassName(shortName);
		}
		return "";
	}
	
	public ArrayList<Attribute> getAttributes(){
		return values;
	}
	
	public GraphNode withTyp(String typ, String value){
		if(typ.equals(GraphIdMap.OBJECT)){
			withId(value);
		}else if(typ.equals(GraphIdMap.CLASS)){
			withClassName(value);
		}
		return this;
	}

	public GraphNode withId(String id) {
		this.id = id;
		return this;
	}

	public void addValue(String property, String clazz, String value) {
		values.add(new Attribute().withKey(property).withClazz(clazz).withValue(value));
	}
	public void addValue(Attribute attribute) {
		values.add(attribute);
	}

	@Override
	public String toString() {
		if(id==null){
			return className;
		}
		return id;
	}

	public String getHeadImage() {
		return headImage;
	}

	public GraphNode withHeadImage(String headImage) {
		this.headImage = headImage;
		return this;
	}

	public BaseListEntity with(Object... values) {
		for(Object item : values){
			if(item instanceof Attribute){
				this.values.add((Attribute) item);
			}
		}
		return this;
	}

	@Override
	public int size() {
		return values.size();
	}

	public boolean add(Attribute value) {
		return values.add(value);
	}

	@Override
	public Attribute get(int z) {
		return values.get(z);
	}

	@Override
	public BaseListEntity with(Collection<?> values) {
		for(Object item : values){
			this.with(item);
		}
		return this;
	}

	@Override
	public BaseListEntity with(Object value) {
		if(value instanceof Attribute){
			values.add((Attribute) value);
		}
		return this;
	}
}
