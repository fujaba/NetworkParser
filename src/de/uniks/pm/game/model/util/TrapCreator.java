package de.uniks.pm.game.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.pm.game.model.Trap;

public class TrapCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      Trap.PROPERTY_SUCCESSRATE,
   };

   @Override
   public String[] getProperties()
   {
      return properties;
   }

   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new Trap();
   }

   public static IdMap createIdMap(String sessionID)
   {
      return de.uniks.pm.game.model.util.CreatorCreator.createIdMap(sessionID);
   }

   public void removeObject(Object entity)
   {
      ((Trap) entity).removeYou();
   }

   @Override
   public Object getValue(Object target, String attrName)
   {
      int pos = attrName.indexOf('.');
      String attribute = attrName;
      if (pos > 0)
      {
         attribute = attrName.substring(0, pos);
      }
      if (Trap.PROPERTY_SUCCESSRATE.equalsIgnoreCase(attribute))
      {
         return ((Trap) target).getSuccessRate();
      }
      return null;
   }

   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (Trap.PROPERTY_SUCCESSRATE.equalsIgnoreCase(attrName))
      {
         ((Trap) target).setSuccessRate(Integer.parseInt(value.toString()));
         return true;
      }
      return false;
   }

}