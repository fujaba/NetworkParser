package de.uniks.jism;

import java.util.LinkedHashSet;

import de.uniks.jism.logic.Condition;

public class Filter {
	private Condition idFilter;
	private Condition convertable;
	private Condition property;
	private LinkedHashSet<String> visitedObjects;

	public Condition getIdFilter() {
		return idFilter;
	}
	
	public Filter withIdFilter(Condition idFilter) {
		this.idFilter = idFilter;
		return this;
	}
	
	public boolean isId(IdMap map, Object entity, String className) {
		if(idFilter!=null){
			return idFilter.matches(map, entity, className, null, false, 0);
		}
		return true;
	}
	
	public Condition getConvertable() {
		return convertable;
	}
	public Filter withConvertable(Condition convertable) {
		this.convertable = convertable;
		return this;
	}
	public Condition getProperty() {
		return property;
	}
	public Filter withProperty(Condition property) {
		this.property = property;
		return this;
	}
	public Filter withStandard(Filter referenceFilter) {
		if(idFilter== null){
			idFilter = referenceFilter.getIdFilter();
		}
		if(convertable== null){
			convertable = referenceFilter.getConvertable();
		}
		if(property== null){
			property = referenceFilter.getProperty();
		}
		visitedObjects=new LinkedHashSet<String>();
		return this;
	}
	
	public Filter clone(){
		return new Filter().withConvertable(convertable).withIdFilter(idFilter).withProperty(property);
	}
	public boolean hasVisitedObjects(String id) {
		return visitedObjects.contains(id);
	}
	
	public void addToVisitedObjects(String visitedObjects) {
		this.visitedObjects.add(visitedObjects);
	}

	public boolean isRegard(IdMap map, Object entity,
			String property, Object value, boolean isMany, int deep) {
		if(this.property!=null){
			return this.property.matches(map, entity, property, value, isMany, deep);
		}
		return true;
	}

	public boolean isConvertable(IdMap map, Object entity,
			String property, Object value, boolean isMany, int deep) {
		if(this.convertable!=null){
			return this.convertable.matches(map, entity, property, value, isMany, deep);
		}
		return true;
	}
}
