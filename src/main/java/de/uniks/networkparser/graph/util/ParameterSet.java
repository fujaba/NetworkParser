package de.uniks.networkparser.graph.util;

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
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class ParameterSet extends SimpleSet<Parameter>{
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for(Parameter item : this) {
			collection.add(item.getMethod());
		}
		return collection;
	}

	public DateTypeSet getDataTypes() {
		DateTypeSet collection = new DateTypeSet();
		for(Parameter item : this) {
			collection.add(item.getType());
		}
		return collection;
	}

	@Override
	public ParameterSet filter(Condition<Parameter> newValue) {
		ParameterSet newList = new ParameterSet();
		filterItems(newList, newValue);
		return newList;
	}

	public ParameterSet hasName(String otherValue) {
		return filter(Parameter.NAME.equals(otherValue));
	}
}
