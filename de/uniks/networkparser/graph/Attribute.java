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
import de.uniks.networkparser.interfaces.BaseItem;

public class Attribute implements BaseItem{
	private String key;
	private String className;
	private String value;
	
	public String getKey() {
		return key;
	}
	public Attribute withKey(String key) {
		this.key = key;
		return this;
	}
	public String getClazz() {
		return className;
	}
	public Attribute withClazz(String clazz) {
		this.className = clazz;
		return this;
	}
	public String getValue() {
		return value;
	}
	public Attribute withValue(String value) {
		this.value = value;
		return this;
	}
	public String getValue(String typ, boolean shortName) {
		if(typ.equals(GraphIdMap.CLASS)){
			if(!shortName || className==null || className.lastIndexOf(".")<0){
				return className;
			}
			return className.substring(className.lastIndexOf(".") + 1);
		}
		return value;
	}
}
