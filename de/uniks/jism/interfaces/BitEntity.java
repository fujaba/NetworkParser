package de.uniks.jism.interfaces;

public class BitEntity {
	public static final String BIT_STRING="string";
	public static final String BIT_NUMBER="number";
	public static final String TYP_VALUE="Value";
	public static final String TYP_REFERENCE="reference";
	
	// Can be a Typ 
	private String property;
	private BitEntity start;
	private BitEntity len;
	protected Object value;
	
	public static final String PROPERTY_PROPERTY="property";
	public static final String PROPERTY_START="start";
	public static final String PROPERTY_LEN="len";
	public static final String PROPERTY_NAME="name";
	
	public BitEntity(String typ, Object value){
		this.property=typ;
		this.value = value;
	}
	public BitEntity(String property, int start, int len){
		this.property=property;
		this.start = new BitEntity(TYP_VALUE, start);
		this.len = new BitEntity(TYP_REFERENCE, len);
	}
	
	
	public BitEntity(String property, BitEntity start, BitEntity len){
		this.property=property;
		this.start = start;
		this.len = len;
	}
	
	
	public BitEntity getStartBit(){
		return start;
	}
	public String getPropertyName(){
		return property;
	}
	
	public BitEntity getSize(){
		return len;
	}
	
//	public BitTyp getTyp() {
//		return typ;
//	}
//	public void setTyp(BitTyp typ) {
//		this.typ = typ;
//	}
	public boolean set(String attribute, Object value){
		if (attribute.equalsIgnoreCase(PROPERTY_NAME)) {
			this.property = (String) value;
			return true;
		}else if (attribute.equalsIgnoreCase(PROPERTY_START)) {
			if(value instanceof Integer){
				this.start = new BitEntity(TYP_VALUE, value);
			}else{
				this.start = new BitEntity(TYP_REFERENCE, value);
			}
			return true;
		}else if (attribute.equalsIgnoreCase(PROPERTY_LEN)) {
			if(value instanceof Integer){
				this.len = new BitEntity(TYP_VALUE, value);
			}else{
				this.len = new BitEntity(TYP_REFERENCE, value);
			}
			return true;
//		}else if (attribute.equalsIgnoreCase(PROPERTY_TYP)) {
//			if(value instanceof Integer){
//				this.len = new BitEntity(TYP_VALUE, value);
//			}else{
//				this.len = new BitEntity(TYP_REFERENCE, value);
//			}
//			return true;
		}
		return false;
	}
}
