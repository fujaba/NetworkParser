package de.uniks.pm.game.model.util;

import de.uniks.pm.game.model.Trap;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.NumberList;

public class TrapSet extends SimpleSet<Trap>
{

   protected Class<?> getTypClass()
   {
      return Trap.class;
   }

   public TrapSet()
   {
      // empty
   }

   public TrapSet(Trap... objects)
   {
      for (Trap obj : objects)
      {
         this.add(obj);
      }
   }

   public TrapSet(Collection<Trap> objects)
   {
      this.addAll(objects);
   }

   public static final TrapSet EMPTY_SET = new TrapSet().withFlag(TrapSet.READONLY);

   public String getEntryType()
   {
      return "de.uniks.pm.game.model.Trap";
   }

   @Override
   public TrapSet getNewList(boolean keyValue)
   {
      return new TrapSet();
   }

   public TrapSet filter(Condition<Trap> condition)
   {
      TrapSet filterList = new TrapSet();
      filterItems(filterList, condition);
      return filterList;
   }

   public TrapSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Trap>)value);
      }
      else if (value != null)
      {
         this.add((Trap) value);
      }
      return this;
   }

   public TrapSet without(Trap value)
   {
      this.remove(value);
      return this;
   }

   public NumberList getSuccessRate()
   {
      NumberList result = new NumberList();
      for (Trap obj : this)
      {
         result.add(obj.getSuccessRate());
      }
      return result;
   }

   public TrapSet filterSuccessRate(int value)
   {
      TrapSet result = new TrapSet();
      for(Trap obj : this)
      {
         if (value == obj.getSuccessRate())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public TrapSet filterSuccessRate(int lower, int upper)
   {
      TrapSet result = new TrapSet();
      for (Trap obj : this)
      {
         if (lower <= obj.getSuccessRate() && upper >= obj.getSuccessRate())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public TrapSet withSuccessRate(int value)
   {
      for (Trap obj : this)
      {
         obj.setSuccessRate(value);
      }
      return this;
   }

}