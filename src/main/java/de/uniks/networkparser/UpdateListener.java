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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.MapListener;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleIteratorSet;
import de.uniks.networkparser.list.SimpleList;
/**
 * The listener interface for receiving update events. The class that is
 * interested in processing a update event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addUpdateListener</code> method. When the update event
 * occurs, that object's appropriate method is invoked.
 *
 */
public class UpdateListener implements MapListener {
	/** The map. */
	private IdMap map;
	private Entity factory;
	/** The suspend id list. */
	private SimpleList<UpdateAccumulate> suspendIdList;

	private Filter updateFilter = new Filter().withStrategy(SendableEntityCreator.UPDATE).withConvertable(new UpdateCondition());
	
	/**
	 * Instantiates a new update listener.
	 *
	 * @param map	the map
	 */
	public UpdateListener(IdMap map, Entity factory) {
		this.map = map;
		this.factory = factory;
	}

	/**
	 * Suspend notification.
	 * 
	 * @return success for suspend Notification
	 */
	public boolean suspendNotification() {
		this.suspendIdList = new SimpleList<UpdateAccumulate>();
		return true;
	}

	/**
	 * Reset notification.
	 * 
	 * @return success for reset Notification
	 */
	public boolean resumeNotification() {
		JsonArray array = this.map.getJsonByIds(this.suspendIdList);
		if(array.size() > 0) {
			JsonObject message = new JsonObject();
			message.put(SendableEntityCreator.UPDATE, array);
			this.map.notify(new SimpleEvent(SendableEntityCreator.NEW, message, map, null, null, null));
		}

		this.suspendIdList = null;
		return true;
	}

	public boolean resetNotification() {
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
		Object source;
		if(evt instanceof SimpleEvent) {
			source = ((SimpleEvent)evt).getModelValue();
		} else {
			source = evt.getSource();
		}
//		evt.get
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
			if(SendableEntityCreator.REMOVE_YOU.equals(propertyName)) {
				this.removeObj(evt.getOldValue(), true);
			}
			return;
		}

		Entity jsonObject = (Entity) factory.getNewList(true);

		String id = this.map.getId(source, true);
		Grammar grammar = this.map.getGrammar();
		grammar.writeBasicValue(jsonObject, source.getClass().getName(), id, map);
		if (oldValue != null) {
			creatorClass = this.map.getCreatorClass(oldValue);

			Entity child = (Entity) factory.getNewList(true);
			if (creatorClass != null) {
				String oldId = this.map.getId(oldValue, true);
				if (oldId != null) {
					Entity childItem = (Entity) factory.getNewList(true);
					childItem.put(IdMap.ID, oldId);
					child.put(propertyName, childItem);
				}
			} else {
				child.put(propertyName, oldValue);
			}
			jsonObject.put(SendableEntityCreator.REMOVE, child);
		}

		if (newValue != null) {
			creatorClass = this.map.getCreatorClass(newValue);

			Entity child = (Entity) factory.getNewList(true);
			if (creatorClass != null) {
				String key = this.map.getKey(newValue);
				if (key != null) {
					Entity item = (Entity) factory.getNewList(true);
					item.put(IdMap.CLASS, newValue.getClass().getName());
					item.put(IdMap.ID, key);
					child.put(propertyName, item);
				} else {
					Entity item = this.map.toJsonObject(newValue, this.updateFilter);
					child.put(propertyName, item);
					if (this.suspendIdList != null) {
						this.suspendIdList.add(this.map.getId(newValue, true));
					}
				}
			} else {
				// plain attribute
				child.put(propertyName, newValue);
			}
			jsonObject.put(SendableEntityCreator.UPDATE, child);
		}
		if (this.suspendIdList == null) {
			// Add Message Value
			ObjectCondition listener = this.map.getUpdateListener();
			if(listener == null) {
				return;
			}
			if (oldValue != null && newValue != null) {
				listener.update(new SimpleEvent(SendableEntityCreator.UPDATE, jsonObject, evt,  map));
			} else {
				listener.update(new SimpleEvent(SendableEntityCreator.NEW, jsonObject, evt,  map));	
			}
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
		if(!updateMessage.has(SendableEntityCreator.UPDATE) && !updateMessage.has(SendableEntityCreator.REMOVE)) {
			return null;
		}
		if(this.map == null) {
			return null;
		}

		String id = updateMessage.getString(IdMap.ID);
		Entity remove = (Entity) updateMessage.getValue(SendableEntityCreator.REMOVE);
		Entity update = (Entity) updateMessage.getValue(SendableEntityCreator.UPDATE);
//		Object prio = updateMessage.getValue(Filter.PRIO);
		Object masterObj = this.map.getObject(id);
		if (masterObj == null)
		{
		   String masterObjClassName = (String) updateMessage.getValue(IdMap.CLASS);

		   if (masterObjClassName != null)
		   {
			  // cool, lets make it
			  SendableEntityCreator creator = this.map.getCreator(masterObjClassName, true, null);
			  masterObj = creator.getSendableInstance(false);
			  if (masterObj != null)
			  {
				 this.map.put(id, masterObj, false);
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
					return setValue(creator, masterObj, key, item.getValue(), SendableEntityCreator.NEW);
				} else if (value.equals(creator.getValue(refObject, key))) {
					// Old Value is Standard
					return setValue(creator, masterObj, key,
							update.getValue(key), SendableEntityCreator.NEW);
				}
			}
			return true;
		} else if (update == null && remove != null) {
			// delete Message
			Object refObject = creator.getSendableInstance(true);
			for(int i=0;i<remove.size();i++) {
				String key = remove.getKeyByIndex(i);
				Object value = creator.getValue(masterObj, key);
				if (value instanceof Collection<?>) {
					Entity removeJsonObject = (Entity) remove.getValue(key);
					setValue(creator, masterObj, key, removeJsonObject,
							SendableEntityCreator.REMOVE);
				} else {
					if (checkValue(value, key, remove)) {
						setValue(creator, masterObj, key,
								creator.getValue(refObject, key),
								SendableEntityCreator.REMOVE);
					}
				}
				Object removeJsonObject = remove.getValue(key);
				if (removeJsonObject != null
						&& removeJsonObject instanceof Entity) {
					Entity json = (Entity) removeJsonObject;
					this.map.notify(new SimpleEvent(SendableEntityCreator.REMOVE, json, map, key, this.map.decode(json), null).withModelValue(masterObj));
				}
				
			}
			return masterObj;
		} else if (update != null) {
			// update Message
			for(int i=0;i<update.size();i++) {
				String key = remove.getKeyByIndex(i);
				// CHECK WITH REMOVE key
				Object oldValue = creator.getValue(masterObj, key);

				if (checkValue(oldValue, key, remove)) {
					Object newValue = update.getValue(key);
					setValue(creator, masterObj, key, newValue, SendableEntityCreator.UPDATE);

					this.map.notify(new SimpleEvent(SendableEntityCreator.UPDATE, update, map, key, oldValue, newValue).withModelValue(masterObj));
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
			Entity oldJsonObject) {
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
	 * @param creator	the creator
	 * @param element	the element
	 * @param key		the key
	 * @param newValue	the new value
	 * @param typ		type of set NEW, UPDATE, REMOVE
	 * @return 			true, if successful
	 */
	private Object setValue(SendableEntityCreator creator, Object element,
			String key, Object newValue, String typ) {
		if (newValue instanceof Entity) {
			Entity json = (Entity) newValue;
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
