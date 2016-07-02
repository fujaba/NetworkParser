package de.uniks.networkparser.json;

/*
NetworkParser
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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class UpdateJsonAccumulate {
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
				change = new JsonObject().withValue(IdMap.ID,
						map.getId(source));
			}
			JsonObject child;

			// OldValue
			if (!change.has(IdMap.REMOVE)) {
				child = change.getJsonObject(IdMap.REMOVE);
				change.put(IdMap.REMOVE, child);
			} else {
				child = new JsonObject();
			}
			SendableEntityCreator creatorClass = map.getCreatorClass(oldValue);
			if (creatorClass != null) {
				String oldId = map.getId(oldValue);
				if (oldId != null) {
					child.put(property,
							new JsonObject().withValue(IdMap.ID, oldId));
				}
			} else {
				child.put(property, oldValue);
			}

			// NewValue
			if (!change.has(IdMap.UPDATE)) {
				child = change.getJsonObject(IdMap.UPDATE);
				change.put(IdMap.UPDATE, child);
			} else {
				child = new JsonObject();
			}

			creatorClass = map.getCreatorClass(newValue);
			if (creatorClass != null) {
				String newId = map.getId(newValue);
				if (newId != null) {
					child.put(property,
							new JsonObject().withValue(IdMap.ID, newId));
				}
			} else {
				child.put(property, newValue);
			}
		}
		return true;
	}

	public UpdateJsonAccumulate withMap(IdMap map) {
		this.map = map;
		return this;
	}

	public UpdateJsonAccumulate withAttribute(Object item, Object newValue,
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
				change = new JsonObject().withValue(IdMap.ID,
						map.getId(item));
			}
			JsonObject child;

			// OldValue
			if (change.has(IdMap.REMOVE)) {
				child = change.getJsonObject(IdMap.REMOVE);
				change.put(IdMap.REMOVE, child);
			} else {
				child = new JsonObject();
			}
			SendableEntityCreator creatorClass = map.getCreatorClass(oldValue);
			if (creatorClass != null) {
				String oldId = map.getId(oldValue);
				if (oldId != null) {
					child.put(property,
							new JsonObject().withValue(IdMap.ID, oldId));
				}
			} else {
				child.put(property, oldValue);
			}

			// NewValue
			if (change.has(IdMap.UPDATE)) {
				child = change.getJsonObject(IdMap.UPDATE);
				change.put(IdMap.UPDATE, child);
			} else {
				child = new JsonObject();
			}

			creatorClass = map.getCreatorClass(newValue);
			if (creatorClass != null) {
				String newId = map.getId(newValue);
				if (newId != null) {
					child.put(property,
							new JsonObject().withValue(IdMap.ID, newId));
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

	public UpdateJsonAccumulate withChange(JsonObject change) {
		this.change = change;
		return this;
	}
}
