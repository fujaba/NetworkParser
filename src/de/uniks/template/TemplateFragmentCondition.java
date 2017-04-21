package de.uniks.template;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.logic.Equals;

public class TemplateFragmentCondition implements ParserCondition{
	public static final String TAG="template"; 
	
	@Override
	public boolean isExpression() {
		return true;
	}

	@Override
	public String getKey() {
		return TAG;
	}
	
	@Override
	public boolean update(Object value) {
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			System.out.println("ss");
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getValue(LocalisationInterface variables) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TemplateFragmentCondition create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		// TODO Auto-generated method stub
		return this;
	}



}
