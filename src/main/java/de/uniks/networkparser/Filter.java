package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.logic.SimpleMapEvent;

public class Filter {
	/** The Constant MERGE. */
	public static final String MERGE = "merge";

	/** The Constant COLLISION. */
	public static final String COLLISION = "collision";

	/** The Constant PRIO. */
	public static final String PRIO = "prio";

	protected UpdateListener idFilter;
	protected UpdateListener convertable;
	protected UpdateListener property;

	// Temporary variables
	protected boolean full;
	private String strategy = IdMap.NEW;

	public Filter withIdFilter(UpdateListener idFilter) {
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
			return idFilter.update(new SimpleMapEvent(IdMap.NEW, map, className, null, entity));
		}else {
			SendableEntityCreator creator = map.getCreator(className, true);
			if(creator!=null) {
				return !(creator instanceof SendableEntityCreatorNoIndex);
			}
		}
		return true;
	}

	/**
	 * Serialization the Full object inclusive null value
	 * @return boolean for serialization the full object
	 */
	public boolean isFullSeriation() {
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

	public Filter withPropertyRegard(UpdateListener property) {
		this.property = property;
		return this;
	}

	public Filter withConvertable(UpdateListener convertable) {
		this.convertable = convertable;
		return this;
	}

	boolean isPropertyRegard(Object entity, String property, Object value, IdMap map, int deep) {
		if (this.property != null) {
			return this.property.update(new SimpleMapEvent(IdMap.NEW, map, property, null, value).with(deep).withModelItem(entity));
		}
		return true;
	}

	boolean isConvertable(Object entity, String property, Object value, IdMap map, int deep) {
		if (this.convertable != null) {
			return this.convertable.update(new SimpleMapEvent(IdMap.NEW, map, property, null, value).with(deep).withModelItem(entity));
		}
		return true;
	}

	/**
	 * Create a new Filter for Regard Filter (Encoding Object or remove link)
	 *
	 * @param convertable Condition
	 * @return a new Filter for regard the model
	 */
	public static Filter regard(UpdateListener convertable) {
		return new Filter().withPropertyRegard(convertable);
	}
	/**
	 * Create a new Filter for Converting Filter (Encoding Object or set only the Id)
	 *
	 * @param convertable Condition
	 * @return a new Filter for Filter with Convertable Items
	 */
	public static Filter convertable(UpdateListener convertable) {
		return new Filter().withConvertable(convertable);
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
}
