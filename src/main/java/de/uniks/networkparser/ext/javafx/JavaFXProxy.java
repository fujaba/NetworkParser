package de.uniks.networkparser.ext.javafx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JavaFXProxy implements InvocationHandler{
	private Object obj;

	public JavaFXProxy(Object obj) {
		this.obj = obj;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Class<?>[] newTypes = convertTypes(method.getParameterTypes());
		Method proxyMethod = this.obj.getClass().getMethod(method.getName(), newTypes);
		if(proxyMethod != null) {
			return proxyMethod.invoke(this.obj, args);
		}
		return null;
//		return method.invoke(this.obj, args);
	}
	
	private Class<?>[] convertTypes(Class<?> [] types) {
		Class<?>[] newTypes=new Class<?>[types.length];
		for(int i=0;i<newTypes.length;i++) {
			if(types[i].getName().indexOf("javafx.")>=0) {
				newTypes[i] = Object.class;
			} else {
				newTypes[i] = types[i];
			}
		}
		return newTypes;
	}

	public Object getProxyObject() {
		return obj;
	}

	public void setProxyObject(Object obj) {
		this.obj = obj;
	}
}
