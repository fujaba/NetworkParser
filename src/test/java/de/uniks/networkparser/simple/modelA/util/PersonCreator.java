package de.uniks.networkparser.simple.modelA.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.simple.modelA.Person;
import de.uniks.networkparser.simple.modelA.Room;
import de.uniks.networkparser.IdMap;


public class PersonCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      Person.PROPERTY_ROOM,
      Person.PROPERTY_AGE,
      Person.PROPERTY_NAME,
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
      if (Person.PROPERTY_ROOM.equalsIgnoreCase(attribute))
      {
         return element.getRoom();
      }

      if (Person.PROPERTY_AGE.equalsIgnoreCase(attribute))
      {
         return element.getAge();
      }

      if (Person.PROPERTY_NAME.equalsIgnoreCase(attribute))
      {
         return element.getName();
      }

      if(SendableEntityCreator.DYNAMIC.equalsIgnoreCase(attribute)) {
          return element.getDynamicValues();
      }
      return element.getDynamicValue(attribute);
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

      if (Person.PROPERTY_ROOM.equalsIgnoreCase(attribute))
      {
         element.setRoom((Room) value);
         return true;
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

      element.withDynamicValue(attribute, value);
      return true;
   }

   public static IdMap createIdMap(String session) {
      return CreatorCreator.createIdMap(session);
   }
}