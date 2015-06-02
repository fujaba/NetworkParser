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
import de.uniks.networkparser.list.SimpleList;

public class GraphSimpleList<V> extends SimpleList<V>{
	@Override
	protected boolean checkValue(Object a, Object b) {
		if(!(a instanceof GraphMember)) {
			return a.equals(b);
		}
		String idA = ((GraphMember)a).getId();
		if(idA==null) {
			return a.equals(b);
		}
		String idB;
		if(b instanceof String) {
			idB = (String)b;
		}else {
			idB = ((GraphMember)b).getId();
		}
		return idA.equalsIgnoreCase(idB);
	}

}
