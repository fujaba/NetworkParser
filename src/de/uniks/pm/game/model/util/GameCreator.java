package de.uniks.pm.game.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.pm.game.model.Game;
import de.uniks.pm.game.model.Dice;

public class GameCreator implements SendableEntityCreator
{

   private final String[] properties = new String[]
   {
      Game.PROPERTY_ACTIONPOINTS,
      Game.PROPERTY_DICE,
   };

   @Override
   public String[] getProperties()
   {
      return properties;
   }

   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new Game();
   }

   public static IdMap createIdMap(String sessionID)
   {
      return de.uniks.pm.game.model.util.CreatorCreator.createIdMap(sessionID);
   }

   public void removeObject(Object entity)
   {
      ((Game) entity).removeYou();
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
      if (Game.PROPERTY_ACTIONPOINTS.equalsIgnoreCase(attribute))
      {
         return ((Game) target).getActionPoints();
      }
      if (Game.PROPERTY_DICE.equalsIgnoreCase(attribute))
      {
         return ((Game) target).getDice();
      }
      return null;
   }

   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (Game.PROPERTY_ACTIONPOINTS.equalsIgnoreCase(attrName))
      {
         ((Game) target).setActionPoints(Integer.parseInt(value.toString()));
         return true;
      }
      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }
      if (Game.PROPERTY_DICE.equalsIgnoreCase(attrName))
      {
         ((Game) target).setDice((Dice) value);
         return true;
      }
      return false;
   }

}