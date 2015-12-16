package de.uniks.networkparser.json;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
/**
 * The listener interface for receiving update events. The class that is
 * interested in processing a update event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addUpdateListener</code> method. When the update event
 * occurs, that object's appropriate method is invoked.
 *
 */

public class UpdateListenerJson implements PropertyChangeListener {
	/** The map. */
	private JsonIdMap map;

	/** The suspend id list. */
	private ArrayList<String> suspendIdList;

	private Filter updateFilter = new UpdateFilterJson();

	/**
	 * Instantiates a new update listener.
	 *
	 * @param map
	 *            the map
	 */
	public UpdateListenerJson(IdMap map) {
		if (map instanceof JsonIdMap) {
			this.map = (JsonIdMap) map;
		}
	}

	/**
	 * Garbage collection.
	 *
	 * @param root
	 *            the root
	 * @return the json object
	 */
	public JsonObject garbageCollection(Object root) {
		if(root == null) {
			return null;
		}
		JsonObject initField = this.map.toJsonObject(root);
		ArrayList<String> classCounts = new ArrayList<String>();
		SimpleKeyValueList<String, Object> gc = new SimpleKeyValueList<String, Object>(); 
		countMessage(initField, classCounts, gc);
		// Remove all others
		this.map.garbageCollection(classCounts);
		return initField;
	}

	/**
	 * Suspend notification.
	 */
	public void suspendNotification() {
		this.suspendIdList = new ArrayList<String>();
	}

	/**
	 * Reset notification.
	 */
	public void resetNotification() {
		this.map.toJsonArrayByIds(this.suspendIdList);
		this.suspendIdList = null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();

		if ((oldValue == null && newValue == null)
				|| (oldValue != null && oldValue.equals(newValue))) {
			// Nothing to do
			return;
		}

		// put changes into msg and send to receiver
		Object source = evt.getSource();
		String propertyName = evt.getPropertyName();
		SendableEntityCreator creatorClass = this.map.getCreatorClass(source);

		if (creatorClass == null) {
			// this class is not supported, do nor replicate
			return;
		}
		boolean done = false;
		for (String attrName : creatorClass.getProperties()) {
			if (attrName.equals(propertyName)) {
				done = true;
				break;
			}
		}
		if (!done) {
			// this property is not part of the replicated model, do not
			// replicate
			return;
		}

		JsonObject jsonObject = new JsonObject()
		.withValue(JsonIdMap.ID, this.map.getId(source))
		.withValue(JsonIdMap.CLASS, source.getClass().getName());

		if (oldValue != null) {
			creatorClass = this.map.getCreatorClass(oldValue);

			JsonObject child = new JsonObject();
			if (creatorClass != null) {
				String oldId = this.map.getId(oldValue);
				if (oldId != null) {
					child.put(propertyName,
							new JsonObject().withValue(JsonIdMap.ID, oldId));
				}
			} else {
				child.put(propertyName, oldValue);
			}
			jsonObject.put(IdMap.REMOVE, child);
		}

		if (newValue != null) {
			creatorClass = this.map.getCreatorClass(newValue);

			JsonObject child = new JsonObject();
			if (creatorClass != null) {
				String key = this.map.getKey(newValue);
				if (key != null) {
					JsonObject item = new JsonObject()
							.withValue(JsonIdMap.ID, key)
							.withValue(JsonIdMap.CLASS, newValue.getClass().getName());
					child.put(propertyName, item);
				} else {
					JsonObject item = this.map.toJsonObject(newValue,
							this.updateFilter);
					child.put(propertyName, item);
					if (this.suspendIdList != null) {
						this.suspendIdList.add(this.map.getId(newValue));
					}
				}
			} else {
				// plain attribute
				child.put(propertyName, newValue);
			}
			jsonObject.put(IdMap.UPDATE, child);
		}
		if (this.map.getCounter().getPrio() != null) {
			jsonObject.put(IdMap.PRIO, this.map.getCounter().getPrio());
		}
		if (this.suspendIdList == null) {
			this.map.sendUpdateMsg(evt, jsonObject);
		}
	}
	
	/**
	 * Execute.
	 *
	 * @param updateMessage
	 *            the update message
	 * @return the MasterObject, if successful
	 */
	public Object execute(JsonObject updateMessage) {
		return execute(updateMessage, new Filter());
	}

	/**
	 * Execute.
	 *
	 * @param updateMessage
	 *            the update message
	 * @param filter Filter for exclude UpdateMessages
	 * @return the MasterObject, if successful
	 */
	public Object execute(JsonObject updateMessage, Filter filter) {
		if(!updateMessage.has(IdMap.UPDATE) && !updateMessage.has(IdMap.REMOVE)) {
			return null;
		}

		String id = updateMessage.getString(JsonIdMap.ID);
		JsonObject remove = (JsonObject) updateMessage.get(JsonIdMap.REMOVE);
		JsonObject update = (JsonObject) updateMessage.get(JsonIdMap.UPDATE);
		Object prio = updateMessage.get(JsonIdMap.PRIO);
		Object masterObj = this.map.getObject(id);
		if (masterObj == null)
		{
		   String masterObjClassName = (String) updateMessage.get(JsonIdMap.CLASS);
		   
		   if (masterObjClassName != null)
		   {
		      // cool, lets make it
		      SendableEntityCreator creator = this.map.getCreator(masterObjClassName, true);
		      masterObj = creator.getSendableInstance(false);
		      if (masterObj != null)
		      {
		         this.map.put(id, masterObj);
		      }
		   }
		}
		if (masterObj == null) {
			return null;
		}
		SendableEntityCreator creator = this.map.getCreatorClass(masterObj);
		if (remove == null && update != null) {
			// create Message
			Object refObject = creator.getSendableInstance(true);
			Iterator<String> keys = update.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				Object value = creator.getValue(masterObj, key);
				if (value == null) {
					// Old Value is Standard
					return setValue(creator, masterObj, key, update.get(key), IdMap.NEW);
				} else if (value.equals(creator.getValue(refObject, key))) {
					// Old Value is Standard
					return setValue(creator, masterObj, key,
							update.get(key), IdMap.NEW);
				} else {
					// ERROR
					if (checkPrio(prio)) {
						return setValue(creator, masterObj, key,
								update.get(key), IdMap.COLLISION);
					}
				}
			}
			return true;
		} else if (update == null && remove != null) {
			// delete Message
			Object refObject = creator.getSendableInstance(true);
			Iterator<String> keys = remove.keyIterator();
			while (keys.hasNext()) {
				String key = keys.next();
				Object value = creator.getValue(masterObj, key);
				if (value instanceof Collection<?>) {
					JsonObject removeJsonObject = remove.getJsonObject(key);
					setValue(creator, masterObj, key, removeJsonObject,
							IdMap.REMOVE);
				} else {
					if (checkValue(value, key, remove)) {
						setValue(creator, masterObj, key,
								creator.getValue(refObject, key),
								IdMap.REMOVE);
					} else if (checkPrio(prio)) {
						// RESET TO DEFAULTVALUE
						setValue(creator, masterObj, key,
								creator.getValue(refObject, key),
								IdMap.REMOVE);
					}
				}
				Object removeJsonObject = remove.get(key);
				if (removeJsonObject != null
						&& removeJsonObject instanceof JsonObject) {
					JsonObject json = (JsonObject) removeJsonObject;
					this.map.readMessages(key, masterObj,
							this.map.decode(json), json,
							IdMap.REMOVE);
				}
			}
			return masterObj;
		} else if (update != null) {
			// update Message
			Iterator<String> keys = update.keyIterator();
			while (keys.hasNext()) {
				String key = keys.next();
				// CHECK WITH REMOVE key
				Object oldValue = creator.getValue(masterObj, key);

				if (checkValue(oldValue, key, remove)) {
					Object newValue = update.get(key);
					setValue(creator, masterObj, key, newValue,
							IdMap.UPDATE);
					this.map.readMessages(key, masterObj, newValue, update,
							IdMap.UPDATE);
				} else if (checkPrio(prio)) {
					Object newValue = update.get(key);
					setValue(creator, masterObj, key, newValue,
							IdMap.UPDATE);
					this.map.readMessages(key, masterObj, newValue, update,
							IdMap.UPDATE);
				}
			}
			return masterObj;
		}
		return null;
	}

	/**
	 * Check value.
	 *
	 * @param value
	 *            the value
	 * @param key
	 *            the key
	 * @param oldJsonObject
	 *            the json obj
	 * @return true, if successful
	 */
	private boolean checkValue(Object value, String key,
			JsonObject oldJsonObject) {
		if (value != null) {
			Object oldValue = oldJsonObject.get(key);
			if (oldValue instanceof JsonObject) {
				// GLAUB ICH MAL
				String oldId = (String) ((JsonObject) oldValue)
						.get(JsonIdMap.ID);
				return oldId.equals(this.map.getId(value));
			} else if (oldValue.equals(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check prio.
	 *
	 * @param prio
	 *            the prio
	 * @return true, if successful
	 */
	private boolean checkPrio(Object prio) {
		Object myPrio = this.map.getCounter().getPrio();
		if (prio != null && myPrio != null) {
			if (prio instanceof Integer && myPrio instanceof Integer) {
				Integer ref = (Integer) myPrio;
				return ref.compareTo((Integer) prio) > 0;
			} else if (prio instanceof String && myPrio instanceof String) {
				String ref = (String) myPrio;
				return ref.compareTo((String) prio) > 0;
			}
		} else if (myPrio == null) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the value.
	 *
	 * @param creator
	 *            the creator
	 * @param element
	 *            the element
	 * @param key
	 *            the key
	 * @param newValue
	 *            the new value
	 * @return true, if successful
	 */
	private Object setValue(SendableEntityCreator creator, Object element,
			String key, Object newValue, String typ) {
		if (newValue instanceof JsonObject) {
			JsonObject json = (JsonObject) newValue;
			Object value = this.map.decode(json);
			if (value != null) {
				creator.setValue(element, key, value, typ);
				if(this.map.readMessages(key, element, value, json, typ)) {
					return element;
				}
			}
		} else {
			creator.setValue(element, key, newValue, typ);
			if(this.map.readMessages(key, element, newValue, null, typ)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Count message.
	 *
	 * @param message
	 *            the message
	 */
	private void countMessage(JsonObject message, ArrayList<String> classCounts, SimpleKeyValueList<String, Object> gc) {
		if (message.has(JsonIdMap.ID)) {
			String id = (String) message.get(JsonIdMap.ID);
			if (gc.containsKey(id)) {
				gc.put(id, (Integer) gc.getValueItem(id) + 1);
			} else {
				gc.put(id, 1);
			}
			if (message.has(JsonIdMap.CLASS)) {
				if (classCounts.contains(id)) {
					return;
				}
				classCounts.add(id);
				// Its a new Object
				JsonObject props = (JsonObject) message
						.get(JsonIdMap.JSON_PROPS);
				for (int i = 0; i < props.size(); i++) {
					if (props.getValueByIndex(i) instanceof JsonObject) {
						countMessage((JsonObject) props.getValueByIndex(i), classCounts, gc);
					} else if (props.getValueByIndex(i) instanceof JsonArray) {
						countMessage((JsonArray) props.getValueByIndex(i), classCounts, gc);
					}
				}
			}
		}
	}

	/**
	 * Count message.
	 *
	 * @param message
	 *            the message
	 */
	private void countMessage(JsonArray message, ArrayList<String> classCounts, SimpleKeyValueList<String, Object> gc) {
		for (Iterator<Object> i = message.iterator(); i.hasNext();) {
			Object obj = i.next();
			if (obj instanceof JsonObject) {
				countMessage((JsonObject) obj, classCounts, gc);
			}
		}
	}
}
