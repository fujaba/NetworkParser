package de.uniks.template;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.ChainCondition;
import de.uniks.networkparser.logic.IfCondition;
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
//		String result = template;
		String result = "";
		if(this.token.update(this) == false) {
//		if (!parseCondition(parameters)) {
			return "";
		}
		if(parameters != null) {
			for (String variable : variables) {
				if (parameters.containsKey(variable)) {
					result = replace(result, variable, parameters.get(variable));
				}
			}
		}
		return result;
	}
	
	private String replace(String string, String pattern, String replace) {
		int index = string.indexOf(pattern);
		String result = string;
		while (index != -1) {
			String firstHalf = result.substring(0, index) + replace;
			String secondHalf = result.substring(index + pattern.length());
			result = firstHalf + secondHalf;
			index = result.indexOf(pattern);
		}
		return result;
	}

	public String getTemplate() {
		return null;
//		return template;
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
		CharSequence value2 = tokenTemplate.getValue();
		if(value2 instanceof CharacterBuffer) {
			template = (CharacterBuffer) value2;
		}else {
			template = new CharacterBuffer().with(value2);
		}
		if(parent != null) {
			this.variables.clear();
		}
		
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
				tokenPart = template.nextToken(false, ' ');

				// Switch for If IfNot
				if(tokenPart.equalsIgnoreCase("ifnot")) {
					tokenPart = template.nextToken(false, SPLITEND);
					
					IfCondition token = new IfCondition().withExpression(createVariable(tokenPart));
					

					template.skipChar(SPLITEND);
					template.skipChar(SPLITEND);
					
					tokenPart = template.nextToken(false, SPLITSTART);
					token.withFalse(StringCondition.create(tokenPart));
					template.skipChar(SPLITEND);
					template.skipChar(SPLITEND);
					child = token;
					if(parent != null) {
						parent.addTemplate(child);
					}
				}
				if(tokenPart.equalsIgnoreCase("if")) {
					tokenPart = template.nextToken(false, SPLITEND);
					IfCondition token = new IfCondition().withExpression(createVariable(tokenPart));
					template.skipChar(SPLITEND);
					template.skipChar(SPLITEND);
					
					tokenPart = template.nextToken(false, SPLITSTART);
					token.withTrue(StringCondition.create(tokenPart));
					template.skipChar(SPLITEND);
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
			
			tokenPart.with(character);
			while((character = template.getChar()) != SPLITEND && template.isEnd() == false) {
				tokenPart.with(character);
			}
			String key = tokenPart.toString();
			child = createVariable(key);
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
			tokenPart.with(character);
			while((character = template.getChar()) != SPLITEND && template.isEnd() == false) {
				tokenPart.with(character);
			}
			//{{#if Type}} {{#end}}
			IfCondition token = new IfCondition();
			token.withExpression(createVariable(key));
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
	
	private VariableCondition createVariable(CharSequence value) {
		VariableCondition condition = new VariableCondition().withValue(value);
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
	
	public Template withVariables(String... variables) {
//		for (String string : variables) {
//			this.variables.add(string);
//		}
		return this;
	}
//	
//	public Template withoutVariables(String...variables) {
//		for (String string : variables) {
//			this.variables.remove(string);
//		}
//		return this;
//	}
	
}
