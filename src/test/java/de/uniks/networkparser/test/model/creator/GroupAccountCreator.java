package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.GroupAccount;

public class GroupAccountCreator implements SendableEntityCreator
{
   private final String[] properties = new String[]
   {
      GroupAccount.PROPERTY_PERSONS,
      GroupAccount.PROPERTY_ITEMS,
   };
   
   @Override
   public String[] getProperties()
   {
      return properties;
   }
   
   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new GroupAccount();
   }
   
   @Override
   public Object getValue(Object target, String attrName)
   {
      return ((GroupAccount) target).get(attrName);
   }
   
   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (JsonIdMap.REMOVE.equals(type))
      {
         attrName = attrName + type;
      }
      return ((GroupAccount) target).set(attrName, value);
   }
}

