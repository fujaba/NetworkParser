package de.uniks.template;

import java.util.LinkedHashSet;

import de.uniks.template.file.TemplateFile;

public abstract class TemplateModel {

	protected LinkedHashSet<TemplateFile> files = new LinkedHashSet<TemplateFile>();
	
	protected LinkedHashSet<TemplateAssociation> associations = new LinkedHashSet<TemplateAssociation>();
	
}
