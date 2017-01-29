package de.uniks.template.java.file.links;

import java.util.HashMap;
import java.util.Map.Entry;

import de.uniks.template.file.links.TemplateLink;

public class JavaSetLink extends TemplateLink {

	private String getMethod = ""
			+ "   public {{OtherSetName}} get{{Value}}()\n"
			+ "   {\n"
			+ "      {{OtherSetName}} result = new {{OtherSetName}}();\n"
			+ "      for ({{Name}} obj : this)\n"
			+ "      {\n"
			+ "         result.with(obj.get{{Value}}());\n"
			+ "      }\n"
			+ "      return result;\n"
			+ "   }\n";
	
	private String filterMethod = ""
			+ "   public {{SetName}} filter{{Value}}(Object value)\n"
			+ "   {\n"
			+ "      ObjectSet neighbors = new ObjectSet();\n"
			+ "      if (value instanceof Collection)\n"
			+ "      {\n"
			+ "         neighbors.addAll((Collection<?>) value);\n"
			+ "      }\n"
			+ "      else\n"
			+ "      {\n"
			+ "         neighbors.add(value);\n"
			+ "      }\n"
			+ "      {{SetName}} answer = new {{SetName}}();\n"
			+ "      for ({{Name}} obj : this)\n"
			+ "      {\n"
			+ "         if ({{filterType}})\n"
			+ "         {\n"
			+ "            answer.add(obj);\n"
			+ "         }\n"
			+ "      }\n"
			+ "      return answer;\n"
			+ "   }\n";
	
	private String withMethod = ""
			+ "   public {{SetName}} with{{Value}}({{Value}} value)\n"
			+ "   {\n"
			+ "      for ({{Name}} obj : this)\n"
			+ "      {\n"
			+ "         obj.with{{Value}}(value);\n"
			+ "      }\n"
			+ "      return this;\n"
			+ "   }\n";
	
	public String generate(HashMap<String, String> parameters) {
		String result = ""
				+ getMethod
				+ "\n"
				+ filterMethod
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
