package de.uniks.networkparser;

import java.util.List;
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

public abstract class AbstractEntityList<V> extends AbstractList<V> implements List<V>{
	@Override
    public boolean remove(Object value) {
    	int index = getIndex(value);
        boolean result = values.remove(value);
        if(result){
        	V beforeValue = null;
        	if(index>0){
    			beforeValue = get(index - 1);
    		}
        	fireProperty(value, null, beforeValue);
        }
        return result;
    }

	public List<V> values(){
		return values;
	}
}
