package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.Data;

public class DataCreator implements SendableEntityCreator
{
   private final String[] properties = new String[]
   {
      Data.PROPERTY_NUM
   };
   
   @Override
   public String[] getProperties()
   {
      return properties;
   }
   
   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new Data();
   }
   
   @Override
   public Object getValue(Object target, String attrName)
   {
      return ((Data) target).get(attrName);
   }
   
   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (JsonIdMap.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }
      return ((Data) target).set(attrName, value);
   }
}

