package de.uniks.networkparser.parser;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.ChainCondition;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.logic.IfCondition;
import de.uniks.networkparser.logic.Not;
import de.uniks.networkparser.logic.StringCondition;
import de.uniks.networkparser.logic.TemplateCondition;
import de.uniks.networkparser.logic.VariableCondition;

public class Template implements TemplateParser {
	private static final char SPLITSTART='{';
	private static final char SPLITEND='}';
	private static final char ENTER='=';
	
	private TemplateCondition token = new TemplateCondition();
	
	private int type = -1;
	
	public String name;
	
	private SimpleList<String> imports = new SimpleList<String>();

	private SimpleList<String> variables = new SimpleList<String>();
	
	
	public Template(String name) {
		this.name = name;
	}

	public Template() {
	}
	
	public TemplateResultFragment generate(LocalisationInterface parameters, SendableEntityCreator parent, GraphMember member) {
		if(this.token.getCondition() instanceof StringCondition) {
			this.token.withCondition(this.parsing((StringCondition)this.token.getCondition(), parameters, false));	
		}
		ObjectCondition template = this.token.getTemplate();
		if(template instanceof StringCondition) {
			this.token.withTemplate(null);
			ObjectCondition newTemplate = this.parsing((StringCondition)template, parameters, true);
			this.token.withTemplate(newTemplate);
		}
		TemplateResultFragment templateFragment = new TemplateResultFragment();
		templateFragment.withKey(this.getType());
		templateFragment.setParent(parent);
		templateFragment.withVariable(parameters);
		templateFragment.withMember(member);
		
		if(this.token.update(templateFragment) == false) {
			return null;
		}
		templateFragment.withExpression(false);
		ObjectCondition templateCondition = this.token.getTemplate();
		//Execute Template
		templateCondition.update(templateFragment);
		
		
//		templateFragment.withValue(parser.getResult());
		return templateFragment;
	}
	
	public ObjectCondition parsing(StringCondition tokenTemplate, LocalisationInterface customTemplate, boolean variable) {
//		this.template = template;
		// Parsing Variables
		// Search for Variables and UIUf and combiVariables
		
		// {{Type}}
		// {{#if Type}} {{#end}}
		// {{#if Type}} {{#else}} {{#end}}
		// {{Type} } <=> {{Type}}{{#if Type}} {{#end}}
		// Define Type=int
		// {{{Type}}} <=> {int}

		CharacterBuffer template = null;
		CharSequence value2 = tokenTemplate.getValue(null);
		if(value2 instanceof CharacterBuffer) {
			template = (CharacterBuffer) value2;
		}else {
			template = new CharacterBuffer().with(value2);
		}
		if(variable) {
			this.variables.clear();
		}
		return parsing(template, customTemplate, false);
	}
	
	public ObjectCondition parsing(CharacterBuffer template, LocalisationInterface customTemplate, boolean isExpression, String... stopWords) {
		int start=template.position(), end;
		ObjectCondition child = null;
		ChainCondition parent = new ChainCondition();
		
		while(template.isEnd() == false) {
			char character = template.nextClean(true);
			if(isExpression && character == SPLITEND) {
				break;
			}
			if(character != SPLITSTART) {
				template.skip();
				continue;
			}
			character = template.getChar();
			if(character != SPLITSTART) {
				template.skip();
				continue;
			}
			// Well done found {{
			character = template.getChar();
			// IF {{{
			while(character==SPLITSTART) {
				character = template.getChar();
			}
			end = template.position() - 2;
			if(end-start>0) {
				child = StringCondition.create(template.substring(start,end));
				parent.with(child);
			}
			// Switch for Logic Case
			CharacterBuffer tokenPart = new CharacterBuffer();
			if(character == '#') {
				int startCommand=template.position();
				tokenPart = template.nextToken(false, ' ', SPLITEND);

				// Is It a stopword
				if(stopWords != null) {
					for(String stopword : stopWords) {
						if(tokenPart.equalsIgnoreCase(stopword)) {
							template.withPosition(startCommand);
							if(parent.size() == 1) {
								return parent.first();
							}
							return parent;
						}
					}
				}
				
				ParserCondition condition = null;
				if(customTemplate instanceof TemplateResultModel) {
					ParserCondition creator = ((TemplateResultModel)customTemplate).getTemplate(tokenPart.toString());
					if(creator != null) {
						Object item =creator.getSendableInstance(isExpression);
						if(item instanceof ParserCondition) {
							condition = (ParserCondition) item;
						}
					}
				}
				if(condition != null) {
					condition.create(template, this, customTemplate);
					parent.with(condition);
				}
				template.skip();
				start=template.position();
				continue;
			}
			template.nextString(tokenPart, false, false, SPLITEND);
			String key = tokenPart.toString();
			child = createVariable(key, isExpression);
			character = template.getChar();
			if(character == SPLITEND) {
				template.getChar();
				if(isExpression) {
					// BREAK FOR ONLY VARIABLE
					
					char firstChar=template.getCurrentChar();
					if(firstChar == ENTER || firstChar == '!') {
						// CHECK NEXT TOKEN
						char nextChar = template.getChar();
						if(nextChar == ENTER) {
							// MAY BE A EQUALS
							template.skip();
							Equals equalsExpression = new Equals();
							if(child instanceof ObjectCondition) {
								equalsExpression.withLeft(child);
							}
							child = parsing(template, customTemplate, true);
							if(child instanceof ObjectCondition) {
								equalsExpression.withRight(child);
							}
							if(firstChar == '!') {
								child = new Not().with(equalsExpression);
							} else {
								child = equalsExpression;
							}
						} else {
							// MAY BE ANOTHER CHAR
							template.skip(-1);
						}
					}
					parent.with(child);
					start=template.position();
					break;
				}
				start=template.position();
				parent.with(child);
				continue;
			} else {
				parent.with(child);
			}
			tokenPart.reset();
			template.nextString(tokenPart, false, false, SPLITEND);

			//{{#if Type}} {{#end}}
			IfCondition token = new IfCondition();
			token.withExpression(createVariable(key, true));
			token.withTrue(StringCondition.create(tokenPart.toString()));
			
			child = token;
			parent.with(child);
			template.skip();
			start=template.position();
		}
		end = template.position();
		if(end-start>0) {
			if(isExpression) {
				if (template.charAt(start) == SPLITSTART) {
					child = VariableCondition.create(template.substring(start,end), isExpression);
				}
				child = VariableCondition.create(template.substring(start,end), false);
			}else {
				child = StringCondition.create(template.substring(start,end));
			}
			if(parent.size() == 0) {
				return child;
			}
			parent.with(child);
		}
		if(parent.size() < 1) {
			if(end-start==0) {
				return StringCondition.create(template.substring(start,end+1));
			} else {
				return null;
			}
		}
		if(parent.size() == 1) {
			return parent.first();
		}
		return parent;
	}
	
	private VariableCondition createVariable(CharSequence value, boolean expression) {
		VariableCondition condition = VariableCondition.create(value, expression);
		this.variables.add(value.toString());
		return condition;
	}
	
	public Template withTemplate(String... template) {
		CharacterBuffer sb = new CharacterBuffer();
		if(template == null) {
			setValue(sb);
		} else if(template.length == 1) {
			sb.with(template[0],Entity.CRLF);
			setValue(sb);
		} else {
			sb.with(template[0]);
			for (int i = 1; i < template.length; i++) {
				if (template[i].startsWith("{{#")) {
					sb.with(template[i]);
				} else {
					sb.with(Entity.CRLF+template[i]);
				}
			}
			sb.with(Entity.CRLF);
			setValue(sb);
		}
		return this;
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
		return type+": "+name;
	}

	public String getName() {
		return this.name;
	}
}
