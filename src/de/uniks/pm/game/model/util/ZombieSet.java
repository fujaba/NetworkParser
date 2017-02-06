package de.uniks.pm.game.model.util;

import de.uniks.pm.game.model.Zombie;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.pm.game.model.ZombieOwner;
import de.uniks.pm.game.model.util.ZombieOwnerSet;

public class ZombieSet extends SimpleSet<Zombie>
{

   protected Class<?> getTypClass()
   {
      return Zombie.class;
   }

   public ZombieSet()
   {
      // empty
   }

   public ZombieSet(Zombie... objects)
   {
      for (Zombie obj : objects)
      {
         this.add(obj);
      }
   }

   public ZombieSet(Collection<Zombie> objects)
   {
      this.addAll(objects);
   }

   public static final ZombieSet EMPTY_SET = new ZombieSet().withFlag(ZombieSet.READONLY);

   public String getEntryType()
   {
      return "de.uniks.pm.game.model.Zombie";
   }

   @Override
   public ZombieSet getNewList(boolean keyValue)
   {
      return new ZombieSet();
   }

   public ZombieSet filter(Condition<Zombie> condition)
   {
      ZombieSet filterList = new ZombieSet();
      filterItems(filterList, condition);
      return filterList;
   }

   public ZombieSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Zombie>)value);
      }
      else if (value != null)
      {
         this.add((Zombie) value);
      }
      return this;
   }

   public ZombieSet without(Zombie value)
   {
      this.remove(value);
      return this;
   }

   public NumberList getAp()
   {
      NumberList result = new NumberList();
      for (Zombie obj : this)
      {
         result.add(obj.getAp());
      }
      return result;
   }

   public ZombieSet filterAp(int value)
   {
      ZombieSet result = new ZombieSet();
      for(Zombie obj : this)
      {
         if (value == obj.getAp())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public ZombieSet filterAp(int lower, int upper)
   {
      ZombieSet result = new ZombieSet();
      for (Zombie obj : this)
      {
         if (lower <= obj.getAp() && upper >= obj.getAp())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public ZombieSet withAp(int value)
   {
      for (Zombie obj : this)
      {
         obj.setAp(value);
      }
      return this;
   }

   public NumberList getHp()
   {
      NumberList result = new NumberList();
      for (Zombie obj : this)
      {
         result.add(obj.getHp());
      }
      return result;
   }

   public ZombieSet filterHp(int value)
   {
      ZombieSet result = new ZombieSet();
      for(Zombie obj : this)
      {
         if (value == obj.getHp())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public ZombieSet filterHp(int lower, int upper)
   {
      ZombieSet result = new ZombieSet();
      for (Zombie obj : this)
      {
         if (lower <= obj.getHp() && upper >= obj.getHp())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public ZombieSet withHp(int value)
   {
      for (Zombie obj : this)
      {
         obj.setHp(value);
      }
      return this;
   }

   public StringList getName()
   {
      StringList result = new StringList();
      for (Zombie obj : this)
      {
         result.add(obj.getName());
      }
      return result;
   }

   public ZombieSet filterName(String value)
   {
      ZombieSet result = new ZombieSet();
      for(Zombie obj : this)
      {
         if (value.equals(obj.getName()))
         {
            result.add(obj);
         }
      }
      return result;
   }

   public ZombieSet filterName(String lower, String upper)
   {
      ZombieSet result = new ZombieSet();
      for (Zombie obj : this)
      {
         if (lower.compareTo(obj.getName()) <= 0 && upper.compareTo(obj.getName()) >= 0)
         {
            result.add(obj);
         }
      }
      return result;
   }

   public ZombieSet withName(String value)
   {
      for (Zombie obj : this)
      {
         obj.setName(value);
      }
      return this;
   }

   public ZombieOwnerSet getZombieOwner()
   {
      ZombieOwnerSet result = new ZombieOwnerSet();
      for (Zombie obj : this)
      {
         result.with(obj.getZombieOwner());
      }
      return result;
   }

   public ZombieSet filterZombieOwner(Object value)
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
      ZombieSet answer = new ZombieSet();
      for (Zombie obj : this)
      {
         if (neighbors.contains(obj.getZombieOwner()) || (neighbors.isEmpty() && obj.getZombieOwner() == null))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public ZombieSet withZombieOwner(ZombieOwner value)
   {
      for (Zombie obj : this)
      {
         obj.withZombieOwner(value);
      }
      return this;
   }

}