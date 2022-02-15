package de.uniks.networkparser.parser;

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
import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.logic.CustomCondition;

/**
 * The Class JavaMethodBodyCondition.
 *
 * @author Stefan
 */
public class JavaMethodBodyCondition extends CustomCondition<Method> {
	
	/** The Constant TAG. */
	public static final String TAG = "methodbody";

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	@Override
	public String getKey() {
		return TAG;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public ParserCondition getSendableInstance(boolean prototyp) {
		return new JavaMethodBodyCondition();
	}

	/**
	 * Gets the value.
	 *
	 * @param creator the creator
	 * @param method the method
	 * @return the value
	 */
	@Override
	public Object getValue(SendableEntityCreator creator, Method method) {
		String result = "";
		if (method == null) {
			return result;
		}
		if (method.getBody() == null) {
			String defaultValue = StringUtil.getDefaultValue(method.getReturnType().getName(false));
			if (defaultValue.equals("void") == false) {
				result = "return " + defaultValue + ";";
			}
		} else {
			result = method.getBody();
		}

		/* Check for {} */
		String trim = result.trim();
		if (trim.startsWith("{") && trim.endsWith("}")) {
			/* Its Ok full body */
			return result;
		}
		return "    {" + BaseItem.CRLF + "      " + result + BaseItem.CRLF + "    }" + BaseItem.CRLF;
	}
}
