package de.uniks.networkparser;

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

public class SimpleEntityList<K, V> extends AbstractKeyValueList<K, V>{
	@Override
	public SimpleEntityList<K, V> getNewInstance() {
		return new SimpleEntityList<K, V>();
	}
//FIXME
//	@Override
//	public boolean add(V e) {
//		return addEntity(e);
//	}
	
	private void main(){
		
	}

	@Override
	public V remove(Object key) {
		return removeItem(key); 
	}

	@Override
	public AbstractList<K> with(Object... values) {
		// TODO Auto-generated method stub
		return null;
	}
}
