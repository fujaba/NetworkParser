package org.sdmlib.test.examples.studyrightWithAssignments.model.util;
import org.sdmlib.test.examples.studyrightWithAssignments.model.University;
import java.util.LinkedHashSet;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.list.ObjectSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Student;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Room;
import org.sdmlib.test.examples.studyrightWithAssignments.model.President;

public class UniversitySet extends LinkedHashSet<University>
{
	private static final long serialVersionUID = 1L;
	public static final UniversitySet EMPTY_SET = new UniversitySet();

	public Class<?> getTypClass() {
		return University.class;
	}

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

   public UniversitySet withName(String value) {
      for (University obj : this)
      {
         obj.setName(value);
      }
      return this;
   }
   public StudentSet getStudents()
   {
      StudentSet result = new StudentSet();
      for (University obj : this)
      {
         result.addAll(obj.getStudents());
      }
      return result;
   }

   public UniversitySet filterStudents(Object value)
   {
      ObjectSet neighbors = new ObjectSet().init(value);
      UniversitySet answer = new UniversitySet();
      for (University obj : this)
      {
         if (! neighbors.containsAny(obj.getStudents()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public UniversitySet withStudents(Student value)
   {
      for (University obj : this)
      {
         obj.withStudents(value);
      }
      return this;
   }
   public RoomSet getRooms()
   {
      RoomSet result = new RoomSet();
      for (University obj : this)
      {
         result.addAll(obj.getRooms());
      }
      return result;
   }

   public UniversitySet filterRooms(Object value)
   {
      ObjectSet neighbors = new ObjectSet().init(value);
      UniversitySet answer = new UniversitySet();
      for (University obj : this)
      {
         if (! neighbors.containsAny(obj.getRooms()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public UniversitySet withRooms(Room value)
   {
      for (University obj : this)
      {
         obj.withRooms(value);
      }
      return this;
   }
   public PresidentSet getPresident()
   {
      PresidentSet result = new PresidentSet();
      for (University obj : this)
      {
         result.add(obj.getPresident());
      }
      return result;
   }

   public UniversitySet filterPresident(Object value)
   {
      ObjectSet neighbors = new ObjectSet().init(value);
      UniversitySet answer = new UniversitySet();
      for (University obj : this)
      {
         if (neighbors.contains(obj.getPresident()) || (neighbors.isEmpty() && obj.getPresident() == null))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public UniversitySet withPresident(President value)
   {
      for (University obj : this)
      {
         obj.withPresident(value);
      }
      return this;
   }
}