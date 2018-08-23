package org.networkparser.simple.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import org.networkparser.simple.model.Person;
import org.networkparser.simple.model.Room;
import de.uniks.networkparser.IdMap;


public class PersonCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      Person.PROPERTY_AGE,
      Person.PROPERTY_NAME,
      Person.PROPERTY_ROOM,
      SendableEntityCreator.DYNAMIC
   };

   @Override
   public String[] getProperties()
   {
      return properties;
   }

   @Override
   public Object getSendableInstance(boolean prototyp)
   {
      return new Person();
   }

   @Override
   public Object getValue(Object entity, String attribute)
   {
      if(attribute == null || entity instanceof Person == false) {
          return null;
      }
      Person element = (Person)entity;
      int pos = attribute.indexOf('.');
      String attrName = attribute;

      if (pos > 0)
      {
         attrName = attribute.substring(0, pos);
      }
      if(attrName.length()<1) {
         return null;
      }

      if (Person.PROPERTY_AGE.equalsIgnoreCase(attrName))
      {
         return element.getAge();
      }

      if (Person.PROPERTY_NAME.equalsIgnoreCase(attrName))
      {
         return element.getName();
      }

      if (Person.PROPERTY_ROOM.equalsIgnoreCase(attrName))
      {
         return element.getRoom();
      }

      if(SendableEntityCreator.DYNAMIC.equalsIgnoreCase(attrName)) {
          return element.getDynamicValues();
      }
      return element.getDynamicValue(attrName);
   }

   @Override
   public boolean setValue(Object entity, String attribute, Object value, String type)
   {
      if(attribute == null || entity instanceof Person == false) {
          return false;
      }
      Person element = (Person)entity;
      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attribute = attribute + type;
      }

      if (Person.PROPERTY_AGE.equalsIgnoreCase(attribute))
      {
         element.setAge((int) value);
         return true;
      }

      if (Person.PROPERTY_NAME.equalsIgnoreCase(attribute))
      {
         element.setName((String) value);
         return true;
      }

      if (Person.PROPERTY_ROOM.equalsIgnoreCase(attribute))
      {
         element.setRoom((Room) value);
         return true;
      }

      element.withDynamicValue(attribute, value);
      return true;
   }
    public IdMap createMap(String session) {
 	   return CreatorCreator.createIdMap(session);
    }
}