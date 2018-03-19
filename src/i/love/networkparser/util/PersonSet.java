package i.love.networkparser.util;
import i.love.networkparser.Person;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;

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
      return "i.love.networkparser.Person";
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

}