package de.uniks.model.util;
import de.uniks.model.Person;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.list.StringList;

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
      return "de.uniks.model.Person";
   }
   @Override   public PersonSet getNewList(boolean keyValue)
   {
      return new PersonSet();
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

   public StringList getFirst()
   {
      StringList result = new StringList();
      for (Person obj : this)
      {
         result.add(obj.getFirst());
      }
      return result;
   }

   public PersonSet filterFirst(String value)
   {
      PersonSet result = new PersonSet();
      for(Person obj : this)
      {
         if (value == obj.getFirst())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public PersonSet withFirst(String value) {
      for (Person obj : this)
      {
         obj.setFirst(value);
      }
      return this;
   }
}