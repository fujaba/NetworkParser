package de.uniks.pm.game.model.util;

import de.uniks.pm.game.model.Game;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.pm.game.model.Dice;
import de.uniks.pm.game.model.util.DiceSet;

public class GameSet extends SimpleSet<Game>
{

   protected Class<?> getTypClass()
   {
      return Game.class;
   }

   public GameSet()
   {
      // empty
   }

   public GameSet(Game... objects)
   {
      for (Game obj : objects)
      {
         this.add(obj);
      }
   }

   public GameSet(Collection<Game> objects)
   {
      this.addAll(objects);
   }

   public static final GameSet EMPTY_SET = new GameSet().withFlag(GameSet.READONLY);

   public String getEntryType()
   {
      return "de.uniks.pm.game.model.Game";
   }

   @Override
   public GameSet getNewList(boolean keyValue)
   {
      return new GameSet();
   }

   public GameSet filter(Condition<Game> condition)
   {
      GameSet filterList = new GameSet();
      filterItems(filterList, condition);
      return filterList;
   }

   public GameSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Game>)value);
      }
      else if (value != null)
      {
         this.add((Game) value);
      }
      return this;
   }

   public GameSet without(Game value)
   {
      this.remove(value);
      return this;
   }

   public NumberList getActionPoints()
   {
      NumberList result = new NumberList();
      for (Game obj : this)
      {
         result.add(obj.getActionPoints());
      }
      return result;
   }

   public GameSet filterActionPoints(int value)
   {
      GameSet result = new GameSet();
      for(Game obj : this)
      {
         if (value == obj.getActionPoints())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public GameSet filterActionPoints(int lower, int upper)
   {
      GameSet result = new GameSet();
      for (Game obj : this)
      {
         if (lower <= obj.getActionPoints() && upper >= obj.getActionPoints())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public GameSet withActionPoints(int value)
   {
      for (Game obj : this)
      {
         obj.setActionPoints(value);
      }
      return this;
   }

   public DiceSet getDice()
   {
      DiceSet result = new DiceSet();
      for (Game obj : this)
      {
         result.with(obj.getDice());
      }
      return result;
   }

   public GameSet filterDice(Object value)
   {
      ObjectSet neighbors = new ObjectSet();
      if (value instanceof Collection)
      {
         neighbors.addAll((Collection<?>) value);
      }
      else
      {
         neighbors.add(value);
      }
      GameSet answer = new GameSet();
      for (Game obj : this)
      {
         if (neighbors.contains(obj.getDice()) || (neighbors.isEmpty() && obj.getDice() == null))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public GameSet withDice(Dice value)
   {
      for (Game obj : this)
      {
         obj.withDice(value);
      }
      return this;
   }

   public GameSet checkEnd(  )
   {
      return GameSet.EMPTY_SET;
   }

}