package de.uniks.networkparser.ext.petaf;

/*
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
import java.util.Collection;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.petaf.messages.ChangeMessage;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.SortedSet;
import de.uniks.networkparser.logic.SimpleObjectFilter;

public class ModelHistory implements ObjectCondition {
	public static final String PROPERTY_HISTORY = "history";
	public static final String PROPERTY_LASTMODELCHANGE = "lastmodelchange";
	public static final String PROPERTY_CHANGES = "changes";

	private SimpleSet<ModelChange> history = new SimpleSet<ModelChange>();

	private Space space;

	private SimpleKeyValueList<SendableEntityCreator, Object> prototypeCache = new SimpleKeyValueList<SendableEntityCreator, Object>();
	private SimpleKeyValueList<String, JsonObject> postponedChanges = new SimpleKeyValueList<String, JsonObject>();
	private long allDataMsgNo;
	private boolean isReading = false;
	protected long currentStep;

	public ModelChange getLastModelChange() {
		return history.last();
	}

	// {"session":"42","class":"ChangeMessage","id":"da39a3ee5e6b4b0d3255bfef95601890afd80709","received":[...],"prevChange":"da39a3ee5e6b4b0d3255bfef95601890afd80709",
	//      "changeid":"S810276874033685","property":"name","new":"Alex","changeclass":"de.uniks.networkparser.test.model.Student"}

	public boolean refactoringHistory() {
		SortedSet<NodeProxy> nodes = getSpace().getNodeProxies();
		SimpleList<String> keys = new SimpleList<String>();
		for (NodeProxy proxy : nodes) {
			String key = proxy.getHistory();
			keys.add(key);
		}
		// Now refacotring
		SimpleKeyValueList<String, ModelChange> changes=new SimpleKeyValueList<String, ModelChange>();
		SimpleKeyValueList<String, String> deletedChanges=new SimpleKeyValueList<String, String>();
		String value;
		int pos, i = checkMergeInitModel();
		for(;i<history.size();i++) {
			ModelChange change = history.get(i);

			BaseItem changeMsg = change.getChange();
			if(changeMsg instanceof Entity == false) {
				break;
			}
			Entity changeEntity = (Entity) changeMsg;
			setNewPrevId(changeEntity, deletedChanges);

            value = change.getKey();
            if(keys.contains(value)) {
            	// Some Node know only this change
            	break;
            }

			value = changeEntity.getString(ChangeMessage.PROPERTY_ID);
			if("ChangeMessage".equals(changeEntity.getString(IdMap.CLASS)) == false) {
				// May be another Message ignore it
				continue;
			}

			pos = changes.indexOf(value);
			if(pos < 0) {
				// First changes
				changes.put(value, change);
				continue;
			}

			EntityList changeChanges;
			Entity changeListEntity = (Entity) changes.getValueByIndex(pos).getChange();
			if(changeListEntity.has(PROPERTY_CHANGES)) {
				changeChanges = (EntityList) changeListEntity.getValue(PROPERTY_CHANGES);
			} else {
				// Create List
				changeChanges = (EntityList) changeListEntity.getNewList(false);
				changeListEntity.put(PROPERTY_CHANGES, changeChanges);

				// Copy First Change to Child
				Entity changeChange = (Entity) changeChanges.getNewList(true);
				changeChanges.add(changeChange);

				value = changeListEntity.getString(ChangeMessage.PROPERTY_PROPERTY);
				changeChange.put(ChangeMessage.PROPERTY_PROPERTY, value);

				value = changeListEntity.getString(ChangeMessage.PROPERTY_OLD);
				changeChange.put(ChangeMessage.PROPERTY_OLD, value);

				value = changeListEntity.getString(ChangeMessage.PROPERTY_NEW);
				changeChange.put(ChangeMessage.PROPERTY_NEW, value);

				value = changeListEntity.getString(ChangeMessage.PROPERTY_ID);
				changeChange.put(ChangeMessage.PROPERTY_ID, value);
			}
			// Add Current Change to List or Merge
			Entity changeChange = null;
			value = changeEntity.getString(ChangeMessage.PROPERTY_PROPERTY);
			for(int c=0; c < changeChanges.sizeChildren(); c++) {
				Entity child = (Entity) changeChanges.getChild(c);
				if(value.equals(child.getString(ChangeMessage.PROPERTY_PROPERTY))) {
					changeChange = child;
				}
			}
			if(changeChange == null) {
				changeChange = (Entity) changeChanges.getNewList(true);
				changeChanges.add(changeChange);
				changeChange.put(ChangeMessage.PROPERTY_PROPERTY, value);
				value = changeEntity.getString(ChangeMessage.PROPERTY_OLD);
				changeChange.put(ChangeMessage.PROPERTY_OLD, value);
			}
			value = changeEntity.getString(ChangeMessage.PROPERTY_NEW);
			changeChange.put(ChangeMessage.PROPERTY_NEW, value);

			value = changeEntity.getString(ChangeMessage.PROPERTY_ID);
			changeChange.put(ChangeMessage.PROPERTY_ID, value);

			setNewPrevId(changeEntity, deletedChanges);

			// Remove current Change
			history.remove(i);
			i--;
		}

		// Change Rest of Items PREV-ID to new One
		while(i<history.size()) {
			ModelChange change = history.get(i);
			BaseItem changeMsg = change.getChange();
			if(changeMsg instanceof Entity) {
				Entity changeEntity = (Entity) changeMsg;
				setNewPrevId(changeEntity, deletedChanges);
			}
			i++;
		}
		return true;
	}

	private int checkMergeInitModel() {
		// TODO Auto-generated method stub
		return 0;
	}

	private void setNewPrevId(Entity changeEntity, SimpleKeyValueList<String, String> deletedChanges) {
		// Check IF PREV-ID IS DELETED
		String value = changeEntity.getString(ChangeMessage.PROPERTY_PREVIOUSCHANGE);
		if(value != null) {
			int pos = deletedChanges.indexOf(value);
			if(pos>=0) {
				// Found deleted item
				changeEntity.put(ChangeMessage.PROPERTY_PREVIOUSCHANGE, deletedChanges.getValueByIndex(pos));
			}
		}

	}


	protected boolean isToManyField(SendableEntityCreator createrClass, String fieldName) {
		Object prototype = prototypeCache.get(createrClass);

		if (prototype == null) {
			prototype = createrClass.getSendableInstance(true);
			prototypeCache.put(createrClass, prototype);
		}

		Object fieldValue = createrClass.getValue(prototype, fieldName);

		if (fieldValue != null && fieldValue instanceof Collection) {
			return true;
		} else {
			return false;
		}
	}

	public String getPrevChangeId(String change) {
		if (history.isEmpty()) {
			return null;
		}
		ModelChange last = history.last();
		// ModelChange newChange = new ModelChange().withKey(change);
		// ModelChange lowerChange = history.lower(newChange);

		// JsonObjectTaskSend sendMsg;
		// String key="";
		// if (lower != null)
		// {
		// key=lower.getFullKey();
		// }
		if(last == null) {
			return null;
		}
		return last.getKey();
	}

	public boolean addHistory(Message value) {
		ModelChange change = new ModelChange();
		NodeProxy proxy = value.getReceiver();
		change.withReceiver(space.encode(proxy, null));
		change.withChange(value.getMessage());
		change.withKey(value.getMessageId(space, proxy));
		return addHistory(change);
	}

	private Entity getElement(ModelChange change) {
		BaseItem item = change.getChange();
		if (item instanceof Entity) {
			return (Entity) item;
		}
		return null;
	}

	public boolean addHistory(ModelChange value) {
		boolean success = true;
		ModelChange historyChange = history.ceiling(value, true);

		while (success && historyChange != null) {
			if (value.compareTo(historyChange) == 0) {
				// this change is already known
				return false;
			}

			// might be a conflict, i.e. same object.attr is written by historyChange and
			// value
			Entity historyJsonObject = getElement(historyChange);
			Entity valueJsonObject = getElement(value);

			if (historyJsonObject.has(IdMap.ID) == false) {
				System.out.println("ERROR");
			}
			String historyJsonId = historyJsonObject.getString(IdMap.ID);
			String valueJsonId = valueJsonObject.getString(IdMap.ID);

			// same object
			if (historyJsonId.equals(valueJsonId)) {
				Entity historyPropsObject = (Entity) historyJsonObject.getValue(SendableEntityCreator.UPDATE);

				if (historyPropsObject == null) {
					// must be a remove
					historyPropsObject = (Entity) historyJsonObject.getValue(SendableEntityCreator.REMOVE);
				}
				if (historyPropsObject == null) {
					return false;
				}

				Entity valuePropsObject = (Entity) valueJsonObject.getValue(SendableEntityCreator.UPDATE);

				if (valuePropsObject == null) {
					// must be a remove
					valuePropsObject = (Entity) valueJsonObject.getValue(SendableEntityCreator.REMOVE);
				}
				for (int i = 0; i < historyPropsObject.size(); i++) {
					// for (Iterator<String> iter = historyPropsObject.keyIterator();
					// iter.hasNext();)
					{
						String historyKey = historyPropsObject.getKeyByIndex(i);

						if (historyKey == null)
							continue;
						// no standard key ==> attr name
						String fieldName = historyKey;
						if (valuePropsObject == null) {
							continue;
						}
						Object valueProp = valuePropsObject.getValue(fieldName);
						if (valueProp != null) {
							// value writes same attr, is it to-one? ==> discard message. to-many ==>
							// conflict in case of same kid object
							Object target = space.getObject(valueJsonId);
							if (target == null) {
								return false; // this should not happen
							}

							SendableEntityCreator createrClass = space.getMap().getCreatorClass(target);

							if (createrClass.getValue(target, fieldName) instanceof Collection<?>) {
								// same kid object?
								Entity historyKid = getObject(historyPropsObject, fieldName);
								if (historyKid == null) {
									success = false; // should not happen
									break;
								}

								Entity valueKid = getObject(valuePropsObject, fieldName);
								if (valueKid == null) {
									success = false; // should not happen
									break;
								}

								String historyKidId = historyKid.getString(IdMap.ID);
								String valueKidId = valueKid.getString(IdMap.ID);
								if (historyKidId.equals(valueKidId)) {
									success = false;
									break;
								}
							} else {
								// to-one discard
								success = false;
								break;
							}
						}
					}
				}
				historyChange = history.ceiling(historyChange, false);
			}
		}
		if (value != null && value.getKey() != null) {
			history.add(value);
		}
		// setMaxHistoryId(value.getKey());
		return success;
	}

	public Entity getObject(Entity jsonObject, String fieldName) {
		Object historyKid = jsonObject.getValue(fieldName);
		if (historyKid == null) {
			historyKid = jsonObject.getValue(fieldName + SendableEntityCreator.REMOVE);
		}

		if (!(historyKid instanceof Entity)) {
			return null;
		}

		return (Entity) historyKid;
	}

	public Space getSpace() {
		return space;
	}

	public ModelHistory withSpace(Space space) {
		this.space = space;
		return this;
	}
	public void addFirstHistory(ModelChange change){
		history.add(change);
	}

	public ModelChange createChange(int key, BaseItem receiver, Entity value) {
		ModelChange modelChange = new ModelChange();
		modelChange.withKey(""+key);
		modelChange.withChange(value);
		modelChange.withReceiver(receiver);
		return modelChange;
	}
	public ModelChange createChange(Entity value) {
		ModelChange modelChange = new ModelChange();
		modelChange.withChange(value);
		return modelChange;
	}

	public ModelChange createChange(int key, String receiver, Entity value) {
		NodeProxy proxy = this.space.getProxy(receiver);
		JsonObject receiverObj = this.space.getMap().toJsonObject(proxy, Filter.regard(new SimpleObjectFilter()));
		return createChange(key, receiverObj, value);
	}

	public ModelChange ceiling(ModelChange element, boolean sameElement)	{
		return this.history.ceiling(element, sameElement);
	}

	public ModelChange last() {
		return history.last();
	}

	public boolean checkMessage(Entity change) {
        // ups, the sender of this message has a previous change, I do not know about
        // well, it might stem from before my alldata message.
		Object value = change.getValue(Message.PROPERTY_PREVIOUSCHANGE);
		int previousMsgNo = Integer.parseInt(""+value);

//		String previousChangeFullKey = (String) change.get();
        if (previousMsgNo > getAllDataMsgNo() && previousMsgNo > 1)
        {
//           String format = String.format("%%0%dd", 20);
//           Integer history = Integer.valueOf(""+change.getValue(NodeProxy.PROPERTY_HISTORY));
//           Object name = change.getValue(NodeProxy.PROPERTY_NAME);
//           String currentMsgFullKey = String.format(format, history+"!"+change.getString(NodeProxy.PROPERTY_NAME));

           return false;
        }
        return true;
	}


	public long getNewMsgNo()
	{
		if(this.space != null) {
			NodeProxy myNode = this.space.getMyNode();
			if(myNode != null) {
				return myNode.getNewMsgNo();
			}
		}
		return 0;
	}

	//TODO OLD METHOD WITH NUMERIC-CHANGES
	public long getAllDataMsgNo() {
		return allDataMsgNo;
	}

	public ModelHistory withAllDataMsgNo(long allDataMsgNo) {
		this.allDataMsgNo = allDataMsgNo;
		return this;
	}
	public void addPostponedChanges(String key, JsonObject msg) {
		postponedChanges.put(key, msg);
	}

	public SimpleKeyValueList<String, JsonObject> getPostponedChanges() {
		return postponedChanges;
	}

	public ModelChange lower(ModelChange change) {
		return history.lower(change, false);
	}

	public SimpleSet<ModelChange> getHistory() {
		return history;
	}

	// public Object get(String attrName)
	// {
	// if (PROPERTY_HISTORY.equals(attrName))
	// {
	// return history;
	// } else if (PROPERTY_LASTMODELCHANGE.equals(attrName)) {
	// return getLastModelChange();
	// }
	// return super.get(attrName);
	// }
	//
	// public boolean set(String attrName, Object value)
	// {
	// if(super.set(attrName, value)){
	// return true;
	// }
	// else if (PROPERTY_HISTORY.equals(attrName))
	// {
	// addHistory((ModelChange) value);
	// }
	// return true;
	// }
	//
	// public void setMaxHistoryId(Long value){
	// myProxy.setMaxHistoryId(value);
	// }
	//
	// public ModelChange getLastModelChange()
	// {
	// if (history.size() == 0) return null;
	//
	// return history.last();
	// }
	// public List<ModelChange> getHistoriesById(long id){
	// ArrayList<ModelChange> ids=new ArrayList<ModelChange>();
	// ModelChange change=history.higher(new ModelChange(id));
	// while(change!=null){
	// ids.add(change);
	// change=history.higher(change);
	// }
	// return ids;
	// }
	//
	//
	// public void addPostponedChanges(String key, JsonObject msg) {
	// postponedChanges.put(key, msg);
	// }
	//
	//
	// private LinkedHashMap<SendableEntityCreator, Object> prototypeCache = new
	// LinkedHashMap<SendableEntityCreator, Object>();
	// private TreeMap<String, JsonObject> postponedChanges = new TreeMap<String,
	// JsonObject>();
	// private long allDataMsgNo = 0;
	// private NodeProxy myProxy;
	//
	// public ModelHistory(World world, NodeProxy myProxy)
	// {
	// this.world = world;
	// this.myProxy = myProxy;
	// }
	
	
	
	// LOCAL HISTORY
	public static final ModelHistory createLocalHistory(IdMap map) {
		ModelHistory history = new ModelHistory();
		Space space = new Space();
		space.withHistory(history);
		space.withMap(map);
		history.withSpace(space);
		map.with(history);
		
		history.init();
		
		return history;
	}
	
	public ModelHistory init() {
		// read stopStep from file
//		String fileName = "doc/history.json";
//			CharacterBuffer readFile = FileBuffer.readFile(fileName);
//			char firstChar = readFile.nextClean(true);
//			Entity entity;
//			if(JsonObject.START == firstChar) {
//				entity = new JsonObject().withValue(readFile);
//			} else if(XMLEntity.START == firstChar) {
//				entity = new XMLEntity().withValue(readFile);
//			}
////			if(entity != null) {
////				long value = (Long) entity.getValue("stopStep");
////				this.stop
////			}
//			stopStep = value;
		return this;
	}

	public boolean isReading() {
		return isReading;
	}

	public ModelHistory withReading(boolean isReading) {
		this.isReading = isReading;
		return this;
	}

	@Override
	public boolean update(Object event) {
		if (isReading) {
			// do nothing
			return true;
		}
		// store message in list
		SimpleEvent simpleEvent = (SimpleEvent) event;
		if (simpleEvent.isIdEvent()) {
			return true;
		}
		if (simpleEvent.getEntity() == null) {
			// looks like a bug in IDMap. It fires an empty property change within
			// Filter.isPropertyRegard
			return false;
		}
		ModelChange change = new ModelChange().withChange(simpleEvent.getEntity());
		this.history.add(change);
		currentStep = history.size();
		return true;
	}
	

	public ModelHistory back(long steps) {
		for (long l = 0; l < steps; l++) {
			back();
		}
		return this;
	}

	public ModelHistory forward(long steps) {
		for (long l = 0; l < steps; l++) {
			forward();
		}
		return this;
	}
	
	public ModelHistory forward() {
		if (currentStep >= history.size()) {
			// already at start
			return this;
		}
		ModelChange step = history.get((int) (currentStep));
		// redo step
		BaseItem jo = step.getChange();

		withReading(true);
		space.getMap().decode(jo);
		withReading(false);
		currentStep++;

		return this;
	}
	public ModelHistory back() {
		if (currentStep <= 0) {
			// already at start
			return this;
		}

		ModelChange step = history.get((int) (currentStep - 1));

		// undo step by swapping rem and upd
		BaseItem jo = step.getChange();
		if(jo instanceof Entity == false) {
			return this;
		}
		Entity entity = (Entity) jo;
		JsonObject undo = new JsonObject();
		undo.put(IdMap.ID, entity.getString(IdMap.ID));
		Object update = entity.getValue(SendableEntityCreator.UPDATE);
		if (update != null && update instanceof JsonObject) {
			// if the value is a JsonObject, just use the id
			JsonObject jsonUpdate = (JsonObject) update;
			String key = jsonUpdate.keyIterator().next();
			Object value = jsonUpdate.get(key);
			if (value instanceof Entity) {
				Entity jsonValue = (Entity) value;

				JsonObject newValue = new JsonObject();
				newValue.put(IdMap.ID, jsonValue.getString(IdMap.ID));
				JsonObject newUpdate = new JsonObject();
				newUpdate.put(key, newValue);
				undo.put(SendableEntityCreator.REMOVE, newUpdate);
			} else {
				undo.put(SendableEntityCreator.REMOVE, update);
			}
		}

		Object remove = entity.getValue(SendableEntityCreator.REMOVE);
		if (remove != null) {
			undo.put(SendableEntityCreator.UPDATE, remove);
		}
			
		this.withReading(true);
		space.getMap().decode(jo);
		this.withReading(false);
		currentStep--;
		return this;
	}
	public ModelHistory back(Object target) {
		while (true) {
			back();
			if (currentStep <= 0) {
				return this;
			}

			// does current step operate on target?
			ModelChange step = history.get((int) (currentStep - 1));
			BaseItem jo = step.getChange();
			if(jo instanceof Entity == false) {
				return this;
			}
			Entity change = (Entity) jo;
			String id = change.getString(IdMap.ID);

			Object obj =space.getMap().getObject(id);

			if (obj == target) {
				return this;
			}
		}
	}
	
	public ModelHistory back(Object target, String property) {
		while (true) {
			back();

			if (currentStep <= 0) {
				return this;
			}

			// does current step operate on target?
			ModelChange step = history.get((int) (currentStep - 1));
			BaseItem jo = step.getChange();
			if(jo instanceof Entity == false) {
				return this;
			}
			Entity change = (Entity) jo;
			String id = change.getString(IdMap.ID);
			Object obj =space.getMap().getObject(id);

			if (obj == target) {
				Object update = change.getValue(SendableEntityCreator.UPDATE);
				if (update != null) {
					JsonObject jsonUpdate = (JsonObject) update;
					String key = jsonUpdate.keyIterator().next();
					if (key.equals(property)) {
						return this;
					}
				}
			}
		}
	}
}
