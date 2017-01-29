package de.uniks.template.java;

import de.uniks.template.TemplateAssociation;
import de.uniks.template.java.file.links.JavaLink;

public class JavaAssociation extends TemplateAssociation {

	public JavaAssociation(JavaLink sourceLink, JavaLink otherLink) {
		this.sourceLink = sourceLink;
		this.otherLink = otherLink;
	}

}
