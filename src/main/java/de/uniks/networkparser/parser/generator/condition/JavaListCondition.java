package de.uniks.networkparser.parser.generator.condition;

import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.BooleanList;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.logic.CustomCondition;

public class JavaListCondition extends CustomCondition<Attribute>{
	public static final String TAG="listType";

	@Override
	public String getKey() {
		return TAG;
	}

	@Override
	public ParserCondition getSendableInstance(boolean isExpression) {
		return new JavaListCondition();
	}

	@Override
	public Object getValue(SendableEntityCreator creator, Attribute member) {
		String attributeType = member.getType(true);
		if (attributeType.equals("boolean")) {
			return addImport(creator, BooleanList.class);
		} else if (" long Long short Short int Integer byte Byte float Float double Double ".indexOf(" " + attributeType + " ") >= 0) {
			return addImport(creator, NumberList.class);
		} else if (attributeType.equals("String")) {
			return addImport(creator, StringList.class);
		} else if (attributeType.equals("char")) {
			addImport(creator, SimpleList.class);
			return "SimpleList<Character>";
		} else {
			addImport(creator, SimpleList.class);
			return "SimpleList<" + attributeType + ">";
		}
	}
}
