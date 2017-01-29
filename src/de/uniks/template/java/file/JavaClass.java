package de.uniks.template.java.file;

import java.util.HashMap;
import java.util.Map.Entry;

import de.uniks.template.file.TemplateClass;

public class JavaClass extends TemplateClass {

//	public JavaClass(String name) {
//		this.setName(name);
//		this.imports = new LinkedHashMap<String, String>();
//		this.attributes = new LinkedHashSet<TemplateAttribute>();
//		this.links = new LinkedHashSet<TemplateLink>();
//		this.methods = new LinkedHashSet<TemplateMethod>();
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
//	public JavaAttribute addAttribute(String attributeName, Class<?> value) {
//		JavaAttribute javaAttribute = new JavaAttribute(attributeName, getName(),value);
//		javaAttribute.setModel(this.model);
//		this.attributes.add(javaAttribute);
//		String attributeClassName = value.getName();
//		if (!value.isPrimitive() && !imports.containsKey(attributeClassName)) {
//			imports.put(attributeClassName, getImport(attributeClassName));
//		}
//		return javaAttribute;
//	}
//
//	public JavaLink addLink(String sourceName, String sourceType, JavaClass other, String otherName, String otherType) {
//		JavaLink javaLink = new JavaLink(this, sourceName, sourceType, other, otherName, otherType);
//		javaLink.setModel(this.model);
//		this.links.add(javaLink);
//		String otherClassName = other.getClass().getName();
//		if (!imports.containsKey(otherClassName)) {
//			imports.put(otherClassName, getImport(otherClassName));
//		}
//		String setName = LinkedHashSet.class.getName();
//		if (otherType.equals("many") && !imports.containsKey(setName)) {
//			imports.put(LinkedHashSet.class.getName(), getImport(setName));
//		}
//		return javaLink;
//	}
//	
//	public JavaLink addSuperClass(JavaClass superClass) {
//		JavaLink javaLink = new JavaLink(this, "", JavaLink.EDGE, superClass, "", JavaLink.SUPERCLASS);
//		this.links.add(javaLink);
//		String superClassName = superClass.getClass().getName();
//		if (!imports.containsKey(superClassName)) {
//			imports.put(superClassName, getImport(superClassName));
//		}
//		return javaLink;
//	}
	
	private String packages = "package {{package}};\n";

	private String imports = "{{imports}}";

	private String declaration = "{{visibility}} {{modifiers}}{{classType}} {{Name}}{{superclasses}}\n";
	
	private String listeners = "   protected PropertyChangeSupport listeners = null;\n";
	
	private String firePropertyChange = ""
			+ "   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)\n" 
			+ "   {\n" 
			+ "      if (listeners != null) {\n" 
			+ "         listeners.firePropertyChange(propertyName, oldValue, newValue);\n" 
			+ "         return true;\n" 
			+ "      }\n" 
			+ "      return false;\n" 
			+ "   }\n";

	private String addPropertyChangeListener = ""
			+ "   public boolean addPropertyChangeListener(PropertyChangeListener listener)\n" 
			+ "   {\n" 
			+ "      if (listeners == null) {\n" 
			+ "         listeners = new PropertyChangeSupport(this);\n" 
			+ "      }\n" 
			+ "      listeners.addPropertyChangeListener(listener);\n" 
			+ "      return true;\n" 
			+ "   }\n";

	private String addPropertyChangeListenerName = ""
			+ "   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)\n"
			+ "   {\n"
			+ "      if (listeners == null) {\n"
			+ "         listeners = new PropertyChangeSupport(this);\n"
			+ "      }\n"
			+ "      listeners.addPropertyChangeListener(propertyName, listener);\n"
			+ "      return true;\n"
			+ "   }\n";

	private String removePropertyChange = ""
			+ "   public boolean removePropertyChangeListener(PropertyChangeListener listener)\n"
			+ "   {\n"
			+ "      if (listeners == null) {\n"
			+ "         listeners.removePropertyChangeListener(listener);\n"
			+ "      }\n"
			+ "      listeners.removePropertyChangeListener(listener);\n"
			+ "      return true;\n"
			+ "   }\n";
	
	private String removePropertyChangeName = ""
			+ "   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)\n"
			+ "   {\n"
			+ "      if (listeners != null) {\n"
			+ "         listeners.removePropertyChangeListener(propertyName, listener);\n"
			+ "      }\n"
			+ "      return true;\n"
			+ "   }\n";
	
	private String removeYouMethod = ""
			+ "   public void removeYou()\n"
			+ "   {\n"
			+ "      {{removeYou}}"
			+ "   }\n";
	
	public String generate(HashMap<String, String> parameters, boolean propertyChangeSupport, boolean removeYou) {
		if (parameters == null) {
			return null;
		}
		if (parameters.isEmpty()) {
			return null;
		}
		String result = ""
				+ packages
				+ "\n"
				+ imports
				+ declaration
				+ "{\n"
				+ "\n";
		if (propertyChangeSupport) {
			result += ""
					+ listeners
					+ "\n"
					+ firePropertyChange
					+ "\n"
					+ addPropertyChangeListener
					+ "\n"
					+ addPropertyChangeListenerName
					+ "\n"
					+ removePropertyChange
					+ "\n"
					+ removePropertyChangeName
					+ "\n";
		}
		if (removeYou) {
			result += ""
					+ removeYouMethod
					+ "\n";
		}
		for (Entry<String, String> entry : parameters.entrySet()) {
			result = replace(result, entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	@Override
	public JavaClass enableInterface() {
		this.isInterface = true;
		return this;
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

//	private String determineClassType() {
//		String result = "class";
//		if (isInterface()) {
//			result = "interface";
//		}
//		return result;
//	}
//	
//	private String determineSuperClasses() {
//		String result = "{{exists}}{{extends}}{{seperator}}{{implements}}";
//		String extendReplace = "";
//		String seperatorReplace = "";
//		String implementsReplace = "";
//		String existsReplace = "";
//		boolean firstExtend = true;
//		boolean firstImplement = true;
//		for (TemplateLink templateLink : links) {
//			if (templateLink.isSuperLink()) {
//				if (templateLink.getOther().isInterface()) {
//					if (firstImplement) {
//						firstImplement = false;
//						implementsReplace = "implements ";
//					} else {
//						implementsReplace += ", ";
//					}
//					implementsReplace += templateLink.getOther().getName();
//				} else {
//					if (firstExtend) {
//						firstExtend = false;
//						extendReplace = "extends ";
//					} else {
//						extendReplace += ", ";
//					}
//					extendReplace += templateLink.getOther().getName();
//				}
//			}
//		}
//		if (!firstExtend || !firstImplement) {
//			existsReplace = " ";
//		}
//		if (!firstExtend && !firstImplement) {
//			seperatorReplace = ", ";
//		}
//		result = replace(result, "{{exists}}", existsReplace);
//		result = replace(result, "{{extends}}", extendReplace);
//		result = replace(result, "{{seperator}}", seperatorReplace);
//		result = replace(result, "{{implements}}", implementsReplace);
//		return result;
//	}
//	
//	private String determineImports() {
//		String result = "";
//		boolean found = false;
//		for (Entry<String, String> entry : imports.entrySet()) {
//			if (found == false) {
//				result += "\n";
//				found = true;
//			}
//			result += entry.getValue();
//		}
//		if (found) {
//			result += "\n";
//		}
//		return result;
//	}
//	
//	private String getImport(String string) {
//		return "import " + string + ";\n";
//	}
	
}