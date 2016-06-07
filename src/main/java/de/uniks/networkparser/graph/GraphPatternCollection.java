package de.uniks.networkparser.graph;

import java.util.Iterator;
import java.util.Set;

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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleSet;
/**
 * GraphCreate Clazz for Condition.
 *
 * @author Stefan Lindel
 */
public class GraphPatternCollection implements UpdateListener, SendableEntityCreator {
	/** Constant for ITEM. */
	public static final String ITEM = "item";
	public static final String MATCHES = "matches";
	/** Varibale for Condition. */
	private String property;
	private Set<UpdateListener> matches;

	@Override
	public boolean update(Object evt) {
		return true;
	}

	/** @return The Item to Create
	 */
	public Object getProperty() {
		return property;
	}

	/**
	 * @param value		for new Condition
	 * @return 			Not Instance
	 */
	public GraphPatternCollection withProperty(String value) {
		this.property = value;
		return this;
	}
	
	/**
	 * @param value		for new Condition
	 * @return 			Not Instance
	 */
	public GraphPatternCollection with(UpdateListener... value) {
		if(value == null) {
			return this;
		}
		for(UpdateListener listener : value) {
			if(listener != null) {
				if(this.matches == null) {
					this.matches = new SimpleSet<UpdateListener>();
				}
				this.matches.add(listener);
			}
		}
		return this;
	}

	public Set<UpdateListener> getMatches() {
		return matches;
	}

	@Override
	public String[] getProperties() {
		return new String[] {ITEM,MATCHES};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new GraphPatternCollection();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (ITEM.equalsIgnoreCase(attribute)) {
			return ((GraphPatternCollection) entity).getProperty();
		}
		if (MATCHES.equalsIgnoreCase(attribute)) {
			return ((GraphPatternCollection) entity).getMatches();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (ITEM.equalsIgnoreCase(attribute)) {
			((GraphPatternCollection) entity).withProperty((String)value);
		}
		if (MATCHES.equalsIgnoreCase(attribute)) {
			((GraphPatternCollection) entity).with((UpdateListener[])value);
		}
		return false;
	}

	public static GraphPatternCollection create(String condition) {
		return new GraphPatternCollection().withProperty(condition);
	}
	
	public int size() {
		if(this.matches == null) {
			return 0;
		}
		int size=0;
		for(Iterator<UpdateListener> i = this.matches.iterator();i.hasNext();) {
			UpdateListener child = i.next();
			if(child instanceof GraphPatternCollection) {
				size += ((GraphPatternCollection) child).size();
			} else {
				size++;
			}
		}
		return size;
	}
}
