package de.uniks.networkparser.xml.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.interfaces.EntityList;
/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.xml.XMLEntity;
/**
 * @author Stefan Interface for Grammar of XML reading.
 */

public interface XMLGrammar extends SendableEntityCreator {
	/**
	 * Methgod to parse Children.
	 *
	 * @param entity
	 *			the Entity
	 * @param child
	 *			the new Child
	 * @param value
	 *			the Tokener
	 * @return boolean for parsing Child
	 */
	public boolean parseChild(XMLEntity entity, XMLEntity child, Tokener value);

	/**
	 * Add a Child to parent Element.
	 *
	 * @param map
	 *			the IdMap
	 * @param parent
	 *			the Parent Element
	 * @param child
	 *			the new Child
	 */
	public void addChildren(IdMap map, EntityList parent, EntityList child);

	/**
	 * Set the EndTag of Child.
	 *
	 * @param tag
	 *			the End Tag
	 */
	public void endChild(String tag);


}
