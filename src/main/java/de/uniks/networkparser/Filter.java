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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.logic.Condition;
import de.uniks.networkparser.logic.ValuesMap;
import de.uniks.networkparser.logic.ValuesSimple;

public class Filter {
	protected Condition<ValuesSimple> idFilter;
	protected Condition<ValuesSimple> convertable;
	protected Condition<ValuesSimple> property;

	// Temporary variables
	protected ArrayList<Object> visitedObjects;
	protected ArrayList<ReferenceObject> refs;
	protected Boolean full;
	protected ValuesMap filterMap;
	private String strategy = IdMap.NEW;

	public Condition<ValuesSimple> getIdFilter() {
		return idFilter;
	}

	public Filter withIdFilter(Condition<ValuesSimple> idFilter) {
		this.idFilter = idFilter;
		return this;
	}

	/**
	 * Filter for encoding ID of Element
	 * 
	 * @param map the Map
	 * @param entity Entity for Show Id
	 * @param className ClassName
	 * @return Boolean if encoding ID
	 */
	public boolean isId(IdMap map, Object entity, String className) {
		if (idFilter != null) {
			return idFilter.check(ValuesMap.with(map, entity, className));
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
	 *
	 * @return boolean for serialization the full object
	 */
	public Boolean isFullSeriation() {
		return full;
	}

	public Filter withFull(boolean value) {
		this.full = value;
		return this;
	}

	public Condition<ValuesSimple> getConvertable() {
		return convertable;
	}

	public Filter withConvertable(Condition<ValuesSimple> convertable) {
		this.convertable = convertable;
		return this;
	}

	public Condition<ValuesSimple> getPropertyRegard() {
		return property;
	}

	public Filter withPropertyRegard(Condition<ValuesSimple> property) {
		this.property = property;
		return this;
	}

	public Filter withStandard(Filter referenceFilter) {
		if (idFilter == null && referenceFilter != null) {
			idFilter = referenceFilter.getIdFilter();
		}
		if (convertable == null && referenceFilter != null) {
			convertable = referenceFilter.getConvertable();
		}
		if (property == null && referenceFilter != null) {
			property = referenceFilter.getPropertyRegard();
		}
		refs = new ArrayList<ReferenceObject>();
		if (full == null && referenceFilter != null) {
			full = referenceFilter.isFullSeriation();
			if (full == null) {
				full = false;
			}
		}
		return this;
	}

	public Filter clone() {
		Filter reference = new Filter();
		if (reference.getClass().getName().equals(this.getClass().getName())) {
			return clone(new Filter());
		}
		return this;
	}

	protected Filter clone(Filter newInstance) {
		return newInstance.withConvertable(convertable).withIdFilter(idFilter)
				.withPropertyRegard(property);
	}

	public boolean hasObjects(Object element) {
		return getVisitedObjects().contains(element);
	}

	ArrayList<Object> getVisitedObjects() {
		if (visitedObjects == null) {
			visitedObjects = new ArrayList<Object>();
		}
		return visitedObjects;
	}
	public int getIndexVisitedObjects(Object element) {
		int pos = 0;
		for (Object item : getVisitedObjects()) {
			if (item == element) {
				return pos;
			}
			pos++;
		}
		return -1;
	}

	public Object getVisitedObjects(int index) {
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
	public Filter withObjects(Object... visitedObject) {
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
	
	public void initMapFilter(IdMap map) {
		this.filterMap = ValuesMap.withMap(map);
	}
	
	public boolean isPropertyRegard(Object entity, String property, Object value, int deep) {
		if (this.property != null) {
			this.filterMap.withValues(entity, property, value, deep);
			return this.property.check(filterMap);
		}
		return true;
	}
	
	public boolean isConvertable(Object entity, String property, Object value, int deep) {
		if (this.convertable != null) {
			this.filterMap.withValues(entity, property, value, deep);
			return this.convertable.check(filterMap);
		}
		return true;
	}

	public ArrayList<ReferenceObject> getRefs() {
		return refs;
	}

	public Filter with(ReferenceObject item) {
		if(refs == null) {
			return this;
		}
		refs.add(item);
		return this;
	}

	public Object getRefByEntity(Object value) {
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

	public String getStrategy() {
		return strategy;
	}

	public Filter withStrategy(String strategy) {
		this.strategy = strategy;
		return this;
	}
}
