package de.uniks.template.java;

import de.uniks.template.TemplateModel;
import de.uniks.template.file.TemplateFile;
import de.uniks.template.java.file.JavaClass;
import de.uniks.template.java.file.links.JavaLink;

public class JavaModel extends TemplateModel {

//	public JavaClass addClass(String name) {
//		JavaClass javaClass = new JavaClass(name);
//		javaClass.setModel(this);
//		files.add(javaClass);
//		return javaClass;
//	}
//	
//	public JavaAssociation addAssociation(JavaClass source, String sourceName, String sourceCardinality, JavaClass other, String otherName, String otherCardinality) {
//		if (!(sourceCardinality.equals(JavaLink.CARDINALITY_MANY) || sourceCardinality.equals(JavaLink.CARDINALITY_ONE))
//				|| !(otherCardinality.equals(JavaLink.CARDINALITY_MANY) || otherCardinality.equals(JavaLink.CARDINALITY_ONE))) {
//			return null;
//		}
//		JavaLink sourceLink = source.addLink(sourceName, sourceCardinality, other, otherName, otherCardinality);
//		JavaLink otherLink = other.addLink(otherName, otherCardinality, source, sourceName, sourceCardinality);
//		JavaAssociation javaAssociation = new JavaAssociation(sourceLink, otherLink);
//		associations.add(javaAssociation);
//		return javaAssociation;
//	}

}
