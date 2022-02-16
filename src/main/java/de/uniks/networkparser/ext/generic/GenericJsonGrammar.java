package de.uniks.networkparser.ext.generic;

/*
NetworkParser
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
import java.util.Iterator;

import de.uniks.networkparser.SimpleGrammar;
import de.uniks.networkparser.SimpleMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * The Class GenericJsonGrammar.
 *
 * @author Stefan
 */
public class GenericJsonGrammar extends SimpleGrammar {
	
	/**
	 * Gets the super creator.
	 *
	 * @param map the map
	 * @param searchForSuperCreator the search for super creator
	 * @param modelItem the model item
	 * @return the super creator
	 */
	@Override
	public SendableEntityCreator getSuperCreator(SimpleMap map, boolean searchForSuperCreator, Object modelItem) {
		if (modelItem == null && !searchForSuperCreator) {
			return null;
		}
		Class<?> search;
		if (modelItem instanceof Class<?>) {
			search = (Class<?>) modelItem;
		} else if (modelItem != null) {
			search = modelItem.getClass();
		} else {
			return null;
		}
		for (Iterator<SendableEntityCreator> i = map.iterator(); i.hasNext();) {
			SendableEntityCreator item = i.next();
			Object prototype = item.getSendableInstance(true);
			if (prototype instanceof Class<?>) {
				if (((Class<?>) prototype).isAssignableFrom(search)) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the new entity.
	 *
	 * @param creator the creator
	 * @param className the class name
	 * @param prototype the prototype
	 * @return the new entity
	 */
	@Override
	public Object getNewEntity(SendableEntityCreator creator, String className, boolean prototype) {
		if (creator == null) {
			return null;
		}
		Object entity = creator.getSendableInstance(prototype);
		if (!(entity instanceof Class<?>) || className == null) {
			return entity;
		}
		return ReflectionLoader.newInstance(className);
	}

	@Override
	protected Class<?> getClassForName(String className) {
		try {
			if (className != null) {
				return Class.forName(className);
			}
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
}
