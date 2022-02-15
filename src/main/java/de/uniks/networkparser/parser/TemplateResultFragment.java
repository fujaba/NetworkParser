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
import java.util.List;

import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphSimpleSet;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateItem;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class TemplateResultFragment.
 *
 * @author Stefan
 */
public class TemplateResultFragment
		implements Comparable<TemplateResultFragment>, SendableEntityCreator, ObjectCondition, LocalisationInterface {
	
	/** The Constant PROPERTY_PARENT. */
	public static final String PROPERTY_PARENT = "parent";
	
	/** The Constant PROPERTY_CHILD. */
	public static final String PROPERTY_CHILD = "child";
	
	/** The Constant PROPERTY_CLONE. */
	public static final String PROPERTY_CLONE = "clone";

	/** The Constant FINISH_GENERATE. */
	public static final String FINISH_GENERATE = "generate";

	/** The Constant PROPERTY_FILE. */
	public static final String PROPERTY_FILE = "file";
	
	/** The Constant PROPERTY_KEY. */
	public static final String PROPERTY_KEY = "key";
	
	/** The Constant PROPERTY_MEMBER. */
	public static final String PROPERTY_MEMBER = "member";
	
	/** The Constant PROPERTY_VARIABLE. */
	public static final String PROPERTY_VARIABLE = "variable";
	
	/** The Constant PROPERTY_HEADERS. */
	public static final String PROPERTY_HEADERS = "headers";
	
	/** The Constant PROPERTY_EXPRESSION. */
	public static final String PROPERTY_EXPRESSION = "expression";
	
	/** The Constant PROPERTY_ITEM. */
	public static final String PROPERTY_ITEM = "item";
	
	/** The Constant PROPERTY_ITEMPOS. */
	public static final String PROPERTY_ITEMPOS = "itempos";
	
	/** The Constant PROPERTY_TEMPLATE. */
	public static final String PROPERTY_TEMPLATE = "template";
	
	/** The Constant PROPERTY_TEMPLATEMODEL. */
	public static final String PROPERTY_TEMPLATEMODEL = "templatemodel";
	
	/** The Constant PROPERTY_CURRENTMEMBER. */
	public static final String PROPERTY_CURRENTMEMBER = "currentMember";

	private LocalisationInterface variables;
	private SimpleSet<String> header = null;
	private TemplateItem member;
	private boolean expression = true;
	private boolean useImport;
	private SendableEntityCreator parent;
	private SimpleList<Object> stack;
	private SimpleList<Object> notify;
	private SimpleList<Integer> pos;

	private int key = -1;

	private CharacterBuffer value = new CharacterBuffer();
	private ObjectCondition template;
	private String name;

	/**
	 * Compare to.
	 *
	 * @param other the other
	 * @return the int
	 */
	@Override
	public int compareTo(TemplateResultFragment other) {
		if (other == null) {
			return 1;
		}
		if (other.getKey() == key) {
			if (other.getValue().equals(value)) {
				return 0;
			}
			return -1;
		}
		if (other.getKey() > key) {
			return -1;
		}
		return 1;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public int getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean setKey(int key) {
		if (key != this.key) {
			this.key = key;
			return true;
		}
		return false;
	}

	/**
	 * With key.
	 *
	 * @param key the key
	 * @return the template result fragment
	 */
	public TemplateResultFragment withKey(int key) {
		setKey(key);
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public CharacterBuffer getValue() {
		return value;
	}

	/**
	 * Clone value.
	 *
	 * @param newValue the new value
	 * @return the character buffer
	 */
	public CharacterBuffer cloneValue(CharacterBuffer newValue) {
		CharacterBuffer oldValue = value;
		if (oldValue == null) {
			oldValue = new CharacterBuffer();
		}
		this.value = newValue;
		return oldValue;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(CharacterBuffer value) {
		this.value = value;
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the template result fragment
	 */
	public TemplateResultFragment withValue(CharacterBuffer value) {
		setValue(value);
		return this;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "" + key;
	}

	/**
	 * With variable.
	 *
	 * @param list the list
	 * @return the template result fragment
	 */
	public TemplateResultFragment withVariable(LocalisationInterface list) {
		this.variables = list;
		return this;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (!(value instanceof ObjectCondition)) {
			return false;
		}
		if (value instanceof ParserCondition) {
			ParserCondition tc = (ParserCondition) value;

			Object result = tc.getValue(this);
			if (this.expression || tc.isExpression()) {
				if (result == null) {
					return false;
				}
				if (result instanceof Boolean) {
					return (Boolean) result;
				}
				if (result instanceof String) {
					return !result.equals("");
				}
				return false;
			} else {
				/* Check Stack */
				this.value.withObjects(result);
				if (this.stack != null) {
					Object last = this.stack.last();
					if (last instanceof SendableEntityCreator) {
						((SendableEntityCreator) last).setValue(member, ParserCondition.NOTIFY, result,
								SendableEntityCreator.NEW);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public CharacterBuffer getResult() {
		return value;
	}

	/**
	 * With member.
	 *
	 * @param member the member
	 * @return the template result fragment
	 */
	public TemplateResultFragment withMember(TemplateItem member) {
		this.member = member;
		return this;
	}

	/**
	 * Gets the member.
	 *
	 * @return the member
	 */
	public TemplateItem getMember() {
		return member;
	}

	/**
	 * Gets the current member.
	 *
	 * @return the current member
	 */
	public TemplateItem getCurrentMember() {
		if (this.stack != null) {
			Object item = this.stack.last();
			if (item instanceof GraphMember) {
				return (GraphMember) item;
			}
		}
		return this.member;
	}

	/**
	 * With expression.
	 *
	 * @param value the value
	 * @return the template result fragment
	 */
	public TemplateResultFragment withExpression(boolean value) {
		this.expression = value;
		return this;
	}

	/**
	 * Adds the header.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean addHeader(String value) {
		if (this.header == null) {
			this.header = new SimpleSet<String>();
		}
		return this.header.add(value);
	}

	/**
	 * With header.
	 *
	 * @param value the value
	 * @return the template result fragment
	 */
	public TemplateResultFragment withHeader(String value) {
		addHeader(value);
		return this;
	}

	/**
	 * Removes the header.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean removeHeader(String value) {
		if (this.header == null) {
			return true;
		}
		return this.header.remove(value);
	}

	/**
	 * Sets the parent.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setParent(SendableEntityCreator value) {
		if (this.parent != value) {
			if (this.parent != null) {
				this.parent = null;
			}
			this.parent = value;

			if (value != null) {
				value.setValue(value, PROPERTY_CHILD, this, SendableEntityCreator.NEW);
			}
			/* firePropertyChange(PROPERTY_ROOM, oldValue, value); */
			return true;
		}
		return false;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public SendableEntityCreator getParent() {
		return parent;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public TemplateResultFile getFile() {
		if (parent instanceof TemplateResultFile) {
			return (TemplateResultFile) parent;
		}
		return null;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_FILE, PROPERTY_MEMBER, PROPERTY_VARIABLE, PROPERTY_HEADERS };
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (!(entity instanceof TemplateResultFragment)) {
			return null;
		}
		TemplateResultFragment element = (TemplateResultFragment) entity;
		int pos = attribute.indexOf('.');
		String attrName;
		if (pos > 0) {
			attrName = attribute.substring(0, pos);
		} else {
			attrName = attribute;
		}

		if (PROPERTY_CURRENTMEMBER.equalsIgnoreCase(attrName)) {
			return getCurrentMember();
		}
		if (PROPERTY_CLONE.equalsIgnoreCase(attrName)) {
			TemplateResultFragment cloneObj = element.getSendableInstance(false);
			cloneObj.withMember(element.getMember());
			cloneObj.withVariable(element.getVariable());
			cloneObj.withKey(element.getKey());
			return cloneObj;
		}
		if (PROPERTY_FILE.equalsIgnoreCase(attrName)) {
			if (pos > 0) {
				SendableEntityCreator item = element.getParent();
				return item.getValue(item, attribute.substring(pos + 1));
			}
			return element.getParent();
		}
		if (PROPERTY_MEMBER.equalsIgnoreCase(attrName)) {
			if (pos > 0) {
				TemplateItem item = element.getMember();
				return item.getValue(attribute.substring(pos + 1));
			}
			return element.getMember();
		}
		if (PROPERTY_VARIABLE.equalsIgnoreCase(attrName)) {
			if (pos > 0) {
				SendableEntityCreator item = (SendableEntityCreator) element.getVariable();
				return item.getValue(item, attribute.substring(pos + 1));
			}
			return element.getVariable();
		}
		if (PROPERTY_HEADERS.equalsIgnoreCase(attrName)) {
			return element.getHeaders();
		}
		if (PROPERTY_EXPRESSION.equalsIgnoreCase(attrName)) {
			return element.isExpression();
		}
		if (PROPERTY_KEY.equalsIgnoreCase(attrName)) {
			return element.getKey();
		}
		if (PROPERTY_ITEM.equalsIgnoreCase(attrName)) {
			if (this.stack != null) {
				if (pos > 0) {
					Object last = this.stack.last();
					if (last instanceof GraphMember) {
						GraphMember item = (GraphMember) last;
						return item.getValue(attribute.substring(pos + 1));
					} else if (last instanceof SendableEntityCreator) {
						SendableEntityCreator item = (SendableEntityCreator) last;
						return item.getValue(item, attribute.substring(pos + 1));
					}
					return null;
				}
				return this.stack.last();
			}
			return null;
		}
		if (PROPERTY_ITEMPOS.equalsIgnoreCase(attrName)) {
			if (this.pos != null) {
				Object last = this.pos.last();
				if (last instanceof Integer) {
					return (Integer) last;
				}
			}
			return null;
		}

		if (PROPERTY_TEMPLATE.equalsIgnoreCase(attrName)) {
			if (pos > 0) {
				TemplateResultFragment item = element;
				return item.getValue(item, attribute.substring(pos + 1));
			}
			return element;
		}
		if (PROPERTY_TEMPLATEMODEL.equalsIgnoreCase(attrName)) {
			if (pos > 0) {
				TemplateResultModel item = element.getTemplateModel();
				if (item != null) {
					return item.getValue(item, attribute.substring(pos + 1));
				}
			}
			return element.getTemplateModel();
		}
		if (this.member != null) {
			Object value = this.member.getValue(attribute);
			if (value != null) {
				return value;
			}
		}
		return element.getText(attribute, null, null);
	}

	/**
	 * Gets the variable.
	 *
	 * @return the variable
	 */
	public LocalisationInterface getVariable() {
		return variables;
	}

	/**
	 * Gets the headers.
	 *
	 * @return the headers
	 */
	public SimpleSet<String> getHeaders() {
		return header;
	}

	/**
	 * Checks if is expression.
	 *
	 * @return true, if is expression
	 */
	public boolean isExpression() {
		return expression;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public TemplateResultFragment getSendableInstance(boolean prototyp) {
		return new TemplateResultFragment().withExpression(prototyp);
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (!(entity instanceof TemplateResultFragment)) {
			return false;
		}
		TemplateResultFragment element = (TemplateResultFragment) entity;

		if (FINISH_GENERATE.equalsIgnoreCase(attribute)) {
			/* NOTIFY GRAPHMEMBER */
			ObjectCondition role = GraphUtil.getRole(element.member);
			if (role != null) {
				role.update(value);
			}
		}

		if (PROPERTY_FILE.equalsIgnoreCase(attribute)) {
			element.setParent((SendableEntityCreator) value);
			return true;
		}
		if (PROPERTY_MEMBER.equalsIgnoreCase(attribute)) {
			element.withMember((GraphMember) value);
			return true;
		}
		if (PROPERTY_VARIABLE.equalsIgnoreCase(attribute)) {
			element.withVariable((LocalisationInterface) value);
			return true;
		}
		if (PROPERTY_HEADERS.equalsIgnoreCase(attribute)) {
			if (value instanceof String) {
				element.addHeader((String) value);
				return true;
			}
			if (value instanceof List<?>) {
				List<?> list = (List<?>) value;
				for (Object item : list) {
					if (!(item instanceof String)) {
						continue;
					}
					String itemType = (String) item;
					if (StringUtil.isPrimitiveType(itemType)) {
						if (!StringUtil.isDate(itemType)) {
							continue;
						}
					}
					while (itemType.endsWith(".")) {
						itemType = itemType.substring(0, itemType.length() - 1);
					}
					element.addHeader(itemType);
				}

			}
			return true;
		}
		if (PROPERTY_KEY.equalsIgnoreCase(attribute)) {
			element.withKey((Integer) value);
			return true;
		}
		if (PROPERTY_TEMPLATE.equalsIgnoreCase(attribute)) {
			element.withTemplate((ObjectCondition) value);
			return true;
		}

		return false;
	}

	/**
	 * With template.
	 *
	 * @param template the template
	 * @return the template result fragment
	 */
	public TemplateResultFragment withTemplate(ObjectCondition template) {
		this.template = template;
		return this;
	}

	/**
	 * Gets the text.
	 *
	 * @param label the label
	 * @param model the model
	 * @param gui the gui
	 * @return the text
	 */
	@Override
	public String getText(CharSequence label, Object model, Object gui) {
		/* Global Variables */
		if (this.variables != null) {
			String value = variables.getText(label, model, gui);
			if (value != null) {
				return value;
			}
		}
		/* Global Variables */
		TemplateResultModel templateModel = getTemplateModel();
		if (templateModel != null) {
			String value = templateModel.getText(label, null, null);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Put.
	 *
	 * @param label the label
	 * @param object the object
	 * @return the string
	 */
	@Override
	public String put(String label, Object object) {
		if (label == null) {
			return null;
		}
		if (ParserCondition.NOTIFY.equalsIgnoreCase(label.toString())) {
			if (object == null) {
				if (this.notify != null) {
					this.notify.remove(this.stack.size() - 1);
					return null;
				}
			} else {
				if (this.notify == null) {
					this.notify = new SimpleList<Object>();
				}
				if (this.notify.add(object)) {
					return object.toString();
				}
			}
		}
		if (PROPERTY_ITEM.equalsIgnoreCase(label.toString())) {
			if (object == null) {
				if (this.stack != null) {
					this.stack.remove(this.stack.size() - 1);
					return null;
				}
			} else {
				if (this.stack == null) {
					this.stack = new SimpleList<Object>();
				}
				if (this.stack.add(object)) {
					return object.toString();
				}
			}
		}
		if (PROPERTY_ITEMPOS.equalsIgnoreCase(label.toString())) {
			if (object == null) {
				if (this.pos != null) {
					this.pos.remove(this.pos.size() - 1);
					return null;
				}
			} else {
				if (this.pos == null) {
					this.pos = new SimpleList<Integer>();
				}
				if (this.pos.add(object)) {
					return object.toString();
				}
			}
		}
		return null;
	}

	/**
	 * Gets the template model.
	 *
	 * @return the template model
	 */
	public TemplateResultModel getTemplateModel() {
		SendableEntityCreator item = parent;
		while (item != null) {
			item = (SendableEntityCreator) item.getValue(item, PROPERTY_PARENT);
			if (item instanceof TemplateResultModel) {
				return (TemplateResultModel) item;
			}
		}
		return null;
	}

	/**
	 * With line string.
	 *
	 * @param value the value
	 * @param importClass the import class
	 * @return the template result fragment
	 */
	public TemplateResultFragment withLineString(String value, String... importClass) {
		String result = replacing(value, importClass);
		if (this.value != null) {
			this.value.withLine(result);
		}
		return this;
	}

	/**
	 * Append.
	 *
	 * @param value the value
	 * @return the template result fragment
	 */
	public TemplateResultFragment append(String value) {
		if (this.value == null) {
			this.value = new CharacterBuffer();
		}
		this.value.with(value);
		return this;
	}

	/**
	 * Replacing.
	 *
	 * @param value the value
	 * @param importClass the import class
	 * @return the string
	 */
	public String replacing(String value, String... importClass) {
		if (importClass == null || importClass.length < 1 || importClass[0] == null) {
			if (value != null) {
				return value.replaceAll("#IMPORT", "");
			}
			return null;
		}
		if (useImport) {
			for (int a = importClass.length - 1; a >= 0; a--) {
				if (importClass[a] != null) {
					value = value.replaceAll("#IMPORT" + (char) (65 + a), StringUtil.shortClassName(importClass[a]));
					this.addHeader("import " + importClass[a] + ";");
				}
			}
			return value.replaceAll("#IMPORT", StringUtil.shortClassName(importClass[0]));
		}
		for (int a = importClass.length - 1; a >= 0; a--) {
			if (importClass[a] != null) {
				value = value.replaceAll("#IMPORT" + (char) (65 + a), importClass[a]);
			}
		}
		return value.replaceAll("#IMPORT", importClass[0]);
	}

	/**
	 * With line.
	 *
	 * @param value the value
	 * @param importClass the import class
	 * @return the template result fragment
	 */
	public TemplateResultFragment withLine(String value, Class<?>... importClass) {
		String[] imports = null;
		if (importClass != null) {
			imports = new String[importClass.length];
			for (int i = 0; i < importClass.length; i++) {
				if (importClass[i] != null) {
					imports[i] = importClass[i].getName();
				} else {
					imports[i] = "";
				}
			}
		}
		String result = replacing(value, imports);
		if (this.value != null) {
			this.value.withLine(result);
		}
		return this;
	}

	/**
	 * Update.
	 */
	public void update() {
		if (value != null) {
			this.value.clear();
		}
		if (template != null) {
			this.template.update(this);
		}
	}

	/**
	 * With name.
	 *
	 * @param name the name
	 * @return the template result fragment
	 */
	public TemplateResultFragment withName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Creates the.
	 *
	 * @param setOfDiff the set of diff
	 * @param useImport the use import
	 * @param createModel the create model
	 * @return the template result fragment
	 */
	public static final TemplateResultFragment create(GraphSimpleSet setOfDiff, boolean useImport,
			boolean createModel) {
		GraphList model = new GraphList();
		model.add(setOfDiff);
		return create(model, useImport, createModel);
	}

	/**
	 * Creates the.
	 *
	 * @param model the model
	 * @param useImport the use import
	 * @param createModel the create model
	 * @return the template result fragment
	 */
	public static final TemplateResultFragment create(GraphModel model, boolean useImport, boolean createModel) {
		TemplateResultFragment fragment = new TemplateResultFragment().withMember(model);
		fragment.useImport = useImport;
		if (createModel && model != null) {
			String classModel = "de.uniks.networkparser.ext.ClassModel";
			if (!model.getDefaultPackage().equalsIgnoreCase(model.getName()) && model.getName() != null) {
				String packageName = model.getName();
				fragment.withLineString("#IMPORT model = new #IMPORT(\"" + packageName + "\");", classModel);
			} else {
				fragment.withLineString("#IMPORT model = new #IMPORT();", classModel);
			}
		}
		fragment.expression = createModel;
		return fragment;
	}

	/**
	 * Checks if is use imports.
	 *
	 * @return true, if is use imports
	 */
	public boolean isUseImports() {
		return useImport;
	}
}
