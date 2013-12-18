package de.uniks.networkparser.logic;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or – as soon they
 will be approved by the European Commission - subsequent
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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.Buffer;

public class Equals implements Condition {
	private String strValue;
	// Position of the Byte or -1 for currentPosition
	private int position = -1;
	private Byte bytevalue;

	@Override
	public boolean matches(IdMap map, Object entity, String property,
			Object value, boolean isMany, int deep) {
		if(entity instanceof Buffer){
			Buffer buffer = (Buffer) entity;
			int pos;
			if (position < 0) {
				pos = buffer.position();
			} else {
				pos = position;
			}
			return buffer.byteAt(pos) == bytevalue;
		}
		if(value==null){
			return (strValue==null);
		}
		return value.equals(strValue);
	}

	public Equals withPosition(int value){
		this.position = value;
		return this;
	}
	
	public Equals withValue(Byte value){
		this.bytevalue = value;
		return this;
	}
	public Equals withValue(String value){
		this.strValue = value;
		return this;
	}
	public String toString(){
		if(strValue!=null){
			return "=="+strValue+" ";
		}
		return "=="+bytevalue+" ";
	}
}
