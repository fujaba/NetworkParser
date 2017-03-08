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
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.UpdateCondition;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.MapListener;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleIteratorSet;
/**
 * The listener interface for receiving update events. The class that is
 * interested in processing a update event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addUpdateListener</code> method. When the update event
 * occurs, that object's appropriate method is invoked.
 *
 */

public class UpdateJson implements MapListener {
	/** The map. */
	private IdMap map;

	/** The suspend id list. */
	private ArrayList<String> suspendIdList;

	private Filter updateFilter = new Filter().withStrategy(IdMap.UPDATE).withConvertable(new UpdateCondition());
	
	/**
	 * Instantiates a new update listener.
	 *
	 * @param map
	 *			the map
	 */
	public UpdateJson(IdMap map) {
		this.map = map;
	}

	/**
	 * Suspend notification.
	 * 
	 * @return success for suspend Notification
	 */
	public boolean suspendNotification() {
		this.suspendIdList = new ArrayList<String>();
		return true;
	}

	/**
	 * Reset notification.
	 * 
	 * @return success for reset Notification
	 */
	public boolean resetNotification() {
		JsonArray array = this.map.getJsonByIds(this.suspendIdList);
		if(array.size() > 0) {
			JsonObject message = new JsonObject();
			message.put(IdMap.UPDATE, array);
			this.map.notify(new SimpleEvent(IdMap.NEW, message, map, null, null, null));
		}

		this.suspendIdList = null;
		return true;
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
			// if propertyname is not found and the name is REMOVE_YOU it remove it from the IdMap
			if(IdMap.REMOVE_YOU.equals(propertyName)) {
				this.removeObj(evt.getOldValue(), true);
			}
			return;
		}

		JsonObject jsonObject = new JsonObject()
				.withValue(IdMap.CLASS, source.getClass().getName())
				.withValue(IdMap.ID, this.map.getId(source));

		if (oldValue != null) {
			creatorClass = this.map.getCreatorClass(oldValue);

			JsonObject child = new JsonObject();
			if (creatorClass != null) {
				String oldId = this.map.getId(oldValue);
				if (oldId != null) {
					child.put(propertyName,
							new JsonObject().withValue(IdMap.ID, oldId));
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
							.withValue(IdMap.CLASS, newValue.getClass().getName())
							.withValue(IdMap.ID, key);
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
//FIXME		if (this.map.getCounter().getPrio() != null) {
//			jsonObject.put(Filter.PRIO, this.map.getCounter().getPrio());
//		}
		if (this.suspendIdList == null) {
			this.map.notify(new SimpleEvent(IdMap.NEW, jsonObject, evt,  map));
		}
	}

	/**
	 * Execute.
	 *
	 * @param updateMessage		the update message
	 * @param filter 			Filter for exclude UpdateMessages
	 * @return 					the MasterObject, if successful
	 */
	public Object execute(Entity updateMessage, Filter filter) {
		if(!updateMessage.has(IdMap.UPDATE) && !updateMessage.has(IdMap.REMOVE)) {
			return null;
		}

		String id = updateMessage.getString(IdMap.ID);
		JsonObject remove = (JsonObject) updateMessage.getValue(IdMap.REMOVE);
		JsonObject update = (JsonObject) updateMessage.getValue(IdMap.UPDATE);
		Object prio = updateMessage.getValue(Filter.PRIO);
		Object masterObj = this.map.getObject(id);
		if (masterObj == null)
		{
		   String masterObjClassName = (String) updateMessage.getValue(IdMap.CLASS);

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
			for(SimpleIteratorSet<String, Object> i = new SimpleIteratorSet<String, Object>(update);i.hasNext();) {
				Entry<String, Object> item = i.next();
				String key = item.getKey();
				Object value = creator.getValue(masterObj, key);
				if (value == null) {
					// Old Value is Standard
					return setValue(creator, masterObj, key, item.getValue(), IdMap.NEW);
				} else if (value.equals(creator.getValue(refObject, key))) {
					// Old Value is Standard
					return setValue(creator, masterObj, key,
							update.get(key), IdMap.NEW);
				} else {
					// ERROR
					if (checkPrio(prio)) {
						return setValue(creator, masterObj, key,
								update.get(key), Filter.COLLISION);
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
					this.map.notify(new SimpleEvent(IdMap.REMOVE, json, map, key, this.map.decode(json), null).withModelValue(masterObj));
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

					this.map.notify(new SimpleEvent(IdMap.UPDATE, update, map, key, oldValue, newValue).withModelValue(masterObj));
				} else if (checkPrio(prio)) {
					Object newValue = update.get(key);
					setValue(creator, masterObj, key, newValue,
							IdMap.UPDATE);
					this.map.notify(new SimpleEvent(IdMap.UPDATE, update, map, key, oldValue, newValue).withModelValue(masterObj));
				}
			}
			return masterObj;
		}
		return null;
	}

	/**
	 * Check value.
	 *
	 * @param value				the value
	 * @param key				the key
	 * @param oldJsonObject		the json obj
	 * @return 					true, if successful
	 */
	private boolean checkValue(Object value, String key,
			JsonObject oldJsonObject) {
		if (value != null) {
			Object oldValue = oldJsonObject.get(key);
			if (oldValue instanceof JsonObject) {
				// GLAUB ICH MAL
				String oldId = (String) ((JsonObject) oldValue)
						.get(IdMap.ID);
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
	 * @param prio	the prio
	 * @return 		true, if successful
	 */
	private boolean checkPrio(Object prio) {
//		Object myPrio = this.map.getCounter().getPrio();
		Object myPrio = null;
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
	 * @param creator	the creator
	 * @param element	the element
	 * @param key		the key
	 * @param newValue	the new value
	 * @param typ		type of set NEW, UPDATE, REMOVE
	 * @return 			true, if successful
	 */
	private Object setValue(SendableEntityCreator creator, Object element,
			String key, Object newValue, String typ) {
		if (newValue instanceof JsonObject) {
			JsonObject json = (JsonObject) newValue;
			Object value = this.map.decode(json);
			if (value != null) {
				creator.setValue(element, key, value, typ);
				if(this.map.notify(new SimpleEvent(typ, json, map, key, null, value).withModelValue(element))){
					return element;
				}
			}
		} else {
			creator.setValue(element, key, newValue, typ);
			if(this.map.notify(new SimpleEvent(typ, null, map, key, null, newValue).withModelValue(element))){
				return element;
			}
		}
		return null;
	}

	public UpdateJson withFilter(Filter filter) {
		this.updateFilter = filter;
		return this;
	}

	@Override
	public Filter getFilter() {
		return this.updateFilter;
	}
	
	/**
	 * Remove the given object from the IdMap
	 * @param oldValue Object to remove
	 * @param destroy switch for remove link from object
	 * @return success
	 */
	public boolean removeObj(Object oldValue, boolean destroy) {
		if(this.map != null) {
			return this.map.removeObj(oldValue, destroy);
		}
		return false;
	}
}
