package de.uniks.pm.game.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.pm.game.model.Rock;
import de.uniks.pm.game.model.Zombie;
import de.uniks.pm.game.model.Trainer;

public class RockCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      Rock.PROPERTY_ZOMBIES,
      Rock.PROPERTY_X,
      Rock.PROPERTY_Y,
      Rock.PROPERTY_TRAINERS,
   };

   @Override
   public String[] getProperties()
   {
      return properties;
   }

   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new Rock();
   }

   public static IdMap createIdMap(String sessionID)
   {
      return de.uniks.pm.game.model.util.CreatorCreator.createIdMap(sessionID);
   }

   public void removeObject(Object entity)
   {
      ((Rock) entity).removeYou();
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
      if (Rock.PROPERTY_ZOMBIES.equalsIgnoreCase(attribute))
      {
         return ((Rock) target).getZombies();
      }
      if (Rock.PROPERTY_X.equalsIgnoreCase(attribute))
      {
         return ((Rock) target).getX();
      }
      if (Rock.PROPERTY_Y.equalsIgnoreCase(attribute))
      {
         return ((Rock) target).getY();
      }
      if (Rock.PROPERTY_TRAINERS.equalsIgnoreCase(attribute))
      {
         return ((Rock) target).getTrainers();
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
      if (Rock.PROPERTY_ZOMBIES.equalsIgnoreCase(attrName))
      {
         ((Rock) target).withZombies((Zombie) value);
         return true;
      }
      if ((Rock.PROPERTY_ZOMBIES + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Rock) target).withoutZombies((Zombie) value);
         return true;
      }
      if (Rock.PROPERTY_X.equalsIgnoreCase(attrName))
      {
         ((Rock) target).setX(Integer.parseInt(value.toString()));
         return true;
      }
      if (Rock.PROPERTY_Y.equalsIgnoreCase(attrName))
      {
         ((Rock) target).setY(Integer.parseInt(value.toString()));
         return true;
      }
      if (Rock.PROPERTY_TRAINERS.equalsIgnoreCase(attrName))
      {
         ((Rock) target).withTrainers((Trainer) value);
         return true;
      }
      if ((Rock.PROPERTY_TRAINERS + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Rock) target).withoutTrainers((Trainer) value);
         return true;
      }
      return false;
   }

}