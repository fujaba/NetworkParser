package de.uniks.networkparser.parser;

import de.uniks.networkparser.StringUtil;
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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphSimpleSet;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.ModifyEntry;
import de.uniks.networkparser.graph.SourceCode;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateItem;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.SortedSet;

/**
 * The Class TemplateResultFile.
 *
 * @author Stefan
 */
public class TemplateResultFile extends SortedSet<TemplateResultFragment>
		implements SendableEntityCreator, LocalisationInterface {
	
	/** The Constant PROPERTY_PARENT. */
	public static final String PROPERTY_PARENT = "parent";
	
	/** The Constant PROPERTY_CHILD. */
	public static final String PROPERTY_CHILD = "child";
	
	/** The Constant PROPERTY_MEMBER. */
	public static final String PROPERTY_MEMBER = "member";

	/** The Constant PROPERTY_NAME. */
	public static final String PROPERTY_NAME = "name";
	
	/** The Constant PROPERTY_HEADERS. */
	public static final String PROPERTY_HEADERS = "headers";
	private String name;
	private String postfix;
	private String extension;
	private String path;
	private SendableEntityCreator parent;
	private TemplateItem member;
	private boolean metaModell;

	TemplateResultFile() {
		super(true);
	}

	/**
	 * Instantiates a new template result file.
	 *
	 * @param clazz the clazz
	 * @param comparator the comparator
	 */
	public TemplateResultFile(TemplateItem clazz, boolean comparator) {
		super(comparator);
		this.withName(clazz);
		this.withMember(clazz);
	}

	/**
	 * Instantiates a new template result file.
	 *
	 * @param clazz the clazz
	 * @param name the name
	 * @param comparator the comparator
	 */
	public TemplateResultFile(TemplateItem clazz, String name, boolean comparator) {
		super(comparator);
		this.withName(name);
		this.withMember(clazz);
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
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		CharacterBuffer buffer = new CharacterBuffer();
		if (path != null) {
			buffer.with(this.path);
			buffer.with('/');
		}
		buffer.with(this.name);
		buffer.with(this.postfix);
		buffer.with('.');
		buffer.with(this.extension);
		return buffer.toString();
	}

	/**
	 * With name.
	 *
	 * @param name the name
	 * @return the template result file
	 */
	public TemplateResultFile withName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * With name.
	 *
	 * @param clazz the clazz
	 * @return the template result file
	 */
	public TemplateResultFile withName(TemplateItem clazz) {
		if (clazz != null && clazz.getName() != null) {
			this.name = clazz.getName().replace(".", "/");
		}
		return this;
	}

	/**
	 * With postfix.
	 *
	 * @param value the value
	 * @return the template result file
	 */
	public TemplateResultFile withPostfix(String value) {
		this.postfix = value;
		return this;
	}

	/**
	 * With extension.
	 *
	 * @param value the value
	 * @return the template result file
	 */
	public TemplateResultFile withExtension(String value) {
		this.extension = value;
		return this;
	}

	/**
	 * With path.
	 *
	 * @param value the value
	 * @return the template result file
	 */
	public TemplateResultFile withPath(String value) {
		this.path = value;
		return this;
	}

	/**
	 * Adds the child.
	 *
	 * @param child the child
	 * @return true, if successful
	 */
	public boolean addChild(SendableEntityCreator child) {
		/* FIXME FOR NON COMPARATOR */
		if (!isComparator() && child instanceof TemplateResultFragment) {
			TemplateResultFragment fragment = (TemplateResultFragment) child;
			if (fragment.getKey() == Template.TEMPLATE) {
				super.add(0, fragment);
				return true;
			}
		}
		if (!super.add(child)) {
			return false;
		}
		child.setValue(child, PROPERTY_PARENT, this, SendableEntityCreator.NEW);
		return true;
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
		return null;
	}

	/**
	 * Sets the parent.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setParent(SendableEntityCreator value) {
		if (value != this.parent) {
			this.parent = value;
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
		return this.parent;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TemplateResultFile();
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_NAME, PROPERTY_PARENT };
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
		if (!(entity instanceof TemplateResultFile)) {
			return null;
		}
		TemplateResultFile element = (TemplateResultFile) entity;
		int pos = attribute.indexOf('.');
		String attrName;
		if (pos > 0) {
			attrName = attribute.substring(0, pos);
		} else {
			attrName = attribute;
		}
		if (PROPERTY_PARENT.equalsIgnoreCase(attrName)) {
			if (pos > 0) {
				return element.getParent().getValue(element, attribute.substring(pos + 1));
			}
			return element.getParent();
		}
		if (PROPERTY_MEMBER.equalsIgnoreCase(attrName)) {
			TemplateItem member = element.getMember();
			if (pos > 0) {
				return member.getValue(attribute.substring(pos + 1));
			}
			return member;
		}
		if (PROPERTY_HEADERS.equalsIgnoreCase(attrName)) {
			SimpleSet<String> headers = new SimpleSet<String>();
			for (TemplateResultFragment child : this) {
				if (child != null) {
					headers.addAll(child.getHeaders());
				}
			}
			return headers;
		}

		return null;
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
		if (PROPERTY_PARENT.equalsIgnoreCase(attribute)) {
			return this.setParent((SendableEntityCreator) value);
		}
		if (PROPERTY_CHILD.equalsIgnoreCase(attribute)) {
			return this.addChild((SendableEntityCreator) value);
		}
		return false;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public SourceCode getCode() {
		if (this.member instanceof Clazz) {
			Clazz clazz = (Clazz) this.member;
			SourceCode code = clazz.getChildByClass(SourceCode.class);
			if (code != null) {
				return code;
			}
		}
		return null;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		SourceCode code = getCode();
		if (this.size() < 1) {
			if (code != null) {
				String codeString = code.toString();
				if (codeString != null && codeString.length() > 0) {
					return code.toString();
				}
			}
		}
		/* TODO DONT ADD CORRECTLY ADD */
		/* ADD CODE */
		/* Check for Existing */
		if (code != null && code.size() > 0 && isMetaModell()) {
			CharacterBuffer sb = new CharacterBuffer();
			sb.with(code.getContent().toString());
			TemplateResultFragment importDecl = null;
			/* REMOVE OLD SOURCE */
			GraphSimpleSet children = GraphUtil.getChildren(this.member);

			for (GraphMember member : children) {
				if (!(member instanceof ModifyEntry)) {
					continue;
				}
				ModifyEntry modifierChild = (ModifyEntry) member;
				if (ModifyEntry.TYPE_DELETE.equalsIgnoreCase(modifierChild.getType())) {
					GraphMember entry = modifierChild.getEntry();
					if (entry == null) {
						continue;
					}
					SymTabEntry symbolEntry = code.getSymbolEntry(entry.getClass().getSimpleName(), entry.getName());
					if (symbolEntry != null) {
						sb.replace(symbolEntry.getStartPos(), symbolEntry.getEndPos(), "");
					}
				} else if (ModifyEntry.TYPE_MODIFIER.equalsIgnoreCase(modifierChild.getType())) {
					GraphMember entry = modifierChild.getEntry();
					if (entry == null) {
						continue;
					}
					TemplateResultFragment part = null;
					for (TemplateResultFragment fragment : this) {
						if (fragment.getKey() == Template.VALUE) {
							if (entry.getName().equals(fragment.getMember().getName())) {
								part = fragment;
								break;
							}
						}
					}
					if (part != null) {
						String methodName = StringUtil.upFirstChar(entry.getName());
						SymTabEntry startValue = code.getSymbolEntry("ATTRIBUTE",
								"PROPERTY_" + entry.getName().toUpperCase());
						SymTabEntry oldValue = code.getSymbolEntry("METHOD", "with" + methodName);
						if (oldValue != null && startValue != null) {
							sb.replace(startValue.getStartPos(), oldValue.getEndPos(), part.getValue().toString());
						}
					}

				}
			}
			for (TemplateResultFragment fragment : this) {
				if (fragment.getKey() == Template.DECLARATION) {
					continue;
				}

				if (fragment.getKey() == Template.IMPORT) {
					/* EVALUATION IMPORT */
					/* TextItems */
					fragment.update();
					importDecl = fragment;
					continue;
				}

				if (fragment.getName() != null) {
					if (SymTabEntry.TYPE_METHOD.equalsIgnoreCase(fragment.getName())) {
						SymTabEntry symbolEntry = code.getSymbolEntry(fragment.getName(),
								fragment.getMember().getName());
						if (symbolEntry == null) {
							int pos = code.getEndOfBody();
							sb.replace(pos, pos, fragment.getValue().toString());
						}
					}
				} else if (fragment.getKey() == Template.VALUE) {
					SymTabEntry symbolEntry = code.getSymbolEntry("ATTRIBUTE", fragment.getMember().getName());

					if (fragment.getMember() instanceof Association) {
						Association assoc = (Association) fragment.getMember();
						symbolEntry = code.getSymbolEntry("ATTRIBUTE", assoc.getOther().getName());
					}

					if (symbolEntry == null) {
						/* did not find it: append it */
						int pos = code.getEndOfBody();
						sb.replace(pos, pos, fragment.getValue().toString());
					}
				}
			}

			if (importDecl != null) {
				int start = code.getStartOfImports();
				int end = code.getEndOfImports();
				sb.replace(start, end, importDecl.getValue().toString() + "\n\n");
			}
			return sb.toString();
		}

		CharacterBuffer buffer = new CharacterBuffer();
		for (TemplateResultFragment fragment : this) {
			if (fragment.getKey() == Template.DECLARATION) {
				continue;
			}
			if (fragment.getKey() == Template.IMPORT) {
				/* EVALUATION IMPORT */
				/* TextItems */
				fragment.update();
			}
			buffer.with(fragment.getValue());
		}

		return buffer.toString();
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
	 * With member.
	 *
	 * @param member the member
	 * @return the template result file
	 */
	public TemplateResultFile withMember(TemplateItem member) {
		this.member = member;
		return this;
	}

	/**
	 * With meta model.
	 *
	 * @param value the value
	 * @return the template result file
	 */
	public TemplateResultFile withMetaModel(boolean value) {
		this.metaModell = value;
		return this;
	}

	/**
	 * Checks if is meta modell.
	 *
	 * @return true, if is meta modell
	 */
	public boolean isMetaModell() {
		return metaModell;
	}

	/**
	 * Creates the java.
	 *
	 * @param clazz the clazz
	 * @return the template result file
	 */
	public static TemplateResultFile createJava(Clazz clazz) {
		TemplateResultFile templateResult = new TemplateResultFile(clazz, true);
		templateResult.withExtension(Template.TYPE_JAVA);
		if (clazz != null && clazz.getClassModel() != null) {
			templateResult.withPath((String) clazz.getClassModel().getValue(GraphModel.PROPERTY_PATH));
		}
		return templateResult;
	}
}
