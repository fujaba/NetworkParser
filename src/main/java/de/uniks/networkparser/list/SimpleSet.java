package de.uniks.networkparser.list;

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
import java.util.Set;

public class SimpleSet<V> extends AbstractList<V> implements Set<V> {
	@Override
	public AbstractList<V> getNewList(boolean keyValue) {
		return new SimpleSet<V>();
	}

	@Override
	public boolean remove(Object o) {
		return super.removeByObject(o)>=0;
	}
	
	@Override
	public SimpleSet<V> clone() {
		return ((SimpleSet<V>)getNewList(false)).init(this);
	}
}
