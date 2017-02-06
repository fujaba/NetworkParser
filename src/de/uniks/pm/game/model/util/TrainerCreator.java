package de.uniks.pm.game.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.pm.game.model.Trainer;
import de.uniks.pm.game.model.Ground;
import de.uniks.pm.game.model.Zombie;

public class TrainerCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      Trainer.PROPERTY_COLOR,
      Trainer.PROPERTY_EXPERIENCE,
      Trainer.PROPERTY_NAME,
      Trainer.PROPERTY_PREV,
      Trainer.PROPERTY_NEXT,
      Trainer.PROPERTY_GROUND,
      Trainer.PROPERTY_ZOMBIES,
   };

   @Override
   public String[] getProperties()
   {
      return properties;
   }

   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new Trainer();
   }

   public static IdMap createIdMap(String sessionID)
   {
      return de.uniks.pm.game.model.util.CreatorCreator.createIdMap(sessionID);
   }

   public void removeObject(Object entity)
   {
      ((Trainer) entity).removeYou();
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
      if (Trainer.PROPERTY_COLOR.equalsIgnoreCase(attribute))
      {
         return ((Trainer) target).getColor();
      }
      if (Trainer.PROPERTY_EXPERIENCE.equalsIgnoreCase(attribute))
      {
         return ((Trainer) target).getExperience();
      }
      if (Trainer.PROPERTY_NAME.equalsIgnoreCase(attribute))
      {
         return ((Trainer) target).getName();
      }
      if (Trainer.PROPERTY_PREV.equalsIgnoreCase(attribute))
      {
         return ((Trainer) target).getPrev();
      }
      if (Trainer.PROPERTY_NEXT.equalsIgnoreCase(attribute))
      {
         return ((Trainer) target).getNext();
      }
      if (Trainer.PROPERTY_GROUND.equalsIgnoreCase(attribute))
      {
         return ((Trainer) target).getGround();
      }
      if (Trainer.PROPERTY_ZOMBIES.equalsIgnoreCase(attribute))
      {
         return ((Trainer) target).getZombies();
      }
      return null;
   }

   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (Trainer.PROPERTY_COLOR.equalsIgnoreCase(attrName))
      {
         ((Trainer) target).setColor(value.toString());
         return true;
      }
      if (Trainer.PROPERTY_EXPERIENCE.equalsIgnoreCase(attrName))
      {
         ((Trainer) target).setExperience(Integer.parseInt(value.toString()));
         return true;
      }
      if (Trainer.PROPERTY_NAME.equalsIgnoreCase(attrName))
      {
         ((Trainer) target).setName(value.toString());
         return true;
      }
      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }
      if (Trainer.PROPERTY_PREV.equalsIgnoreCase(attrName))
      {
         ((Trainer) target).setPrev((Trainer) value);
         return true;
      }
      if (Trainer.PROPERTY_NEXT.equalsIgnoreCase(attrName))
      {
         ((Trainer) target).setNext((Trainer) value);
         return true;
      }
      if (Trainer.PROPERTY_GROUND.equalsIgnoreCase(attrName))
      {
         ((Trainer) target).withGround((Ground) value);
         return true;
      }
      if ((Trainer.PROPERTY_GROUND + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Trainer) target).withoutGround((Ground) value);
         return true;
      }
      if (Trainer.PROPERTY_ZOMBIES.equalsIgnoreCase(attrName))
      {
         ((Trainer) target).withZombies((Zombie) value);
         return true;
      }
      if ((Trainer.PROPERTY_ZOMBIES + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Trainer) target).withoutZombies((Zombie) value);
         return true;
      }
      return false;
   }

}