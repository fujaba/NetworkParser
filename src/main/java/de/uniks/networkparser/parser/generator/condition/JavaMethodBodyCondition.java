package de.uniks.networkparser.parser.generator.condition;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.logic.CustomCondition;

public class JavaMethodBodyCondition extends CustomCondition<Method> {
	public static final String TAG="methodbody";

	@Override
	public String getKey() {
		return TAG;
	}
		
	@Override
	public ParserCondition getSendableInstance(boolean prototyp) {
		return new JavaMethodBodyCondition();
	}

	@Override
	public Object getValue(SendableEntityCreator creator, Method method) {
		String result;
		if (method.getBody() == null) {
			String defaultValue = EntityUtil.getDefaultValue(method.getReturnType().getName(false));
			if (defaultValue.equals("void")) {
				result = "";
			} else {
				result = "return " + defaultValue + ";";
			}
		} else {
			result = method.getBody();
		}
		return result;
	}
}
