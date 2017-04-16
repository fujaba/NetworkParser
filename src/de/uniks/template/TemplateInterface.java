package de.uniks.template;

import de.uniks.networkparser.interfaces.SendableEntityCreator;

public interface TemplateInterface extends SendableEntityCreator{
	public boolean add(TemplateInterface result);

	public boolean setParent(TemplateInterface templateResultFile);
	
	public TemplateInterface getParent();
}
