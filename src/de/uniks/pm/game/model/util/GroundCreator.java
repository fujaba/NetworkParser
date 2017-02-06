package de.uniks.pm.game.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.pm.game.model.Ground;
import de.uniks.pm.game.model.Trainer;
import de.uniks.pm.game.model.Zombie;

public class GroundCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      Ground.PROPERTY_X,
      Ground.PROPERTY_Y,
      Ground.PROPERTY_TRAINERS,
      Ground.PROPERTY_ZOMBIES,
   };

   @Override
   public String[] getProperties()
   {
      return properties;
   }

   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new Ground();
   }

   public static IdMap createIdMap(String sessionID)
   {
      return de.uniks.pm.game.model.util.CreatorCreator.createIdMap(sessionID);
   }

   public void removeObject(Object entity)
   {
      ((Ground) entity).removeYou();
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
      if (Ground.PROPERTY_X.equalsIgnoreCase(attribute))
      {
         return ((Ground) target).getX();
      }
      if (Ground.PROPERTY_Y.equalsIgnoreCase(attribute))
      {
         return ((Ground) target).getY();
      }
      if (Ground.PROPERTY_TRAINERS.equalsIgnoreCase(attribute))
      {
         return ((Ground) target).getTrainers();
      }
      if (Ground.PROPERTY_ZOMBIES.equalsIgnoreCase(attribute))
      {
         return ((Ground) target).getZombies();
      }
      return null;
   }

   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (Ground.PROPERTY_X.equalsIgnoreCase(attrName))
      {
         ((Ground) target).setX(Integer.parseInt(value.toString()));
         return true;
      }
      if (Ground.PROPERTY_Y.equalsIgnoreCase(attrName))
      {
         ((Ground) target).setY(Integer.parseInt(value.toString()));
         return true;
      }
      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }
      if (Ground.PROPERTY_TRAINERS.equalsIgnoreCase(attrName))
      {
         ((Ground) target).withTrainers((Trainer) value);
         return true;
      }
      if ((Ground.PROPERTY_TRAINERS + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Ground) target).withoutTrainers((Trainer) value);
         return true;
      }
      if (Ground.PROPERTY_ZOMBIES.equalsIgnoreCase(attrName))
      {
         ((Ground) target).withZombies((Zombie) value);
         return true;
      }
      if ((Ground.PROPERTY_ZOMBIES + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Ground) target).withoutZombies((Zombie) value);
         return true;
      }
      return false;
   }

}