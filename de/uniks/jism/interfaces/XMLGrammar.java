package de.uniks.jism.interfaces;

import de.uniks.jism.Tokener;
import de.uniks.jism.xml.XMLEntity;

public interface XMLGrammar extends SendableEntityCreator{
	public boolean parseChild(XMLEntity entity, XMLEntity child, Tokener value);
	
	public void addChildren(XMLEntity parent, XMLEntity child);

	public void endChild(String tag);
}
