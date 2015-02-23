package de.uniks.networkparser;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
import java.util.Map.Entry;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
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
	 * Gets the creator class.
	 *
	 * @param reference
	 *            the reference
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
	 *            Clazzname for search
	 * @param fullName
	 *            if the clazzName is the Fullname for search
	 * @return return a Creator class for a clazz name
	 */
	public SendableEntityCreator getCreator(String clazz, boolean fullName) {
		if (fullName) {
			return (SendableEntityCreator) this.creators.getValueItem(clazz);
		}
		clazz = "." + clazz;
		for (Iterator<Entry<String, SendableEntityCreator>> i = this.creators
				.entrySet().iterator(); i.hasNext();) {
			Entry<String, SendableEntityCreator> entry = i.next();
			if (entry.getKey().endsWith(clazz)
					&& entry.getValue() instanceof SendableEntityCreator) {
				return (SendableEntityCreator) entry.getValue();
			}
		}
		return null;
	}

	/**
	 * Adds the creator.
	 *
	 * @param creatorSet
	 *            the creater class
	 * @return return a Creator class for a clazz name
	 */
	public AbstractMap withCreator(Collection<SendableEntityCreator> creatorSet) {
		for (SendableEntityCreator sendableEntityCreator : creatorSet) {
			withCreator(sendableEntityCreator);
		}
		return this;
	}

	/**
	 * Adds the creator.
	 *
	 * @param iterator
	 *            the creater classes
	 * @return return a Creator class for a clazz name
	 */
	public AbstractMap withCreator(Iterable<SendableEntityCreator> iterator) {
		for (Iterator<SendableEntityCreator> i = iterator.iterator(); i
				.hasNext();) {
			withCreator(i.next());
		}
		return this;
	}

	/**
	 * add a Creator to list of all creators.
	 *
	 * @param className
	 *            the class name
	 * @param creator
	 *            the creator
	 * @return AbstractIdMap to interlink arguments
	 */
	public AbstractMap withCreator(String className,
			SendableEntityCreator creator) {
		this.creators.add(className, creator);
		return this;
	}

	/**
	 * Adds the creator.
	 *
	 * @param createrClass
	 *            the creater class
	 * @return AbstractIdMap to interlink arguments
	 */
	public AbstractMap withCreator(SendableEntityCreator... createrClass) {
		for (SendableEntityCreator creator : createrClass) {
			try{
				Object reference = creator.getSendableInstance(true);
				if (reference != null) {
					withCreator(reference.getClass().getName(), creator);
				}
			}catch(Exception e){}
		}
		return this;
	}

	/**
	 * remove the creator.
	 *
	 * @param className
	 *            the creater class
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
