package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.ObjectCondition;
/*
NetworkParser
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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

public class Filter {
	/** The Constant MERGE. */
	public static final String MERGE = "merge";

	/** The Constant COLLISION. */
	public static final String COLLISION = "collision";

	/** The Constant PRIO. */
	public static final String PRIO = "prio";

	public static final Filter SIMPLEFORMAT = new Filter().withSimpleFormat(true);

	protected ObjectCondition idFilter;
	protected ObjectCondition convertable;
	protected ObjectCondition property;

	// Temporary variables
	protected boolean full;
	private String strategy = SendableEntityCreator.NEW;
	private boolean simpleFormat;

	public Filter withIdFilter(ObjectCondition idFilter) {
		this.idFilter = idFilter;
		return this;
	}

	/**
	 * Filter for encoding ID of Element
	 *
	 * @param entity Entity for Show Id
	 * @param className ClassName
	 * @param map The IdMap
	 * @return boolean if encoding ID
	 */
	public boolean isId(Object entity, String className, IdMap map) {
		if (idFilter != null) {
			return idFilter.update(new SimpleEvent(SendableEntityCreator.NEW, null, map, className, null, entity));
		}else {
			SendableEntityCreator creator = map.getCreator(className, true, null);
			if(creator!=null) {
				return !(creator instanceof SendableEntityCreatorNoIndex);
			}
		}
		return true;
	}
	
	public boolean isSimpleFormat(Object entity, SendableEntityCreator creator, String className, IdMap map){
		if(this.isSimpleFormat()) {
			return true;
		}
		if (creator instanceof SendableEntityCreatorNoIndex || isId(entity, className, map) == false) {
			return true;
		}
		return false;
	}

	/**
	 * Serialization the Full object inclusive null value
	 * @return boolean for serialization the full object
	 */
	public boolean isFullSerialization() {
		return full;
	}
	/**
	 * Serialization the Full object inclusive null value
	 * @param value for serialization the full object
	 * @return self instance
	 */
	public Filter withFull(boolean value) {
		this.full = value;
		return this;
	}

	public Filter withPropertyRegard(ObjectCondition property) {
		this.property = property;
		return this;
	}

	public Filter withConvertable(ObjectCondition convertable) {
		this.convertable = convertable;
		return this;
	}

	boolean isPropertyRegard(Object entity, String property, Object value, IdMap map, int deep) {
		if (this.property != null) {
			return this.property.update(new SimpleEvent(this.strategy, map, property, null, value, deep, entity));
		}
		return true;
	}
	
	public ObjectCondition getPropertyRegard() {
		return property;
	}

	boolean isConvertable(Object entity, String property, Object value, IdMap map, int deep) {
		if (this.convertable != null) {
			return this.convertable.update(new SimpleEvent(this.strategy, map, property, null, value, deep, entity));
		}
		return true;
	}

	/**
	 * Create a new Filter for Regard Filter (Encoding Object or remove link)
	 *
	 * @param convertable Condition
	 * @return a new Filter for regard the model
	 */
	public static Filter regard(ObjectCondition convertable) {
		return new Filter().withPropertyRegard(convertable);
	}
	/**
	 * Create a new Filter for Converting Filter (Encoding Object or set only the Id)
	 *
	 * @param convertable Condition
	 * @return a new Filter for Filter with Convertable Items
	 */
	public static Filter convertable(ObjectCondition convertable) {
		return new Filter().withConvertable(convertable);
	}

	public String[] getProperties(SendableEntityCreator creator) {
		return creator.getProperties();
	}

	/**
	 * Strategy for setting property value in model
	 * @return String type of set Value
	 */
	public String getStrategy() {
		return strategy;
	}

	public Filter withStrategy(String strategy) {
		this.strategy = strategy;
		return this;
	}

	public boolean isSimpleFormat() {
		return simpleFormat;
	}

	public Filter withSimpleFormat(boolean value) {
		this.simpleFormat = value;
		return this;
	}
	
	public void suspendNotification() {
		this.strategy = SendableEntityCreator.UPDATE;
	}
}
