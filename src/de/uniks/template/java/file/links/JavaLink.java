package de.uniks.template.java.file.links;

import java.util.HashMap;
import java.util.Map.Entry;

import org.sdmlib.StrUtil;

import de.uniks.networkparser.graph.Cardinality;
import de.uniks.template.file.TemplateClass;
import de.uniks.template.file.links.TemplateLink;
import de.uniks.template.java.JavaModel;
import de.uniks.template.java.file.JavaClass;
import de.uniks.template.java.file.methods.JavaMethod;

public class JavaLink extends TemplateLink {

//	public JavaLink(JavaClass source, String sourceName, String sourceType, JavaClass other, String otherName, String otherType) {
//		this.source = source;
//		this.sourceName = sourceName;
//		this.sourceType = sourceType;
//		this.other = other;
//		this.otherName = otherName;
//		this.otherType = otherType;
//		getMethod = new JavaMethod();
//		getMethod.setSignature(""
//				+ "   public {{cardinalityType}} get{{OtherName}}()\n");
//		getMethod.setBody(""
//				+ "      return this.{{otherName}};\n");
//		setMethod = new JavaMethod();
//		setMethod.setSignature("   public boolean set{{OtherName}}({{other}} value)\n");
//		setMethod.setBody(""
//				+ "      boolean changed = false;\n"
//				+ "      if (this.{{otherName}} != value) {\n"
//				+ "         {{other}} oldValue = this.{{otherName}};\n"
//				+ "         if (this.{{otherName}} != null) {\n"
//				+ "            this.{{otherName}} = null;\n"
//				+ "            {{setRemove}}"
//				+ "         }\n"
//				+ "         this.{{otherName}} = value;\n"
//				+ "         {{setSet}}"
//				+ "         changed = true;\n"
//				+ "      }\n"
//				+ "      return changed;\n");
//		withMethodOne = new JavaMethod();
//		withMethodOne.setSignature("   public {{source}} with{{OtherName}}({{other}} value)\n");
//		withMethodOne.setBody(""
//				+ "      this.set{{OtherName}}(value);\n"
//				+ "      return this;\n");
//		withMethodMany = new JavaMethod();
//		withMethodMany.setSignature("   public {{source}} with{{OtherName}}({{other}}... value)\n");
//		withMethodMany.setBody(""
//				+ "      if (value == null) {\n"
//				+ "         return this;\n"
//				+ "      }\n"
//				+ "      for ({{other}} item : value) {\n"
//				+ "         if (item != null) {\n"
//				+ "            if (this.{{otherName}} == null) {\n"
//				+ "               this.{{otherName}} = new {{cardinalityType}}();\n"
//				+ "            }\n"
//				+ "            boolean changed = this.{{otherName}}.add(item);\n"
//				+ "            {{withSet}}"
//				+ "         }\n"
//				+ "      }\n"
//				+ "      return this;\n");
//		withoutMethod = new JavaMethod();
//		withoutMethod.setSignature("   public {{source}} without{{OtherName}}({{other}}... value)\n");
//		withoutMethod.setBody(""
//				+ "      for ({{other}} item : value) { \n"
//				+ "         if (this.{{otherName}} != null && item != null) {\n"
//				+ "            if (this.{{otherName}}.remove(item)) {\n"
//				+ "               {{withoutRemove}}"
//				+ "            }\n"
//				+ "         }\n"
//				+ "      }\n"
//				+ "      return this;\n");
//		createMethod = new JavaMethod();
//		createMethod.setSignature("   public {{other}} create{{OtherName}}()\n");
//		createMethod.setBody(""
//				+ "      {{other}} value = new {{other}}();\n"
//				+ "      with{{OtherName}}(value);\n"
//				+ "      return value;\n");
//	}
	
//	private JavaModel model = null;
//	
//	public JavaModel getModel() {
//		return model;
//	}
//
//	public void setModel(JavaModel model) {
//		this.model = model;
//	}
//	
//	private JavaMethod getMethod;
//	
//	private JavaMethod setMethod;
//	
//	private JavaMethod withMethodOne;
//	
//	private JavaMethod withMethodMany;
//	
//	private JavaMethod withoutMethod;
//	
//	private JavaMethod createMethod;

	private String property = "   public static String {{PROPERTY_LINK}} = \"{{otherName}}\";\n";
	
	private String field = "   private {{modifiers}}{{cardinalityType}} {{otherName}} = null;\n";
	
//	private String getMethod = ""
//			+ "   public {{cardinalityType}} get{{OtherName}}()\n"
//			+ "   {\n"
//			+ "      return this.{{otherName}};\n"
//			+ "   }\n";

	private String getMethodSignature = "   public {{cardinalityType}} get{{OtherName}}()";
	
	private String getMethodBody = ""
			+ "   {\n"
			+ "      return this.{{otherName}};\n"
			+ "   }\n";
	
//	private String setMethod = ""
//			+ "   public boolean set{{OtherName}}({{other}} value)\n"
//			+ "   {\n"
//			+ "      boolean changed = false;\n"
//			+ "      if (this.{{otherName}} != value) {\n"
//			+ "         {{other}} oldValue = this.{{otherName}};\n"
//			+ "         if (this.{{otherName}} != null) {\n"
//			+ "            this.{{otherName}} = null;\n"
//			+ "            {{setRemove}}"
//			+ "         }\n"
//			+ "         this.{{otherName}} = value;\n"
//			+ "         {{setSet}}"
//			+ "         changed = true;\n"
//			+ "      }\n"
//			+ "      return changed;\n"
//			+ "   }\n";
	
	private String setMethodSignature = "   public boolean set{{OtherName}}({{other}} value)";
	
	private String setMethodBody = ""
			+ "   {\n"
			+ "      boolean changed = false;\n"
			+ "      if (this.{{otherName}} != value) {\n"
			+ "         {{other}} oldValue = this.{{otherName}};\n"
			+ "         if (this.{{otherName}} != null) {\n"
			+ "            this.{{otherName}} = null;\n"
			+ "            {{setRemove}}"
			+ "         }\n"
			+ "         this.{{otherName}} = value;\n"
			+ "         {{setSet}}"
			+ "         changed = true;\n"
			+ "      }\n"
			+ "      return changed;\n"
			+ "   }\n";
	
//	private String withMethodOne = ""
//			+ "   public {{source}} with{{OtherName}}({{other}} value)\n"
//			+ "   {\n"
//			+ "      this.set{{OtherName}}(value);\n"
//			+ "      return this;\n"
//			+ "   }\n";

	private String withMethodOneSignature = "   public {{source}} with{{OtherName}}({{other}} value)";
	
	private String withMethodOneBody = ""
			+ "   {\n"
			+ "      this.set{{OtherName}}(value);\n"
			+ "      return this;\n"
			+ "   }\n";
	
//	private String withMethodMany = ""
//			+ "   public {{source}} with{{OtherName}}({{other}}... value)\n"
//			+ "   {\n"
//			+ "      if (value == null) {\n"
//			+ "         return this;\n"
//			+ "      }\n"
//			+ "      for ({{other}} item : value) {\n"
//			+ "         if (item != null) {\n"
//			+ "            if (this.{{otherName}} == null) {\n"
//			+ "               this.{{otherName}} = new {{cardinalityType}}();\n"
//			+ "            }\n"
//			+ "            boolean changed = this.{{otherName}}.add(item);\n"
//			+ "            {{withSet}}"
//			+ "         }\n"
//			+ "      }\n"
//			+ "      return this;\n"
//			+ "   }\n";
	
	private String withMethodManySignature = "   public {{source}} with{{OtherName}}({{other}}... value)";
	
	private String withMethodManyBody = ""
			+ "   {\n"
			+ "      if (value == null) {\n"
			+ "         return this;\n"
			+ "      }\n"
			+ "      for ({{other}} item : value) {\n"
			+ "         if (item != null) {\n"
			+ "            if (this.{{otherName}} == null) {\n"
			+ "               this.{{otherName}} = new {{cardinalityType}}();\n"
			+ "            }\n"
			+ "            boolean changed = this.{{otherName}}.add(item);\n"
			+ "            {{withSet}}"
			+ "         }\n"
			+ "      }\n"
			+ "      return this;\n"
			+ "   }\n";
	
//	private String withoutMethod = ""
//			+ "   public {{source}} without{{OtherName}}({{other}}... value)\n"
//			+ "   {\n"
//			+ "      for ({{other}} item : value) { \n"
//			+ "         if (this.{{otherName}} != null && item != null) {\n"
//			+ "            if (this.{{otherName}}.remove(item)) {\n"
//			+ "               {{withoutRemove}}"
//			+ "            }\n"
//			+ "         }\n"
//			+ "      }\n"
//			+ "      return this;\n"
//			+ "   }\n";
	
	private String withoutMethodSignature = "   public {{source}} without{{OtherName}}({{other}}... value)";
	
	private String withoutMethodBody = ""
			+ "   {\n"
			+ "      for ({{other}} item : value) { \n"
			+ "         if (this.{{otherName}} != null && item != null) {\n"
			+ "            if (this.{{otherName}}.remove(item)) {\n"
			+ "               {{withoutRemove}}"
			+ "            }\n"
			+ "         }\n"
			+ "      }\n"
			+ "      return this;\n"
			+ "   }\n";
	
//	private String createMethod = ""
//			+ "   public {{other}} create{{OtherName}}()\n"
//			+ "   {\n"
//			+ "      {{other}} value = new {{other}}();\n"
//			+ "      with{{OtherName}}(value);\n"
//			+ "      return value;\n"
//			+ "   }\n";
	
	private String createMethodSignature = "   public {{other}} create{{OtherName}}()";
	
	private String createMethodBody = ""
			+ "   {\n"
			+ "      {{other}} value = new {{other}}();\n"
			+ "      with{{OtherName}}(value);\n"
			+ "      return value;\n"
			+ "   }\n";
	
	public String generate(HashMap<String, String> parameters, Cardinality cardinality, boolean isInterfaceImplementation) {
		if (parameters == null) {
			return null;
		}
		if (parameters.isEmpty()) {
			return null;
		}
		String result = "";
		String signatureEnd = "\n";
		if (isInterfaceImplementation) {
			signatureEnd = ";\n";
			result += ""
					+ property
					+ "\n";
			if (cardinality.equals(Cardinality.MANY)) {
				result += ""
						+ getMethodSignature + signatureEnd
						+ "\n"
						+ withMethodManySignature + signatureEnd
						+ "\n"
						+ withoutMethodSignature + signatureEnd
						+ "\n"
						+ createMethodSignature + signatureEnd
						+ "\n";
			} else if (cardinality.equals(Cardinality.ONE)) {
				result += ""
						+ getMethodSignature + signatureEnd
						+ "\n"
						+ setMethodSignature + signatureEnd
						+ "\n"
						+ withMethodOneSignature + signatureEnd
						+ "\n";
			}
		} else {
			result += ""
					+ property
					+ "\n"
					+ field
					+ "\n";
			
			if (cardinality.equals(Cardinality.MANY)) {
				result += ""
						+ getMethodSignature + signatureEnd
						+ getMethodBody
						+ "\n"
						+ withMethodManySignature + signatureEnd
						+ withMethodManyBody
						+ "\n"
						+ withoutMethodSignature + signatureEnd
						+ withoutMethodBody
						+ "\n"
						+ createMethodSignature + signatureEnd
						+ createMethodBody
						+ "\n";
			} else if (cardinality.equals(Cardinality.ONE)) {
				result += ""
						+ getMethodSignature + signatureEnd
						+ getMethodBody
						+ "\n"
						+ setMethodSignature + signatureEnd
						+ setMethodBody
						+ "\n"
						+ withMethodOneSignature + signatureEnd
						+ withMethodOneBody
						+ "\n";
			}
		}
		
		for (Entry<String, String> entry : parameters.entrySet()) {
			result = replace(result, entry.getKey(), entry.getValue());
		}
		return result;
	}
	
//	@Override
//	public boolean execute() {
//		if (otherType.equals(JavaLink.SUPERCLASS)) {
//			return true;
//		}
//		if (!getMethod.execute()
//				|| !setMethod.execute()
//				|| !withMethodOne.execute()
//				|| !withMethodMany.execute()
//				|| !withoutMethod.execute()
//				|| !createMethod.execute()) {
//			return false;
//		}
//		boolean bidirectional = true;
//		if (sourceType.equals(JavaLink.EDGE)) {
//			bidirectional = false;
//		}
//		// wrong direction for unidirectional
//		if (otherType.equals(JavaLink.EDGE)) {
//			return false;
//		}
//		String result = ""
//				+ "   public static String {{PROPERTY_LINK}} = \"{{otherName}}\";\n"
//				+ "   \n"
//				+ "   private {{cardinalityType}} {{otherName}} = null;\n"
//				+ "   \n"
//				+ getMethod.getResult()
//				+ "   \n";
//		String add = "";
//		if (otherType.equals(JavaLink.CARDINALITY_ONE)) {
//			add = ""
//					+ setMethod.getResult()
//					+ "   \n"
//					+ withMethodOne.getResult()
//					+ "   \n";
//		} else if (otherType.equals(JavaLink.CARDINALITY_MANY)) {
//			add = ""
//					+ withMethodMany.getResult()
//					+ "   \n"
//					+ withoutMethod.getResult()
//					+ "   \n"
//					+ createMethod.getResult()
//					+ "   \n";
//		}
//		String setRemove = "";
//		String setSet = "";
//		String withSet = "";
//		String withoutRemove = "";
//		if (bidirectional) {
//			setRemove = "oldValue.{{removeType}};\n";
//			setSet = ""
//					+ "if (value != null) {\n"
//					+ "            value.with{{SourceName}}(this);\n"
//					+ "         }\n";
//			withSet = "if (changed) {\n"
//					+ "               item.with{{SourceName}}(this);\n"
//					+ "            }\n";
//			withoutRemove = "item.{{removeType}};\n";
//		}
//		result += add;
//		//
//		result = replace(result,"{{setRemove}}", setRemove);
//		result = replace(result,"{{setSet}}", setSet);
//		result = replace(result,"{{withSet}}", withSet);
//		result = replace(result,"{{withoutRemove}}", withoutRemove);
//		//
//		String cardinalityType = determineCardType(other, otherType);
//		String removeType = determineRemoveType(sourceName, sourceType);
//		result = replace(result, "{{PROPERTY_LINK}}", "PROPERTY_" + otherName.toUpperCase());
//		result = replace(result, "{{otherName}}", otherName);
//		result = replace(result, "{{cardinalityType}}", cardinalityType);
//		result = replace(result, "{{removeType}}", removeType);
//		result = replace(result, "{{SourceName}}", StrUtil.upFirstChar(sourceName));
//		result = replace(result, "{{OtherName}}", StrUtil.upFirstChar(otherName));
//		result = replace(result, "{{source}}", source.getName());
//		result = replace(result, "{{other}}", other.getName());
//		
//		this.setResult(result);
//		return true;
//	}

//	private String determineCardType(TemplateClass other, String type) {
//		String result = "";
//		if (type.equals(JavaLink.CARDINALITY_ONE) || type.equals(JavaLink.EDGE)) {
//			result = other.getName();
//		} else if (type.equals(JavaLink.CARDINALITY_MANY)) {
//			result = "LinkedHashSet<" + other.getName() +">";
//		}
//		return result;
//	}
//	
//	private String determineRemoveType(String sourceName, String type) {
//		String result = "";
//		if (type.equals(JavaLink.CARDINALITY_ONE) || type.equals(JavaLink.EDGE)) {
//			result = "set" + StrUtil.upFirstChar(sourceName) + "(null)";
//		} else if (type.equals(JavaLink.CARDINALITY_MANY)) {
//			result = "without" + StrUtil.upFirstChar(sourceName) + "(this)";
//		}
//		return result;
//	}
	
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
