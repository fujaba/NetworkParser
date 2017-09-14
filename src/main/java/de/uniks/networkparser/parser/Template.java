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
	private static final char SPACE = ' ';
	
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
		return parsing(template, customTemplate, false, true);
	}
	
	public ObjectCondition parsing(CharacterBuffer buffer, LocalisationInterface customTemplate, boolean isExpression, boolean allowSpace, String... stopWords) {
		int start=buffer.position(), end;
		ObjectCondition child = null;
		ChainCondition parent = new ChainCondition();
		
		while(buffer.isEnd() == false) {
			if(isExpression && (buffer.getCurrentChar() == SPACE)) {
				break;
			}
			char character = buffer.nextClean(true);
			if(isExpression && (character == SPLITEND)) {
				break;
			}
			if(character != SPLITSTART) {
				buffer.skip();
				continue;
			}
			character = buffer.getChar();
			if(character != SPLITSTART) {
				buffer.skip();
				continue;
			}
			// Well done found {{
			character = buffer.getChar();
			// IF {{{
			while(character==SPLITSTART) {
				character = buffer.getChar();
			}
			end = buffer.position() - 2;
			if(end-start>0) {
				child = StringCondition.create(buffer.substring(start,end));
				parent.with(child);
			}
			// Switch for Logic Case
			CharacterBuffer tokenPart = new CharacterBuffer();
			if(character == '#') {
				int startCommand=buffer.position();
				tokenPart = buffer.nextToken(false, ' ', SPLITEND);

				// Is It a stopword
				if(stopWords != null) {
					for(String stopword : stopWords) {
						if(tokenPart.equalsIgnoreCase(stopword)) {
							buffer.withPosition(startCommand);
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
					condition.create(buffer, this, customTemplate);
					parent.with(condition);
					
					// If StopWords and Expression may be And or 
					if(stopWords != null && isExpression) {
						if(buffer.getCurrentChar() == ' ') {
							buffer.skip();
						}
					}					
				}
				start=buffer.position();
				continue;
			}
			buffer.nextString(tokenPart, false, false, SPLITEND);
			String key = tokenPart.toString();
			child = createVariable(key, isExpression);
			character = buffer.getChar();
			if(character == SPLITEND) {
				buffer.getChar();
				if(isExpression) {
					// BREAK FOR ONLY VARIABLE
					
					char firstChar=buffer.getCurrentChar();
					if(firstChar == ENTER || firstChar == '!') {
						// CHECK NEXT TOKEN
						char nextChar = buffer.getChar();
						if(nextChar == ENTER) {
							// MAY BE A EQUALS
							buffer.skip();
							Equals equalsExpression = new Equals();
							equalsExpression.withLeft(child);
							child = parsing(buffer, customTemplate, true, allowSpace, stopWords);
							equalsExpression.withRight(child);

							if(firstChar == '!') {
								child = new Not().with(equalsExpression);
							} else {
								child = equalsExpression;
							}
						} else {
							// MAY BE ANOTHER CHAR
							buffer.skip(-1);
						}
						parent.with(child);
						start=buffer.position();
					}else {
						parent.with(child);
						start=buffer.position();
						// Move to next }
					}
					if(stopWords == null) {
						break;
					} else {
						if(buffer.getCurrentChar() == ' ') {
							start++;
							buffer.skip();
							if(allowSpace) {
								continue;
							}else {
								break;
							}
						}
					}
				}
				start=buffer.position();
				parent.with(child);
				continue;
			} else {
				parent.with(child);
			}
			tokenPart.reset();
			buffer.nextString(tokenPart, false, false, SPLITEND);

			//{{#if Type}} {{#end}}
			IfCondition token = new IfCondition();
			token.withExpression(createVariable(key, true));
			token.withTrue(StringCondition.create(tokenPart.toString()));
			
			child = token;
			parent.with(child);
			buffer.skip();
			start=buffer.position();
		}
		end = buffer.position();
		if(end-start>0) {
			// Is It a stopword
			String token = buffer.substring(start,end);
			if(token.startsWith("#")) {
				token = token.substring(1);
				if(stopWords != null) {
					for(String stopword : stopWords) {
						if(token.equalsIgnoreCase(stopword)) {
							return parent;
						}
					}
				}
			}
			
			if(isExpression) {
				if (buffer.charAt(start) == SPLITSTART) {
					VariableCondition.create(token, isExpression);
				}else {
					child = VariableCondition.create(token, false);
//					if(buffer.getCurrentChar()==SPLITEND) {
//						buffer.skip();
//						buffer.skip();
//					}
				}
			} else {
				child = StringCondition.create(token);
			}
			if(parent.size() == 0) {
				return child;
			}
			parent.with(child);
		}
		if(parent.size() < 1) {
			if(end-start==0) {
				return StringCondition.create(buffer.substring(start,end+1));
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
