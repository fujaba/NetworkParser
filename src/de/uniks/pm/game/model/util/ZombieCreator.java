package de.uniks.pm.game.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.pm.game.model.Zombie;
import de.uniks.pm.game.model.ZombieOwner;

public class ZombieCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      Zombie.PROPERTY_AP,
      Zombie.PROPERTY_HP,
      Zombie.PROPERTY_NAME,
      Zombie.PROPERTY_ZOMBIEOWNER,
   };

   @Override
   public String[] getProperties()
   {
      return properties;
   }

   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new Zombie();
   }

   public static IdMap createIdMap(String sessionID)
   {
      return de.uniks.pm.game.model.util.CreatorCreator.createIdMap(sessionID);
   }

   public void removeObject(Object entity)
   {
      ((Zombie) entity).removeYou();
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
      if (Zombie.PROPERTY_AP.equalsIgnoreCase(attribute))
      {
         return ((Zombie) target).getAp();
      }
      if (Zombie.PROPERTY_HP.equalsIgnoreCase(attribute))
      {
         return ((Zombie) target).getHp();
      }
      if (Zombie.PROPERTY_NAME.equalsIgnoreCase(attribute))
      {
         return ((Zombie) target).getName();
      }
      if (Zombie.PROPERTY_ZOMBIEOWNER.equalsIgnoreCase(attribute))
      {
         return ((Zombie) target).getZombieOwner();
      }
      return null;
   }

   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (Zombie.PROPERTY_AP.equalsIgnoreCase(attrName))
      {
         ((Zombie) target).setAp(Integer.parseInt(value.toString()));
         return true;
      }
      if (Zombie.PROPERTY_HP.equalsIgnoreCase(attrName))
      {
         ((Zombie) target).setHp(Integer.parseInt(value.toString()));
         return true;
      }
      if (Zombie.PROPERTY_NAME.equalsIgnoreCase(attrName))
      {
         ((Zombie) target).setName(value.toString());
         return true;
      }
      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }
      if (Zombie.PROPERTY_ZOMBIEOWNER.equalsIgnoreCase(attrName))
      {
         ((Zombie) target).setZombieOwner((ZombieOwner) value);
         return true;
      }
      return false;
   }

}