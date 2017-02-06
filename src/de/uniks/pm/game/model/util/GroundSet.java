package de.uniks.pm.game.model.util;

import de.uniks.pm.game.model.Ground;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.pm.game.model.Trainer;
import java.util.Collections;
import de.uniks.pm.game.model.util.TrainerSet;
import de.uniks.pm.game.model.Zombie;
import de.uniks.pm.game.model.util.ZombieSet;

public class GroundSet extends SimpleSet<Ground>
{

   protected Class<?> getTypClass()
   {
      return Ground.class;
   }

   public GroundSet()
   {
      // empty
   }

   public GroundSet(Ground... objects)
   {
      for (Ground obj : objects)
      {
         this.add(obj);
      }
   }

   public GroundSet(Collection<Ground> objects)
   {
      this.addAll(objects);
   }

   public static final GroundSet EMPTY_SET = new GroundSet().withFlag(GroundSet.READONLY);

   public String getEntryType()
   {
      return "de.uniks.pm.game.model.Ground";
   }

   @Override
   public GroundSet getNewList(boolean keyValue)
   {
      return new GroundSet();
   }

   public GroundSet filter(Condition<Ground> condition)
   {
      GroundSet filterList = new GroundSet();
      filterItems(filterList, condition);
      return filterList;
   }

   public GroundSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Ground>)value);
      }
      else if (value != null)
      {
         this.add((Ground) value);
      }
      return this;
   }

   public GroundSet without(Ground value)
   {
      this.remove(value);
      return this;
   }

   public NumberList getX()
   {
      NumberList result = new NumberList();
      for (Ground obj : this)
      {
         result.add(obj.getX());
      }
      return result;
   }

   public GroundSet filterX(int value)
   {
      GroundSet result = new GroundSet();
      for(Ground obj : this)
      {
         if (value == obj.getX())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public GroundSet filterX(int lower, int upper)
   {
      GroundSet result = new GroundSet();
      for (Ground obj : this)
      {
         if (lower <= obj.getX() && upper >= obj.getX())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public GroundSet withX(int value)
   {
      for (Ground obj : this)
      {
         obj.setX(value);
      }
      return this;
   }

   public NumberList getY()
   {
      NumberList result = new NumberList();
      for (Ground obj : this)
      {
         result.add(obj.getY());
      }
      return result;
   }

   public GroundSet filterY(int value)
   {
      GroundSet result = new GroundSet();
      for(Ground obj : this)
      {
         if (value == obj.getY())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public GroundSet filterY(int lower, int upper)
   {
      GroundSet result = new GroundSet();
      for (Ground obj : this)
      {
         if (lower <= obj.getY() && upper >= obj.getY())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public GroundSet withY(int value)
   {
      for (Ground obj : this)
      {
         obj.setY(value);
      }
      return this;
   }

   public TrainerSet getTrainers()
   {
      TrainerSet result = new TrainerSet();
      for (Ground obj : this)
      {
         result.with(obj.getTrainers());
      }
      return result;
   }

   public GroundSet filterTrainers(Object value)
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
      GroundSet answer = new GroundSet();
      for (Ground obj : this)
      {
         if (! Collections.disjoint(neighbors, obj.getTrainers()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public GroundSet withTrainers(Trainer value)
   {
      for (Ground obj : this)
      {
         obj.withTrainers(value);
      }
      return this;
   }

   public GroundSet withoutTrainers(Trainer value)
   {
      for (Ground obj : this)
      {
         obj.withoutTrainers(value);
      }
      return this;
   }

   public ZombieSet getZombies()
   {
      ZombieSet result = new ZombieSet();
      for (Ground obj : this)
      {
         result.with(obj.getZombies());
      }
      return result;
   }

   public GroundSet filterZombies(Object value)
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
      GroundSet answer = new GroundSet();
      for (Ground obj : this)
      {
         if (! Collections.disjoint(neighbors, obj.getZombies()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public GroundSet withZombies(Zombie value)
   {
      for (Ground obj : this)
      {
         obj.withZombies(value);
      }
      return this;
   }

   public GroundSet withoutZombies(Zombie value)
   {
      for (Ground obj : this)
      {
         obj.withoutZombies(value);
      }
      return this;
   }

}