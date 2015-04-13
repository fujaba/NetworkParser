package de.uniks.networkparser.json;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class UpdateAccumulate {
	private JsonObject change;
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
				change = new JsonObject().withValue(JsonIdMap.ID,
						map.getId(source));
			}
			JsonObject child;

			// OldValue
			if (!change.has(IdMapEncoder.REMOVE)) {
				child = change.getJsonObject(IdMapEncoder.REMOVE);
				change.put(IdMapEncoder.REMOVE, child);
			} else {
				child = new JsonObject();
			}
			SendableEntityCreator creatorClass = map.getCreatorClass(oldValue);
			if (creatorClass != null) {
				String oldId = map.getId(oldValue);
				if (oldId != null) {
					child.put(property,
							new JsonObject().withValue(JsonIdMap.ID, oldId));
				}
			} else {
				child.put(property, oldValue);
			}

			// NewValue
			if (!change.has(IdMapEncoder.UPDATE)) {
				child = change.getJsonObject(IdMapEncoder.UPDATE);
				change.put(IdMapEncoder.UPDATE, child);
			} else {
				child = new JsonObject();
			}

			creatorClass = map.getCreatorClass(newValue);
			if (creatorClass != null) {
				String newId = map.getId(newValue);
				if (newId != null) {
					child.put(property,
							new JsonObject().withValue(JsonIdMap.ID, newId));
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
				change = new JsonObject().withValue(JsonIdMap.ID,
						map.getId(item));
			}
			JsonObject child;

			// OldValue
			if (change.has(IdMapEncoder.REMOVE)) {
				child = change.getJsonObject(IdMapEncoder.REMOVE);
				change.put(IdMapEncoder.REMOVE, child);
			} else {
				child = new JsonObject();
			}
			SendableEntityCreator creatorClass = map.getCreatorClass(oldValue);
			if (creatorClass != null) {
				String oldId = map.getId(oldValue);
				if (oldId != null) {
					child.put(property,
							new JsonObject().withValue(JsonIdMap.ID, oldId));
				}
			} else {
				child.put(property, oldValue);
			}

			// NewValue
			if (change.has(IdMapEncoder.UPDATE)) {
				child = change.getJsonObject(IdMapEncoder.UPDATE);
				change.put(IdMapEncoder.UPDATE, child);
			} else {
				child = new JsonObject();
			}

			creatorClass = map.getCreatorClass(newValue);
			if (creatorClass != null) {
				String newId = map.getId(newValue);
				if (newId != null) {
					child.put(property,
							new JsonObject().withValue(JsonIdMap.ID, newId));
				}
			} else {
				child.put(property, newValue);
			}
		}
		return true;
	}

	public JsonObject getChange() {
		return change;
	}

	public UpdateAccumulate withChange(JsonObject change) {
		this.change = change;
		return this;
	}
}
