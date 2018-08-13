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
	private SimpleList<UpdateAccumulate> suspendIdList;

	/** The update listener. */
	protected ObjectCondition condition;

	private Filter updateFilter = new Filter().withStrategy(SendableEntityCreator.UPDATE).withConvertable(new UpdateCondition());

	/**
	 * Instantiates a new update listener.
	 *
	 * @param map	the map
	 * @param factory Factory to create new Items
	 */
	public UpdateListener(IdMap map, Tokener factory) {
		this.map = map;
		this.factory = factory;
	}

	/**
	 * Suspend notification.
	 * @param accumulates Notification Listener
	 *
	 * @return success for suspend Notification
	 */
	public boolean suspendNotification(UpdateAccumulate... accumulates) {
		this.suspendIdList = new SimpleList<UpdateAccumulate>();
		if(accumulates == null) {
			this.suspendIdList.add(new UpdateAccumulate(this.map).withTokener(this.factory));
		}else {
			for(UpdateAccumulate item : accumulates) {
				this.suspendIdList.add(item);
			}
		}
		return true;
	}

	public SimpleList<UpdateAccumulate> resetNotification() {
		SimpleList<UpdateAccumulate> list = this.suspendIdList;
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
		if(evt == null) {
			return;
		}
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
		String property = evt.getPropertyName();
		SendableEntityCreator creatorClass = this.map.getCreatorClass(source);
		if (creatorClass == null) {
			// this class is not supported, do nor replicate
			return;
		}

		if (this.suspendIdList != null) {
			boolean notifiy = true;
			for(UpdateAccumulate listener : this.suspendIdList) {
				if( listener.changeAttribute(this, source,creatorClass, property, oldValue, newValue) ) {
					notifiy = false;
				}
			}
			if(notifiy == false) {
				return;
			}
		}

		Entity jsonObject = change(property, source, creatorClass, oldValue, newValue);

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

	public Entity change(String property, Object source, SendableEntityCreator creatorClass, Object oldValue, Object newValue) {
		if(factory == null) {
			return null;
		}
		Entity jsonObject = factory.newInstance();
		String id = this.map.getId(source, true);
		Grammar grammar = this.map.getGrammar();
		grammar.writeBasicValue(jsonObject, source.getClass().getName(), id, SendableEntityCreator.UPDATE, map);
		change(property, creatorClass, jsonObject, oldValue, newValue);
		return jsonObject;
	}

	public boolean change(String property, SendableEntityCreator creator, Entity change, Object oldValue, Object newValue) {
		boolean done = false;
		if(creator == null) {
			return false;
		}
		String[] properties = creator.getProperties();
		if(properties == null) {
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
			// if propertyname is not found and the name is REMOVE_YOU it remove it from the IdMap
			if(SendableEntityCreator.REMOVE_YOU.equals(property)) {
				this.removeObj(oldValue, true);
			}
			return false;
		}

		SendableEntityCreator creatorClass;
		Object child = null;
		Entity entity;
		if (oldValue != null) {
			creatorClass = this.map.getCreatorClass(oldValue);
			child = change.getValue(SendableEntityCreator.REMOVE);
			if(child instanceof Entity) {
				entity = (Entity) child;
			} else {
				entity = factory.newInstance();
				change.put(SendableEntityCreator.REMOVE, entity);
			}

			if (creatorClass != null) {
				String oldId = this.map.getId(oldValue, true);
				if (oldId != null) {
					Entity childItem = factory.newInstance();
					childItem.put(IdMap.ID, oldId);
					entity.put(property, childItem);
				}
			} else {
				entity.put(property, oldValue);
			}
		}

		if (newValue != null) {
			creatorClass = this.map.getCreatorClass(newValue);
			child = change.getValue(SendableEntityCreator.UPDATE);
			if(child instanceof Entity) {
				entity = (Entity) child;
			} else {
				entity = factory.newInstance();
				change.put(SendableEntityCreator.UPDATE, entity);
			}

			if (creatorClass != null) {
				String key = this.map.getKey(newValue);
				if (key != null) {
					Entity item = factory.newInstance();
					item.put(IdMap.CLASS, newValue.getClass().getName());
					item.put(IdMap.ID, key);
					entity.put(property, item);
				} else {
					Entity item = (Entity) this.map.encode(newValue, this.factory, this.updateFilter);
					entity.put(property, item);
				}
			} else {
				// plain attribute
				entity.put(property, newValue);
			}
		}
		return true;
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
			for(SimpleIteratorSet<String, Object> i = new SimpleIteratorSet<String, Object>(update);i.hasNext();) {
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
				String key = update.getKeyByIndex(i);
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
		if(oldJsonObject == null) {
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
	
	public UpdateListener withCondition(ObjectCondition condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false || condition == null) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();

		if ((oldValue == null && newValue == null)
				|| (oldValue != null && oldValue.equals(newValue))) {
			// Nothing to do
			return false;
		}
		// put changes into msg and send to receiver
		Object source;
		if(evt instanceof SimpleEvent) {
			source = ((SimpleEvent)evt).getModelValue();
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
}
