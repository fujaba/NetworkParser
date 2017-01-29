package de.uniks.template.java.file;

import java.util.HashMap;
import java.util.Map.Entry;

import de.uniks.template.Template;

public class JavaSet extends Template {

	private String packages = "package {{package}}{{util}};\n";
	
	private String imports = "{{imports}}";
	
	private String declaration = "{{visibility}} {{modifiers}}class {{SetName}} extends SimpleSet<{{Name}}>\n";
	
	private String emptyConstructor = ""
			+ "   public {{SetName}}()\n"
			+ "   {\n"
			+ "      \n"
			+ "   }\n";
	
	private String constructor = ""
			+ "   public {{SetName}}({{Name}}... objects)\n"
			+ "   {\n"
			+ "      for ({{Name}} obj : objects)\n"
			+ "      {\n"
			+ "         this.add(obj);\n"
			+ "      }\n"
			+ "   }\n";
	
	private String collectionConstructor = ""
			+ "   public {{SetName}}(Collection<{{Name}}> objects)\n"
			+ "   {\n"
			+ "      this.addAll(objects);\n"
			+ "   }\n";

	private String emptySet = "   public static final {{SetName}} EMPTY_SET = new {{SetName}}().withFlag({{SetName}}.READONLY);\n";
	
	
	// TODO add PO
//	   public DicePO createDicePO()
//	   {
//	      return new DicePO(this.toArray(new Dice[this.size()]));
//	   }
	
	private String getEntryType = ""
			+ "   public String getEntryType()\n"
			+ "   {\n"
			+ "      return \"{{FullName}}\";\n"
			+ "   }\n";
	
	private String getNewList = ""
			+ "   @Override\n"
			+ "   public {{SetName}} getNewList(boolean keyValue)\n"
			+ "   {\n"
			+ "      return new {{SetName}}();\n"
			+ "   }\n";
	
	private String filterMethod = ""
			+ "   public {{SetName}} filter(Condition<{{Name}}> condition) {\n"
			+ "      {{SetName}} filterList = new {{SetName}}();\n"
			+ "      filterItems(filterList, condition);\n"
			+ "      return filterList;\n"
			+ "   }\n";
	
	private String withMethod = ""
			+ "   @SuppressWarnings(\"unchecked\")\n"
			+ "   public {{SetName}} with(Object value)\n"
			+ "   {\n"
			+ "      if (value == null)\n"
			+ "      {\n"
			+ "         return this;\n"
			+ "      }\n"
			+ "      else if (value instanceof java.util.Collection)\n"
			+ "      {\n"
			+ "         this.addAll((Collection<{{Name}}>)value);\n"
			+ "      }\n"
			+ "      else if (value != null)\n"
			+ "      {\n"
			+ "         this.add(({{Name}}) value);\n"
			+ "      }\n"
			+ "      return this;\n"
			+ "   }\n";
	
	private String withoutMethod = ""
			+ "   public {{SetName}} without({{Name}} value)\n"
			+ "   {\n"
			+ "      this.remove(value);\n"
			+ "      return this;\n"
			+ "   }\n";

	public String generate(HashMap<String, String> parameters) {
		String result = ""
				+ packages
				+ "\n"
				+ imports
				+ declaration
				+ "{\n"
				+ "\n"
				+ emptyConstructor
				+ "\n"
				+ constructor
				+ "\n"
				+ collectionConstructor
				+ "\n"
				+ emptySet
				+ "\n"
				+ getEntryType
				+ "\n"
				+ getNewList
				+ "\n"
				+ filterMethod
				+ "\n"
				+ withMethod
				+ "\n"
				+ withoutMethod
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
