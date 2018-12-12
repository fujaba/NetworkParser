package de.uniks.networkparser;

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
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Map.Entry;

import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.MapListener;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleIteratorSet;
import de.uniks.networkparser.list.SimpleList;

public class UpdateListener implements MapListener, ObjectCondition {
	/** The map. */
	private IdMap map;
	private Tokener factory;
	/** The suspend id list. */
	private SimpleList<UpdateCondition> suspendIdList;

	public static final String TYPE_OP_ADD = "add";
	public static final String TYPE_OP_REMOVE = "remove";
	public static final String TYPE_OP_REPLACE = "replace";
	public static final String TYPE_OP_COPY = "copy";
	public static final String TYPE_OP_MOVE = "move";
	public static final String TYPE_OP_TEST = "test";

	/** The update listener. */
	protected ObjectCondition condition;

	private Filter updateFilter = new Filter().withStrategy(SendableEntityCreator.UPDATE)
			.withConvertable(new UpdateCondition());
	private Object root;

	/**
	 * Instantiates a new update listener.
	 *
	 * @param map     the map
	 * @param factory Factory to create new Items
	 */
	public UpdateListener(IdMap map, Tokener factory) {
		this.map = map;
		this.factory = factory;
	}

	/**
	 * Suspend notification.
	 * 
	 * @param accumulates Notification Listener
	 *
	 * @return success for suspend Notification
	 */
	public boolean suspendNotification(UpdateCondition... accumulates) {
		this.suspendIdList = new SimpleList<UpdateCondition>();
		if (accumulates == null) {
			this.suspendIdList.add(UpdateCondition.createAcumulateCondition(this.factory));
		} else {
			for (UpdateCondition item : accumulates) {
				this.suspendIdList.add(item);
			}
		}
		return true;
	}

	public SimpleList<UpdateCondition> resetNotification() {
		SimpleList<UpdateCondition> list = this.suspendIdList;
		this.suspendIdList = null;
		return list;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt == null) {
			return;
		}
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();

		if ((oldValue == null && newValue == null) || (oldValue != null && oldValue.equals(newValue))) {
			// Nothing to do
			return;
		}
		// put changes into msg and send to receiver
		Object source;
		if (evt instanceof SimpleEvent) {
			source = ((SimpleEvent) evt).getModelValue();
		} else {
			source = evt.getSource();
		}
		String property = evt.getPropertyName();
		SendableEntityCreator creatorClass = this.map.getCreatorClass(source);
		if (creatorClass == null) {
			// this class is not supported, do nor replicate
			return;
		}

		if (this.suspendIdList != null) {
			boolean notifiy = true;
			for (UpdateCondition listener : this.suspendIdList) {
				if (listener.changeAttribute(this, source, creatorClass, property, oldValue, newValue)) {
					notifiy = false;
				}
			}
			if (notifiy == false) {
				return;
			}
		}

		Entity jsonObject = change(property, source, creatorClass, oldValue, newValue);

		// Add Message Value
		ObjectCondition listener = this.map.getUpdateListener();
		if (listener == null) {
			return;
		}
		if (oldValue != null && newValue != null) {
			listener.update(new SimpleEvent(SendableEntityCreator.UPDATE, jsonObject, evt, map));
		} else {
			listener.update(new SimpleEvent(SendableEntityCreator.NEW, jsonObject, evt, map));
		}
	}

	public Entity change(String property, Object source, SendableEntityCreator creatorClass, Object oldValue,
			Object newValue) {
		if (factory == null) {
			return null;
		}
		Entity jsonObject = factory.newInstance();
		String id = this.map.getId(source, true);
		Grammar grammar = this.map.getGrammar();
		grammar.writeBasicValue(jsonObject, source.getClass().getName(), id, SendableEntityCreator.UPDATE, map);
		change(property, creatorClass, jsonObject, oldValue, newValue);
		return jsonObject;
	}

	public boolean change(String property, SendableEntityCreator creator, Entity change, Object oldValue,
			Object newValue) {
		boolean done = false;
		if (creator == null) {
			return false;
		}
		String[] properties = creator.getProperties();
		if (properties == null) {
			return false;
		}
		for (String attrName : properties) {
			if (attrName.equals(property)) {
				done = true;
				break;
			}
		}
		if (!done) {
			// this property is not part of the replicated model, do not
			// replicate
			// if propertyname is not found and the name is REMOVE_YOU it remove it from the
			// IdMap
			if (SendableEntityCreator.REMOVE_YOU.equals(property)) {
				this.removeObj(oldValue, true);
			}
			return false;
		}

		SendableEntityCreator creatorClass;
		Object child = null;
		Entity entity;
		Grammar grammar = this.map.getGrammar();
		if (oldValue != null) {
			String key = property;
			creatorClass = this.map.getCreatorClass(oldValue);
			if(grammar.isFlatFormat()) {
				//NEW VERSION
				key = IdMap.ENTITYSPLITTER+SendableEntityCreator.REMOVE+IdMap.ENTITYSPLITTER+property;
				entity = change;
			} else {
				child = change.getValue(SendableEntityCreator.REMOVE);
				if (child instanceof Entity) {
					entity = (Entity) child;
				} else {
					entity = factory.newInstance();
					change.put(SendableEntityCreator.REMOVE, entity);
				}
			}
			
			if (creatorClass != null) {
				String oldId = this.map.getId(oldValue, true);
				if (oldId != null) {
					Entity childItem = factory.newInstance();
					childItem.put(IdMap.ID, oldId);
					entity.put(key, childItem);
				}
			} else {
				entity.put(key, oldValue);
			}
		}

		if (newValue != null) {
			String key = property;
			creatorClass = this.map.getCreatorClass(newValue);
			if(grammar.isFlatFormat()) {
				//NEW VERSION
				key = IdMap.ENTITYSPLITTER+SendableEntityCreator.UPDATE+IdMap.ENTITYSPLITTER+property;
				entity = change;
			} else {
				child = change.getValue(SendableEntityCreator.UPDATE);
				if (child instanceof Entity) {
					entity = (Entity) child;
				} else {
					entity = factory.newInstance();
					change.put(SendableEntityCreator.UPDATE, entity);
				}
			}

			if (creatorClass != null) {
				String keys = this.map.getKey(newValue);
				if (keys != null) {
					Entity item = factory.newInstance();
					item.put(IdMap.CLASS, newValue.getClass().getName());
					item.put(IdMap.ID, keys);
					entity.put(key, item);
				} else {
					Entity item = (Entity) this.map.encode(newValue, this.factory, this.updateFilter);
					entity.put(key, item);
				}
			} else {
				// plain attribute
				entity.put(key, newValue);
			}
		}
		return true;
	}

	/**
	 * Execute.
	 *
	 * @param updateMessage the update message
	 * @param filter        Filter for exclude UpdateMessages
	 * @return the MasterObject, if successful
	 */
	public Object execute(Entity updateMessage, Filter filter) {
		if (!updateMessage.has(SendableEntityCreator.UPDATE) && !updateMessage.has(SendableEntityCreator.REMOVE)) {
			return null;
		}
		if (this.map == null) {
			return null;
		}

		String id = updateMessage.getString(IdMap.ID);
		// Check for JSONPatch
		String op = updateMessage.getString("OP");
		String path = updateMessage.getString("PATH");
		if (op.length() > 0 && path.length() > 0) {
			return this.executePatch(op, path, updateMessage);
		}
		Entity remove = (Entity) updateMessage.getValue(SendableEntityCreator.REMOVE);
		Entity update = (Entity) updateMessage.getValue(SendableEntityCreator.UPDATE);

//		Object prio = updateMessage.getValue(Filter.PRIO);
		Object masterObj = this.map.getObject(id);
		if (masterObj == null) {
			String masterObjClassName = (String) updateMessage.getValue(IdMap.CLASS);
			if (masterObjClassName != null) {
				// cool, lets make it
				SendableEntityCreator creator = this.map.getCreator(masterObjClassName, true, null);
				masterObj = creator.getSendableInstance(false);
			}
			if (masterObj == null) {
				return null;
			}
			this.map.put(id, masterObj, false);
		}
		SendableEntityCreator creator = this.map.getCreatorClass(masterObj);
		if (remove == null && update != null) {
			// create Message
			Object refObject = creator.getSendableInstance(true);
			for (SimpleIteratorSet<String, Object> i = new SimpleIteratorSet<String, Object>(update); i.hasNext();) {
				Entry<String, Object> item = i.next();
				String key = item.getKey();
				Object value = creator.getValue(masterObj, key);
				if (value == null) {
					// Old Value is Standard
					return setValue(creator, masterObj, key, item.getValue(), SendableEntityCreator.NEW);
				} else if (value instanceof Collection<?>) {
					// just add the element
					// It is a to many Link
					return setValue(creator, masterObj, key, item.getValue(), SendableEntityCreator.NEW);
				} else if (value.equals(creator.getValue(refObject, key))) {
					// Old Value is Standard
					return setValue(creator, masterObj, key, update.getValue(key), SendableEntityCreator.NEW);
				}
			}
			return true;
		} else if (update == null && remove != null) {
			// delete Message
			Object refObject = creator.getSendableInstance(true);
			for (int i = 0; i < remove.size(); i++) {
				String key = remove.getKeyByIndex(i);
				Object value = creator.getValue(masterObj, key);
				if (value instanceof Collection<?>) {
					Entity removeJsonObject = (Entity) remove.getValue(key);
					setValue(creator, masterObj, key, removeJsonObject, SendableEntityCreator.REMOVE);
				} else {
					if (checkValue(value, key, remove)) {
						setValue(creator, masterObj, key, creator.getValue(refObject, key),
								SendableEntityCreator.REMOVE);
					}
				}
				Object removeJsonObject = remove.getValue(key);
				if (removeJsonObject != null && removeJsonObject instanceof Entity) {
					Entity json = (Entity) removeJsonObject;
					this.map.notify(
							new SimpleEvent(SendableEntityCreator.REMOVE, json, map, key, this.map.decode(json), null)
									.withModelValue(masterObj));
				}

			}
			return masterObj;
		} else if (update != null) {
			// update Message
			for (int i = 0; i < update.size(); i++) {
				String key = update.getKeyByIndex(i);
				// CHECK WITH REMOVE key
				Object oldValue = creator.getValue(masterObj, key);

				if (checkValue(oldValue, key, remove)) {
					Object newValue = update.getValue(key);
					setValue(creator, masterObj, key, newValue, SendableEntityCreator.UPDATE);

					this.map.notify(new SimpleEvent(SendableEntityCreator.UPDATE, update, map, key, oldValue, newValue)
							.withModelValue(masterObj));
				}
			}
			return masterObj;
		}
		return null;
	}

	private Entity getElement(String path, Entity element, Entity parent) {
		if(path == null) {
			return null;
		}
		int pos = path.indexOf("/");
		if (pos > 0) {
			Object child = element.getValue(path.substring(0, pos));
			if (child != null && child instanceof Entity) {
				return getElement(path.substring(pos + 1), (Entity) child, element);
			}
		} else {
			// Last One
			return parent;
		}
		return null;
	}

	public Object executePatch(String op, String path, Entity updateMessage) {
		if (root == null || path == null) {
			return null;
		}
		if (root instanceof Entity) {
			// Check for Element
			Entity element = getElement(path, (Entity) root, (Entity) root);
			if (element != null) {
				String key = path;
				int pos = path.lastIndexOf("/");
				if (pos > 0) {
					key = path.substring(pos + 1);
				}
				if (TYPE_OP_ADD.equalsIgnoreCase(op)) {
					element.put(key, updateMessage.getValue("value"));
					return element;
				}
//				public static final String  = "add";
//				public static final String TYPE_OP_REMOVE = "remove";
//				public static final String TYPE_OP_REPLACE = "replace";
//				public static final String TYPE_OP_COPY = "copy";
//				public static final String TYPE_OP_MOVE = "move";
//				public static final String TYPE_OP_TEST = "test";

			}

		}
//		Add
//		{ "op": "add", "path": "/biscuits/1", "value": { "name": "Ginger Nut" } }
//		Adds a value to an object or inserts it into an array. In the case of an array, the value is inserted before the given index. The - character can be used instead of an index to insert at the end of an array.
//
//		Remove
//		{ "op": "remove", "path": "/biscuits" }
//		Removes a value from an object or array.
//
//		{ "op": "remove", "path": "/biscuits/0" }
//		Removes the first element of the array at biscuits (or just removes the "0" key if biscuits is an object)
//
//		Replace
//		{ "op": "replace", "path": "/biscuits/0/name", "value": "Chocolate Digestive" }
//		Replaces a value. Equivalent to a "remove" followed by an "add".
//
//		Copy
//		{ "op": "copy", "from": "/biscuits/0", "path": "/best_biscuit" }
//		Copies a value from one location to another within the JSON document. Both from and path are JSON Pointers.
//
//		Move
//		{ "op": "move", "from": "/biscuits", "path": "/cookies" }
//		Moves a value from one location to the other. Both from and path are JSON Pointers.
//
//		Test
//		{ "op": "test", "path": "/best_biscuit/name", "value": "Choco Leibniz" }
//		Tests that the specified value is set in the document. If the test fails, then the patch as a whole should not apply.
		return null;
	}

	/**
	 * Check value.
	 *
	 * @param value         the value
	 * @param key           the key
	 * @param oldJsonObject the json obj
	 * @return true, if successful
	 */
	private boolean checkValue(Object value, String key, Entity oldJsonObject) {
		if (oldJsonObject == null) {
			return false;
		}
		Object oldValue = oldJsonObject.getValue(key);
		if (value != null) {
			if (oldValue instanceof Entity) {
				// GLAUB ICH MAL
				String oldId = (String) ((Entity) oldValue).getValue(IdMap.ID);
				return oldId.equals(this.map.getId(value, true));
			}
			return value.equals(oldValue);
		}
		return oldValue == null;
	}

	/**
	 * Sets the value.
	 *
	 * @param creator  the creator
	 * @param element  the element
	 * @param key      the key
	 * @param newValue the new value
	 * @param typ      type of set NEW, UPDATE, REMOVE
	 * @return true, if successful
	 */
	private Object setValue(SendableEntityCreator creator, Object element, String key, Object newValue, String typ) {
		if (newValue instanceof Entity) {
			Entity json = (Entity) newValue;
			Object value = this.map.decode(json);
			if (value != null) {
				creator.setValue(element, key, value, typ);
				if (this.map.notify(new SimpleEvent(typ, json, map, key, null, value).withModelValue(element))) {
					return element;
				}
			}
		} else if(creator != null) {
			creator.setValue(element, key, newValue, typ);
			if (this.map.notify(new SimpleEvent(typ, null, map, key, null, newValue).withModelValue(element))) {
				return element;
			}
		}
		return null;
	}

	public UpdateListener withFilter(Filter filter) {
		this.updateFilter = filter;
		return this;
	}

	@Override
	public Filter getFilter() {
		return this.updateFilter;
	}

	/**
	 * Remove the given object from the IdMap
	 * 
	 * @param oldValue Object to remove
	 * @param destroy  switch for remove link from object
	 * @return success
	 */
	public boolean removeObj(Object oldValue, boolean destroy) {
		if (this.map != null) {
			return this.map.removeObj(oldValue, destroy);
		}
		return false;
	}

	public UpdateListener withCondition(ObjectCondition condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public boolean update(Object value) {
		if (value instanceof SimpleEvent == false || condition == null) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();

		if ((oldValue == null && newValue == null) || (oldValue != null && oldValue.equals(newValue))) {
			// Nothing to do
			return false;
		}
		// put changes into msg and send to receiver
		Object source;
		if (evt instanceof SimpleEvent) {
			source = ((SimpleEvent) evt).getModelValue();
		} else {
			source = evt.getSource();
		}
		String property = evt.getPropertyName();
		SendableEntityCreator creatorClass = this.map.getCreatorClass(source);
		if (creatorClass == null) {
			// this class is not supported, do nor replicate
			return false;
		}
		condition.update(change(property, source, creatorClass, oldValue, newValue));
		return true;
	}

	@Override
	public Tokener getTokener() {
		return factory;
	}
}
