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
import java.util.Map;
import de.uniks.networkparser.interfaces.BaseEntity;
import de.uniks.networkparser.interfaces.BaseEntityList;

public class BidiMapEntry implements BaseEntity, Map.Entry<String, Object> {
	  private String key;
	  private Object value;

	  public BidiMapEntry withKey(String key){
	    this.key = key;
	    return this;
	  }
	  public BidiMapEntry withValue(Object value){
	    this.value = value;
	    return this;
	  }

	  public String getKey() {
	    return key;
	  }

	  public Object getValue() {
	    return value;
	  }

	  public Object setValue(Object value) {
	    return this.value = value;
	  }
	  

	@Override
	public BaseEntityList getNewArray() {
		return new ArrayEntryList();
	}

	@Override
	public BaseEntity getNewObject() {
		return new BidiMapEntry();
	}

	@Override
	public String toString(int indentFactor) {
		return toString();
	}

	@Override
	public String toString(int indentFactor, int intent) {
		return toString();
	}

	@Override
	public BaseEntity withVisible(boolean value) {
		return this;
	}

	@Override
	public boolean isVisible() {
		return true;
	}
}
