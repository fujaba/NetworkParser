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
import java.beans.PropertyChangeSupport;

public class GraphDataType

{
	public static final String PROPERTY_VALUE = "value";

	public static final GraphDataType VOID = new GraphDataType("void");
	public static final GraphDataType INT = new GraphDataType("int");
	public static final GraphDataType LONG = new GraphDataType("long");
	public static final GraphDataType DOUBLE = new GraphDataType("double");
	public static final GraphDataType STRING = new GraphDataType("String");
	public static final GraphDataType BOOLEAN = new GraphDataType("boolean");
	public static final GraphDataType OBJECT = new GraphDataType("Object");

	private String value;

	GraphDataType(String value) {
		this.with(value);
	}

	public String getValue() {
		return value;
	}

	public String getValue(boolean shortName) {
		if (!shortName || value == null || value.lastIndexOf(".") < 0) {
			return value;
		}
		return value.substring(value.lastIndexOf(".") + 1);
	}

	public GraphDataType with(String value) {
		this.value = value;
		return this;
	}

	public static GraphDataType ref(String value) {
		return new GraphDataType(value);
	}

	public static GraphDataType ref(Class<?> value) {
		return new GraphDataType(value.getName().replace("$", "."));
	}

	public static GraphDataType ref(GraphNode value) {
		return new GraphDataType(value.getClassName());
	}

	@Override
	public String toString() {
		return "DataType." + value.toUpperCase();
	}

	protected final PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);
}
