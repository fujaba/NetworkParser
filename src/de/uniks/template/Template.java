package de.uniks.template;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import de.uniks.networkparser.interfaces.Entity;

public class Template {

	public static final int PACKAGE = 0;
	
	public static final int IMPORT = 1;
	
	public static final int DECLARATION = 2;
	
	public static final int FIELD = 3;
	
	public static final int VALUE = 4;
	
	private String template = "";
	
	private String condition = "";
	
	private int type = -1;
	
	private Template prevTemplate = null;
	
	private Template nextTemplate = null;

	private LinkedHashSet<String> variables = new LinkedHashSet<String>();
	
	public String generate(HashMap<String, String> parameters) {
		if (parameters == null) {
			return null;
		}
		String result = template;
		if (!parseCondition(parameters)) {
			return "";
		}
		// #if
		int ifIndex = result.indexOf("{{#");
		int endIndex = -1;
		String ifSearch = result;
		String parameter = "";
		String searchResult = "";
		String replacement = "";
		String value = "";
		String condition = "";
		while(ifIndex > -1) {
			ifSearch = ifSearch.substring(ifIndex);
			endIndex = ifSearch.indexOf("{{#end}}");
			if (ifSearch.startsWith("{{#if")) {
				condition = "{{#if";
			} else if (ifSearch.startsWith("{{#!")) {
				condition = "{{#!";
			}
			parameter = ifSearch.substring(condition.length() + 1, ifSearch.indexOf("}}"));
			searchResult = parameters.get("{{" + parameter + "}}");
			value = ifSearch.substring(ifSearch.indexOf("}}") + 2, endIndex);
			if ((condition.equals("{{#!") && (searchResult == null || searchResult.equals("")))
					|| (condition.equals("{{#if") && (searchResult != null && !searchResult.equals("")))) {
				replacement = value;
			} else {
				replacement = "";
			}
			result = replace(result, condition + " " + parameter +"}}" + value + "{{#end}}", replacement);
			ifSearch = ifSearch.substring(endIndex, ifSearch.length());
			ifIndex = ifSearch.indexOf("{{#", 1);
		}
		for (Entry<String, String> entry : parameters.entrySet()) {
			if (variables.contains(entry.getKey())) {
				result = replace(result, entry.getKey(), entry.getValue());
			}
		}
		return result;
	}
	
	private boolean parseCondition(HashMap<String, String> parameters) {
		boolean result = true;
		if (condition != null && condition != "") {
			boolean negateCondition = false;
			String currentCondition = condition;
			if (condition.startsWith("! ")) {
				currentCondition = currentCondition.substring(2, condition.length());
				negateCondition = true;
			}
			String currentResult = parameters.get(currentCondition);
			if (negateCondition) {
				result = currentResult == null || currentResult.equals("") == true;
			} else {
				result = currentResult != null && currentResult.equals("") == false;
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
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public Template withTemplate(String... template) {
		if(template == null) {
			setTemplate("");
		} else if(template.length == 1) {
			setTemplate(template[0]+Entity.CRLF);
		} else {
			StringBuilder sb = new StringBuilder(template[0]);
			for (int i = 1; i < template.length; i++) {
				if (template[i].startsWith("{{#")) {
					sb.append(template[i]);
				} else {
					sb.append(Entity.CRLF+template[i]);
				}
			}
			sb.append(Entity.CRLF);
			setTemplate(sb.toString());
		}
		return this;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public Template withCondition(String condition) {
		setCondition(condition);
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
	
	public LinkedHashSet<String> getVariables() {
		return variables;
	}
	
	public Template withVariables(String... variables) {
		for (String string : variables) {
			this.variables.add(string);
		}
		return this;
	}
	
	public Template withoutVariables(String...variables) {
		for (String string : variables) {
			this.variables.remove(string);
		}
		return this;
	}
	
}
