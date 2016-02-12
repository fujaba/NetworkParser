package de.uniks.networkparser.logic;
/*
NetworkParser
Copyright (c) 2011 - 2016, Stefan Lindel
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
import java.beans.PropertyChangeEvent;
import de.uniks.networkparser.list.AbstractArray;

public final class SimpleCollectionEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;
	public static final String PROPERTY="items";
	private Object beforeValue;
	private Object value;
	private String type;

	public SimpleCollectionEvent(AbstractArray<?> source, String type, Object oldValue, Object newValue, Object beforeValue, Object value) {
		super(source, PROPERTY, oldValue, newValue);
		this.type = type;
		this.beforeValue = beforeValue;
		this.value = value;
	}

	public SimpleCollectionEvent withSource(Object source) {
		this.source = source;
		return this;
	}

	public Object getBeforeValue() {
		return beforeValue;
	}

	public Object getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	public SimpleCollectionEvent with(String type) {
		this.type = type;
		return this;
	}
}
