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
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.StringList;

public class GraphLiteral extends GraphMember{
	private SimpleKeyValueList<String, StringList> values=new SimpleKeyValueList<String, StringList>();
	
	@Override
	public GraphLiteral with(String name) {
		super.with(name);
		return this;
	}
	public GraphLiteral withKeyValue(String key, Object value) {
		if(value instanceof StringList) {
			this.values.put(key, (StringList) value);
			return this;
		}
		if(value instanceof String) {
			StringList list =new StringList();
			list.add(""+value);
			this.values.add(key, list);
		}
		return this;
	}
	
	public SimpleKeyValueList<String, StringList> getValues() {
		return values;
	}
}
