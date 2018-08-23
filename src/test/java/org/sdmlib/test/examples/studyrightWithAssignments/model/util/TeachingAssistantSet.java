package org.sdmlib.test.examples.studyrightWithAssignments.model.util;
import org.sdmlib.test.examples.studyrightWithAssignments.model.TeachingAssistant;
import de.uniks.networkparser.list.BooleanList;
import de.uniks.networkparser.list.ObjectSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Room;

public class TeachingAssistantSet extends <TeachingAssistant>
{
	private static final long serialVersionUID = 1L;
	public static final TeachingAssistantSet EMPTY_SET = new TeachingAssistantSet();

	public Class<?> getTypClass() {
		return TeachingAssistant.class;
	}

	public TeachingAssistantSet getNewList(boolean keyValue) {
		return new TeachingAssistantSet();
	}


   public BooleanList isCertified()
   {
      BooleanList result = new BooleanList();
      for (TeachingAssistant obj : this)
      {
         result.add(obj.isCertified());
      }
      return result;
   }

   public TeachingAssistantSet filterCertified(boolean value)
   {
      TeachingAssistantSet result = new TeachingAssistantSet();
      for(TeachingAssistant obj : this)
      {
         if ( value == obj.isCertified())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public TeachingAssistantSet withCertified(boolean value) {
      for (TeachingAssistant obj : this)
      {
         obj.setCertified(value);
      }
      return this;
   }
   public RoomSet getRoom()
   {
      RoomSet result = new RoomSet();
      for (TeachingAssistant obj : this)
      {
         result.add(obj.getRoom());
      }
      return result;
   }

   public TeachingAssistantSet filterRoom(Object value)
   {
      ObjectSet neighbors = new ObjectSet().init(value);
      TeachingAssistantSet answer = new TeachingAssistantSet();
      for (TeachingAssistant obj : this)
      {
         if (neighbors.contains(obj.getRoom()) || (neighbors.isEmpty() && obj.getRoom() == null))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public TeachingAssistantSet withRoom(Room value)
   {
      for (TeachingAssistant obj : this)
      {
         obj.withRoom(value);
      }
      return this;
   }
}