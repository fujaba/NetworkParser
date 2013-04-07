package de.uniks.jism.json;

/*
 Copyright (c) 2012, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE 'Json Id Serialisierung Map' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import de.uniks.jism.IdMap;
import de.uniks.jism.interfaces.SendableEntityCreator;

/**
 * The listener interface for receiving update events. The class that is
 * interested in processing a update event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addUpdateListener<code> method. When
 * the update event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see UpdateEvent
 */
public class UpdateListener implements PropertyChangeListener {
	/** The map. */
	private JsonIdMap map;

	/** The suspend id list. */
	private ArrayList<String> suspendIdList;

	/** The garbage collection. */
	private HashMap<String, Integer> garbageCollection = null;

	/** The class counts. */
	private HashSet<String> classCounts;

	private UpdateFilter updateFilter = new UpdateFilter();

	/**
	 * Instantiates a new update listener.
	 * 
	 * @param map
	 *            the map
	 */
	public UpdateListener(IdMap map) {
		if (map instanceof JsonIdMap) {
			this.map = (JsonIdMap) map;
		}
	}

	/**
	 * Start carbage colection.
	 * 
	 * @param root
	 *            the root
	 * @return the json object
	 */
	public JsonObject startGarbageColection(Object root) {
		this.garbageCollection = new HashMap<String, Integer>();
		this.classCounts = new HashSet<String>();
		JsonObject initField = this.map.toJsonObject(root);
		countMessage(initField);
		return initField;
	}

	/**
	 * Garbage collection.
	 * 
	 * @param root
	 *            the root
	 * @return the json object
	 */
	public JsonObject garbageCollection(Object root) {
		boolean isStarted = this.garbageCollection != null;
		this.garbageCollection = new HashMap<String, Integer>();
		this.classCounts = new HashSet<String>();
		JsonObject initField = this.map.toJsonObject(root);
		countMessage(initField);
		// Remove all others
		this.map.garbageCollection(this.classCounts);

		if (!isStarted) {
			this.garbageCollection = null;
			this.classCounts = null;
		}
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
		// put changes into msg and send to receiver
		Object source = evt.getSource();
		String propertyName = evt.getPropertyName();
		SendableEntityCreator creatorClass = this.map.getCreatorClass(source);

		if (creatorClass == null) {
			// this class is not supported, do nor replicate
			return;
		}
		boolean done = false;
		String gc = null;
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

		JsonObject jsonObject = new JsonObject(JsonIdMap.ID,
				this.map.getId(source));

		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();

		if ((oldValue == null && newValue == null)
				|| (oldValue != null && oldValue.equals(newValue))) {
			// Nothing to do
			return;
		}

		if (oldValue != null) {
			creatorClass = this.map.getCreatorClass(oldValue);

			JsonObject child = new JsonObject();
			if (creatorClass != null) {

				String oldId = this.map.getId(oldValue);
				if (oldId != null) {
					gc = oldId;
					child.put(propertyName, new JsonObject(JsonIdMap.ID, oldId));
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
					JsonObject item = new JsonObject(JsonIdMap.ID, key);
					countMessage(item);
					child.put(propertyName, item);
				} else {
					JsonObject item = this.map.toJsonObject(newValue,
							this.updateFilter);
					countMessage(item);
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

		if (gc != null && this.garbageCollection != null) {
			if (this.garbageCollection.containsKey(gc)) {
				int newAssocValue = this.garbageCollection.get(gc) - 1;
				if (newAssocValue > 0) {
					this.garbageCollection.put(gc, newAssocValue);
				} else {
					// GC
					this.garbageCollection.remove(gc);
					this.classCounts.remove(gc);
					Object assoc = this.map.getObject(gc);
					if (assoc != null) {
						this.map.remove(assoc);
					}
				}
			}
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
	 * @return true, if successful
	 */
	public boolean execute(JsonObject updateMessage) {
		if (updateMessage.has(JsonIdMap.JSON_PROPS)) {
			// its a new Object
			this.map.readJson(updateMessage);
			return true;
		}

		String id = updateMessage.getString(JsonIdMap.ID);
		JsonObject remove = (JsonObject) updateMessage.get(JsonIdMap.REMOVE);
		JsonObject update = (JsonObject) updateMessage.get(JsonIdMap.UPDATE);
		Object prio = updateMessage.get(JsonIdMap.PRIO);
		Object masterObj = this.map.getObject(id);
		if (masterObj != null) {
			SendableEntityCreator creator = this.map.getCreatorClass(masterObj);
			if (remove == null) {
				// create Message
				Object refObject = creator.getSendableInstance(true);
				Iterator<String> keys = update.keys();
				while (keys.hasNext()) {
					String key = keys.next();
					Object value = creator.getValue(masterObj, key);
					if (value == null) {
						if (creator.getValue(refObject, key) == null) {
							// Old Value is Standard
							return setValue(creator, masterObj, key,
									update.get(key), IdMap.NEW);
						}
						// ERROR
						if (!this.map.skipCollision(masterObj, key, value,
								remove, update)) {
							if (checkPrio(prio)) {
								return setValue(creator, masterObj, key,
										update.get(key), IdMap.NEW);
							}
						}
					} else if (creator.getValue(masterObj, key).equals(
							creator.getValue(refObject, key))) {
						// Old Value is standard
						return setValue(creator, masterObj, key,
								update.get(key), IdMap.NEW);
					} else {
						// ERROR
						if (!this.map.skipCollision(masterObj, key, value,
								remove, update)) {
							if (checkPrio(prio)) {
								return setValue(creator, masterObj, key,
										update.get(key), IdMap.NEW);
							}
						}
					}
				}
				return true;
			} else if (update == null) {
				// delete Message
				Object refObject = creator.getSendableInstance(true);
				Iterator<String> keys = remove.keys();
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
						this.map.readMessages(key, masterObj, this.map.readJson(json), json, IdMap.REMOVE);
					}
				}
				return true;
			} else {
				// update Message
				Iterator<String> keys = update.keys();
				while (keys.hasNext()) {
					String key = keys.next();
					// CHECK WITH REMOVE key
					Object oldValue = creator.getValue(masterObj, key);

					if (checkValue(oldValue, key, remove)) {
						Object newValue = update.get(key);
						setValue(creator, masterObj, key, newValue, IdMap.UPDATE);
						this.map.readMessages(key, masterObj, newValue, update, IdMap.UPDATE);
					} else if (checkPrio(prio)) {
						Object newValue = update.get(key);
						setValue(creator, masterObj, key,newValue , IdMap.UPDATE);
						this.map.readMessages(key, masterObj, newValue, update, IdMap.UPDATE);
					}
				}
				return true;

			}
		}
		return false;
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
	private boolean setValue(SendableEntityCreator creator, Object element,
			String key, Object newValue, String typ) {
		if (newValue instanceof JsonObject) {
			JsonObject json = (JsonObject) newValue;
			SendableEntityCreator typeInfo = this.map.getCreatorClasses(json
					.getString(JsonIdMap.CLASS, ""));
			if (typeInfo != null) {
				// notify in readJson
			} else {
				if(!this.map.isReadMessages(key, element, json, typ)){
					return false;
				}
			}
			Object value = this.map.readJson(json);
			if (value != null) {
				creator.setValue(element, key, value, typ);
				this.map.readMessages(key, element, value, json, typ);
			}
		} else {
			creator.setValue(element, key, newValue, typ);
			this.map.readMessages(key, element, newValue, null, typ);
		}
		return true;
	}

	/**
	 * Count message.
	 * 
	 * @param message
	 *            the message
	 */
	private void countMessage(JsonObject message) {
		if (this.garbageCollection != null) {
			if (message.has(JsonIdMap.ID)) {
				String id = (String) message.get(JsonIdMap.ID);
				if (this.garbageCollection.containsKey(id)) {
					this.garbageCollection.put(id,
							this.garbageCollection.get(id) + 1);
				} else {
					this.garbageCollection.put(id, 1);
				}
				if (message.has(JsonIdMap.CLASS)) {
					if (this.classCounts.contains(id)) {
						return;
					}
					this.classCounts.add(id);
					// Its a new Object
					JsonObject props = (JsonObject) message
							.get(JsonIdMap.JSON_PROPS);
					Iterator<String> keys = props.keys();
					while (keys.hasNext()) {
						String key = keys.next();
						Object value = props.get(key);
						if (value instanceof JsonObject) {
							countMessage((JsonObject) value);
						} else if (value instanceof JsonArray) {
							countMessage((JsonArray) value);
						}
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
	private void countMessage(JsonArray message) {
		for(Iterator<Object> i = message.iterator();i.hasNext();){
			Object obj=i.next();
			if (obj instanceof JsonObject) {
				countMessage((JsonObject) obj);
			}
		}
	}
}
