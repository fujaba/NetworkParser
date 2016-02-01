package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;

public interface Grammar {
	/**
	 * @param baseItem  The Object for read or write
	 * @param map		The IdMap
	 * @param filter	The current filter
	 * @param isId		The isReadId
	 * @param type		can be Write or Read
	 *
	 * @return the props of theJsonObject
	 */
	public BaseItem getProperties(BaseItem jsonObject,
			IdMap map, Filter filter, boolean isId, String type);
	
	/**
	 * @param baseItem The Object for read or write 
	 * @param map 	   The IdMap
	 * @param searchForSuperCreator Is Searching for Creator in superclasses
 	 * @param  type		can be Write or Read
	 * @return the Creator for this Item
	 */
	public SendableEntityCreator getCreator(BaseItem baseItem,
			IdMap map, boolean searchForSuperCreator, String type);
}
