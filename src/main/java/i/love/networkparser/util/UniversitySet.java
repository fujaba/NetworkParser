package i.love.networkparser.util;
import i.love.networkparser.University;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.StringList;

public class UniversitySet extends SimpleSet<University>
{
	public static final UniversitySet EMPTY_SET = new UniversitySet().withFlag(UniversitySet.READONLY);

	public Class<?> getTypClass() {
		return University.class;
	}

	@Override
	public UniversitySet getNewList(boolean keyValue) {
		return new UniversitySet();
	}


   public StringList getName()
   {
      StringList result = new StringList();
      for (University obj : this)
      {
         result.add(obj.getName());
      }
      return result;
   }

   public UniversitySet filterName(String value)
   {
      UniversitySet result = new UniversitySet();
      for(University obj : this)
      {
         if (value == obj.getName())
         {
            result.add(obj);
         }
      }
      return result;
   }
   public UniversitySet filterName(String minValue, String maxValue)
   {
      UniversitySet result = new UniversitySet();
      for(University obj : this)
      {
         if (minValue.compareTo(obj.getName()) <= 0 && maxValue.compareTo(obj.getName()) >= 0)
         {
            result.add(obj);
         }
      }
      return result;
   }

   public UniversitySet withName(String value) {
      for (University obj : this)
      {
         obj.setName(value);
      }
      return this;
   }
}