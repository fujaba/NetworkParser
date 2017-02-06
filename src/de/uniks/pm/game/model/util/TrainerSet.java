package de.uniks.pm.game.model.util;

import de.uniks.pm.game.model.Trainer;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.pm.game.model.Ground;
import java.util.Collections;
import de.uniks.pm.game.model.util.GroundSet;
import de.uniks.pm.game.model.Zombie;
import de.uniks.pm.game.model.util.ZombieSet;
import de.uniks.pm.game.model.Trap;

public class TrainerSet extends SimpleSet<Trainer>
{

   protected Class<?> getTypClass()
   {
      return Trainer.class;
   }

   public TrainerSet()
   {
      // empty
   }

   public TrainerSet(Trainer... objects)
   {
      for (Trainer obj : objects)
      {
         this.add(obj);
      }
   }

   public TrainerSet(Collection<Trainer> objects)
   {
      this.addAll(objects);
   }

   public static final TrainerSet EMPTY_SET = new TrainerSet().withFlag(TrainerSet.READONLY);

   public String getEntryType()
   {
      return "de.uniks.pm.game.model.Trainer";
   }

   @Override
   public TrainerSet getNewList(boolean keyValue)
   {
      return new TrainerSet();
   }

   public TrainerSet filter(Condition<Trainer> condition)
   {
      TrainerSet filterList = new TrainerSet();
      filterItems(filterList, condition);
      return filterList;
   }

   public TrainerSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Trainer>)value);
      }
      else if (value != null)
      {
         this.add((Trainer) value);
      }
      return this;
   }

   public TrainerSet without(Trainer value)
   {
      this.remove(value);
      return this;
   }

   public StringList getColor()
   {
      StringList result = new StringList();
      for (Trainer obj : this)
      {
         result.add(obj.getColor());
      }
      return result;
   }

   public TrainerSet filterColor(String value)
   {
      TrainerSet result = new TrainerSet();
      for(Trainer obj : this)
      {
         if (value.equals(obj.getColor()))
         {
            result.add(obj);
         }
      }
      return result;
   }

   public TrainerSet filterColor(String lower, String upper)
   {
      TrainerSet result = new TrainerSet();
      for (Trainer obj : this)
      {
         if (lower.compareTo(obj.getColor()) <= 0 && upper.compareTo(obj.getColor()) >= 0)
         {
            result.add(obj);
         }
      }
      return result;
   }

   public TrainerSet withColor(String value)
   {
      for (Trainer obj : this)
      {
         obj.setColor(value);
      }
      return this;
   }

   public NumberList getExperience()
   {
      NumberList result = new NumberList();
      for (Trainer obj : this)
      {
         result.add(obj.getExperience());
      }
      return result;
   }

   public TrainerSet filterExperience(int value)
   {
      TrainerSet result = new TrainerSet();
      for(Trainer obj : this)
      {
         if (value == obj.getExperience())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public TrainerSet filterExperience(int lower, int upper)
   {
      TrainerSet result = new TrainerSet();
      for (Trainer obj : this)
      {
         if (lower <= obj.getExperience() && upper >= obj.getExperience())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public TrainerSet withExperience(int value)
   {
      for (Trainer obj : this)
      {
         obj.setExperience(value);
      }
      return this;
   }

   public StringList getName()
   {
      StringList result = new StringList();
      for (Trainer obj : this)
      {
         result.add(obj.getName());
      }
      return result;
   }

   public TrainerSet filterName(String value)
   {
      TrainerSet result = new TrainerSet();
      for(Trainer obj : this)
      {
         if (value.equals(obj.getName()))
         {
            result.add(obj);
         }
      }
      return result;
   }

   public TrainerSet filterName(String lower, String upper)
   {
      TrainerSet result = new TrainerSet();
      for (Trainer obj : this)
      {
         if (lower.compareTo(obj.getName()) <= 0 && upper.compareTo(obj.getName()) >= 0)
         {
            result.add(obj);
         }
      }
      return result;
   }

   public TrainerSet withName(String value)
   {
      for (Trainer obj : this)
      {
         obj.setName(value);
      }
      return this;
   }

   public TrainerSet getPrev()
   {
      TrainerSet result = new TrainerSet();
      for (Trainer obj : this)
      {
         result.with(obj.getPrev());
      }
      return result;
   }

   public TrainerSet filterPrev(Object value)
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
      TrainerSet answer = new TrainerSet();
      for (Trainer obj : this)
      {
         if (neighbors.contains(obj.getPrev()) || (neighbors.isEmpty() && obj.getPrev() == null))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public TrainerSet withPrev(Trainer value)
   {
      for (Trainer obj : this)
      {
         obj.withPrev(value);
      }
      return this;
   }

   public TrainerSet getNext()
   {
      TrainerSet result = new TrainerSet();
      for (Trainer obj : this)
      {
         result.with(obj.getNext());
      }
      return result;
   }

   public TrainerSet filterNext(Object value)
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
      TrainerSet answer = new TrainerSet();
      for (Trainer obj : this)
      {
         if (neighbors.contains(obj.getNext()) || (neighbors.isEmpty() && obj.getNext() == null))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public TrainerSet withNext(Trainer value)
   {
      for (Trainer obj : this)
      {
         obj.withNext(value);
      }
      return this;
   }

   public GroundSet getGround()
   {
      GroundSet result = new GroundSet();
      for (Trainer obj : this)
      {
         result.with(obj.getGround());
      }
      return result;
   }

   public TrainerSet filterGround(Object value)
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
      TrainerSet answer = new TrainerSet();
      for (Trainer obj : this)
      {
         if (! Collections.disjoint(neighbors, obj.getGround()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public TrainerSet withGround(Ground value)
   {
      for (Trainer obj : this)
      {
         obj.withGround(value);
      }
      return this;
   }

   public TrainerSet withoutGround(Ground value)
   {
      for (Trainer obj : this)
      {
         obj.withoutGround(value);
      }
      return this;
   }

   public ZombieSet getZombies()
   {
      ZombieSet result = new ZombieSet();
      for (Trainer obj : this)
      {
         result.with(obj.getZombies());
      }
      return result;
   }

   public TrainerSet filterZombies(Object value)
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
      TrainerSet answer = new TrainerSet();
      for (Trainer obj : this)
      {
         if (! Collections.disjoint(neighbors, obj.getZombies()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public TrainerSet withZombies(Zombie value)
   {
      for (Trainer obj : this)
      {
         obj.withZombies(value);
      }
      return this;
   }

   public TrainerSet withoutZombies(Zombie value)
   {
      for (Trainer obj : this)
      {
         obj.withoutZombies(value);
      }
      return this;
   }

   public TrainerSet attackZombie( Zombie v0, Zombie v1 )
   {
      return TrainerSet.EMPTY_SET;
   }

   public TrainerSet catchZombie( Zombie v0, Trap v1 )
   {
      return TrainerSet.EMPTY_SET;
   }

   public TrainerSet movePlayer( Ground v0 )
   {
      return TrainerSet.EMPTY_SET;
   }

}