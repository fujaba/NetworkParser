package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;

public class UpdateAccumulate {
	private Entity factory = new JsonObject();
	private Entity change;
	private IdMap map;

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
				change = (Entity) factory.getNewList(true);
				change.put(IdMap.ID, map.getId(source, true));
			}
			Entity child;

			// OldValue
			if (!change.has(SendableEntityCreator.REMOVE)) {
				child = (Entity) change.getValue(SendableEntityCreator.REMOVE);
				change.put(SendableEntityCreator.REMOVE, child);
			} else {
				child = (Entity) factory.getNewList(true);
			}
			SendableEntityCreator creatorClass = map.getCreatorClass(oldValue);
			if (creatorClass != null) {
				String oldId = map.getId(oldValue, true);
				if (oldId != null) {
					Entity item = (Entity) factory.getNewList(true);
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
				child = (Entity) factory.getNewList(true);
			}

			creatorClass = map.getCreatorClass(newValue);
			if (creatorClass != null) {
				String newId = map.getId(newValue, true);
				if (newId != null) {
					Entity item = (Entity) factory.getNewList(true);
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

	public UpdateAccumulate withAttribute(Object item, Object newValue,
			String property) {
		changeAttribute(item, newValue, property);
		return this;
	}

	public boolean changeAttribute(Object item, Object newValue, String property) {
		SendableEntityCreator creator = map.getCreatorClass(item);
		Object defaultItem = creator.getSendableInstance(true);
		Object oldValue = creator.getValue(item, property);

		if ((oldValue == null && newValue == null)
				|| (oldValue != null && oldValue.equals(newValue))) {
			return false;
		}

		if (oldValue != creator.getValue(defaultItem, property)) {
			if (change == null) {
				change = (Entity) factory.getNewList(true);
				change.put(IdMap.ID, map.getId(item, true));
			}
			Entity child;

			// OldValue
			if (change.has(SendableEntityCreator.REMOVE)) {
				child = (Entity) change.getValue(SendableEntityCreator.REMOVE);
				change.put(SendableEntityCreator.REMOVE, child);
			} else {
				child = (Entity) factory.getNewList(true);
				change.put(SendableEntityCreator.REMOVE, child);
			}
			SendableEntityCreator creatorClass = map.getCreatorClass(oldValue);
			if (creatorClass != null) {
				String oldId = map.getId(oldValue, true);
				if (oldId != null) {
					Entity childItem = (Entity) factory.getNewList(true);
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
				child = (Entity) factory.getNewList(true);
				change.put(SendableEntityCreator.UPDATE, child);
			}

			creatorClass = map.getCreatorClass(newValue);
			if (creatorClass != null) {
				String newId = map.getId(newValue, true);
				if (newId != null) {
					Entity childItem = (Entity) factory.getNewList(true);
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
