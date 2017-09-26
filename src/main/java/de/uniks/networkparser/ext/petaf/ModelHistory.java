package de.uniks.networkparser.ext.petaf;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.SortedSet;

//TODO REMOVE COMMENTS
//TODO ADD FUNCTIONALITY
public class ModelHistory
{
	public static final String PROPERTY_HISTORY = "history";
	public static final String PROPERTY_LASTMODELCHANGE = "lastmodelchange";

	private SimpleSet<ModelChange> history = new SimpleSet<ModelChange>();
	private Space space;
	
	private SimpleKeyValueList<SendableEntityCreator, Object> prototypeCache = new SimpleKeyValueList<SendableEntityCreator, Object>();
	private SimpleKeyValueList<String, JsonObject> postponedChanges = new SimpleKeyValueList<String, JsonObject>();

	
	//TODO Functionality
	public void refactoringHistory(){
		boolean refactoring=true;
		int lowestId=0;
		SortedSet<NodeProxy> nodes = getSpace().getNodeProxies();
		for(NodeProxy proxy : nodes){
			if(!proxy.isOnline()){
				refactoring=false;
				break;
			}
		}
		
	}
	
	public String getPrevChangeId(String change){
		if(history.isEmpty()){
			return null;
		}
		ModelChange last = history.last();
//		ModelChange newChange = new ModelChange().withKey(change);
//		ModelChange lowerChange = history.lower(newChange);
		
		
//		JsonObjectTaskSend sendMsg;
//		String key="";
//		if (lower != null)
//		{
//			key=lower.getFullKey();
//		}
		return last.getKey();
	}
	
	public boolean addHistory(Message value){
		ModelChange change=new ModelChange();
		NodeProxy proxy = value.getReceiver();
		change.withReceiver(space.encode(proxy, null));
		change.withChange(value.getMessage());
		change.withKey(value.getMessageId(space, proxy));
		return addHistory(change);
	}
	
	private Entity getElement(ModelChange change){
		BaseItem item = change.getChange();
		if(item instanceof Entity){
			return (Entity)item;
		}
		return null;
	}
	
	public boolean addHistory(ModelChange value) 
	{
		boolean success = true;
		ModelChange historyChange = history.ceiling(value, true);
		
		while (success && historyChange != null)
		{
			if (value.compareTo(historyChange) == 0)
			{
				// this change is already known
				return false; 
			}
			
			// might be a conflict, i.e. same object.attr is written by historyChange and value
			Entity historyJsonObject = getElement(historyChange);
			Entity valueJsonObject = getElement(value);
			
			if(!historyJsonObject.has(IdMap.ID)){
				System.out.println("ERROR");
			}
			String historyJsonId = historyJsonObject.getString(IdMap.ID);
			String valueJsonId = valueJsonObject.getString(IdMap.ID);

			// same object
			if (historyJsonId.equals(valueJsonId))
			{
				JsonObject historyPropsObject = (JsonObject) historyJsonObject.getValue(SendableEntityCreator.UPDATE);
				
				if (historyPropsObject == null)
				{
					// must be a remove
					historyPropsObject = (JsonObject) historyJsonObject.getValue(SendableEntityCreator.REMOVE);
				}
				if(historyPropsObject==null){
					return false;
				}
				
				JsonObject valuePropsObject = (JsonObject) valueJsonObject.getValue(SendableEntityCreator.UPDATE);
				
				if (valuePropsObject == null)
				{
					// must be a remove
					valuePropsObject = (JsonObject) valueJsonObject.getValue(SendableEntityCreator.REMOVE);
				}
				
				for (Iterator<String> iter = historyPropsObject.keyIterator(); iter.hasNext();)
				{
					String historyKey = iter.next();

					if(historyKey==null)
						continue;
					// no standard key ==> attr name
					String fieldName = historyKey;
					if(valuePropsObject==null){
						continue;
					}
					
					if (valuePropsObject.get(fieldName) != null)
					{
						// value writes same attr, is it to-one? ==> discard message. to-many ==> conflict in case of same kid object
						Object target = space.getObject(valueJsonId);
						if (target == null) 
						{
							return false; // this should not happen
						}

						SendableEntityCreator createrClass = space.getMap().getCreatorClass(target);
						
						if (createrClass.getValue(target, fieldName) instanceof Collection<?>)
						{
							// same kid object?
							JsonObject historyKid = getJSONObject(historyPropsObject, fieldName);
							if (historyKid == null)
							{
								success = false; // should not happen
								break;
							}

							JsonObject valueKid = getJSONObject(valuePropsObject, fieldName);
							if (valueKid == null)
							{
								success = false; // should not happen
								break;
							}

							String historyKidId = historyKid.getString(IdMap.ID);
							String valueKidId = valueKid.getString(IdMap.ID);
							if (historyKidId.equals(valueKidId))
							{
								success = false;
								break;
							}
						}
						else
						{
							// to-one discard
							success = false; 
							break;
						}
					}

				}
			}

			historyChange = history.ceiling(historyChange, false);
		}
		
		history.add(value);
		
//		setMaxHistoryId(value.getKey());
		return success;
	}
	
	
	//FIXME ALTES VERFAHREN
	public JsonObject getJSONObject(JsonObject jsonObject, String fieldName)
	{
		Object historyKid = jsonObject.get(fieldName);
		if (historyKid == null)
		{
			historyKid = jsonObject.get(fieldName+SendableEntityCreator.REMOVE);
		}
		
		if ( ! (historyKid instanceof JsonObject) )
		{
			return null;
		}
		
		return (JsonObject) historyKid;
	}

	public Space getSpace() {
		return space;
	}

	public ModelHistory withSpace(Space space) {
		this.space = space;
		return this;
	}
	
	
	
	
	//	public Object get(String attrName)
//	{
//		if (PROPERTY_HISTORY.equals(attrName))
//		{
//			return history;
//		} else if (PROPERTY_LASTMODELCHANGE.equals(attrName)) {
//		   return getLastModelChange();
//		}
//		return super.get(attrName);
//	}
//
//	public boolean set(String attrName, Object value)
//	{
//		if(super.set(attrName, value)){
//			return true;
//		}
//		else if (PROPERTY_HISTORY.equals(attrName))
//		{
//			addHistory((ModelChange) value);
//		}
//		return true;
//	}
//
//	public JsonObject getJSONObject(JsonObject jsonObject, String fieldName)
//	{
//		Object historyKid = jsonObject.get(fieldName);
//		if (historyKid == null)
//		{
//			historyKid = jsonObject.get(fieldName+JsonIdMap.REMOVE);
//		}
//		
//		if ( ! (historyKid instanceof JsonObject) )
//		{
//			return null;
//		}
//		
//		return (JsonObject) historyKid;
//	}
//	
//	private boolean isToManyField(SendableEntityCreator createrClass, String fieldName)
//	{
//	   Object prototype = prototypeCache.get(createrClass);
//	   
//	   if (prototype == null)
//	   {
//	      prototype = createrClass.getSendableInstance(true);
//	      prototypeCache.put(createrClass, prototype);
//	   }
//	   
//      Object fieldValue = createrClass.getValue(prototype, fieldName);
//      
//      //FIXME Muss umgebaut werden
//      if (fieldValue != null && fieldValue instanceof Collection)
//      {
//         return true;
//      }
//      else
//      {
//         return false;
//      }
//	}
//	

//	
//	public void addFirstHistory(ModelChange change){
//		history.add(change);
//	}
//	public void setMaxHistoryId(Long value){
//		myProxy.setMaxHistoryId(value);
//	}
//	
//	public TreeSet<ModelChange> getHistory() 
//	{
//		return history;
//	}
//	
//	public ModelChange getLastModelChange()
//	{
//		if (history.size() == 0) return null;
//		
//		return history.last();
//	}
//	public List<ModelChange> getHistoriesById(long id){
//		ArrayList<ModelChange> ids=new ArrayList<ModelChange>();
//		ModelChange change=history.higher(new ModelChange(id));
//		while(change!=null){
//			ids.add(change);
//			change=history.higher(change);
//		}
//		return ids;
//	}
//
//	
//	public TreeMap<String, JsonObject> getPostponedChanges()
//	{
//		return postponedChanges;
//	}
//
//	public void addPostponedChanges(String key, JsonObject msg) {
//		postponedChanges.put(key, msg);
//	}
//
//	public long getAllDataMsgNo()
//	{
//		return allDataMsgNo;
//	}
//	public void setAllDataMsgNo(long allDataMsgNo)
//	{
//		this.allDataMsgNo = allDataMsgNo;
//	}
//
//	private LinkedHashMap<SendableEntityCreator, Object> prototypeCache = new LinkedHashMap<SendableEntityCreator, Object>();
//	private TreeMap<String, JsonObject> postponedChanges = new TreeMap<String, JsonObject>();
//	private long allDataMsgNo = 0;
//	private NodeProxy myProxy;
//
//	public ModelHistory(World world, NodeProxy myProxy)
//	{
//		this.world = world;
//		this.myProxy = myProxy;
//	}
}
