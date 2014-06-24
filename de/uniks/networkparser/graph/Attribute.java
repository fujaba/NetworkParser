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

public class Attribute extends Value implements GraphMember {
	public static final String PROPERTY_CLAZZ="clazz";
	public static final String PROPERTY_VALUE="value";
	public static final String PROPERTY_VISIBILITY="visibility";

	private GraphNode clazz = null;
	private String value = null;
	private Visibility visibility = Visibility.PRIVATE;

	public String getValue() {
		return value;
	}
	public Attribute withValue(String value) {
		this.value = value;
		return this;
	}
	public Visibility getVisibility() {
		return visibility;
	}
	public void with(Visibility visibility) {
		this.visibility = visibility;
	}
	public GraphNode getClazz() {
		return clazz;
	}
	public void setClazz(GraphNode clazz) {
		this.clazz = clazz;
	}
	
	// Redirect
	@Override
	public Attribute with(String value) {
		super.with(value);
		return this;
	}
	@Override
	public Attribute with(DataType value){
		super.with(value);
		return this;
	}
}
