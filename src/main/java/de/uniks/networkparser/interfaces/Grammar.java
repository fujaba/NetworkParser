package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.Tokener;

public interface Grammar {
	public static final String READ = "read";
	public static final String WRITE = "write";
	/**
	 * @param item		The Object for read or write
	 * @param map		The IdMap
	 * @param isId		The Id enable for object
	 *
	 * @return the props of theJsonObject
	 */
	public BaseItem getProperties(Entity item, MapEntity map, boolean isId);

	/**
 	 * @param type		can be Write or Read
 	 * @param item 		The Object for read or write
	 * @param map 	   	The MapEntity
	 * @param className 	   The ClassName of Item
	 * @return the Creator for this Item
	 */
	public SendableEntityCreator getCreator(String type, Object item, MapEntity  map, String className);

	public String getId(Object obj, IdMap map);

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

	public Entity writeBasicValue(Entity entity, String className, String id, IdMap map);
	
	public BaseItem encode(Object entity, MapEntity map);

	public boolean writeValue(BaseItem parent, String property, Object value, MapEntity map, Tokener tokener);
}
