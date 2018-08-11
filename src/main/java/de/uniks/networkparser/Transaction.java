package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleSet;

public class Transaction implements ObjectCondition{
	private ObjectCondition startCondition;
	private String startProperty; // May be class<?> or Object
	private Object startClass;
	private ObjectCondition endCondition;
	private String endProperty; // May be class<?> or Object
	private Object endClass;
	private ObjectCondition condition;
	private IdMap map;
	private SimpleSet<SimpleEvent> changes;
	private SendableEntityCreator startCreator;
	private SendableEntityCreator endCreator;
	
	public Transaction(IdMap map) {
		this.map = map;
	}

	public Transaction withStart(String property, Object startClass) {
		this.startClass = startClass;
		this.startProperty = property;
		if(startClass instanceof Class<?> == false) {
			startCreator = map.getCreatorClass(startClass);
		}
		return this;
	}
	
	public Transaction withStartConition(ObjectCondition condition) {
		this.startCondition = condition;
		return this;
	}
	
	public Transaction withEnd(String property, Object endClass) {
		this.endClass = endClass;
		this.endProperty = property;
		if(endClass instanceof Class<?> == false) {
			endCreator = map.getCreatorClass(endClass);
		}
		return this;
	}

	public Transaction withEndConition(ObjectCondition condition) {
		this.endCondition = condition;
		return this;
	}
	
	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		if(startCondition != null && startCondition.update(value) == false) {
			return false;
		}
		SimpleEvent event = (SimpleEvent) value;
		Object source = event.getSource(); 
		if(source == null) {
			return false;
		}
		// 
		if(changes == null && startProperty != null && startProperty.equalsIgnoreCase(event.getPropertyName())) {
			// Search for Start Transaction
			if(startClass instanceof Class<?>) {
				if(source.getClass() == startClass) {
					this.changes = new SimpleSet<SimpleEvent>();
					return true;
				}
			} else if(startClass != null && startCreator != null) {
				SendableEntityCreator creator = map.getCreatorClass(source);
				if(creator != null && creator == startCreator) {
					this.changes = new SimpleSet<SimpleEvent>();
					return true;
				}
			}
		}
		if(this.changes != null) {
			this.changes.add(event);
			// Check for End
			if(endCondition != null && endCondition.update(value) == false) {
				return true;
			}
			if(endProperty != null && endProperty.equalsIgnoreCase(event.getPropertyName())) {
				// Search for Start Transaction
				if(endClass instanceof Class<?>) {
					if(source.getClass() == endClass) {
						if(this.condition != null) {
							return this.condition.update(this.changes);
						}
						this.changes = null;
						return true;
					}
				} else if(endClass != null && endCreator != null) {
					SendableEntityCreator creator = map.getCreatorClass(source);
					if(creator != null && creator == endCreator) {
						if(this.condition != null) {
							return this.condition.update(this.changes);
						}
						this.changes = null;
						return true;
					}
				}
			}
			return true;
		}
		return false;
	}
	
}
