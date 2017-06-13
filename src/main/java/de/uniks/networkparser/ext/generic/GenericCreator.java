package de.uniks.networkparser.ext.generic;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
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
				String name = getValidMethod(methodName);
				if(name != null) {
					fieldNames.add(name.toLowerCase());
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
	public Object getSendableInstance(boolean prototype) {
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
		} catch (ReflectiveOperationException e) {
//			System.out.println(e);
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
			int intValue = Integer.parseInt("" + value);
			this.clazz.getMethod(methodName, int.class)
					.invoke(entity, intValue);
			return true;
		} catch (ReflectiveOperationException e) {
		} catch (NumberFormatException e) {
		}
		// maybe a double
		try {
			double doubleValue = Double.parseDouble("" + value);
			this.clazz.getMethod(methodName, double.class).invoke(entity,
					doubleValue);
			return true;
		} catch (ReflectiveOperationException e) {
		} catch (NumberFormatException e) {
		}
		// maybe a float
		try {
			float floatValue = Float.parseFloat("" + value);
			this.clazz.getMethod(methodName, float.class).invoke(entity,
					floatValue);
			return true;
		} catch (ReflectiveOperationException e) {
		} catch (NumberFormatException e) {
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
		if (setNewValue(entity, "with" + this.getMethodName(attribute), value)){
			return true;
		}
		// May be Collection???
		if(attribute.endsWith("s")) {
			if (setNewValue(entity, "addTo" + this.getMethodName(attribute), value)){
				return true;
			}	
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
		} catch (ReflectiveOperationException e) {
			System.out.println(e);
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

	String getValidMethod(String methodName ) {
		String name = null;
		if(badProperties.contains(methodName) == false) {
			if(methodName.startsWith("get")) {
				name = methodName.substring(3);
			} else if(methodName.startsWith("is")) {
				name = methodName.substring(2);
			}
			if(name == null || "".equals(name.trim())) {
				return null;
			}
		}
		return name;
	}
	public static GenericCreator create(IdMap map, String className) {
		try {
			return create(map, Class.forName(className));
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
	public static GenericCreator create(IdMap map, Class<?> instance) {
		SendableEntityCreator creator = map.getCreator(instance.getName(), true, null);
		if(creator != null) {
			return (GenericCreator) creator;
		}

		GenericCreator genericCreator = new GenericCreator();
		// Add all Properties
		try {
			if(instance.isInterface() == false) {
				genericCreator.withItem(instance.newInstance());
			}
		} catch (Exception e1) {
			genericCreator.withClass(instance);
		}

		map.add(genericCreator);

		// VODOO
		Method[] methods = instance.getMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			if (genericCreator.getValidMethod(methodName) != null) {
				Class<?> child = method.getReturnType();
				if (EntityUtil.isPrimitiveType(child.getName()) == false) {
					try {
						Type types = child.getGenericSuperclass();
						if (types != null && types instanceof ParameterizedType) {
							ParameterizedType genericSuperclass = (ParameterizedType) types;
							if (genericSuperclass.getActualTypeArguments().length > 0) {
								Type type = genericSuperclass.getActualTypeArguments()[0];
								String typeClass = ""+ReflectionLoader.call("getTypeName", type);
								if(typeClass.length() > 0) {
									child = Class.forName(typeClass);
								}
							}
						}
					} catch (ReflectiveOperationException e) {
						// Try to find SubClass for Set
					}
					if(child.isInterface() == false && child instanceof Class<?> == false) {
						create(map, child);
					}
				}
			}
		}
		Field[] fields = instance.getDeclaredFields();
		for (Field field : fields) {
			Class<?> child = field.getType();
			if (EntityUtil.isPrimitiveType(child.getName()) == false) {
				Type types = field.getGenericType();
				if (types != null && types instanceof ParameterizedType) {
					ParameterizedType genericSuperclass = (ParameterizedType) types;
					if (genericSuperclass.getActualTypeArguments().length > 0) {
						Type type = genericSuperclass.getActualTypeArguments()[0];
						Object childClass = ReflectionLoader.call("getTypeName", type);
						if(childClass != null) {
							child = ReflectionLoader.getClass(""+childClass);
						}
					}
				}
				create(map, child);
			}
		}
		return genericCreator;
	}
}
