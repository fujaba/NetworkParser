package de.uniks.template;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.list.SimpleList;

public class TemplateResultFragment implements Comparable<TemplateResultFragment>, TemplateInterface, ObjectCondition {
	public static final String PROPERTY_FILE="file";
	public static final String PROPERTY_MEMBER="member";
	public static final String PROPERTY_VARIABLE="variable";
	public static final String PROPERTY_HEADERS="headers";
	public static final String PROPERTY_EXPRESSION="expression";
	private LocalisationInterface variables;
	private SimpleList<String> header = new SimpleList<String>();
	private GraphMember member;
	private boolean expression=true;
	private TemplateInterface parent;
//	private Template template;
	
	private int key = -1;
	
	private CharacterBuffer value = new CharacterBuffer();
	
	@Override
	public int compareTo(TemplateResultFragment other) {
		if (other.getKey() == key) {
			if(other.getValue().equals(value)) {
				return 0;
			}
			return -1;
		}
		if (other.getKey() > key) {
			return -1;
		}
		return 1;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
	public TemplateResultFragment withKey(int key) {
		setKey(key);
		return this;
	}

	public CharacterBuffer getValue() {
		return value;
	}

	public void setValue(CharacterBuffer value) {
		this.value = value;
	}

	public TemplateResultFragment withValue(CharacterBuffer value) {
		setValue(value);
		return this;
	}
	
	@Override
	public String toString() {
		return "" + key;
	}

	@Override
	public boolean add(TemplateInterface result) {
		return false;
	}
	
	public TemplateResultFragment withVariable(LocalisationInterface list) {
		this.variables = list;
		return this;
	}
	
	@Override
	public boolean update(Object value) {
		if(value instanceof ObjectCondition == false) {
			return false;
		}
		if(value instanceof ParserCondition) {
			ParserCondition tc = (ParserCondition) value;
			if(this.expression || tc.isExpression()) {
				return tc.update(variables);
			} else {
				this.value.with(tc.getValue(variables));	
			}
		}
		return true;
	}
	
	public CharacterBuffer getResult() {
		return value;
	}

	public TemplateResultFragment withMember(GraphMember member) {
		this.member = member;
		return this;
	}
	
	public GraphMember getMember() {
		return member;
	}
	
	public TemplateResultFragment withExpression(boolean value) {
		this.expression = value;
		return this;
	}

	public boolean addHeader(String value) {
		return this.header.add(value);	
	}

	@Override
	public boolean setParent(TemplateInterface value) {
		if (this.parent != value) {
//			TemplateInterface oldValue = this.parent;

			if (this.parent != null) {
				this.parent = null;
				// oldValue.remove(this);
			}

			this.parent = value;

			if (value != null) {
				value.add(this);
			}
			// firePropertyChange(PROPERTY_ROOM, oldValue, value);
			return true;
		}
		return false;
	}

	@Override
	public TemplateInterface getParent() {
		return parent;
	}
	
	@Override
	public String[] getProperties() {
		return new String[] {PROPERTY_FILE, PROPERTY_MEMBER, PROPERTY_VARIABLE, PROPERTY_HEADERS};
	}
	
	@Override
	public Object getValue(Object entity, String attribute) {
		if(entity instanceof TemplateResultFragment == false) {
			return null;
		}
		TemplateResultFragment element = (TemplateResultFragment) entity;
		int pos = attribute.indexOf('.');
		String attrName;
		if(pos>0) {
			attrName = attribute.substring(0, pos);
		}else {
			attrName = attribute;
		}
		if(PROPERTY_FILE.equalsIgnoreCase(attrName)) {
			if(pos>0) {
				TemplateInterface item = element.getParent();
				return item.getValue(item, attribute.substring(pos+1));
			}
			return element.getParent();
		}
		if(PROPERTY_MEMBER.equalsIgnoreCase(attrName)) {
			return element.getMember();
		}
		if(PROPERTY_VARIABLE.equalsIgnoreCase(attrName)) {
			return element.getVariable();
		}
		if(PROPERTY_HEADERS.equalsIgnoreCase(attrName)) {
			return element.getHeaders();
		}
		if(PROPERTY_EXPRESSION.equalsIgnoreCase(attrName)) {
			return element.isExpression();
		}
		return null;
	}

	public LocalisationInterface getVariable() {
		return variables;
	}
	
	public SimpleList<String> getHeaders() {
		return header;
	}
	
	public boolean isExpression() {
		return expression;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TemplateResultFragment();
	}


	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(entity instanceof TemplateResultFragment == false) {
			return false;
		}
		TemplateResultFragment element = (TemplateResultFragment) entity;
//		public static final String PROPERTY_FILE="file";
//		public static final String PROPERTY_MEMBER="member";
//		public static final String PROPERTY_VARIABLE="variable";
//		public static final String PROPERTY_HEADERS="headers";
//		public static final String PROPERTY_EXPRESSION="expression";
		if(PROPERTY_HEADERS.equalsIgnoreCase(attribute)) {
			element.addHeader(""+value);
			return true;
		}
		return false;
	}

}
