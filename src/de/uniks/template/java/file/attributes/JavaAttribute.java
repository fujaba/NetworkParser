package de.uniks.template.java.file.attributes;

import java.util.HashMap;
import java.util.Map.Entry;

import de.uniks.template.file.attributes.TemplateAttribute;

public class JavaAttribute extends TemplateAttribute {

//	public JavaAttribute(String name, String parent, Class<?> value) {
//		this.name = name;
//		this.parent = parent;
//		this.value = value;
//		setMethod = new JavaMethod();
//		setMethod.setSignature("   public void set{{Name}}({{value}} value)\n");
//		setMethod.setBody(""
//				+ "      this.{{name}} = value;\n");
//		withMethod = new JavaMethod();
//		withMethod.setSignature("   public with{{Name}}({{value}} value)\n");
//		withMethod.setBody(""
//				+ "      this.set{{Name}}(value);\n"
//				+ "      return this;\n");
//		getMethod = new JavaMethod();
//		getMethod.setSignature("   public {{value}} get{{Name}}()\n");
//		getMethod.setBody(""
//				+ "      return this.{{name}};\n");
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
//	private JavaMethod setMethod;
//	
//	private JavaMethod getMethod;
//	
//	private JavaMethod withMethod;
	
	private String property = "   public static final String {{PROPERTY_NAME}} = \"{{name}}\";\n";
	
	private String field = "   {{field_visibility}} {{field_modifiers}}{{value}} {{name}} = {{default}};\n";

//	private String getMethod = ""
//			+ "   {{get_visibility}} {{get_modifiers}}{{value}} get{{Name}}()\n"
//			+ "   {\n"
//			+ "      return this.{{name}};\n"
//			+ "   }\n";
	
	private String getMethodSignature = "   {{get_visibility}} {{get_modifiers}}{{value}} get{{Name}}()";
	//MOF {{NAME}}:{{TYPE}}={{VALUE}}
	//MOF {{NAME}} {{#if TYPE}}:{{#end}} {{TYPE}}
	//MOF {{NAME}}={{VALUE}}
	//public main(args:String){}
	//public main(args:String){return value;}
	//{{visibility}} {{name}}({{parameter}}){{{body}}}
	
	
	private String getMethodBody = ""
			+ "   {\n"
			+ "      return this.{{name}};\n"
			+ "   }\n";
	
//	private String setMethod = ""
//			+ "   {{set_visibility}} {{set_modifiers}}void set{{Name}}({{value}} value)\n"
//			+ "   {\n"
//			+ "      this.{{name}} = value;\n"
//			+ "   }\n";
	
	private String setMethodSignature = "   {{set_visibility}} {{set_modifiers}}void set{{Name}}({{value}} value)";
	
	private String setMethodBody = "   {\n"
			+ "      this.{{name}} = value;\n"
			+ "   }\n";
	
//	private String withMethod = ""
//			+ "   {{with_visibility}} {{with_modifiers}}{{value}} with{{Name}}({{value}} value)\n"
//			+ "   {\n"
//			+ "      this.set{{Name}}(value);\n"
//			+ "      return this;\n"
//			+ "   }\n";
	
	private String withMethodSignature = "   {{with_visibility}} {{with_modifiers}}{{value}} with{{Name}}({{value}} value)";
	
	private String withMethodBody = ""
			+ "   {\n"
			+ "      this.set{{Name}}(value);\n"
			+ "      return this;\n"
			+ "   }\n";
	
	public String generate(HashMap<String, String> parameters, boolean isInterfaceImplementation) {
		if (parameters == null) {
			return null;
		}
		if (parameters.isEmpty()) {
			return null;
		}
		String signatureEnd = "\n";
		String result = "";
		if (isInterfaceImplementation) {
			signatureEnd = ";\n";
			result += ""
					+ property
					+ "\n"
					+ getMethodSignature + signatureEnd
					+ "\n"
					+ setMethodSignature + signatureEnd
					+ "\n"
					+ withMethodSignature + signatureEnd
					+ "\n";
		} else {
			result += ""
					+ property
					+ "\n"
					+ field
					+ "\n"
					+ getMethodSignature + signatureEnd
					+ getMethodBody
					+ "\n"
					+ setMethodSignature + signatureEnd
					+ setMethodBody
					+ "\n"
					+ withMethodSignature + signatureEnd
					+ withMethodBody
					+ "\n";
		}
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
