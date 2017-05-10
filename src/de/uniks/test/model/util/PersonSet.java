package de.uniks.test.model.util;
import de.uniks.test.model.Person;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.interfaces.Condition;
import java.util.Collection;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.test.model.Room;

public class PersonSet extends SimpleSet<Person>
{

   public Class<?> getTypClass()
   {
      return Person.class;
   }

   public PersonSet()
   {
      // empty
   }

   public PersonSet(Person... objects)
   {
      for (Person obj : objects)
      {
         this.add(obj);
      }
   }

   public PersonSet(Collection<Person> objects)
   {
      this.addAll(objects);
   }

   public static final PersonSet EMPTY_SET = new PersonSet().withFlag(PersonSet.READONLY);

   public String getEntryType()
   {
      return "de.uniks.test.model.Person";
   }

   @Override
   public PersonSet getNewList(boolean keyValue)
   {
      return new PersonSet();
   }

   public PersonSet filter(Condition<Person> condition)
   {
      PersonSet filterList = new PersonSet();
      filterItems(filterList, condition);
      return filterList;
   }

   @SuppressWarnings("unchecked")
   public PersonSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Person>)value);
      }
      else if (value != null)
      {
         this.add((Person) value);
      }
      return this;
   }

   public StringList getName()
   {
      StringList result = new StringList();
      for (Person obj : this)
      {
         result.add(obj.getName());
      }
      return result;
   }

   public PersonSet filterName(String value)
   {
      PersonSet result = new PersonSet();
      for(Person obj : this)
      {
         if (value == obj.getName())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public PersonSet withName(String value)
   {
      for (Person obj : this)
      {
         obj.setName(value);
      }
      return this;
   }

   public PersonSet getRoom()
   {
      PersonSet result = new PersonSet();
      for (Person obj : this)
      {
         result.with(obj.getRoom());
      }
      return result;
   }

   public PersonSet filterRoom(Object value)
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
      PersonSet answer = new PersonSet();
      for (Person obj : this)
      {
         if (neighbors.contains(obj.getRoom()) || (neighbors.isEmpty() && obj.getRoom() == null))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public PersonSet withRoom(Room value)
   {
      for (Person obj : this)
      {
         obj.withRoom(value);
      }
      return this;
   }
   public PersonSet eat()
   {
      return PersonSet.EMPTY_SET;
   }

}