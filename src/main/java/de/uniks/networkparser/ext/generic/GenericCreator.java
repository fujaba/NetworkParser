package de.uniks.networkparser.ext.generic;

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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class GenericCreator implements SendableEntityCreator {
	private Object item;
	private Class<?> clazz;
	private String[] properties = null;
	private static final LinkedHashSet<String> badProperties = new LinkedHashSet<String>();

	public GenericCreator() {
		badProperties.add("getClass");
		badProperties.add("getPropertyChangeSupport");
	}

	public GenericCreator(Object item) {
		this();
		withItem(item);
	}

	public GenericCreator withClass(String value) {
		try {
			this.clazz = Class.forName(value);
		} catch (ClassNotFoundException e) {
		}
		return this;
	}
	
	public GenericCreator withClass(Class<?> clazz) {
		this.clazz = clazz;
		return this;
	}

	public GenericCreator withItem(Object value) {
		this.item = value;

		// Init all Values
		if (this.item == null) {
			this.properties = new String[0];
		} else {
			this.clazz = item.getClass();
			Method[] methods = this.clazz.getMethods();
			LinkedHashSet<String> fieldNames = new LinkedHashSet<String>();
			for (Method method : methods) {
				String methodName = method.getName();
				if(isValidMethod(methodName)) {
					fieldNames.add(methodName);
				}
			}
			properties = fieldNames.toArray(new String[] {});
		}
		return this;
	}

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		if (item != null) {
			try {
				return item.getClass().newInstance();
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
		} else if( this.clazz != null) {
			try {
				return clazz.newInstance();
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			
		}
		return null;
	}

	private String getMethodName(String value) {
		return value.substring(0, 1).toUpperCase()
				+ value.substring(1);
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (entity == null) {
			return null;
		}
		try {
			Method method = this.clazz.getMethod("get" + this.getMethodName(attribute));
			Object invoke = method.invoke(entity);
			return invoke;
		} catch (ReflectiveOperationException e) {
		}
		try {
			Method method = this.clazz.getMethod(attribute);
			Object invoke = method.invoke(entity);
			return invoke;
		} catch (ReflectiveOperationException e) {
		}
		// No Method Found
		try {
			Field field = this.clazz.getDeclaredField(attribute);
			if(!field.isAccessible()) {
				field.setAccessible(true);
				Object invoke = field.get(entity);
				field.setAccessible(false);
				return invoke;
			}
			Object invoke = field.get(entity);
			return invoke;
		} catch (Exception e2) {
			System.out.println(e2);
		}
		return null;
	}

	private boolean setNewValue(Object entity, String methodName, Object value) {
		try {
			this.clazz.getMethod(methodName, value.getClass()).invoke(entity,
					value);
			return true;
		} catch (ReflectiveOperationException e) {
		}
		// maybe a number
		try {
			int intValue = Integer.parseInt((String) value);
			this.clazz.getMethod(methodName, int.class)
					.invoke(entity, intValue);
			return true;
		} catch (Exception e) {
		}
		// maybe a double
		try {
			double doubleValue = Double.parseDouble((String) value);
			this.clazz.getMethod(methodName, double.class).invoke(entity,
					doubleValue);
			return true;
		} catch (Exception e) {
		}
		// maybe a float
		try {
			float floatValue = Float.parseFloat((String) value);
			this.clazz.getMethod(methodName, float.class).invoke(entity,
					floatValue);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (entity == null) {
			return false;
		}
		if (setNewValue(entity, "set" + this.getMethodName(attribute), value)) {
			return true;
		}
		if (setNewValue(entity, "with" + this.getMethodName(attribute),
				value)){
			return true;
		}
		// No Method Found
		try {
			Field field = this.clazz.getDeclaredField(attribute);
			if(!field.isAccessible()) {
				field.setAccessible(true);
				field.set(entity, value);
				field.setAccessible(false);
				return true;
			}
			field.set(entity, value);
			return true;
		} catch (Exception e2) {
			System.out.println(e2);
		}
		return false;
	}
	protected Class<?> getClassForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
	
	boolean isValidMethod(String methodName ) {
		return (methodName.startsWith("get")
				&& !badProperties.contains(methodName)
				&& !"".equals(methodName.trim()));
	}
	
	public static GenericCreator create(IdMap map, Class<?> instance) {
		SendableEntityCreator creator = map.getCreator(instance.getName(), true);
		if(creator != null) {
			return (GenericCreator) creator;
		}
		
		GenericCreator genericCreator = new GenericCreator();
		genericCreator.withClass(instance);
		map.with(genericCreator);

		// VODOO
		try {
			Method[] methods = instance.getMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				if(genericCreator.isValidMethod(methodName)) {
					Class<?> child = method.getReturnType();
					if(EntityUtil.isPrimitiveType(child.getName()) == false) {
						try {
							Type types = child.getGenericSuperclass();
							if(types != null) {
								ParameterizedType genericSuperclass = (ParameterizedType) types;
								if(genericSuperclass.getActualTypeArguments().length>0){
									Type type = genericSuperclass.getActualTypeArguments()[0];
									child = Class.forName(type.getTypeName());
								}
							}
						}catch(Exception e) {
							// Try to find SubClass for Set
						}
						create(map, child);
					}
				}
			}
			Field[] fields = instance.getDeclaredFields();
			for (Field field : fields) {
				Class<?> child = field.getType();
				if(EntityUtil.isPrimitiveType(child.getName()) == false) {
					try {
						Type types = field.getGenericType();
						if(types != null) {
							ParameterizedType genericSuperclass = (ParameterizedType) types;
							if(genericSuperclass.getActualTypeArguments().length>0){
								Type type = genericSuperclass.getActualTypeArguments()[0];
								child = Class.forName(type.getTypeName());
							}
						}
					}catch(Exception e) {
						// Try to find SubClass for Set
					}
					create(map, child);
				}
			}
		}catch (Exception e) {
			System.out.println(e);
		}
		return genericCreator;
	}
}
