package de.uniks.networkparser.ext.javafx;

import java.lang.reflect.Method;
import de.uniks.networkparser.ext.javafx.controller.ModelListenerProperty;

public class JavaFXClasses {
	public static final Class<?> CHANGELISTENER = getClass("javafx.beans.value.ChangeListener");
	public static final Class<?> NODE = getClass("javafx.scene.Node");
	public static final Class<?> OBSERVABLEVALUE = getClass("javafx.beans.value.ObservableValue");
	public static final Class<?> INVALIDATIONLISTENER = getClass("javafx.beans.InvalidationListener");
	public static final Class<?> BINDINGS = getClass("javafx.beans.binding.Bindings");
	
	public static final Class<?> PROPERTY = getClass("javafx.beans.property.Property");
	public static final Class<?> SIMPLEOBJECTPROPERTY = getClass("javafx.beans.property.SimpleObjectProperty");
	public static final Class<?> STRINGPROPERTY = getClass("javafx.beans.property.StringProperty");
	public static final Class<?> BOOLEANPROPERTY = getClass("javafx.beans.property.BooleanProperty");
	public static final Class<?> INTEGERPROPERTY = getClass("javafx.beans.property.IntegerProperty");
	public static final Class<?> DOUBLEPROPERTY = getClass("javafx.beans.property.DoubleProperty");

	public static final Class<?> COLOR = getClass("javafx.scene.paint.Color");
	public static final Class<?> COLORPICKER = getClass("javafx.scene.control.ColorPicker");
	public static final Class<?> TEXTFIELD = getClass("javafx.scene.control.TextField");
	public static final Class<?> COMBOBOX = getClass("javafx.scene.control.ComboBox");
	public static final Class<?> LABEL = getClass("javafx.scene.control.Label");
	public static final Class<?> CHECKBOX = getClass("javafx.scene.control.CheckBox");
	public static final Class<?> RADIOBUTTON = getClass("javafx.scene.control.RadioButton");

	
	public static Object newInstance(Class<?> instance) {
		Object newInstance = null;
		try {
			newInstance = JavaFXClasses.SIMPLEOBJECTPROPERTY.newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return newInstance;
	}
	
	public static Class<?> getClass(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
	
	public static Object createProxy(Object proxy, Class<?>... proxys){
		return java.lang.reflect.Proxy.newProxyInstance(ModelListenerProperty.class.getClassLoader(),
				proxys, new JavaFXProxy(proxy));
	}
	
	public static Object call(String methodName, Object item, Object... arguments) {
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
			boolean staticCall = item instanceof Class<?>;
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
			e.printStackTrace();
		}
		return null;
	}
}
