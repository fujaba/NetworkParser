package de.uniks.networkparser.logic;

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
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class InstanceOf extends ConditionMap implements SendableEntityCreator {
	public static final String CLAZZNAME="clazzname";
	public static final String PROPERTY="property";
	public static final String CLAZZ="clazz";
	public static final String VALUE="value";
	private Class<?> clazzName;
	private Object clazz;
	private String property;
	private Object value;

	@Override
	public String[] getProperties() {
		return new String[]{CLAZZNAME, CLAZZ, PROPERTY, VALUE};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new InstanceOf();
	}
	
	@Override
	public Object getValue(Object entity, String attribute) {
		if(CLAZZNAME.equalsIgnoreCase(attribute)){
			return ((InstanceOf)entity).getClazzName();
		}
		if(CLAZZ.equalsIgnoreCase(attribute)){
			return ((InstanceOf)entity).getClazz();
		}
		if(PROPERTY.equalsIgnoreCase(attribute)){
			return ((InstanceOf)entity).getProperty();
		}
		if(VALUE.equalsIgnoreCase(attribute)){
			return ((InstanceOf)entity).getValue();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if(CLAZZNAME.equalsIgnoreCase(attribute)){
			((InstanceOf)entity).withClazzName((Class<?>)value);
			return true;
		}
		if(CLAZZ.equalsIgnoreCase(attribute)){
			((InstanceOf)entity).withClazz(value);
			return true;
		}
		if(PROPERTY.equalsIgnoreCase(attribute)){
			((InstanceOf)entity).withProperty(""+value);
			return true;
		}
		if(VALUE.equalsIgnoreCase(attribute)){
			((InstanceOf)entity).withValue(value);
			return true;
		}
		return false;
	}

	
	public static InstanceOf value(Class<?>  clazzName, String property, Object element){
		return new InstanceOf().withClazzName(clazzName).withProperty(property).withValue(element);
	}
	
	public static InstanceOf value(Class<?>  clazzName, String property){
		return new InstanceOf().withClazzName(clazzName).withProperty(property);
	}
	
	public static InstanceOf value(Object clazz, String property){
		return new InstanceOf().withClazz(clazz).withProperty(property);
	}

	public Class<?> getClazzName() {
		return clazzName;
	}

	public InstanceOf withClazzName(Class<?> clazzName) {
		this.clazzName = clazzName;
		return this;
	}

	public Object getClazz() {
		return clazz;
	}

	public InstanceOf withClazz(Object clazz) {
		this.clazz = clazz;
		return this;
	}

	public String getProperty() {
		return property;
	}

	public InstanceOf withProperty(String property) {
		this.property = property;
		return this;
	}

	public Object getValue() {
		return value;
	}

	public InstanceOf withValue(Object value) {
		this.value = value;
		return this;
	}

	@Override
	public boolean matches(ValuesMap values) {
		if(this.clazzName!=null && values.entity.getClass()!=this.clazzName){
			return true;
		}
		if(this.clazz!=null && values.entity!=this.clazz){
			return true;
		}
		if(!this.property.equalsIgnoreCase(values.property)){
			return true;
		}
		return (this.value != null && this.value==values.value);
	}
}
