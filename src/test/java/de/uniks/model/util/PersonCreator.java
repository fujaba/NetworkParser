package de.uniks.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.model.Person;


public class PersonCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      Person.PROPERTY_FIRST,
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
      int pos = attribute.indexOf('.');
      String attrName = attribute;

      if (pos > 0)
      {
         attrName = attribute.substring(0, pos);
      }
      if(attrName.length()<1) {
         return null;
      }

      if (Person.PROPERTY_FIRST.equalsIgnoreCase(attrName))
      {
         return ((Person) entity).getFirst();
      }

      return null;
   }

   @Override
   public boolean setValue(Object entity, String attribute, Object value, String type)
   {
      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attribute = attribute + type;
      }

      if (Person.PROPERTY_FIRST.equalsIgnoreCase(attribute))
      {
         ((Person) entity).setFirst((String) value);
         return true;
      }

      return false;
   }

}