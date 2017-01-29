package de.uniks.template;

import de.uniks.template.file.links.TemplateLink;

public abstract class TemplateAssociation {

	protected TemplateLink sourceLink;
	
	protected TemplateLink otherLink;

	public TemplateLink getSourceLink() {
		return sourceLink;
	}

	public TemplateLink getOtherLink() {
		return otherLink;
	}

}
