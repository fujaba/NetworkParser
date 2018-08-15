package org.networkparser.simple.model.util;
import org.networkparser.simple.model.Room;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.list.ObjectSet;
import java.util.Collections;
import org.networkparser.simple.model.Person;

public class RoomSet extends SimpleSet<Room>
{

   public Class<?> getTypClass()
   {
      return Room.class;
   }

   public RoomSet()
   {
      // empty
   }

   public RoomSet(Room... objects)
   {
      for (Room obj : objects)
      {
         this.add(obj);
      }
   }

   public RoomSet(Collection<Room> objects)
   {
      this.addAll(objects);
   }
		public static final RoomSet EMPTY_SET = new RoomSet().withFlag(RoomSet.READONLY);

   public String getEntryType()
   {
      return "org.networkparser.simple.model.Room";
   }
   @Override   public RoomSet getNewList(boolean keyValue)
   {
      return new RoomSet();
   }

   @SuppressWarnings("unchecked")
   public RoomSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Room>)value);
      }
      else if (value != null)
      {
         this.add((Room) value);
      }
      return this;
   }

   public RoomSet getPersons()
   {
      RoomSet result = new RoomSet();
      for (Room obj : this)
      {
         result.with(obj.getPersons());
      }
      return result;
   }

   public RoomSet filterPersons(Object value)
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
      RoomSet answer = new RoomSet();
      for (Room obj : this)
      {
         if (! Collections.disjoint(neighbors, obj.getPersons()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public RoomSet withPersons(Person value)
   {
      for (Room obj : this)
      {
         obj.withPersons(value);
      }
      return this;
   }
   public RoomSet init()
   {
      return RoomSet.EMPTY_SET;
   }

}