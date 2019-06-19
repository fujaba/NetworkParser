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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationSet;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.AttributeSet;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureSet;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.MethodSet;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateItem;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.And;
import de.uniks.networkparser.logic.ChainCondition;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.logic.FeatureCondition;
import de.uniks.networkparser.logic.ForeachCondition;
import de.uniks.networkparser.logic.IfCondition;
import de.uniks.networkparser.logic.ImportCondition;
import de.uniks.networkparser.logic.Not;
import de.uniks.networkparser.logic.Or;
import de.uniks.networkparser.logic.StringCondition;
import de.uniks.networkparser.logic.TemplateCondition;
import de.uniks.networkparser.logic.TemplateFragmentCondition;
import de.uniks.networkparser.logic.VariableCondition;

public class Template implements TemplateParser {
	public static final String PROPERTY_FEATURE = "features";
	public static final String TYPE_JAVA = "java";
	public static final String TYPE_TYPESCRIPT = "typescript";
	public static final String TYPE_CPP = "cpp";

	public static final char SPLITSTART = '{';
	public static final char SPLITEND = '}';
	public static final char ENTER = '=';
	public static final char SPACE = ' ';

	// Template Variables
	private TemplateCondition token = new TemplateCondition();

	protected boolean isValid;

	protected Template owner;

	protected int type = -1;

	private SimpleList<String> imports = new SimpleList<String>();

	private SimpleList<String> variables = new SimpleList<String>();

	// Configuration
	protected String id;
	protected String fileType;
	protected String extension;
	protected String path;
	protected String postfix;
	protected boolean metaModel;
	protected boolean includeSuperValues;
	protected SimpleList<Template> children;

	public Template(String name) {
		this.id = name;
	}

	public Template withFileType(String value) {
		this.fileType = value;
		return this;
	}

	public Template() {
	}

	public SimpleList<Template> getTemplates(String filter) {
		if (owner != null) {
			return owner.getTemplates(filter);
		}
		return null;
	}

	public String getId(boolean full) {
		if (full == false) {
			return id;
		}
		if (this.owner != null) {
			String id2 = this.owner.getId(full);
			if (this.id != null && id2 != null) {
				return id2 + "." + this.id;
			}
			return this.id;
		}
		return this.id;
	}

	public TemplateResultFragment generate(LocalisationInterface parameters, SendableEntityCreator parent,
			TemplateItem member) {
		ObjectCondition condition = this.token.getCondition();
		if (condition instanceof StringCondition) {
			this.token.withCondition(this.parsing((StringCondition) condition, parameters, false));
		}
		condition = this.token.getTemplate();
		if (condition instanceof StringCondition) {
			this.token.withTemplate(null);
			condition = this.parsing((StringCondition) condition, parameters, true);
			this.token.withTemplate(condition);
		}
		TemplateResultFragment templateFragment = new TemplateResultFragment();
		templateFragment.withKey(this.getType());
		templateFragment.withName(this.getId(false));
		templateFragment.setParent(parent);
		templateFragment.withVariable(parameters);
		templateFragment.withMember(member);

		if (this.token.update(templateFragment) == false) {
			return null;
		}
		templateFragment.withExpression(false);
		ObjectCondition templateCondition = this.token.getTemplate();
		if (templateCondition == null) {
			return null;
		}
		// Execute Template
		templateCondition.update(templateFragment);

		templateFragment.setValue(templateCondition, TemplateResultFragment.FINISH_GENERATE, templateCondition,
				SendableEntityCreator.NEW);

		return templateFragment;
	}

	public ObjectCondition parsing(StringCondition tokenTemplate, LocalisationInterface customTemplate,
			boolean variable) {
//		this.template = template;
		// Parsing Variables
		// Search for Variables and UIUf and combiVariables

		// {{Type}}
		// {{#if Type}} {{#end}}
		// {{#if Type}} {{#else}} {{#end}}
		// {{Type} } <=> {{Type}}{{#if Type}} {{#end}}
		// Define Type=int
		// {{{Type}}} <=> {int}
		if (tokenTemplate == null) {
			return null;
		}
		CharacterBuffer template = null;
		CharSequence value2 = tokenTemplate.getValue(null);
		if (value2 instanceof CharacterBuffer) {
			template = (CharacterBuffer) value2;
		} else {
			template = new CharacterBuffer().with(value2);
		}
		if (variable) {
			this.variables.clear();
		}
		return parsing(template, customTemplate, false, true);
	}

	public ObjectCondition parsing(ParserCondition... customTemplates) {
		TemplateResultModel result = new TemplateResultModel();
		SimpleList<ParserCondition> templateCondition = getTemplateCondition();
		if (templateCondition != null) {
			for (ParserCondition condition : templateCondition) {
				result.withTemplate(condition);
			}
		}
		LocalisationInterface customTemplate = result;
		if (customTemplates != null) {
			for (ParserCondition condition : customTemplates) {
				result.withTemplate(condition);
			}
			result.withTemplate(customTemplates);
		}
		ObjectCondition condition = this.token.getCondition();
		if (condition instanceof StringCondition) {
			this.token.withCondition(this.parsing((StringCondition) condition, customTemplate, false));
		}

		condition = this.token.getTemplate();
		if (condition instanceof StringCondition) {
			this.token.withTemplate(null);
			condition = this.parsing((StringCondition) condition, customTemplate, true);
			this.token.withTemplate(condition);
		}
		return condition;
	}

	public ObjectCondition parsing(CharacterBuffer buffer, LocalisationInterface customTemplate, boolean isExpression,
			boolean allowSpace, String... stopWords) {
		if (buffer == null) {
			return null;
		}
		int start = buffer.position(), end;
		ObjectCondition child = null;
		ChainCondition parent = new ChainCondition();
		int startDif;
		boolean isQuote = false;
		String stopCharacter = null;

		if (stopWords != null && stopWords.length > 0) {
			char[] values = new char[stopWords.length];
			startDif = 0;
			for (String stopword : stopWords) {
				if (stopword != null) {
					if (stopword.length() != 1) {
						values = null;
						break;
					}
					values[startDif++] = stopword.charAt(0);
				} else {
					values[startDif++] = 0;
				}
			}
			if (values != null) {
				stopCharacter = new String(values);
			}

		}
		startDif = 2;

		while (buffer.isEnd() == false) {
			if (isExpression && buffer.getCurrentChar() == SPACE) {
				break;
			} else if (stopCharacter != null && isQuote == false
					&& stopCharacter.indexOf(buffer.getCurrentChar()) >= 0) {
				break;
			}

			char character = buffer.nextClean(true);
			if (isExpression && character == SPLITEND) {
				break;
			} else if (isQuote == false && stopCharacter != null) {
				if (stopCharacter.indexOf(character) >= 0) {
					break;
				}
			}
			if (character != SPLITSTART) {
				if (character == '"') {
					isQuote = !isQuote;
				}
				buffer.skip();
				startDif = 2;
				continue;
			}
			character = buffer.getChar();
			if (character == '!') {
				startDif = 3;
				character = buffer.getChar();
			}
			if (character != SPLITSTART) {
				buffer.skip();
				startDif = 2;
				continue;
			}
			// Well done found {{
			character = buffer.getChar();
			// IF {{{
			while (character == SPLITSTART) {
				character = buffer.getChar();
			}
			end = buffer.position() - startDif;
			if (end - start > 0) {
				child = StringCondition.create(buffer.substring(start, end));
				parent.with(child);
			}
			// Switch for Logic Case
			CharacterBuffer tokenPart = new CharacterBuffer();
			if (character == '#') {
				int startCommand = buffer.position();
				tokenPart = buffer.nextToken(false, ' ', SPLITEND);

				// Is It a stopword
				if (stopWords != null) {
					for (String stopword : stopWords) {
						if (tokenPart.equalsIgnoreCase(stopword)) {
							buffer.withPosition(startCommand);
							if (parent.size() == 1) {
								return parent.first();
							}
							return parent;
						}
					}
				}

				ParserCondition condition = null;
				if (customTemplate instanceof TemplateResultModel) {
					ParserCondition creator = ((TemplateResultModel) customTemplate).getTemplate(tokenPart.toString());
					if (creator != null) {
						Object item = creator.getSendableInstance(isExpression);
						if (item instanceof ParserCondition) {
							condition = (ParserCondition) item;
						}
					}
				}
				if (condition != null) {
					condition.create(buffer, this, customTemplate);
					if (startDif == 3) {
						parent.with(Not.create(condition));
						startDif = 2;
					} else {
						parent.with(condition);
					}

					// If StopWords and Expression may be And or
					if (stopWords != null && isExpression) {
						if (buffer.getCurrentChar() == ' ') {
							buffer.skip();
						}
					}
				}
				start = buffer.position();
				continue;
			}
			buffer.nextString(tokenPart, false, false, SPLITEND);
			String key = tokenPart.toString();
			child = createVariable(key, isExpression);
			character = buffer.getChar();
			if (character == SPLITEND) {
				buffer.getChar();
				if (isExpression) {
					// BREAK FOR ONLY VARIABLE

					char firstChar = buffer.getCurrentChar();
					if (firstChar == ENTER || firstChar == '!') {
						// CHECK NEXT TOKEN
						char nextChar = buffer.getChar();
						if (nextChar == ENTER) {
							Equals equalsExpression = new Equals();
							equalsExpression.create(buffer, this, customTemplate);
							equalsExpression.withLeft(child);
							if (firstChar == '!') {
								child = new Not().with(equalsExpression);
							} else {
								child = equalsExpression;
							}
						} else {
							// MAY BE ANOTHER CHAR
							buffer.skip(-1);
						}
						parent.with(child);
						start = buffer.position();
					} else {
						if (startDif == 3) {
							parent.with(Not.create(child));
							startDif = 2;
						} else {
							parent.with(child);
						}
						start = buffer.position();
						// Move to next }
					}
					if (stopWords == null) {
						break;
					} else {
						if (buffer.getCurrentChar() == ' ') {
							start++;
							buffer.skip();
							if (allowSpace) {
								continue;
							} else {
								break;
							}
						}
					}
				}
				start = buffer.position();
				parent.with(child);
				continue;
			} else {
				parent.with(child);
			}
			tokenPart.clear();
			buffer.nextString(tokenPart, false, false, SPLITEND);

			// {{#if Type}} {{#end}}
			IfCondition token = new IfCondition();
			token.withExpression(createVariable(key, true));
			token.withTrue(StringCondition.create(tokenPart.toString()));

			child = token;
			parent.with(child);
			buffer.skip();
			start = buffer.position();
		}
		end = buffer.position();
		if (end - start > 0) {
			// Is It a stopword
			String token = buffer.substring(start, end);
			if (token.equals("##")) {
				buffer.back();
				buffer.back();
				return parent;
			}
			if (token.startsWith("#")) {
				token = token.substring(1);
				if (stopWords != null) {
					for (String stopword : stopWords) {
						if (token.equalsIgnoreCase(stopword)) {
							return parent;
						}
					}
				}
			}

			if (isExpression) {
				if (buffer.charAt(start) == SPLITSTART) {
					VariableCondition.create(token, isExpression);
				} else {
					VariableCondition variableCondition = VariableCondition.create(token, isExpression);
					variableCondition.withDefaultStringValue(true);
					child = variableCondition;
				}
			} else {
				child = StringCondition.create(token);
			}
			if (parent.size() == 0) {
				return child;
			}
			parent.with(child);
		}
		if (parent.size() < 1) {
			if (end - start == 0) {
				return StringCondition.create(buffer.substring(start, end + 1));
			} else {
				return null;
			}
		}
		if (parent.size() == 1) {
			return parent.first();
		}
		return parent;
	}

	private VariableCondition createVariable(CharSequence value, boolean expression) {
		VariableCondition condition = VariableCondition.create(value, expression);
		if (value != null) {
			this.variables.add(value.toString());
		}
		return condition;
	}

	public Template withTemplate(String... template) {
		CharacterBuffer sb = new CharacterBuffer();
		if (template == null || template.length < 1) {
			setValue(sb);
			return this;
		}
		String value = template[0];
		if (value == null) {
			sb.with("");
		} else {
			sb.with(value);
		}
		for (int i = 1; i < template.length; i++) {
			value = template[i];
			if (value == null) {
				sb.with("");
			} else if (value.startsWith("{{#")) {
				sb.with(value);
			} else {
				sb.with(Entity.CRLF + value);
			}
		}
		sb.with(Entity.CRLF);
		setValue(sb);
		return this;
	}

	public static final Template create(String... templateValues) {
		Template template = new Template();
		template.withTemplate(templateValues);
		return template;
	}

	protected void setValue(CharSequence value) {
		this.token.withTemplate(new StringCondition().withValue(value));
	}

	public Template withCondition(CharSequence condition) {
		this.token.withCondition(new StringCondition().withValue(condition));
		return this;
	}

	public Template withImport(String item) {
		this.imports.add(item);
		return this;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Template withType(int type) {
		setType(type);
		return this;
	}

	public SimpleList<String> getVariables() {
		return variables;
	}

	@Override
	public String toString() {
		return type + ": " + id;
	}

	public boolean addTemplate(Template template, boolean addOwner) {
		if (template == null) {
			return false;
		}
		if (this.children == null) {
			this.children = new SimpleList<Template>();
		}
		if (addOwner) {
			if (this.children.add(template)) {
				template.withOwner(this);
			}
			return true;
		}
		return true;
	}

	public Template withOwner(Template template) {
		this.owner = template;
		return this;
	}

	public Template getOwner() {
		return owner;
	}

	public String getFileName() {
		return null;
	}

	public TemplateResultFragment executeEntity(ObjectCondition condition, LocalisationInterface parameters) {
		return executingEntity(condition, parameters);
	}

	public TemplateResultFragment executeSimpleEntity(ObjectCondition condition, TemplateItem parameters) {
		return executingEntity(condition, parameters);
	}

	private TemplateResultFragment executingEntity(ObjectCondition condition, Object parameters) {
		this.isValid = true;

		TemplateResultFragment templateFragment = new TemplateResultFragment();
		templateFragment.withKey(this.getType());
		templateFragment.withName(this.getId(false));
		if (parameters != null) {
			if (parameters instanceof LocalisationInterface) {
				templateFragment.withVariable((LocalisationInterface) parameters);
			}
			if (parameters instanceof TemplateItem) {
				templateFragment.withMember((TemplateItem) parameters);
			}
		}

		if (this.token.update(templateFragment) == false) {
			return null;
		}
		templateFragment.withExpression(false);
		ObjectCondition templateCondition = this.token.getTemplate();
		// Execute Template
		templateCondition.update(templateFragment);
		templateFragment.setValue(templateCondition, TemplateResultFragment.FINISH_GENERATE, templateCondition,
				SendableEntityCreator.NEW);
		return templateFragment;
	}

	public TemplateResultFile executeEntity(TemplateItem model, LocalisationInterface parameters, boolean isStandard) {
		if (isValid(model, parameters) == false) {
			return null;
		}
		TemplateResultFile templateResult = createResultFile(model, isStandard);
		return templateResult;
	}

	public TemplateResultFile executeClazz(Clazz clazz, LocalisationInterface parameters, boolean isStandard) {
		if (isValid(clazz, parameters) == false) {
			return null;
		}
		TemplateResultFile templateResult = createResultFile(clazz, isStandard);
		if (parameters instanceof SendableEntityCreator) {
			templateResult.setParent((SendableEntityCreator) parameters);
		}
		if (children == null) {
			return templateResult;
		}

		String id2 = getId(true);
		SimpleList<Template> templates = getTemplates(id2 + ".");
		executeChildren(clazz, parameters, templates, id2, templateResult);
		if (includeSuperValues) {
			for (Clazz superClazz : clazz.getSuperClazzes(true)) {
				executeChildren(superClazz, parameters, templates, id2, templateResult);
			}
		}
		return templateResult;
	}

	protected void executeChildren(Clazz clazz, LocalisationInterface parameters, SimpleList<Template> templates,
			String id2, TemplateResultFile templateResult) {
		// FIRST ATTRIBUTE
		AttributeSet attributes = clazz.getAttributes();
		if (attributes.size() > 0) {
			for (Template template : templates) {
				if (template.getId(true).equals(id2 + ".attribute")) {
					// FOUND IT
					for (Attribute attribute : attributes) {
						template.executeTemplate(parameters, templateResult, attribute);
					}
					break;
				}
			}
		}

		// SECOND ASSOCITAION
		AssociationSet associations = clazz.getAssociations();
		if (associations.size() > 0) {
			for (Template template : templates) {
				if (template.getId(true).equals(id2 + ".association")) {
					// FOUND IT
					for (Association assoc : associations) {
						template.executeTemplate(parameters, templateResult, assoc);
						if (assoc.getClazz().equals(assoc.getOtherClazz())
								&& assoc.getName().equals(assoc.getOther().getName()) == false) {
							template.executeTemplate(parameters, templateResult, assoc.getOther());
						}
					}
					break;
				}
			}
		}
		MethodSet methods = clazz.getMethods();
		if (methods.size() > 0) {
			for (Template template : templates) {
				if (template.getId(true).equals(id2 + ".method")) {
					// FOUND IT
					for (Method method : methods) {
						template.executeTemplate(parameters, templateResult, method);
					}
					break;
				}
			}
		}
	}

	public boolean readTemplate(CharacterBuffer buffer) {
		boolean result = false;
		if (buffer == null) {
			return false;
		}
		CharacterBuffer id = buffer.nextToken(false, Template.SPLITEND, Template.SPACE);
		this.id = id.toString();
		if (buffer.getCurrentChar() == Template.SPACE) {
			String value = buffer.nextToken(false, Template.SPLITEND, Template.SPACE).toString();
			int type = TemplateFragmentCondition.getIdKey(value);
			this.withType(type);
			result = true;
		}
		String strTemplate = buffer.toString();
		withTemplate(strTemplate);
		return result;
	}

	protected Feature getFeature(Feature value, Clazz... values) {
		if (this.owner != null) {
			return this.owner.getFeature(value, values);
		}
		return null;
	}

	public void executeTemplate(LocalisationInterface parameters, TemplateResultFile templateResult,
			GraphMember member) {
		if (isValid(member, parameters) == false) {
			return;
		}
		String id2 = this.getId(true);
		SimpleList<Template> templates = getTemplates(id2 + ".");
		templates.add(this);
		for (Template template : templates) {
			if (template == null) {
				continue;
			}
			TemplateResultFragment fragment = template.generate(parameters, templateResult, member);
			if (this.getType() == Template.DECLARATION && fragment != null) {
				parameters.put(this.getId(false), fragment.getResult().toString());
			}
		}
	}

	protected boolean isValid(TemplateItem member, LocalisationInterface parameters) {
		if (member == null) {
			return false;
		}
		if (isValid) {
			return true;
		}

		String type = member.getClass().getSimpleName().toLowerCase();
		if (this.fileType != null) {
			if (this.fileType.equals(type)) {
				return true;
			}
			// Try to get Custom FileType
			Object value = member.getValue(GraphMember.PROPERTY_FILETYPE);
			if (type instanceof String) {
				type = (String) value;
			}
			if (this.fileType.equals(value)) {
				return true;
			}
		}
		if (this.id != null && this.id.equals(type)) {
			return true;
		}
		return false;
	}

	protected FeatureSet getFeatures(LocalisationInterface value) {
		if (value instanceof TemplateResultModel) {
			TemplateResultModel model = (TemplateResultModel) value;
			Object features = model.getValue(model, PROPERTY_FEATURE);
			if (features != null) {
				return (FeatureSet) features;
			}
			return null;
		}
		return null;

	}

	public TemplateResultFile createResultFile(TemplateItem clazz, boolean isStandard) {
		TemplateResultFile templateResult = new TemplateResultFile(clazz, isStandard);
		templateResult.withExtension(this.extension);
		String fileName = this.getFileName();
		if (fileName != null) {
			templateResult.withName(fileName);
		}
		templateResult.withPath(this.path);
		templateResult.withPostfix(this.postfix);
		return templateResult;
	}

	public boolean isMetaModel() {
		return metaModel;
	}

	public SimpleList<Template> getChildren() {
		return this.children;

	}

	public static SimpleList<ParserCondition> getTemplateCondition() {
		SimpleList<ParserCondition> customTemplates = new SimpleList<ParserCondition>();
		customTemplates.add(new FeatureCondition());
		customTemplates.add(new ImportCondition());
		customTemplates.add(new ForeachCondition());
		customTemplates.add(new TemplateFragmentCondition());
		customTemplates.add(new IfCondition());
		customTemplates.add(new IfCondition().withKey(IfCondition.IFNOT));
		customTemplates.add(new JavaMethodBodyCondition());
		customTemplates.add(new JavaListCondition());
		customTemplates.add(new And());
		customTemplates.add(new Or());
		customTemplates.add(new DebugCondition());
		customTemplates.add(new Not());
		return customTemplates;
	}
}
