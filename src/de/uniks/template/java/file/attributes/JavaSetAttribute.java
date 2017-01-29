package de.uniks.template.java.file.attributes;

import java.util.HashMap;
import java.util.Map.Entry;

import de.uniks.template.file.attributes.TemplateAttribute;

public class JavaSetAttribute extends TemplateAttribute {

	private String getMethod = ""
			+ "   public {{listType}} get{{Value}}()\n"
			+ "   {\n"
			+ "      {{listType}} result = new {{listType}}();\n"
			+ "      for ({{Name}} obj : this)\n"
			+ "      {\n"
			+ "         result.add(obj.get{{Value}}());\n"
			+ "      }\n"
			+ "      return result;\n"
			+ "   }\n";
	
	
	private String filterMethod = ""
			+ "   public {{SetName}} filter{{Value}}({{type}} value)\n"
			+ "   {\n"
			+ "      {{SetName}} result = new {{SetName}}();\n"
			+ "      for({{Name}} obj : this)\n"
			+ "      {\n"
			+ "         if (value == obj.get{{Value}}())\n"
			+ "         {\n"
			+ "            result.add(obj);\n"
			+ "         }\n"
			+ "      }\n"
			+ "      return result;\n"
			+ "   }\n";
	
	private String filterLowerHigherMethod = ""
			+ "   public {{SetName}} filter{{Value}}({{type}} lower, {{type}} upper)\n"
			+ "   {\n"
			+ "      {{SetName}} result = new {{SetName}}();\n"
			+ "      for ({{Name}} obj : this)\n"
			+ "      {\n"
			+ "         if (lower{{lowerCompare}} && upper{{upperCompare}})\n"
			+ "         {\n"
			+ "            result.add(obj);\n"
			+ "         }\n"
			+ "      }\n"
			+ "      return result;\n"
			+ "   }\n";
	
	private String withMethod = ""
			+ "   public {{SetName}} with{{Value}}({{type}} value)\n"
			+ "   {\n"
			+ "      for ({{Name}} obj : this)\n"
			+ "      {\n"
			+ "         obj.set{{Value}}(value);\n"
			+ "      }\n"
			+ "      return this;\n"
			+ "   }\n";

	public String generate(HashMap<String, String> parameters) {
		String result = ""
				+ getMethod
				+ "\n"
				+ filterMethod
				+ "\n"
				+ filterLowerHigherMethod
				+ "\n"
				+ withMethod
				+ "\n";
		for (Entry<String, String> entry : parameters.entrySet()) {
			result = replace(result, entry.getKey(), entry.getValue());
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
	
}
