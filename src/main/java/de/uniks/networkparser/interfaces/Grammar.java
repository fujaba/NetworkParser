package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;

public interface Grammar {
	public static final String READ = "read";
	public static final String WRITE = "write";
	/**
	 * @param item		The Object for read or write
	 * @param map		The IdMap
	 * @param filter	The current filter
	 * @param isId		The Id enable for object
	 * @param type		can be Write or Read
	 *
	 * @return the props of theJsonObject
	 */
	public BaseItem getProperties(Entity item,
			IdMap map, Filter filter, boolean isId, String type);
	
	/**
	 * @param item The Object for read or write
	 * @param className 	   The ClassName of Item
	 * @param map 	   The IdMap
	 * @param searchForSuperCreator Is Searching for Creator in superclasses
 	 * @param  type		can be Write or Read
	 * @return the Creator for this Item
	 */
	public SendableEntityCreator getCreator(Object item, String className,
			IdMap map, boolean searchForSuperCreator, String type);
	
	public String getId(Object obj, IdMapCounter counter);
	
	public String getValue(Entity item, String property);
	
	public boolean hasValue(Entity item, String property);
	
	public Object getNewEntity(SendableEntityCreator creator, String className, boolean prototype);
	
	/**
	 * @param map		The IdMap
	 * @param prototyp	The new Prototyp
	 * @param className	The ClassName
	 * @param id		The Id of the Item
	 * @param properties The Properties for Set
	 * @param filter	The current filter
	 *
	 * @return the props of theJsonObject
	 */
	public BaseItem setProperties(IdMap map,
			SendableEntityCreator prototyp, String className, String id,
			Entity properties, Filter filter);
}
