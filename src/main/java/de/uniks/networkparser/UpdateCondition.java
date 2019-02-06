package de.uniks.networkparser;

import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleSet;

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

/**
 * Condition for Listener for changes in Element (Datamodel) in IdMap Or
 * AtomarCondition with PropertyChange
 * 
 * @author Stefan Lindel
 */
public class UpdateCondition implements ObjectCondition {
	private Object owner;
	private ObjectCondition condition; // FOR ATOM OR TRANSACTION

	// FOR ACCUMULATE
	private Tokener tokener;
	private Entity change;
	private IdMap map;
	// Target
	private Object defaultItem;

	// Target or StartClass
	private SendableEntityCreator creator;
	private String property; // May be class<?> or Object
	private ObjectCondition startCondition;

	private ObjectCondition endCondition;
	private String endProperty; // May be class<?> or Object
	private Object endClass;
	private SimpleSet<SimpleEvent> changes;
	private SendableEntityCreator endCreator;

	UpdateCondition() {

	}

	public UpdateCondition withContidion(ObjectCondition condition) {
		this.condition = condition;
		return this;
	}

	public static UpdateCondition createAtomarCondition(ObjectCondition listener) {
		UpdateCondition condition = new UpdateCondition();
		condition.condition = listener;
		return condition;
	}

	public static UpdateCondition createUpdateCondition() {
		return new UpdateCondition();
	}

	public static UpdateCondition createAcumulateCondition(Tokener tokener) {
		UpdateCondition condition = new UpdateCondition();
		condition.tokener = tokener;
		return condition;
	}

	public static UpdateCondition createAcumulateCondition(IdMap map) {
		UpdateCondition condition = new UpdateCondition();
		if(map != null) {
			condition.tokener = map.getMapListener().getTokener();
		}
		condition.map = map;
		return condition;
	}

	public static UpdateCondition createAcumulateCondition(Tokener tokener, Object target,
			SendableEntityCreator creator, String property) {
		UpdateCondition condition = new UpdateCondition();
		condition.tokener = tokener;
		condition.owner = target;
		condition.creator = creator;
		condition.property = property;
		return condition;
	}

	public static UpdateCondition createTransaction(IdMap map) {
		UpdateCondition condition = new UpdateCondition();
		condition.map = map;
		return condition;
	}

	public boolean isAtomar() {
		return condition != null;
	}

	public boolean isTransaction() {
		return (endProperty != null || endClass != null);
	}

	public boolean isAccumulate() {
		return tokener != null;
	}

	public boolean isChangeListener() {
		return condition == null;
	}

	public UpdateCondition withStart(String property, Object startClass) {
		this.owner = startClass;
		this.property = property;
		if (startClass instanceof Class<?> == false && map != null) {
			this.creator = map.getCreatorClass(startClass);
		}
		return this;
	}
	
	public UpdateCondition withStart(Object startClass) {
		this.owner = startClass;
		if (startClass instanceof Class<?> == false && map != null) {
			this.creator = map.getCreatorClass(startClass);
		}
		return this;
	}
	
	public UpdateCondition withEnd(String property) {
		this.endProperty = property;
		return this;
	}

	public UpdateCondition withStartConition(ObjectCondition condition) {
		this.startCondition = condition;
		return this;
	}

	public UpdateCondition withEnd(String property, Object endClass) {
		this.endClass = endClass;
		this.endProperty = property;
		if (endClass instanceof Class<?> == false && map != null) {
			endCreator = map.getCreatorClass(endClass);
		}
		return this;
	}

	public UpdateCondition withEndConition(ObjectCondition condition) {
		this.endCondition = condition;
		return this;
	}

	@Override
	public boolean update(Object evt) {
		if (isAtomar()) {
			if (evt instanceof PropertyChangeEvent) {
				return condition.update(evt);
			}
			return false;
		}
		// MUST BE A SIMPLEEVENT
		if (evt == null || evt instanceof SimpleEvent == false) {
			return false;
		}

		SimpleEvent event = (SimpleEvent) evt;
		if (isChangeListener()) {
			if (creator != null && property != null) {
				if (event.getNewValue() != null) {
					// CREATE ONE
					creator.setValue(event.getNewValue(), property, owner, SendableEntityCreator.NEW);
				} else {
					creator.setValue(event.getOldValue(), property, owner, SendableEntityCreator.REMOVE);
				}
				return false;
			}
			Object source = event.getSource();
			if(source instanceof IdMap) {
				IdMap map = (IdMap) source;
				return map.getKey(event.getModelValue()) == null && map.getKey(event.getNewValue()) == null;
			}
			return false;
		}
		if (isTransaction()) {
			if (startCondition != null && startCondition.update(evt) == false) {
				return false;
			}
			Object source = event.getSource();
			if (source == null) {
				return false;
			}
			//
			if (changes == null && property != null && property.equalsIgnoreCase(event.getPropertyName())) {
				// Search for Start Transaction
				if (owner instanceof Class<?>) {
					if (source.getClass() == owner) {
						this.changes = new SimpleSet<SimpleEvent>();
						return true;
					}
				} else if (owner != null && creator != null) {
					SendableEntityCreator creator = map.getCreatorClass(source);
					if (creator != null && creator == this.creator) {
						this.changes = new SimpleSet<SimpleEvent>();
						return true;
					}
				}
			}
			if (this.changes != null) {
				this.changes.add(event);
				// Check for End
				if (endCondition != null && endCondition.update(evt) == false) {
					return true;
				}
				if (endProperty != null && endProperty.equalsIgnoreCase(event.getPropertyName())) {
					// Search for Start Transaction
					if (endClass instanceof Class<?>) {
						if (source.getClass() == endClass) {
							if (this.condition != null) {
								return this.condition.update(this.changes);
							}
							this.changes = null;
							return true;
						}
					} else if (endClass != null && endCreator != null) {
						SendableEntityCreator creator = map.getCreatorClass(source);
						if (creator != null && creator == endCreator) {
							if (this.condition != null) {
								return this.condition.update(this.changes);
							}
							this.changes = null;
							return true;
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean changeItem(Object source, Object target, String property) {
		if(map == null) {
			return false;
		}
		SendableEntityCreator creator = map.getCreatorClass(source);
		Object defaultItem = creator.getSendableInstance(true);
		Object oldValue = creator.getValue(source, property);
		Object newValue = creator.getValue(source, property);

		if ((oldValue == null && newValue == null) || (oldValue != null && oldValue.equals(newValue))) {
			return false;
		}

		if (oldValue != creator.getValue(defaultItem, property)) {
			if (change == null) {
				change = tokener.newInstance();
				change.put(IdMap.ID, map.getId(source, true));
			}
			Entity child;

			// OldValue
			if (!change.has(SendableEntityCreator.REMOVE)) {
				child = (Entity) change.getValue(SendableEntityCreator.REMOVE);
				change.put(SendableEntityCreator.REMOVE, child);
			} else {
				child = tokener.newInstance();
			}
			SendableEntityCreator creatorClass = map.getCreatorClass(oldValue);
			if (creatorClass != null) {
				String oldId = map.getId(oldValue, true);
				if (oldId != null) {
					Entity item = tokener.newInstance();
					item.put(IdMap.ID, oldId);
					child.put(property, item);
				}
			} else {
				child.put(property, oldValue);
			}

			// NewValue
			if (!change.has(SendableEntityCreator.UPDATE)) {
				child = (Entity) change.getValue(SendableEntityCreator.UPDATE);
				change.put(SendableEntityCreator.UPDATE, child);
			} else {
				child = tokener.newInstance();
			}

			creatorClass = map.getCreatorClass(newValue);
			if (creatorClass != null) {
				String newId = map.getId(newValue, true);
				if (newId != null) {
					Entity item = tokener.newInstance();
					item.put(IdMap.ID, newId);
					child.put(property, item);
				}
			} else {
				child.put(property, newValue);
			}
		}
		return true;
	}

	public UpdateCondition withAttribute(Object newValue, String property) {
		changeAttribute(newValue, property);
		return this;
	}

	public UpdateCondition withTarget(Object value) {
		this.owner = value;
		if (value != null && map != null) {
			this.creator = map.getCreatorClass(owner);
			if (this.creator != null) {
				this.defaultItem = creator.getSendableInstance(true);
			}
		}
		return this;
	}

	public UpdateCondition withAcumulateTarget(Object value, SendableEntityCreator creator, String property) {
		this.owner = value;
		this.creator = creator;
		this.property = property;
		return this;
	}

	private void addChange(UpdateListener listener, Object source, SendableEntityCreator creator, String property,
			Object oldValue, Object newValue) {
		if(listener == null) {
			return;
		}
		if (this.change == null) {
			this.change = listener.change(property, source, creator, oldValue, newValue);
		} else {
			listener.change(property, creator, change, oldValue, newValue);
		}
	}

	public boolean changeAttribute(UpdateListener listener, Object source, SendableEntityCreator creator,
			String property, Object oldValue, Object newValue) {
		if (this.owner == null) {
			addChange(listener, source, creator, property, oldValue, newValue);
			return true;
		} else if (this.property == null) {
			addChange(listener, source, creator, property, oldValue, newValue);
			return true;
		} else if (this.property.equals(property)) {
			addChange(listener, source, creator, property, oldValue, newValue);
			return true;
		}
		return false;
	}

	public boolean changeAttribute(Object newValue, String property) {
		return changeAttribute(owner, newValue, property, creator, defaultItem);
	}

	private boolean changeAttribute(Object target, Object newValue, String property, SendableEntityCreator creator,
			Object defaultItem) {
		if (creator == null) {
			return false;
		}
		Object oldValue = creator.getValue(target, property);

		if ((oldValue == null && newValue == null) || (oldValue != null && oldValue.equals(newValue))) {
			return false;
		}

		if (oldValue != creator.getValue(defaultItem, property)) {
			if (change == null) {
				change = tokener.newInstance();
				change.put(IdMap.ID, map.getId(target, true));
			}
			Entity child;

			// OldValue
			if (change.has(SendableEntityCreator.REMOVE)) {
				child = (Entity) change.getValue(SendableEntityCreator.REMOVE);
				change.put(SendableEntityCreator.REMOVE, child);
			} else {
				child = tokener.newInstance();
				change.put(SendableEntityCreator.REMOVE, child);
			}
			SendableEntityCreator creatorClass = map.getCreatorClass(oldValue);
			if (creatorClass != null) {
				String oldId = map.getId(oldValue, true);
				if (oldId != null) {
					Entity childItem = tokener.newInstance();
					childItem.put(IdMap.ID, oldId);
					child.put(property, childItem);
				}
			} else {
				child.put(property, oldValue);
			}

			// NewValue
			if (change.has(SendableEntityCreator.UPDATE)) {
				child = (Entity) change.getValue(SendableEntityCreator.UPDATE);
				change.put(SendableEntityCreator.UPDATE, child);
			} else {
				child = tokener.newInstance();
				change.put(SendableEntityCreator.UPDATE, child);
			}

			creatorClass = map.getCreatorClass(newValue);
			if (creatorClass != null) {
				String newId = map.getId(newValue, true);
				if (newId != null) {
					Entity childItem = tokener.newInstance();
					childItem.put(IdMap.ID, newId);
					child.put(property, childItem);
				}
			} else {
				child.put(property, newValue);
			}
		}
		return true;
	}

	public Entity getChange() {
		return change;
	}

	public UpdateCondition withChange(Entity change) {
		this.change = change;
		return this;
	}
}
