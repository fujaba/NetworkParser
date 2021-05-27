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

public class TemplateResultFragment
		implements Comparable<TemplateResultFragment>, SendableEntityCreator, ObjectCondition, LocalisationInterface {
	public static final String PROPERTY_PARENT = "parent";
	public static final String PROPERTY_CHILD = "child";
	public static final String PROPERTY_CLONE = "clone";

	public static final String FINISH_GENERATE = "generate";

	public static final String PROPERTY_FILE = "file";
	public static final String PROPERTY_KEY = "key";
	public static final String PROPERTY_MEMBER = "member";
	public static final String PROPERTY_VARIABLE = "variable";
	public static final String PROPERTY_HEADERS = "headers";
	public static final String PROPERTY_EXPRESSION = "expression";
	public static final String PROPERTY_ITEM = "item";
	public static final String PROPERTY_ITEMPOS = "itempos";
	public static final String PROPERTY_TEMPLATE = "template";
	public static final String PROPERTY_TEMPLATEMODEL = "templatemodel";
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

	public int getKey() {
		return key;
	}

	public boolean setKey(int key) {
		if (key != this.key) {
			this.key = key;
			return true;
		}
		return false;
	}

	public TemplateResultFragment withKey(int key) {
		setKey(key);
		return this;
	}

	public CharacterBuffer getValue() {
		return value;
	}

	public CharacterBuffer cloneValue(CharacterBuffer newValue) {
		CharacterBuffer oldValue = value;
		if (oldValue == null) {
			oldValue = new CharacterBuffer();
		}
		this.value = newValue;
		return oldValue;
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
		if (value instanceof ObjectCondition == false) {
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

	public CharacterBuffer getResult() {
		return value;
	}

	public TemplateResultFragment withMember(TemplateItem member) {
		this.member = member;
		return this;
	}

	public TemplateItem getMember() {
		return member;
	}

	public TemplateItem getCurrentMember() {
		if (this.stack != null) {
			Object item = this.stack.last();
			if (item instanceof GraphMember) {
				return (GraphMember) item;
			}
		}
		return this.member;
	}

	public TemplateResultFragment withExpression(boolean value) {
		this.expression = value;
		return this;
	}

	public boolean addHeader(String value) {
		if (this.header == null) {
			this.header = new SimpleSet<String>();
		}
		return this.header.add(value);
	}

	public TemplateResultFragment withHeader(String value) {
		addHeader(value);
		return this;
	}

	public boolean removeHeader(String value) {
		if (this.header == null) {
			return true;
		}
		return this.header.remove(value);
	}

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

	public SendableEntityCreator getParent() {
		return parent;
	}

	public TemplateResultFile getFile() {
		if (parent instanceof TemplateResultFile) {
			return (TemplateResultFile) parent;
		}
		return null;
	}

	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_FILE, PROPERTY_MEMBER, PROPERTY_VARIABLE, PROPERTY_HEADERS };
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (entity instanceof TemplateResultFragment == false) {
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

	public LocalisationInterface getVariable() {
		return variables;
	}

	public SimpleSet<String> getHeaders() {
		return header;
	}

	public boolean isExpression() {
		return expression;
	}

	@Override
	public TemplateResultFragment getSendableInstance(boolean prototyp) {
		return new TemplateResultFragment().withExpression(prototyp);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (entity instanceof TemplateResultFragment == false) {
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
					if (item instanceof String == false) {
						continue;
					}
					String itemType = (String) item;
					if (StringUtil.isPrimitiveType(itemType)) {
						if (StringUtil.isDate(itemType) == false) {
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

	public TemplateResultFragment withTemplate(ObjectCondition template) {
		this.template = template;
		return this;
	}

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

	public TemplateResultFragment withLineString(String value, String... importClass) {
		String result = replacing(value, importClass);
		if (this.value != null) {
			this.value.withLine(result);
		}
		return this;
	}

	public TemplateResultFragment append(String value) {
		if (this.value == null) {
			this.value = new CharacterBuffer();
		}
		this.value.with(value);
		return this;
	}

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

	public void update() {
		if (value != null) {
			this.value.clear();
		}
		if (template != null) {
			this.template.update(this);
		}
	}

	public TemplateResultFragment withName(String name) {
		this.name = name;
		return this;
	}

	public String getName() {
		return name;
	}

	public static final TemplateResultFragment create(GraphSimpleSet setOfDiff, boolean useImport,
			boolean createModel) {
		GraphList model = new GraphList();
		model.add(setOfDiff);
		return create(model, useImport, createModel);
	}

	public static final TemplateResultFragment create(GraphModel model, boolean useImport, boolean createModel) {
		TemplateResultFragment fragment = new TemplateResultFragment().withMember(model);
		fragment.useImport = useImport;
		if (createModel && model != null) {
			String classModel = "de.uniks.networkparser.ext.ClassModel";
			if (model.getDefaultPackage().equalsIgnoreCase(model.getName()) == false && model.getName() != null) {
				String packageName = model.getName();
				fragment.withLineString("#IMPORT model = new #IMPORT(\"" + packageName + "\");", classModel);
			} else {
				fragment.withLineString("#IMPORT model = new #IMPORT();", classModel);
			}
		}
		fragment.expression = createModel;
		return fragment;
	}

	public boolean isUseImports() {
		return useImport;
	}
}
