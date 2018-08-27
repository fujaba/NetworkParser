package org.networkparser.simple.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import org.networkparser.simple.model.Room;
import org.networkparser.simple.model.Person;
import de.uniks.networkparser.IdMap;


public class RoomCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      Room.PROPERTY_PERSONS,
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
      return new Room();
   }

   @Override
   public Object getValue(Object entity, String attribute)
   {
      if(attribute == null || entity instanceof Room == false) {
          return null;
      }
      Room element = (Room)entity;
      if (Room.PROPERTY_PERSONS.equalsIgnoreCase(attribute))
      {
         return element.getPersons();
      }

      if(SendableEntityCreator.DYNAMIC.equalsIgnoreCase(attribute)) {
          return element.getDynamicValues();
      }
      return element.getDynamicValue(attribute);
   }

   @Override
   public boolean setValue(Object entity, String attribute, Object value, String type)
   {
      if(attribute == null || entity instanceof Room == false) {
          return false;
      }
      Room element = (Room)entity;
      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attribute = attribute + type;
      }

      if (Room.PROPERTY_PERSONS.equalsIgnoreCase(attribute))
      {
         element.withPersons((Person) value);
         return true;
      }

      element.withDynamicValue(attribute, value);
      return true;
   }
    public IdMap createMap(String session) {
 	   return CreatorCreator.createIdMap(session);
    }
}