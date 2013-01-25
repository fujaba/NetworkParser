package de.uniks.jism.event;
/*
Copyright (c) 2013, Stefan Lindel
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. All advertising materials mentioning features or use of this software
   must display the following acknowledgement:
   This product includes software developed by Stefan Lindel.
4. Neither the name of contributors may be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE 'Json Id Serialisierung Map' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
public class BitEntity {
	public static final String BIT_STRING="string";
	public static final String BIT_NUMBER="number";
	public static final String BIT_BYTE="byte";
	public static final String TYP_REFERENCE="reference";
	public static final String TYP_VALUE="value";
	
	// Can be a Typ 
	private String typ;
	private BitEntity start;
	private BitEntity len;
	protected String property;
	
	public static final String PROPERTY_PROPERTY="property";
	public static final String PROPERTY_START="start";
	public static final String PROPERTY_LEN="len";
	public static final String PROPERTY_TYP="typ";
	public static final String PROPERTY_NAME="name";
	
	
	public BitEntity(String typ, String value){
		this.property = value;
		this.typ = typ;
	}

	public BitEntity(String property, String typ, int start, int len){
		this.property=property;
		this.start = new BitEntity(TYP_VALUE, ""+start);
		this.len = new BitEntity(TYP_VALUE, ""+len);
		this.typ = typ;
	}
	
	public BitEntity(String property, String typ, String start, String startTyp, String len, String lenTyp){
		this.property=property;
		this.start = new BitEntity(startTyp, start);
		this.len = new BitEntity(lenTyp, len);
		this.typ = typ;
	}
	
	
	public BitEntity(String property, String typ, BitEntity start, BitEntity len){
		this.property=property;
		this.start = start;
		this.len = len;
		this.typ = typ;
	}
	
	
	public BitEntity getStartBit(){
		return start;
	}
	
	public BitEntity getLen(){
		return len;
	}
	
	public String getPropertyName(){
		return property;
	}
	
	public BitEntity getSize(){
		return len;
	}
	
	public String getTyp(){
		return typ;
	}
	
	public boolean isTyp(String referenceTyp){
		return typ.equals(referenceTyp);
	}
	
	public boolean set(String attribute, Object value){
		if (attribute.equalsIgnoreCase(PROPERTY_NAME)) {
			this.property = (String) value;
			return true;
		}else if (attribute.equalsIgnoreCase(PROPERTY_START)) {
			if(value instanceof Integer){
				this.start = new BitEntity(TYP_VALUE, ""+value);
			}else{
				this.start = new BitEntity(TYP_REFERENCE, ""+value);
			}
			return true;
		}else if (attribute.equalsIgnoreCase(PROPERTY_LEN)) {
			if(value instanceof Integer){
				this.len = new BitEntity(TYP_VALUE, ""+value);
			}else{
				this.len = new BitEntity(TYP_REFERENCE, ""+value);
			}
			return true;
		}else if (attribute.equalsIgnoreCase(PROPERTY_PROPERTY)) {
			this.property = (String)value;
			return true;
		}else if (attribute.equalsIgnoreCase(PROPERTY_TYP)) {
			this.typ = (String)value;
			return true;
		}
		return false;
	}
	
	/*
	 * Generic Getter for Attributes
	 */
	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		
		if (attribute.equalsIgnoreCase(PROPERTY_NAME)) {
			return this.property;
		}else if (attribute.equalsIgnoreCase(PROPERTY_START)) {
			return this.start;
		}else if (attribute.equalsIgnoreCase(PROPERTY_LEN)) {
			return this.len;
		}else if (attribute.equalsIgnoreCase(PROPERTY_PROPERTY)) {
			return this.property;
		}else if (attribute.equalsIgnoreCase(PROPERTY_TYP)) {
			return this.typ;
		}
		return null;
	}
}
