package de.uniks.networkparser.interfaces;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.SimpleMap;
import de.uniks.networkparser.Tokener;

// TODO: Auto-generated Javadoc
/**
 * The Interface Grammar.
 *
 * @author Stefan
 */
public interface Grammar {
	
	/** The Constant READ. */
	public static final String READ = "read";
	
	/** The Constant WRITE. */
	public static final String WRITE = "write";

	/**
	 * Gets the properties.
	 *
	 * @param item The Object for read or write
	 * @param map  The IdMap
	 * @param isId The Id enable for object
	 * @return the props of theJsonObject
	 */
	public BaseItem getProperties(Entity item, MapEntity map, boolean isId);

	/**
	 * Gets the creator.
	 *
	 * @param type      can be Write or Read
	 * @param item      The Object for read or write
	 * @param map       The MapEntity
	 * @param className The ClassName of Item
	 * @return the Creator for this Item
	 */
	public SendableEntityCreator getCreator(String type, Object item, MapEntity map, String className);

	/**
	 * Gets the id.
	 *
	 * @param obj the obj
	 * @param map the map
	 * @return the id
	 */
	public String getId(Object obj, SimpleMap map);

	/**
	 * Get a Value from the Item.
	 *
	 * @param item     target item
	 * @param property the Property
	 * @return get the Value of the key as String
	 */
	public String getValue(Entity item, String property);

	/**
	 * Checks for value.
	 *
	 * @param item the item
	 * @param property the property
	 * @return true, if successful
	 */
	public boolean hasValue(Entity item, String property);

	/**
	 * Checks if is flat format.
	 *
	 * @return true, if is flat format
	 */
	public boolean isFlatFormat();

	/**
	 * Get a new Instance of Element from the Creator.
	 *
	 * @param creator   The EntityCreator
	 * @param className Alternative Name of Class
	 * @param prototype switch for getNewEntity only for prototype
	 * @return The new Instance
	 */
	public Object getNewEntity(SendableEntityCreator creator, String className, boolean prototype);

	/**
	 * Write basic value.
	 *
	 * @param entity the entity
	 * @param className the class name
	 * @param id the id
	 * @param type the type
	 * @param map the map
	 * @return the entity
	 */
	public Entity writeBasicValue(Entity entity, String className, String id, String type, SimpleMap map);

	/**
	 * Encode.
	 *
	 * @param entity the entity
	 * @param map the map
	 * @return the base item
	 */
	public BaseItem encode(Object entity, MapEntity map);

	/**
	 * Write value.
	 *
	 * @param parent the parent
	 * @param property the property
	 * @param value the value
	 * @param map the map
	 * @param tokener the tokener
	 * @return true, if successful
	 */
	public boolean writeValue(BaseItem parent, String property, Object value, MapEntity map, Tokener tokener);
}
