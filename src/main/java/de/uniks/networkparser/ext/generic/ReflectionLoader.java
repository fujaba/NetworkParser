package de.uniks.networkparser.ext.generic;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ReflectionLoader {
	public static final Class<?> CHANGELISTENER;
	public static final Class<?> NODE;
	public static final Class<?> OBSERVABLEVALUE;
	public static final Class<?> INVALIDATIONLISTENER;
	public static final Class<?> BINDINGS;
	
	public static final Class<?> PROPERTY;
	public static final Class<?> SIMPLEOBJECTPROPERTY;
	public static final Class<?> STRINGPROPERTY;
	public static final Class<?> BOOLEANPROPERTY;
	public static final Class<?> INTEGERPROPERTY;
	public static final Class<?> DOUBLEPROPERTY;

	public static final Class<?> COLOR;
	public static final Class<?> COLORPICKER;
	public static final Class<?> TEXTFIELD;
	public static final Class<?> COMBOBOX;
	public static final Class<?> LABEL;
	public static final Class<?> CHECKBOX;
	public static final Class<?> RADIOBUTTON;
	public static final Class<?> SYSTEMTRAY;
//	public static final Class<?> JUNIT = getClass("org.junit.Assert");
	
	static {
		CHANGELISTENER = getClass("javafx.beans.value.ChangeListener");
		if(CHANGELISTENER != null) {
			NODE = getClass("javafx.scene.Node");
			OBSERVABLEVALUE = getClass("javafx.beans.value.ObservableValue");
			INVALIDATIONLISTENER = getClass("javafx.beans.InvalidationListener");
			BINDINGS = getClass("javafx.beans.binding.Bindings");
			
			PROPERTY = getClass("javafx.beans.property.Property");
			SIMPLEOBJECTPROPERTY = getClass("javafx.beans.property.SimpleObjectProperty");
			STRINGPROPERTY = getClass("javafx.beans.property.StringProperty");
			BOOLEANPROPERTY = getClass("javafx.beans.property.BooleanProperty");
			INTEGERPROPERTY = getClass("javafx.beans.property.IntegerProperty");
			DOUBLEPROPERTY = getClass("javafx.beans.property.DoubleProperty");

			COLOR = getClass("javafx.scene.paint.Color");
			COLORPICKER = getClass("javafx.scene.control.ColorPicker");
			TEXTFIELD = getClass("javafx.scene.control.TextField");
			COMBOBOX = getClass("javafx.scene.control.ComboBox");
			LABEL = getClass("javafx.scene.control.Label");
			CHECKBOX = getClass("javafx.scene.control.CheckBox");
			RADIOBUTTON = getClass("javafx.scene.control.RadioButton");
			SYSTEMTRAY = getClass("java.awt.SystemTray");
		} else {
			NODE = null;
			OBSERVABLEVALUE = null;
			INVALIDATIONLISTENER = null;
			BINDINGS = null;
			
			PROPERTY = null;
			SIMPLEOBJECTPROPERTY = null;
			STRINGPROPERTY = null;
			BOOLEANPROPERTY = null;
			INTEGERPROPERTY = null;
			DOUBLEPROPERTY = null;

			COLOR = null;
			COLORPICKER = null;
			TEXTFIELD = null;
			COMBOBOX = null;
			LABEL = null;
			CHECKBOX = null;
			RADIOBUTTON = null;
			SYSTEMTRAY = null;
		}
	}
	
	public static Object newInstance(Class<?> instance) {
		Object newInstance = null;
		try {
			newInstance = instance.newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return newInstance;
	}
	
	public static Class<?> getClass(String name) {
		try {
			return Class.forName(name, false, ReflectionLoader.class.getClassLoader());
		} catch (Throwable e) {
		}
		return null;
	}
	
	public static Object createProxy(Object proxy, Class<?>... proxys){
		return java.lang.reflect.Proxy.newProxyInstance(ReflectionLoader.class.getClassLoader(),
				proxys, new ReflectionInterfaceProxy(proxy));
	}
	
	public static Object call(String methodName, Object item, Object... arguments) {
		if(methodName == null || item == null) {
			return null;
		}
		int len=0;
		if(arguments != null) {
			if(arguments.length %2 ==1) {
				return null;
			}
			len = arguments.length / 2;
		}
		Class<?>[] methodArguments=new Class[len];
		Object[] methodArgumentsValues=new Object[len];
		int pos=0;
		for(int i=0;i<arguments.length;i+=2) {
			methodArguments[pos] = (Class<?>) arguments[i];
			methodArgumentsValues[pos] = arguments[i+1];
			pos++;
		}
		try {
			boolean staticCall =false;
			if(item instanceof Type == false) {
				staticCall = item instanceof Class<?>;
			}
			Class<?> itemClass;
			if(staticCall) {
				itemClass = ((Class<?>) item);
			}else {
				itemClass = item.getClass();
			}
			Method method = itemClass.getMethod(methodName, methodArguments);
			if(method != null) {
				if(staticCall) {
					return method.invoke(null, methodArgumentsValues);
				}
				method.setAccessible(true);
				return method.invoke(item, methodArgumentsValues);
			}				
		} catch (Exception e) {
		}
		return null;
	}
}
