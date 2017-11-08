package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonTokener;

public class UpdateAccumulate {
	private Tokener tokener;
	private Entity change;
	private IdMap map;
	
	// Target
	private Object target;
	private Object defaultItem;
	private SendableEntityCreator creator;
	private String property;
	
	
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

	public UpdateAccumulate withMap(IdMap map) {
		this.map = map;
		return this;
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
