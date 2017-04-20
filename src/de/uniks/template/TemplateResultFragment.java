package de.uniks.template;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;

public class TemplateResultFragment implements Comparable<TemplateResultFragment>, SendableEntityCreator, ObjectCondition, LocalisationInterface {
	public static final String PROPERTY_PARENT="parent";
	public static final String PROPERTY_CHILD="child";
	
	public static final String PROPERTY_FILE="file";
	public static final String PROPERTY_MEMBER="member";
	public static final String PROPERTY_VARIABLE="variable";
	public static final String PROPERTY_HEADERS="headers";
	public static final String PROPERTY_EXPRESSION="expression";
	public static final String PROPERTY_ITEM="item";
	public static final String PROPERTY_TEMPLATE="template";
	public static final String PROPERTY_TEMPLATEMODEL="templatemodel";

	private LocalisationInterface variables;
	private SimpleList<String> header = new SimpleList<String>();
	private GraphMember member;
	private boolean expression=true;
	private SendableEntityCreator parent;
	private SimpleList<String> stack;
	
	private int key = -1;
	
	private CharacterBuffer value = new CharacterBuffer();
	
	private ObjectCondition template;
	
	@Override
	public int compareTo(TemplateResultFragment other) {
		if (other.getKey() == key) {
			if(other.getValue().equals(value)) {
				return 0;
			}
			return 1;
		}
		if (other.getKey() > key) {
			return 1;
		}
		return -1;
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
				Object object = tc.getValue(this);
				return  object != null && !object.equals("");
			} else {
				this.value.withObjects(tc.getValue(this));	
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
	public boolean removeHeader(String value) {
		return this.header.remove(value);	
	}

	public boolean setParent(SendableEntityCreator value) {
		if (this.parent != value) {
//			TemplateInterface oldValue = this.parent;
			if (this.parent != null) {
				this.parent = null;
				// oldValue.remove(this);
			}
			this.parent = value;

			if (value != null) {
				value.setValue(value, PROPERTY_CHILD, this, SendableEntityCreator.NEW);
			}
			// firePropertyChange(PROPERTY_ROOM, oldValue, value);
			return true;
		}
		return false;
	}

	public SendableEntityCreator getParent() {
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
				SendableEntityCreator item = element.getParent();
				return item.getValue(item, attribute.substring(pos+1));
			}
			return element.getParent();
		}
		if(PROPERTY_MEMBER.equalsIgnoreCase(attrName)) {
			if (pos > 0) {
				GraphMember item = element.getMember();
				return item.getValue(attribute.substring(pos + 1));
			}
			return element.getMember();
		}
		if(PROPERTY_VARIABLE.equalsIgnoreCase(attrName)) {
			if(pos>0) {
				SendableEntityCreator item = (SendableEntityCreator) element.getVariable();
				return item.getValue(item, attribute.substring(pos+1));
			}
			return element.getVariable();
		}
		if(PROPERTY_HEADERS.equalsIgnoreCase(attrName)) {
			return element.getHeaders();
		}
		if(PROPERTY_EXPRESSION.equalsIgnoreCase(attrName)) {
			return element.isExpression();
		}
		if(PROPERTY_ITEM.equalsIgnoreCase(attrName)) {
			if(this.stack != null) {
				return this.stack.last();
			}
			return null;
		}
		if(PROPERTY_TEMPLATE.equalsIgnoreCase(attrName)) {
			if(pos>0) {
				TemplateResultFragment item = element;
				return item.getValue(item, attribute.substring(pos+1));
			}
			return element;
		}
		if(PROPERTY_TEMPLATEMODEL.equalsIgnoreCase(attrName)) {
			if(pos>0) {
				TemplateResultModel item = element.getTemplateModel();
				return item.getValue(item, attribute.substring(pos+1));
			}
			return element.getTemplateModel();
		}
		if(this.member != null) {
			Object value = this.member.getValue(attrName);
			if(value != null) {
				return value;
			}
		}
//		parameters.put("imports", determineImports(clazz, templateResult));
//		parameters.put("packageName", getPackageName(clazz.getName(false)));
//		parameters.put("visibility", "public");
//		parameters.put("modifiers", determineModifiers(clazz.getModifier()));
//		parameters.put("clazzType", determineClazzType(clazz));
//		parameters.put("name", clazz.getName(true));
//		parameters.put("superclasses", determineSuperClasses(clazz, superPropertyChangeEnabled));
//		parameters.put("propertyChange", propertyChange);

		
		return element.getText(attribute, null, null);
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
	
	@Override
	public String getText(CharSequence label, Object model, Object gui) {
		// Global Variables
		if(this.variables != null) {
			String value = variables.getText(label, model, gui);
			if(value != null) {
				return value;
			}
		}
		// Global Variables
		TemplateResultModel templateModel = getTemplateModel();
		if(templateModel != null) {
			String value = templateModel.getText(label, null, null);
			if(value != null) {
				return value;
			}
		}
		return null;
	}
	
	@Override
	public String put(String label, String text) {
		if(label == null) {
			return null;
		}
		if(PROPERTY_ITEM.equalsIgnoreCase(label.toString())) {
			if(text == null) {
				if(this.stack != null) {
					this.stack.remove(this.stack.size() - 1);
					return text;
				}
			} else {
				if(this.stack == null) {
					this.stack = new SimpleList<String>();
				}
				if(this.stack.add(text)) {
					return text;
				}
			}
		}
		return null;
	}

	public TemplateResultModel getTemplateModel() {
		SendableEntityCreator item = parent;
		while(item != null) {
			item = (SendableEntityCreator) item.getValue(item, PROPERTY_PARENT);
			if(item instanceof TemplateResultModel) {
				return (TemplateResultModel)item;
			}
		}
		return null;
	}
	
	public ObjectCondition getTemplate() {
		return template;
	}
}
