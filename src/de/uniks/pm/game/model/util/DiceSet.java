package de.uniks.pm.game.model.util;

import de.uniks.pm.game.model.Dice;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.pm.game.model.Game;
import de.uniks.pm.game.model.util.GameSet;

public class DiceSet extends SimpleSet<Dice>
{

   protected Class<?> getTypClass()
   {
      return Dice.class;
   }

   public DiceSet()
   {
      // empty
   }

   public DiceSet(Dice... objects)
   {
      for (Dice obj : objects)
      {
         this.add(obj);
      }
   }

   public DiceSet(Collection<Dice> objects)
   {
      this.addAll(objects);
   }

   public static final DiceSet EMPTY_SET = new DiceSet().withFlag(DiceSet.READONLY);

   public String getEntryType()
   {
      return "de.uniks.pm.game.model.Dice";
   }

   @Override
   public DiceSet getNewList(boolean keyValue)
   {
      return new DiceSet();
   }

   public DiceSet filter(Condition<Dice> condition)
   {
      DiceSet filterList = new DiceSet();
      filterItems(filterList, condition);
      return filterList;
   }

   public DiceSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Dice>)value);
      }
      else if (value != null)
      {
         this.add((Dice) value);
      }
      return this;
   }

   public DiceSet without(Dice value)
   {
      this.remove(value);
      return this;
   }

   public NumberList getValue()
   {
      NumberList result = new NumberList();
      for (Dice obj : this)
      {
         result.add(obj.getValue());
      }
      return result;
   }

   public DiceSet filterValue(int value)
   {
      DiceSet result = new DiceSet();
      for(Dice obj : this)
      {
         if (value == obj.getValue())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public DiceSet filterValue(int lower, int upper)
   {
      DiceSet result = new DiceSet();
      for (Dice obj : this)
      {
         if (lower <= obj.getValue() && upper >= obj.getValue())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public DiceSet withValue(int value)
   {
      for (Dice obj : this)
      {
         obj.setValue(value);
      }
      return this;
   }

   public GameSet getGame()
   {
      GameSet result = new GameSet();
      for (Dice obj : this)
      {
         result.with(obj.getGame());
      }
      return result;
   }

   public DiceSet filterGame(Object value)
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
      DiceSet answer = new DiceSet();
      for (Dice obj : this)
      {
         if (neighbors.contains(obj.getGame()) || (neighbors.isEmpty() && obj.getGame() == null))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public DiceSet withGame(Game value)
   {
      for (Dice obj : this)
      {
         obj.withGame(value);
      }
      return this;
   }

}