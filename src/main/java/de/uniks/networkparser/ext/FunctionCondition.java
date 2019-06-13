package de.uniks.networkparser.ext;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.parser.ParserEntity;
import de.uniks.networkparser.parser.TemplateResultFragment;

public class FunctionCondition implements ParserCondition {
	public static final String KEY = "func";
	private ObjectCondition value;

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Object getSendableInstance(boolean isExpression) {
		return new FunctionCondition();
	}

	@Override
	public Object getValue(LocalisationInterface variables) {
		return null;
	}

	@Override
	public boolean isExpression() {
		return false;
	}

	@Override
	public boolean update(Object evt) {
		if (evt instanceof TemplateResultFragment) {
			exeuteTemplate((TemplateResultFragment) evt);
		}
		if (evt instanceof SimpleEvent) {
			SimpleEvent simpleEvt = (SimpleEvent) evt;
			if (simpleEvt.getSource() instanceof ParserEntity) {
			}
		}
		return true;
	}

	public boolean exeuteTemplate(TemplateResultFragment fragment) {
		if(fragment == null || this.value == null) {
			return false;
		}
		CharacterBuffer original = fragment.cloneValue(new CharacterBuffer());
		
		this.value.update(fragment);

		CharacterBuffer newValue = fragment.cloneValue(original);
		String value2 = newValue.toString();
		int start = value2.indexOf('(');
		int end = value2.lastIndexOf(')');
		if(start<0 || end <0) {
			return false;
		}
		String function = value2.substring(0, start);
		int classPos = function.lastIndexOf(".");
		if(classPos< 0) {
			return false;
		}
		String className = function.substring(0, classPos);
		String method = function.substring(classPos + 1);
		String[] params;
		if(start+1<end) {
			params = value2.substring(start+1, end).split(",");
		}else {
			params = new String[0];
		}
		Object[] values = new Object[params.length];
		for(int i=0;i<values.length;i++) {
			values[i ]= parseParam(params[i]);
		}
		Class<?> class1 = ReflectionLoader.getClass(className);
		if(class1 == null) {
			return false;
		}
		Object returnValue = ReflectionLoader.call(class1, method, values);
		if(returnValue != null) {
			String result = ""+returnValue;
			fragment.append(result);
		}
		return true;
	}
	
	
	private Object parseParam(String item) {
		if(item == null) {
			return null;
		}
		try {
			return Byte.valueOf(item);
		}catch (NumberFormatException e) {
		}
		try {
			return Integer.valueOf(item);
		}catch (NumberFormatException e) {
		}
		try {
			return Float.valueOf(item);
		}catch (NumberFormatException e) {
		}
		try {
			return Double.valueOf(item);
		}catch (NumberFormatException e) {
		}
		return item;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		// Parsing
		if (buffer == null || parser == null ) {
			return;
		}
		buffer.skip();
		value = parser.parsing(buffer, customTemplate, false, true, "}");

		buffer.skipTo(SPLITEND, false);
		buffer.skip();
		buffer.skip();
	}
}
