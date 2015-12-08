package de.uniks.networkparser.graph;

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
import de.uniks.networkparser.interfaces.BaseItem;

public abstract class GraphValue implements BaseItem {
	public static final String PROPERTY_INITIALIZATION = "initialization";
	public static final String PROPERTY_TYPE = "type";

	protected GraphType type = null;
	protected String initialization = null;
	protected String id;

	public String getId() {
		return id;
	}
	
	public GraphValue with(String value) {
		this.id = value;
		return this;
	}

	public GraphValue with(GraphType value) {
		if ((this.type == null && value != null)
				|| (this.type != null && this.type != value)) {
			this.type = value;
		}
		return this;
	}

	public GraphType getType() {
		return type;
	}

	public String getType(boolean shortName) {
		if(type==null) {
			return "?";
		}
		return type.getName(shortName);
	}

	@Override
	public BaseItem withAll(Object... values) {
		if(values==null){
			return this;
		}
		for(Object item : values) {
			if(item instanceof String) {
				with((String) item);	
			} else if(item instanceof GraphDataType) {
				with((GraphType) item);
			}
		}
		return this;
	}

	public boolean setInitialization(String value) {
		if ((this.initialization == null && value != null)
				|| (this.initialization != null && !this.initialization.equals(value))) {
			this.initialization = value;
			return true;
		}
		return false;
	}

	public GraphValue withInitialization(String value) {
		setInitialization(value);
		return this;
	}

	public String getInitialization() {
		return this.initialization;
	}
}
