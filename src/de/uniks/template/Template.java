package de.uniks.template;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.ChainCondition;
import de.uniks.networkparser.logic.IfCondition;
import de.uniks.networkparser.logic.Not;
import de.uniks.networkparser.logic.StringCondition;
import de.uniks.networkparser.logic.VariableCondition;

public class Template {
	private static final char SPLITSTART='{';
	private static final char SPLITEND='}';
	public static final int PACKAGE = 0;
	
	public static final int IMPORT = 1;
	
	public static final int DECLARATION = 2;
	
	public static final int FIELD = 3;
	
	public static final int VALUE = 4;
	
	private ChainCondition token = new ChainCondition();
	
	private int type = -1;
	
	private Template prevTemplate = null;
	
	private Template nextTemplate = null;

	private SimpleList<String> variables = new SimpleList<String>();
	
	public String generate(SimpleKeyValueList<String, String> parameters) {
		if(this.token.getCondition() instanceof StringCondition) {
			this.token.withCondition(this.parsing((StringCondition)this.token.getCondition(), null));	
		}
		ObjectCondition template = this.token.getTemplate();
		if(template instanceof StringCondition) {
			this.token.withTemplate(null);
			this.parsing((StringCondition)template, this.token);	
		}
		if(this.token.update(this) == false) {
			return "";
		}
		TemplateParser parser =new TemplateParser();
		parser.withVariable(parameters);
		SimpleList<ObjectCondition> templates = this.token.getTemplates();
		for(ObjectCondition part : templates) {
			part.update(parser);
			
		}
		return parser.getResult().toString();
	}
	
	public ObjectCondition parsing(StringCondition tokenTemplate, ChainCondition parent) {
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
		if(parent != null) {
			this.variables.clear();
		}
		return parseCharacterBuffer(template, parent);
	}
	
	private ObjectCondition parseCharacterBuffer(CharacterBuffer template, ChainCondition parent, String... stopWords) {
		int start=template.position(), end;
		ObjectCondition child = null;
		while(template.isEnd() == false) {
			char character = template.nextClean(true);
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
				if(parent != null) {
					parent.addTemplate(child);
				}
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
							return child;
						}
					}
				}
				
				// Switch for If IfNot
				if(tokenPart.equalsIgnoreCase("ifnot") || tokenPart.equalsIgnoreCase("if")) {
					IfCondition token = new IfCondition();
					if(tokenPart.equalsIgnoreCase("ifnot")) {
						tokenPart = template.nextToken(false, SPLITEND);
						VariableCondition expression = createVariable(tokenPart, true);
						token.withExpression(Not.create(expression));	
					}else {
						tokenPart = template.nextToken(false, SPLITEND);
						token.withExpression(createVariable(tokenPart, true));
					}
					
					template.skipChar(SPLITEND);
					template.skipChar(SPLITEND);
					
					// Add Children
					token.withTrue(parseCharacterBuffer(template, null, "else", "endif"));
					
					// ELSE OR ENDIF
					tokenPart = template.nextToken(false, SPLITEND);
					if("else".equalsIgnoreCase(tokenPart.toString())) {
						template.skipChar(SPLITEND);
						template.skipChar(SPLITEND);
						token.withFalse(parseCharacterBuffer(template, null, "endif"));
						template.skipTo(SPLITEND, false);
					}
					template.skipChar(SPLITEND);
					
					child = token;
					if(parent != null) {
						parent.addTemplate(child);
					}
				}
				template.skip();
				start=template.position();
				continue;
			}
			template.nextString(tokenPart, false, false, SPLITEND);
			String key = tokenPart.toString();
			child = createVariable(key, false);
			if(parent != null) {
				parent.addTemplate(child);
			}
			character = template.getChar();
			if(character == SPLITEND) {
				template.skip();
				start=template.position();
				continue;
			}
			tokenPart.reset();
			template.nextString(tokenPart, false, false, SPLITEND);

			//{{#if Type}} {{#end}}
			IfCondition token = new IfCondition();
			token.withExpression(createVariable(key, true));
			token.withTrue(StringCondition.create(tokenPart.toString()));
			
			child = token;
			if(parent != null) {
				parent.addTemplate(child);
			}
			template.skip();
			start=template.position();
		}
		end = template.position();
		if(end-start>0) {
			child = StringCondition.create(template.substring(start,end));
			if(parent != null) {
				parent.addTemplate(child);
			}
		}
		return child;
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
	
	public Template getPrevTemplate() {
		return prevTemplate;
	}

	public void setPrevTemplate(Template prevTemplate) {
		if (this.prevTemplate != prevTemplate) {
			Template oldValue = this.prevTemplate;
			if (this.prevTemplate != null) {
				this.prevTemplate = null;
				oldValue.setNextTemplate(null);
			}
			this.prevTemplate = prevTemplate;
			if (prevTemplate != null) {
				prevTemplate.withNextTemplate(this);
			}
		}
	}

	public Template withPrevTemplate(Template prevTemplate) {
		setPrevTemplate(prevTemplate);
		return this;
	}
	
	public Template getNextTemplate() {
		return nextTemplate;
	}

	public void setNextTemplate(Template nextTemplate) {
		if (this.nextTemplate != nextTemplate) {
			Template oldValue = this.nextTemplate;
			if (this.nextTemplate != null) {
				this.nextTemplate = null;
				oldValue.setPrevTemplate(null);
			}
			this.nextTemplate = nextTemplate;
			if (nextTemplate != null) {
				nextTemplate.withPrevTemplate(this);
			}
		}
	}
	
	public Template withNextTemplate(Template nextTemplate) {
		setNextTemplate(nextTemplate);
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
}
