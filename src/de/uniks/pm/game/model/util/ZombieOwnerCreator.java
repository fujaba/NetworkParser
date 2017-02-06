package de.uniks.pm.game.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.pm.game.model.ZombieOwner;
import de.uniks.pm.game.model.Zombie;

public class ZombieOwnerCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      ZombieOwner.PROPERTY_ZOMBIES,
   };

   @Override
   public String[] getProperties()
   {
      return properties;
   }

   @Override
   public Object getSendableInstance(boolean reference)
   {
      return ZombieOwner.class;
   }

   public static IdMap createIdMap(String sessionID)
   {
      return de.uniks.pm.game.model.util.CreatorCreator.createIdMap(sessionID);
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
      if (ZombieOwner.PROPERTY_ZOMBIES.equalsIgnoreCase(attribute))
      {
         return ((ZombieOwner) target).getZombies();
      }
      return null;
   }

   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }
      if (ZombieOwner.PROPERTY_ZOMBIES.equalsIgnoreCase(attrName))
      {
         ((ZombieOwner) target).withZombies((Zombie) value);
         return true;
      }
      if ((ZombieOwner.PROPERTY_ZOMBIES + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((ZombieOwner) target).withoutZombies((Zombie) value);
         return true;
      }
      return false;
   }

}