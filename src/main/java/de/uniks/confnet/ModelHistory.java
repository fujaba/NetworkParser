package de.uniks.confnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import de.uniks.confnet.model.Identifier;
import de.uniks.confnet.model.LogItem;
import de.uniks.confnet.network.NodeProxy;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;

public class ModelHistory extends Identifier  
{
	public static final String PROPERTY_HISTORY = "history";

   public static final String PROPERTY_LASTMODELCHANGE = "lastmodelchange";
	
	private World world;
	private TreeSet<ModelChange> history = new TreeSet<ModelChange>();
//	private long lastMsgNo = 0; 
	private LinkedHashMap<SendableEntityCreator, Object> prototypeCache = new LinkedHashMap<SendableEntityCreator, Object>();
	private TreeMap<String, JsonObject> postponedChanges = new TreeMap<String, JsonObject>();
	private long allDataMsgNo = 0;
	private NodeProxy myProxy;

	public ModelHistory(World world, NodeProxy myProxy)
	{
		this.world = world;
		this.myProxy = myProxy;
	}
	
	public Integer getNewMsgNo()
	{
		if(myProxy==null){
			return 0;
		}
		return myProxy.getNewMsgNo();
	}
	
	public long getLastMsgNo()
   {
		if(myProxy==null){
			return 0;
		}
		Integer value = myProxy.getHistory();
		if(value==null){
			return 0;
		}
      return value;
   }

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
	
	private boolean isToManyField(SendableEntityCreator createrClass, String fieldName)
	{
	   Object prototype = prototypeCache.get(createrClass);
	   
	   if (prototype == null)
	   {
	      prototype = createrClass.getSendableInstance(true);
	      prototypeCache.put(createrClass, prototype);
	   }
	   
      Object fieldValue = createrClass.getValue(prototype, fieldName);
      
      //FIXME Muss umgebaut werden
      if (fieldValue != null && fieldValue instanceof Collection)
      {
         return true;
      }
      else
      {
         return false;
      }
	}
	
	public boolean addHistory(ModelChange value) 
	{
		boolean success = true;
		ModelChange historyChange = history.ceiling(value);
		
		while (success && historyChange != null)
		{
			if (value.compareTo(historyChange) == 0)
			{
				// this change is already known
				return false; 
			}
			
			// might be a conflict, i.e. same object.attr is written by historyChange and value
			JsonObject historyJsonObject = historyChange.getValue();
			JsonObject valueJsonObject = value.getValue();
			if(!historyJsonObject.has(IdMap.ID)){
				System.out.println("ERROR");
			}
			String historyJsonId = historyJsonObject.getString(IdMap.ID, "");
			String valueJsonId = valueJsonObject.getString(IdMap.ID, "");

			// same object
			if (historyJsonId.equals(valueJsonId))
			{
				JsonObject historyPropsObject = (JsonObject) historyJsonObject.get(SendableEntityCreator.UPDATE);
				
				if (historyPropsObject == null)
				{
					// must be a remove
					historyPropsObject = (JsonObject) historyJsonObject.get(SendableEntityCreator.REMOVE);
				}
				if(historyPropsObject==null){
					return false;
				}
				
				JsonObject valuePropsObject = (JsonObject) valueJsonObject.get(SendableEntityCreator.UPDATE);
				
				if (valuePropsObject == null)
				{
					// must be a remove
					valuePropsObject = (JsonObject) valueJsonObject.get(SendableEntityCreator.REMOVE);
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
						Object target = world.getMap().getObject(valueJsonId);
						if (target == null) 
						{
							world.addMessage(new LogItem(world.getCompName()+ ": change target unknown \n"
									+ valueJsonObject));
							return false; // this should not happen
						}

						SendableEntityCreator createrClass = world.getMap().getCreatorClass(target);

						if (isToManyField(createrClass, fieldName))
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

			historyChange = history.higher(historyChange);
		}
		
		if(value != null && value.getKey() != null) {
			history.add(value);
		}
		
		setMaxHistoryId(value.getKey());
		return success;
	}
	
	public void addFirstHistory(ModelChange change){
		history.add(change);
	}
	public void setMaxHistoryId(Integer value){
		if(myProxy!=null) {
			myProxy.setMaxHistoryId(value);
		}
	}
	
	public TreeSet<ModelChange> getHistory() 
	{
		return history;
	}
	
	public ModelChange getLastModelChange()
	{
		if (history.size() == 0) return null;
		
		return history.last();
	}
	public List<ModelChange> getHistoriesById(Integer id){
		ArrayList<ModelChange> ids=new ArrayList<ModelChange>();
		ModelChange change=history.higher(new ModelChange(id));
		while(change!=null){
			ids.add(change);
			change=history.higher(change);
		}
		return ids;
	}

	
	public TreeMap<String, JsonObject> getPostponedChanges()
	{
		return postponedChanges;
	}
//
	public void addPostponedChanges(String key, JsonObject msg) {
//		postponedChanges.put(key, msg);
	}

	public long getAllDataMsgNo()
	{
		return allDataMsgNo;
	}
	public void setAllDataMsgNo(long allDataMsgNo)
	{
		this.allDataMsgNo = allDataMsgNo;
	}

}
