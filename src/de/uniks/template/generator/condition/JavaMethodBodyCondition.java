package de.uniks.template.generator.condition;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

public class JavaMethodBodyCondition extends CustomCondition<Method> {
	public static final String TAG="methodbody";

	@Override
	public String getKey() {
		return TAG;
	}

	// {{#BODY}}
	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		// SKIP }}
		buffer.skipTo(SPLITEND, true);
		buffer.skip();
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
