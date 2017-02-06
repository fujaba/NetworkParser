package de.uniks.pm.game.model.util;

import de.uniks.pm.game.model.Grass;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.pm.game.model.Zombie;
import java.util.Collections;
import de.uniks.pm.game.model.util.ZombieSet;
import de.uniks.pm.game.model.Trainer;
import de.uniks.pm.game.model.util.TrainerSet;

public class GrassSet extends SimpleSet<Grass>
{

   protected Class<?> getTypClass()
   {
      return Grass.class;
   }

   public GrassSet()
   {
      // empty
   }

   public GrassSet(Grass... objects)
   {
      for (Grass obj : objects)
      {
         this.add(obj);
      }
   }

   public GrassSet(Collection<Grass> objects)
   {
      this.addAll(objects);
   }

   public static final GrassSet EMPTY_SET = new GrassSet().withFlag(GrassSet.READONLY);

   public String getEntryType()
   {
      return "de.uniks.pm.game.model.Grass";
   }

   @Override
   public GrassSet getNewList(boolean keyValue)
   {
      return new GrassSet();
   }

   public GrassSet filter(Condition<Grass> condition)
   {
      GrassSet filterList = new GrassSet();
      filterItems(filterList, condition);
      return filterList;
   }

   public GrassSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Grass>)value);
      }
      else if (value != null)
      {
         this.add((Grass) value);
      }
      return this;
   }

   public GrassSet without(Grass value)
   {
      this.remove(value);
      return this;
   }

   public ZombieSet getZombies()
   {
      ZombieSet result = new ZombieSet();
      for (Grass obj : this)
      {
         result.with(obj.getZombies());
      }
      return result;
   }

   public GrassSet filterZombies(Object value)
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
      GrassSet answer = new GrassSet();
      for (Grass obj : this)
      {
         if (! Collections.disjoint(neighbors, obj.getZombies()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public GrassSet withZombies(Zombie value)
   {
      for (Grass obj : this)
      {
         obj.withZombies(value);
      }
      return this;
   }

   public GrassSet withoutZombies(Zombie value)
   {
      for (Grass obj : this)
      {
         obj.withoutZombies(value);
      }
      return this;
   }

   public TrainerSet getTrainers()
   {
      TrainerSet result = new TrainerSet();
      for (Grass obj : this)
      {
         result.with(obj.getTrainers());
      }
      return result;
   }

   public GrassSet filterTrainers(Object value)
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
      GrassSet answer = new GrassSet();
      for (Grass obj : this)
      {
         if (! Collections.disjoint(neighbors, obj.getTrainers()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public GrassSet withTrainers(Trainer value)
   {
      for (Grass obj : this)
      {
         obj.withTrainers(value);
      }
      return this;
   }

   public GrassSet withoutTrainers(Trainer value)
   {
      for (Grass obj : this)
      {
         obj.withoutTrainers(value);
      }
      return this;
   }

}