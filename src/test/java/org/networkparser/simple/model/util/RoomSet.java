package org.networkparser.simple.model.util;
import org.networkparser.simple.model.Room;
import de.uniks.networkparser.list.ObjectSet;
import org.networkparser.simple.model.Person;

public class RoomSet extends <Room>
{
	public static final RoomSet EMPTY_SET = new RoomSet().withFlag(RoomSet.READONLY);

	public Class<?> getTypClass() {
		return Room.class;
	}

	@Override
	public RoomSet getNewList(boolean keyValue) {
		return new RoomSet();
	}


   public PersonSet getPersons()
   {
      PersonSet result = new PersonSet();
      for (Room obj : this)
      {
         result.addAll(obj.getPersons());
      }
      return result;
   }

   public RoomSet filterPersons(Object value)
   {
      ObjectSet neighbors = new ObjectSet().init(value);
      RoomSet answer = new RoomSet();
      for (Room obj : this)
      {
         if (! neighbors.containsAny(obj.getPersons()))
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