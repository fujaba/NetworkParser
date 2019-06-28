package de.uniks.networkparser.ext.generic;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ReflectionInterfaceProxy implements InvocationHandler {
	private Object obj;

	public ReflectionInterfaceProxy(Object obj) {
		this.obj = obj;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (proxy == null || method == null) {
			return null;
		}
		Class<?>[] newTypes = convertTypes(method.getParameterTypes());
		Method proxyMethod = null;
		try {
			proxyMethod = this.obj.getClass().getMethod(method.getName(), newTypes);
		} catch (Exception e) {
			newTypes = convertTypesObject(newTypes);
			proxyMethod = this.obj.getClass().getMethod(method.getName(), newTypes);
		}
		if (proxyMethod != null) {
			return proxyMethod.invoke(this.obj, args);
		}
		return null;
	}

	private Class<?>[] convertTypes(Class<?>[] types) {
		if (types == null) {
			return null;
		}
		Class<?>[] newTypes = new Class<?>[types.length];
		for (int i = 0; i < newTypes.length; i++) {
			if (types[i] == null) {
				continue;
			}
			if (types[i].getName().indexOf("javafx.") >= 0) {
				newTypes[i] = Object.class;
			} else if (types[i].getName().indexOf("java.awt.event.") >= 0) {
				newTypes[i] = Object.class;
			} else {
				newTypes[i] = types[i];
			}
		}
		return newTypes;
	}

	private Class<?>[] convertTypesObject(Class<?>[] types) {
		if (types == null) {
			return null;
		}
		Class<?>[] newTypes = new Class<?>[types.length];
		for (int i = 0; i < newTypes.length; i++) {
			newTypes[i] = Object.class;
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
