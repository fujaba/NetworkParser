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
 	 * @param type		can be Write or Read
 	 * @param item 		The Object for read or write
	 * @param map 	   	The IdMap
	 * @param searchForSuperCreator Is Searching for Creator in superclasses
	 * @param className 	   The ClassName of Item
	 * @return the Creator for this Item
	 */
	public SendableEntityCreator getCreator(String type, Object item,
			IdMap map, boolean searchForSuperCreator, String className);
	
	public String getId(Object obj, IdMapCounter counter);
	
	/**
	 * Get a Value from the Item
	 * @param item target item
	 * @param property the Property
	 * @return get the Value of the key as String
	 */
	public String getValue(Entity item, String property);
	
	public boolean hasValue(Entity item, String property);
	
	/**
	 * Get a new Instance of Element from the Creator
	 * @param creator The EntityCreator
	 * @param className Alternative Name of Class
	 * @param prototype switch for getNewEntity only for prototype
	 * @return The new Instance
	 */
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
