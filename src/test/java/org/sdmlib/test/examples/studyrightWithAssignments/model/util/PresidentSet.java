package org.sdmlib.test.examples.studyrightWithAssignments.model.util;
import org.sdmlib.test.examples.studyrightWithAssignments.model.President;
import de.uniks.networkparser.list.ObjectSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.University;

public class PresidentSet extends <President>
{
	private static final long serialVersionUID = 1L;
	public static final PresidentSet EMPTY_SET = new PresidentSet();

	public Class<?> getTypClass() {
		return President.class;
	}

	public PresidentSet getNewList(boolean keyValue) {
		return new PresidentSet();
	}


   public UniversitySet getUniversity()
   {
      UniversitySet result = new UniversitySet();
      for (President obj : this)
      {
         result.add(obj.getUniversity());
      }
      return result;
   }

   public PresidentSet filterUniversity(Object value)
   {
      ObjectSet neighbors = new ObjectSet().init(value);
      PresidentSet answer = new PresidentSet();
      for (President obj : this)
      {
         if (neighbors.contains(obj.getUniversity()) || (neighbors.isEmpty() && obj.getUniversity() == null))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public PresidentSet withUniversity(University value)
   {
      for (President obj : this)
      {
         obj.withUniversity(value);
      }
      return this;
   }
}