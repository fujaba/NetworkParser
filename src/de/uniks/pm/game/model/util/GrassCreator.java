package de.uniks.pm.game.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.pm.game.model.Grass;
import de.uniks.pm.game.model.Zombie;
import de.uniks.pm.game.model.Trainer;

public class GrassCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      Grass.PROPERTY_ZOMBIES,
      Grass.PROPERTY_TRAINERS,
   };

   @Override
   public String[] getProperties()
   {
      return properties;
   }

   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new Grass();
   }

   public static IdMap createIdMap(String sessionID)
   {
      return de.uniks.pm.game.model.util.CreatorCreator.createIdMap(sessionID);
   }

   public void removeObject(Object entity)
   {
      ((Grass) entity).removeYou();
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
      if (Grass.PROPERTY_ZOMBIES.equalsIgnoreCase(attribute))
      {
         return ((Grass) target).getZombies();
      }
      if (Grass.PROPERTY_TRAINERS.equalsIgnoreCase(attribute))
      {
         return ((Grass) target).getTrainers();
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
      if (Grass.PROPERTY_ZOMBIES.equalsIgnoreCase(attrName))
      {
         ((Grass) target).withZombies((Zombie) value);
         return true;
      }
      if ((Grass.PROPERTY_ZOMBIES + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Grass) target).withoutZombies((Zombie) value);
         return true;
      }
      if (Grass.PROPERTY_TRAINERS.equalsIgnoreCase(attrName))
      {
         ((Grass) target).withTrainers((Trainer) value);
         return true;
      }
      if ((Grass.PROPERTY_TRAINERS + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Grass) target).withoutTrainers((Trainer) value);
         return true;
      }
      return false;
   }

}