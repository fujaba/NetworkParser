package de.uniks.networkparser;

/*
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
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleSet;

public class UpdateAccumulate implements ObjectCondition {
	private Tokener tokener;
	private Entity change;
	private IdMap map;

	// Target
	private Object defaultItem;

	// Target or StartClass
	private Object target;
	private SendableEntityCreator creator;
	private String property;	// May be class<?> or Object
	private ObjectCondition startCondition;
	
	private ObjectCondition endCondition;
	private String endProperty; // May be class<?> or Object
	private Object endClass;
	private ObjectCondition condition;
	private SimpleSet<SimpleEvent> changes;
	private SendableEntityCreator endCreator;

	
	public UpdateAccumulate(IdMap map) {
		this.map = map;
	}

	public UpdateAccumulate withStart(String property, Object startClass) {
		this.target = startClass;
		this.property = property;
		if(startClass instanceof Class<?> == false) {
			this.creator = map.getCreatorClass(startClass);
		}
		return this;
	}

	public UpdateAccumulate withEnd(String property, Object endClass) {
		this.endClass = endClass;
		this.endProperty = property;
		if(endClass instanceof Class<?> == false) {
			endCreator = map.getCreatorClass(endClass);
		}
		return this;
	}

	public UpdateAccumulate withEndConition(ObjectCondition condition) {
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
		if(changes == null && property != null && property.equalsIgnoreCase(event.getPropertyName())) {
			// Search for Start Transaction
			if(target instanceof Class<?>) {
				if(source.getClass() == target) {
					this.changes = new SimpleSet<SimpleEvent>();
					return true;
				}
			} else if(target != null && creator != null) {
				SendableEntityCreator creator = map.getCreatorClass(source);
				if(creator != null && creator == this.creator) {
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

	public UpdateAccumulate withStartConition(ObjectCondition condition) {
		this.startCondition = condition;
		return this;
	}

	public Tokener getTokener() {
		if(this.tokener == null) {
			this.tokener = new JsonTokener();
		}
		return tokener;
	}

	/**
	 * Set the Factory
	 * @param tokener the new Factory Tokener
	 * @return This Instance
	 */
	public UpdateAccumulate withTokener(Tokener tokener) {
		this.tokener = tokener;
		return this;
	}

	public boolean changeItem(Object source, Object target, String property) {
		SendableEntityCreator creator = map.getCreatorClass(source);
		Object defaultItem = creator.getSendableInstance(true);
		Object oldValue = creator.getValue(source, property);
		Object newValue = creator.getValue(source, property);

		if ((oldValue == null && newValue == null)
				|| (oldValue != null && oldValue.equals(newValue))) {
			return false;
		}

		if (oldValue != creator.getValue(defaultItem, property)) {
			if (change == null) {
				change = getTokener().newInstance();
				change.put(IdMap.ID, map.getId(source, true));
			}
			Entity child;

			// OldValue
			if (!change.has(SendableEntityCreator.REMOVE)) {
				child = (Entity) change.getValue(SendableEntityCreator.REMOVE);
				change.put(SendableEntityCreator.REMOVE, child);
			} else {
				child = getTokener().newInstance();
			}
			SendableEntityCreator creatorClass = map.getCreatorClass(oldValue);
			if (creatorClass != null) {
				String oldId = map.getId(oldValue, true);
				if (oldId != null) {
					Entity item = getTokener().newInstance();
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
				child = getTokener().newInstance();
			}

			creatorClass = map.getCreatorClass(newValue);
			if (creatorClass != null) {
				String newId = map.getId(newValue, true);
				if (newId != null) {
					Entity item = getTokener().newInstance();
					item.put(IdMap.ID, newId);
					child.put(property, item);
				}
			} else {
				child.put(property, newValue);
			}
		}
		return true;
	}

	public UpdateAccumulate withAttribute(Object newValue, String property) {
		changeAttribute(newValue, property);
		return this;
	}

	public UpdateAccumulate withTarget(Object value, SendableEntityCreator creator, String property) {
		this.target = value;
		this.creator = creator;
		this.property = property;
		return this;
	}

	public UpdateAccumulate withTarget(Object value) {
		this.target = value;
		if(value != null && map != null) {
			this.creator = map.getCreatorClass(target);
			if(this.creator!= null) {
				this.defaultItem = creator.getSendableInstance(true);
			}
		}
		return this;
	}

	private void addChange(UpdateListener listener, Object source, SendableEntityCreator creator, String property, Object oldValue, Object newValue) {
		if(this.change == null) {
			this.change = listener.change(property, source, creator, oldValue, newValue);
		} else {
			listener.change(property, creator, change, oldValue, newValue);
		}
	}

	public boolean changeAttribute(UpdateListener listener, Object source, SendableEntityCreator creator, String property, Object oldValue, Object newValue) {
		if(this.target == null) {
			addChange(listener, source, creator, property, oldValue, newValue);
			return true;
		} else if(this.property == null ) {
			addChange(listener, source, creator, property, oldValue, newValue);
			return true;
		} else if(this.property.equals(property)) {
			addChange(listener, source, creator, property, oldValue, newValue);
			return true;
		}
		return false;
	}


	public boolean changeAttribute(Object newValue, String property) {
		return changeAttribute(target, newValue, property, creator, defaultItem);
	}
	private boolean changeAttribute(Object target, Object newValue, String property, SendableEntityCreator creator, Object defaultItem) {
		if(creator == null) {
			return false;
		}
		Object oldValue = creator.getValue(target, property);

		if ((oldValue == null && newValue == null)
				|| (oldValue != null && oldValue.equals(newValue))) {
			return false;
		}

		if (oldValue != creator.getValue(defaultItem, property)) {
			if (change == null) {
				change = getTokener().newInstance();
				change.put(IdMap.ID, map.getId(target, true));
			}
			Entity child;

			// OldValue
			if (change.has(SendableEntityCreator.REMOVE)) {
				child = (Entity) change.getValue(SendableEntityCreator.REMOVE);
				change.put(SendableEntityCreator.REMOVE, child);
			} else {
				child = getTokener().newInstance();
				change.put(SendableEntityCreator.REMOVE, child);
			}
			SendableEntityCreator creatorClass = map.getCreatorClass(oldValue);
			if (creatorClass != null) {
				String oldId = map.getId(oldValue, true);
				if (oldId != null) {
					Entity childItem = getTokener().newInstance();
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
				child = getTokener().newInstance();
				change.put(SendableEntityCreator.UPDATE, child);
			}

			creatorClass = map.getCreatorClass(newValue);
			if (creatorClass != null) {
				String newId = map.getId(newValue, true);
				if (newId != null) {
					Entity childItem = getTokener().newInstance();
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

	public UpdateAccumulate withChange(Entity change) {
		this.change = change;
		return this;
	}
}
