package de.uniks.networkparser.interfaces;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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

	public Entity writeBasicValue(Entity entity, String className, String id, String type, IdMap map);

	public BaseItem encode(Object entity, MapEntity map);

	public boolean writeValue(BaseItem parent, String property, Object value, MapEntity map, Tokener tokener);
}
