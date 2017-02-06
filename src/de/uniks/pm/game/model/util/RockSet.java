package de.uniks.pm.game.model.util;

import de.uniks.pm.game.model.Rock;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.pm.game.model.Zombie;
import java.util.Collections;
import de.uniks.pm.game.model.util.ZombieSet;
import de.uniks.pm.game.model.Trainer;
import de.uniks.pm.game.model.util.TrainerSet;

public class RockSet extends SimpleSet<Rock>
{

   protected Class<?> getTypClass()
   {
      return Rock.class;
   }

   public RockSet()
   {
      // empty
   }

   public RockSet(Rock... objects)
   {
      for (Rock obj : objects)
      {
         this.add(obj);
      }
   }

   public RockSet(Collection<Rock> objects)
   {
      this.addAll(objects);
   }

   public static final RockSet EMPTY_SET = new RockSet().withFlag(RockSet.READONLY);

   public String getEntryType()
   {
      return "de.uniks.pm.game.model.Rock";
   }

   @Override
   public RockSet getNewList(boolean keyValue)
   {
      return new RockSet();
   }

   public RockSet filter(Condition<Rock> condition)
   {
      RockSet filterList = new RockSet();
      filterItems(filterList, condition);
      return filterList;
   }

   public RockSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Rock>)value);
      }
      else if (value != null)
      {
         this.add((Rock) value);
      }
      return this;
   }

   public RockSet without(Rock value)
   {
      this.remove(value);
      return this;
   }

   public NumberList getX()
   {
      NumberList result = new NumberList();
      for (Rock obj : this)
      {
         result.add(obj.getX());
      }
      return result;
   }

   public RockSet filterX(int value)
   {
      RockSet result = new RockSet();
      for(Rock obj : this)
      {
         if (value == obj.getX())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public RockSet filterX(int lower, int upper)
   {
      RockSet result = new RockSet();
      for (Rock obj : this)
      {
         if (lower <= obj.getX() && upper >= obj.getX())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public RockSet withX(int value)
   {
      for (Rock obj : this)
      {
         obj.setX(value);
      }
      return this;
   }

   public NumberList getY()
   {
      NumberList result = new NumberList();
      for (Rock obj : this)
      {
         result.add(obj.getY());
      }
      return result;
   }

   public RockSet filterY(int value)
   {
      RockSet result = new RockSet();
      for(Rock obj : this)
      {
         if (value == obj.getY())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public RockSet filterY(int lower, int upper)
   {
      RockSet result = new RockSet();
      for (Rock obj : this)
      {
         if (lower <= obj.getY() && upper >= obj.getY())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public RockSet withY(int value)
   {
      for (Rock obj : this)
      {
         obj.setY(value);
      }
      return this;
   }

   public ZombieSet getZombies()
   {
      ZombieSet result = new ZombieSet();
      for (Rock obj : this)
      {
         result.with(obj.getZombies());
      }
      return result;
   }

   public RockSet filterZombies(Object value)
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
      RockSet answer = new RockSet();
      for (Rock obj : this)
      {
         if (! Collections.disjoint(neighbors, obj.getZombies()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public RockSet withZombies(Zombie value)
   {
      for (Rock obj : this)
      {
         obj.withZombies(value);
      }
      return this;
   }

   public RockSet withoutZombies(Zombie value)
   {
      for (Rock obj : this)
      {
         obj.withoutZombies(value);
      }
      return this;
   }

   public TrainerSet getTrainers()
   {
      TrainerSet result = new TrainerSet();
      for (Rock obj : this)
      {
         result.with(obj.getTrainers());
      }
      return result;
   }

   public RockSet filterTrainers(Object value)
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
      RockSet answer = new RockSet();
      for (Rock obj : this)
      {
         if (! Collections.disjoint(neighbors, obj.getTrainers()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public RockSet withTrainers(Trainer value)
   {
      for (Rock obj : this)
      {
         obj.withTrainers(value);
      }
      return this;
   }

   public RockSet withoutTrainers(Trainer value)
   {
      for (Rock obj : this)
      {
         obj.withoutTrainers(value);
      }
      return this;
   }

}