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
import java.util.ArrayList;

import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.logic.ValuesSimple;

public class Filter {
	protected Condition<ValuesSimple> idFilter;
	protected Condition<ValuesSimple> convertable;
	protected Condition<ValuesSimple> property;

	// Temporary variables
	protected ArrayList<Object> visitedObjects;
	protected Boolean full;
	protected ValuesMap filter;
	private String strategy = IdMap.NEW;

	public Filter withIdFilter(Condition<ValuesSimple> idFilter) {
		this.idFilter = idFilter;
		return this;
	}
	
	public Filter withMap(IdMap map) {
		this.filter = new ValuesMap().with(map);
		return this;
	}

	/**
	 * Filter for encoding ID of Element
	 * 
	 * @param entity Entity for Show Id
	 * @param className ClassName
	 * @return Boolean if encoding ID
	 */
	public boolean isId(Object entity, String className) {
		if (idFilter != null) {
			this.filter.with(entity, className);
			return idFilter.check(this.filter);
		}else {
			SendableEntityCreator creator = filter.getMap().getCreator(className, true);
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
	public Boolean isFullSeriation() {
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

	public Filter withPropertyRegard(Condition<ValuesSimple> property) {
		this.property = property;
		return this;
	}
	
	public Filter withConvertable(Condition<ValuesSimple> convertable) {
		this.convertable = convertable;
		return this;
	}

	public Filter newInstance(Filter referenceFilter) {
		if(referenceFilter == null) {
			referenceFilter = new Filter();
		}
		if (convertable != null) {
			referenceFilter.withConvertable(convertable);
		}
		if (idFilter != null) {
			referenceFilter.withIdFilter(idFilter);
		}
		if (property != null) {
			referenceFilter.withPropertyRegard(property);
		}
		if (full != null) {
			referenceFilter.withFull(full);
		}else if(referenceFilter.isFullSeriation() == null) {
			referenceFilter.withFull(false);
		}
		referenceFilter.withMap(this.filter.getMap());
		return referenceFilter;
	}
	
	ValuesMap getFilter() {
		return filter;
	}

	boolean hasObjects(Object element) {
		return getVisitedObjects().contains(element);
	}

	ArrayList<Object> getVisitedObjects() {
		if (visitedObjects == null) {
			visitedObjects = new ArrayList<Object>();
		}
		return visitedObjects;
	}
	int getIndexVisitedObjects(Object element) {
		int pos = 0;
		for (Object item : getVisitedObjects()) {
			if (item == element) {
				return pos;
			}
			pos++;
		}
		return -1;
	}

	Object getVisitedObjects(int index) {
		if (index>=0 && index < getVisitedObjects().size()) {
			return getVisitedObjects().get(index);
		}
		return null;
	}

	/**
	 * @param visitedObject
	 *            Visited Object to Add to List
	 * @return Filter
	 */
	Filter withObjects(Object... visitedObject) {
		if (visitedObject == null) {
			return this;
		}
		for (Object item : visitedObject) {
			if (item != null) {
				getVisitedObjects().add(item);
			}
		}
		return this;
	}

	public String[] getProperties(SendableEntityCreator creator) {
		return creator.getProperties();
	}

	boolean isPropertyRegard(Object entity, String property, Object value, int deep) {
		if (this.property != null) {
			this.filter.with(entity, property, value, deep);
			return this.property.check(filter);
		}
		return true;
	}
	
	boolean isConvertable(Object entity, String property, Object value, int deep) {
		if (this.convertable != null) {
			this.filter.with(entity, property, value, deep);
			return this.convertable.check(filter);
		}
		return true;
	}
	
	Object getRefByEntity(Object value) {
		if(visitedObjects == null)
			return null;
		for (int i = 0; i < visitedObjects.size(); i += 2) {
			if (visitedObjects.get(i) == value) {
				return visitedObjects.get(i + 1);
			}
		}
		return null;
	}

	/**
	 * Create a new Filter for Regard Filter (Encoding Object or remove link)
	 * 
	 * @param convertable Condition
	 * @return a new Filter for regard the model 
	 */
	public static Filter regard(Condition<ValuesSimple> convertable) {
		return new Filter().withPropertyRegard(convertable);
	}
	/**
	 * Create a new Filter for Converting Filter (Encoding Object or set only the Id)
	 * 
	 * @param convertable Condition
	 * @return a new Filter for Filter with Convertable Items 
	 */
	public static Filter convertable(Condition<ValuesSimple> convertable) {
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
