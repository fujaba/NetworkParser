package de.uniks.networkparser;

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
import java.util.Collection;
import java.util.Iterator;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleKeyValueList;
/**
 * AbstractIdMap embedded all methods for all formats.
 *
 */

public abstract class AbstractMap implements Iterable<SendableEntityCreator> {
	/** The creators. */
	protected SimpleKeyValueList<String, SendableEntityCreator> creators = new SimpleKeyValueList<String, SendableEntityCreator>()
			.withAllowDuplicate(false);
	/**
	 * boolean for switch of search for Interface or Abstract superclass for entity
	 */
	protected boolean searchForSuperCreator;
	/**
	 * Gets the creator class.
	 *
	 * @param reference
	 *			the reference
	 * @return the creator class
	 */
	public SendableEntityCreator getCreatorClass(Object reference) {
		if (reference == null) {
			return null;
		}
		return getCreator(reference.getClass().getName(), true);
	}

	/**
	 * Gets the creator classes.
	 *
	 * @param clazz
	 *			Clazzname for search
	 * @param fullName
	 *			if the clazzName is the Fullname for search
	 * @return return a Creator class for a clazz name
	 */
	public SendableEntityCreator getCreator(String clazz, boolean fullName) {
		Object creator = this.creators.getValue(clazz);
		if (creator != null || fullName ) {
			return (SendableEntityCreator) creator;
		}

		if(clazz.lastIndexOf(".")>=0) {
			clazz = "."+clazz.substring(clazz.lastIndexOf(".")+1);
		} else {
			clazz = "." + clazz;
		}
		for(int i=0;i<this.creators.size();i++) {
			String key = this.creators.getKeyByIndex(i);
			SendableEntityCreator value = this.creators.getValueByIndex(i);
			if (key.endsWith(clazz)
					&& value instanceof SendableEntityCreator) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Adds the creator.
	 *
	 * @param creatorSet
	 *			the creater class
	 * @return return a Creator class for a clazz name
	 */
	public AbstractMap with(Collection<SendableEntityCreator> creatorSet) {
		if(creatorSet == null) {
			return this;
		}
		for (SendableEntityCreator sendableEntityCreator : creatorSet) {
			with(sendableEntityCreator);
		}
		return this;
	}

	/**
	 * Adds the creator.
	 *
	 * @param iterator
	 *			the creater classes
	 * @return return a Creator class for a clazz name
	 */
	public AbstractMap with(Iterable<SendableEntityCreator> iterator) {
		if(iterator == null) {
			return null;
		}
		for (Iterator<SendableEntityCreator> i = iterator.iterator(); i
				.hasNext();) {
			with(i.next());
		}
		return this;
	}

	/**
	 * add a Creator to list of all creators.
	 *
	 * @param className
	 *			the class name
	 * @param creator
	 *			the creator
	 * @return AbstractIdMap to interlink arguments
	 */
	public AbstractMap with(String className,
			SendableEntityCreator creator) {
		addCreator(className, creator);
		return this;
	}

    public boolean addCreator(String className, SendableEntityCreator creator) {
    	boolean result = this.creators.add(className, creator);
		if (creator instanceof SendableEntityCreatorTag) {
			SendableEntityCreatorTag creatorTag = (SendableEntityCreatorTag) creator;
			this.creators.add(creatorTag.getTag(), creator);
		}
		return result;
    }

	/**
	 * Adds the creator.
	 *
	 * @param createrClass
	 *			the creater class
	 * @return AbstractIdMap to interlink arguments
	 */
	public AbstractMap with(SendableEntityCreator... createrClass) {
		addCreator(createrClass);
		return this;
	}

	public boolean addCreator(SendableEntityCreator... createrClass) {
		if(createrClass == null) {
			return false;
		}
		for (SendableEntityCreator creator : createrClass) {
			if(creator == null)
				continue;
			try{
				Object reference = creator.getSendableInstance(true);
				if (reference != null) {
					if (reference instanceof Class<?>) {
						this.searchForSuperCreator = true;
						addCreator(((Class<?>)reference).getName(), creator);
					} else {
						addCreator(reference.getClass().getName(), creator);
					}
				}
			}catch(Exception e){}
		}
		return true;
    }

	/**
	 * remove the creator.
	 *
	 * @param className
	 *			the creater class
	 * @return true, if successful
	 */
	public boolean removeCreator(String className) {
		return this.creators.remove(className) != null;
	}

	@Override
	public Iterator<SendableEntityCreator> iterator() {
		return this.creators.values().iterator();
	}
}
