package de.uniks.pm.game.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.pm.game.model.Dice;
import de.uniks.pm.game.model.Game;

public class DiceCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      Dice.PROPERTY_VALUE,
      Dice.PROPERTY_GAME,
   };

   @Override
   public String[] getProperties()
   {
      return properties;
   }

   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new Dice();
   }

   public static IdMap createIdMap(String sessionID)
   {
      return de.uniks.pm.game.model.util.CreatorCreator.createIdMap(sessionID);
   }

   public void removeObject(Object entity)
   {
      ((Dice) entity).removeYou();
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
      if (Dice.PROPERTY_VALUE.equalsIgnoreCase(attribute))
      {
         return ((Dice) target).getValue();
      }
      if (Dice.PROPERTY_GAME.equalsIgnoreCase(attribute))
      {
         return ((Dice) target).getGame();
      }
      return null;
   }

   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (Dice.PROPERTY_VALUE.equalsIgnoreCase(attrName))
      {
         ((Dice) target).setValue(Integer.parseInt(value.toString()));
         return true;
      }
      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }
      if (Dice.PROPERTY_GAME.equalsIgnoreCase(attrName))
      {
         ((Dice) target).setGame((Game) value);
         return true;
      }
      return false;
   }

}