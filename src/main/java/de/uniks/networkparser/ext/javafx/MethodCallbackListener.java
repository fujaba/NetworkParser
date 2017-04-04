package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class MethodCallbackListener implements ObjectCondition {
	private Object element;
	private String methodName;
	public MethodCallbackListener (Object element, String methodName) {
		this.element = element;
		this.methodName = methodName;
	}
	@Override
	public boolean update(Object value) {
		ReflectionLoader.call(this.methodName, element, value);
		return true;
//		throw new RuntimeException("The function cannot be found on the given Object...");
	}
}
