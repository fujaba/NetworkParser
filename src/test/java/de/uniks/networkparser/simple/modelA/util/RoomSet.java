package de.uniks.networkparser.simple.modelA.util;
import de.uniks.networkparser.simple.modelA.Room;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.networkparser.simple.modelA.Person;

public class RoomSet extends SimpleSet<Room>
{
	public static final RoomSet EMPTY_SET = new RoomSet().withFlag(RoomSet.READONLY);

	public Class<?> getTypClass() {
		return Room.class;
	}

	@Override
	public RoomSet getNewList(boolean keyValue) {
		return new RoomSet();
	}


   public StringList getName()
   {
      StringList result = new StringList();
      for (Room obj : this)
      {
         result.add(obj.getName());
      }
      return result;
   }

   public RoomSet filterName(String value)
   {
      RoomSet result = new RoomSet();
      for(Room obj : this)
      {
         if (value == obj.getName())
         {
            result.add(obj);
         }
      }
      return result;
   }
   public RoomSet filterName(String minValue, String maxValue)
   {
      RoomSet result = new RoomSet();
      for(Room obj : this)
      {
         if (minValue.compareTo(obj.getName()) <= 0 && maxValue.compareTo(obj.getName()) >= 0)
         {
            result.add(obj);
         }
      }
      return result;
   }

   public RoomSet withName(String value) {
      for (Room obj : this)
      {
         obj.setName(value);
      }
      return this;
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