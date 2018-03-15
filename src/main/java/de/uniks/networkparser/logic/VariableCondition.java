package de.uniks.networkparser.logic;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

public class VariableCondition implements ParserCondition{
	private CharSequence value;
	private boolean expression;

	@Override
	public boolean update(Object value) {
		if(value instanceof ObjectCondition) {
			return ((ObjectCondition)value).update(this);
		}
		if(value instanceof LocalisationInterface) {
			LocalisationInterface variables = (LocalisationInterface) value;
			Object object = getValue(variables);
			return  object != null && !object.equals("");
		}
		if(this.value == null) {
			return value == null;
		}
		return this.value.equals(value);
	}

	public VariableCondition withValue(CharSequence value) {
		this.value = value;
		return this;
	}
	// key = Variable
	// value = String

	//variable = string
	//Variable = String
	// VARIABLE = STRING
	// vAriable = nix
	public Object getValue(LocalisationInterface value) {
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator variables = (SendableEntityCreator) value;
			String key = this.value.toString();
			// SWITCH FOR #
			int pos = key.indexOf('#');
			String v = null;
			String format = null;
			boolean shortName=true;
			if(pos >= 0) {
				v = key.substring(0, pos);
				format = key.substring(pos+1);
			} else {
				v = key;
			}
			pos = v.indexOf("(");
			if(pos>0) {
				String temp = v.substring(pos+1, v.length() - 1);
				v = key.substring(0, pos);
				shortName = Boolean.valueOf(temp);
			}
			Object object = variables.getValue(variables, v);

			if(object == null && this.expression == false) {
				return key;
			}
			if(object instanceof DataType) {
				object = ((DataType)object).getName(shortName);
			}
			if(object instanceof String) {
				return replaceText(v, format, (String)object);
			}
			if (object instanceof Boolean) {
				return "" + object;
			}
			return object;
		}
		if(value != null && this.value != null) {
			return value.getText(this.value, null, null);
		}
		if(this.value == null) {
			return null;
		}
		return null;
	}

	public String replaceText(String name, String format, String value) {
		if(name == null) {
			return name;
		}
		boolean upper=false;
		boolean firstUpper=false;
		boolean small=false;
		int startIndex;
		int i;
		// other.NAME
		startIndex = name.lastIndexOf('.');
		// startIndex is last '.' therefore next proper index is startIndex + 1
		startIndex++;
		for(i = startIndex; i<name.length(); i++) {
			if(name.charAt(i)>='A' && name.charAt(i)<='Z') {
				upper = true;

				firstUpper = startIndex==i;
			} else if(name.charAt(i)>='a' && name.charAt(i)<='z') {
				small = true;
			}
		}
//		if ((small && upper==false) || "tolower".equalsIgnoreCase(format)) {
		if ("tolower".equalsIgnoreCase(format)) {
			return value.toLowerCase();
		}
		if((small == false && upper) || "toupper".equalsIgnoreCase(format)) {
			return value.toUpperCase();
		}
		if(firstUpper || "firstUpper".equalsIgnoreCase(format)) {
			return EntityUtil.upFirstChar(value);
		}
		if(format == null) {
			return value;
		}
		if(format.startsWith("sub(")) {
			String substring = format.substring(4, format.length() - 1);
			String[] item = substring.split(",");
			int start=0;
			int end=value.length() - 1;
			if(item.length>0) {
				start = Integer.valueOf(item[0].trim());
			}
			if(item.length>1) {
				int temp = Integer.valueOf(item[1].trim());;
				if(temp < end) {
					end = temp;
				}
			}
			return value.substring(start, end);
		}
		if(format.startsWith("contains(")) {
			String substring = format.substring(9, format.length() - 1);
			boolean boolValue = value.indexOf(substring)>=0;
			if(boolValue) {
				return "true";
			}
			return "";
		}
		return value;
	}


	public VariableCondition withExpression(boolean value) {
		this.expression = value;
		return this;
	}

	public static VariableCondition create(CharSequence sequence, boolean expression) {
		return new VariableCondition().withValue(sequence).withExpression(expression);
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		this.value = buffer.nextToken(true, ' ', '}');
	}

	@Override
	public boolean isExpression() {
		return expression;
	}
	@Override
	public String getKey() {
		return null;
	}

	@Override
	public String toString() {
		return "{{"+this.value+"}}";
	}

	@Override
	public VariableCondition getSendableInstance(boolean prototyp) {
		return new VariableCondition();
	}
}
