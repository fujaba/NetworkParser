package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.ObjectCondition;
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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

/**
 * Fitler for Serialization or Deserializaition.
 *
 * @author Stefan Lindel
 */
public class Filter {
	/** The Constant MERGE. */
	public static final String MERGE = "merge";

	/** The Constant COLLISION. */
	public static final String COLLISION = "collision";

	protected ObjectCondition idFilter;
	protected ObjectCondition convertable; /* Or Notification for Decode */
	protected ObjectCondition property;

	/* Entweder eines der unteren Formate */
	protected static final byte FORMAT_NULL = 1;
	protected static final byte FORMAT_FULL = 2;
	protected static final byte FORMAT_TYPESAVE = 3;
	protected static final byte FORMAT_SHORTCLASS = 4;

	/**
	 * Format 0= REFERENCE 1 = NULL-Check 2 = FULL 3 = TYPESAVE
	 */
	private byte format; /* FORMAT */

	/* Temporary variables */
	private String strategy = SendableEntityCreator.UPDATE;
	private boolean simpleFormat;

	/**
	 * With id filter.
	 *
	 * @param idFilter the id filter
	 * @return the filter
	 */
	public Filter withIdFilter(ObjectCondition idFilter) {
		this.idFilter = idFilter;
		return this;
	}

	/**
	 * Filter for encoding ID of Element.
	 *
	 * @param entity    Entity for Show Id
	 * @param className ClassName
	 * @param map       The IdMap
	 * @return boolean if encoding ID
	 */
	public boolean isId(Object entity, String className, SimpleMap map) {
		if (idFilter != null) {
			return idFilter.update(new SimpleEvent(SendableEntityCreator.NEW, null, map, className, null, entity));
		} else if (map != null) {
			SendableEntityCreator creator = map.getCreator(className, true, true, null);
			if (creator != null) {
				return !(creator instanceof SendableEntityCreatorNoIndex);
			}
		}
		return true;
	}

	/**
	 * Checks if is simple format.
	 *
	 * @param entity the entity
	 * @param creator the creator
	 * @param className the class name
	 * @param map the map
	 * @return true, if is simple format
	 */
	public boolean isSimpleFormat(Object entity, SendableEntityCreator creator, String className, SimpleMap map) {
		if (this.isSimpleFormat()) {
			return true;
		}
		if (creator instanceof SendableEntityCreatorNoIndex || !isId(entity, className, map)) {
			return true;
		}
		return false;
	}

	/**
	 * Serialization the Full object inclusive null value.
	 *
	 * @return boolean for serialization the full object
	 */
	public boolean isFullSerialization() {
		return (format % FORMAT_SHORTCLASS) >= FORMAT_FULL;
	}

	/**
	 * Checks if is typ save.
	 *
	 * @return true, if is typ save
	 */
	public boolean isTypSave() {
		return (format % FORMAT_SHORTCLASS) >= FORMAT_TYPESAVE;
	}

	/**
	 * Checks if is null check.
	 *
	 * @return true, if is null check
	 */
	public boolean isNullCheck() {
		return (format % FORMAT_SHORTCLASS) >= FORMAT_NULL;
	}

	/**
	 * Checks if is short class.
	 *
	 * @return true, if is short class
	 */
	public boolean isShortClass() {
		return format >= FORMAT_SHORTCLASS;
	}

	/**
	 * Serialization the Full object inclusive null value.
	 *
	 * @param format for serialization the full object
	 * @return self instance
	 */
	public Filter withFormat(byte format) {
		this.format = format;
		return this;
	}

	/**
	 * With property regard.
	 *
	 * @param property the property
	 * @return the filter
	 */
	public Filter withPropertyRegard(ObjectCondition property) {
		this.property = property;
		return this;
	}

	/**
	 * With convertable.
	 *
	 * @param convertable the convertable
	 * @return the filter
	 */
	public Filter withConvertable(ObjectCondition convertable) {
		this.convertable = convertable;
		return this;
	}

	/**
	 * Convert the Entity.
	 *
	 * @param entity   The Entity
	 * @param property The Property to Convert
	 * @param value    The childValue
	 * @param map      IdMap
	 * @param deep     Deep
	 * @return Number for Convert 1 for Convert 0 for Reference -1 for not Convert
	 */
	public int convert(Object entity, String property, Object value, SimpleMap map, int deep) {
		if (this.convertable == null && this.property == null) {
			return 1;
		}
		SimpleEvent event = new SimpleEvent(this.strategy, map, property, deep, null, entity, value, null);
		if (this.property != null && !this.property.update(event)) {
			return -1;
		}
		if (this.convertable != null && !this.convertable.update(event)) {
			return 0;
		}
		return 1;
	}

	/**
	 * Checks if is convertable.
	 *
	 * @param event the event
	 * @return true, if is convertable
	 */
	public boolean isConvertable(SimpleEvent event) {
		return this.convertable == null || this.convertable.update(event);
	}

	/**
	 * Gets the property regard.
	 *
	 * @return the property regard
	 */
	public ObjectCondition getPropertyRegard() {
		return property;
	}

	/**
	 * Create a new Filter for Regard Filter (Encoding Object or remove link).
	 *
	 * @param convertable Condition
	 * @return a new Filter for regard the model
	 */
	public static Filter regard(ObjectCondition convertable) {
		return new Filter().withPropertyRegard(convertable);
	}

	/**
	 * Create a new Filter for Converting Filter (Encoding Object or set only the
	 * Id).
	 *
	 * @param convertable Condition
	 * @return a new Filter for Filter with Convertable Items
	 */
	public static Filter convertable(ObjectCondition convertable) {
		return new Filter().withConvertable(convertable);
	}

	/**
	 * Gets the properties.
	 *
	 * @param creator the creator
	 * @return the properties
	 */
	public String[] getProperties(SendableEntityCreator creator) {
		if (creator == null) {
			return new String[0];
		}
		return creator.getProperties();
	}

	/**
	 * Strategy for setting property value in model.
	 *
	 * @return String type of set Value
	 */
	public String getStrategy() {
		return strategy;
	}

	/**
	 * With strategy.
	 *
	 * @param strategy the strategy
	 * @return the filter
	 */
	public Filter withStrategy(String strategy) {
		this.strategy = strategy;
		return this;
	}

	/**
	 * Checks if is simple format.
	 *
	 * @return true, if is simple format
	 */
	public boolean isSimpleFormat() {
		return simpleFormat;
	}

	/**
	 * With simple format.
	 *
	 * @param value the value
	 * @return the filter
	 */
	public Filter withSimpleFormat(boolean value) {
		this.simpleFormat = value;
		return this;
	}

	/**
	 * Suspend notification.
	 */
	public void suspendNotification() {
		this.strategy = SendableEntityCreator.NEW;
	}

	/**
	 * Full Serialization.
	 *
	 * @return a Filter for Full Serialization
	 */
	public static Filter createFull() {
		return new Filter().withFormat(FORMAT_FULL);
	}

	/**
	 * Full Serialization.
	 *
	 * @return a Filter for Full Serialization
	 */
	public static Filter createChange() {
		return new Filter().withStrategy(SendableEntityCreator.NEW);
	}

	/**
	 * Simple Serialization.
	 *
	 * @return a Filter for Simple Serialization
	 */
	public static Filter createSimple() {
		return new Filter().withSimpleFormat(true);
	}

	/**
	 * Null Check Serialization.
	 *
	 * @return a Filter for Null-Check Serialization
	 */
	public static Filter createNull() {
		return new Filter().withFormat(FORMAT_NULL);
	}

	/**
	 * TypeSave Serialization.
	 *
	 * @return a Filter for TypeSave Serialization
	 */
	public static Filter createTypSave() {
		return new Filter().withFormat(FORMAT_TYPESAVE);
	}

	/**
	 * Convert property.
	 *
	 * @param entity the entity
	 * @param fullProp the full prop
	 */
	public void convertProperty(Object entity, String fullProp) {
	}
}
