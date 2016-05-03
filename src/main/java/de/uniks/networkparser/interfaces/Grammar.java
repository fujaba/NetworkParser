package de.uniks.networkparser.interfaces;

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
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;

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
	 * Get The Prefix For Properties
	 * @param creator The Creator
	 * @param format The Format Token
	 * @param isId is Id is Set
	 * @return The Propertyprefix
	 */
	public CharacterBuffer getPrefixProperties(SendableEntityCreator creator, Tokener format, boolean isId);
	
	public Entity writeBasicValue(Entity entity, BaseItem parent, String className, String id, MapEntity map);
	
	public BaseItem encode(Object entity, MapEntity map, Tokener tokener);

	public boolean writeValue(BaseItem parent, String property, Object value, MapEntity map, Tokener tokener);
}
