package de.uniks.pm.game.model.util;

import de.uniks.pm.game.model.ZombieOwner;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.pm.game.model.Zombie;
import java.util.Collections;
import de.uniks.pm.game.model.util.ZombieSet;

public class ZombieOwnerSet extends SimpleSet<ZombieOwner>
{

   protected Class<?> getTypClass()
   {
      return ZombieOwner.class;
   }

   public ZombieOwnerSet()
   {
      // empty
   }

   public ZombieOwnerSet(ZombieOwner... objects)
   {
      for (ZombieOwner obj : objects)
      {
         this.add(obj);
      }
   }

   public ZombieOwnerSet(Collection<ZombieOwner> objects)
   {
      this.addAll(objects);
   }

   public static final ZombieOwnerSet EMPTY_SET = new ZombieOwnerSet().withFlag(ZombieOwnerSet.READONLY);

   public String getEntryType()
   {
      return "de.uniks.pm.game.model.ZombieOwner";
   }

   @Override
   public ZombieOwnerSet getNewList(boolean keyValue)
   {
      return new ZombieOwnerSet();
   }

   public ZombieOwnerSet filter(Condition<ZombieOwner> condition)
   {
      ZombieOwnerSet filterList = new ZombieOwnerSet();
      filterItems(filterList, condition);
      return filterList;
   }

   public ZombieOwnerSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<ZombieOwner>)value);
      }
      else if (value != null)
      {
         this.add((ZombieOwner) value);
      }
      return this;
   }

   public ZombieOwnerSet without(ZombieOwner value)
   {
      this.remove(value);
      return this;
   }

   public ZombieSet getZombies()
   {
      ZombieSet result = new ZombieSet();
      for (ZombieOwner obj : this)
      {
         result.with(obj.getZombies());
      }
      return result;
   }

   public ZombieOwnerSet filterZombies(Object value)
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
      ZombieOwnerSet answer = new ZombieOwnerSet();
      for (ZombieOwner obj : this)
      {
         if (! Collections.disjoint(neighbors, obj.getZombies()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public ZombieOwnerSet withZombies(Zombie value)
   {
      for (ZombieOwner obj : this)
      {
         obj.withZombies(value);
      }
      return this;
   }

   public ZombieOwnerSet withoutZombies(Zombie value)
   {
      for (ZombieOwner obj : this)
      {
         obj.withoutZombies(value);
      }
      return this;
   }

}