package de.uniks.networkparser.graph;

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
/**
 * GraphCreate Clazz for Condition.
 *
 * @author Stefan Lindel
 */
public class GraphPatternDelete implements UpdateListener, SendableEntityCreator {
	/** Constant for ITEM. */
	public static final String ITEM = "item";
	/** Varibale for Condition. */
	private Object item;

	@Override
	public boolean update(Object evt) {
		return true;
	}

	/** @return The Item to Create
	 */
	public Object getItem() {
		return item;
	}

	/**
	 * @param value		for new Condition
	 * @return 			Not Instance
	 */
	public GraphPatternDelete withItem(Object value) {
		this.item = value;
		return this;
	}

	@Override
	public String[] getProperties() {
		return new String[] {ITEM };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new GraphPatternDelete();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (ITEM.equalsIgnoreCase(attribute)) {
			return ((GraphPatternDelete) entity).getItem();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (ITEM.equalsIgnoreCase(attribute)) {
			((GraphPatternDelete) entity).withItem(value);
		}
		return false;
	}

	public static GraphPatternDelete create(Object condition) {
		return new GraphPatternDelete().withItem(condition);
	}
}
