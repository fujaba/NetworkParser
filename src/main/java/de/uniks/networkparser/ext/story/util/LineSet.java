package de.uniks.networkparser.ext.story.util;
import de.uniks.networkparser.list.SimpleSet;
import java.util.Collection;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.ext.story.Board;
import de.uniks.networkparser.ext.story.Line;
import de.uniks.networkparser.ext.story.Task;
import de.uniks.networkparser.list.ObjectSet;
import java.util.Collections;

public class LineSet extends SimpleSet<Line>
{

   public Class<?> getTypClass()
   {
      return Line.class;
   }

   public LineSet()
   {
      // empty
   }

   public LineSet(Line... objects)
   {
      for (Line obj : objects)
      {
         this.add(obj);
      }
   }

   public LineSet(Collection<Line> objects)
   {
      this.addAll(objects);
   }
		public static final LineSet EMPTY_SET = new LineSet().withFlag(LineSet.READONLY);

   public String getEntryType()
   {
      return "de.uniks.simplescrum.model.Line";
   }
   @Override   public LineSet getNewList(boolean keyValue)
   {
      return new LineSet();
   }

   @SuppressWarnings("unchecked")
   public LineSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Line>)value);
      }
      else if (value != null)
      {
         this.add((Line) value);
      }
      return this;
   }

   public StringList getCaption()
   {
      StringList result = new StringList();
      for (Line obj : this)
      {
         result.add(obj.getCaption());
      }
      return result;
   }

   public LineSet filterCaption(String value)
   {
      LineSet result = new LineSet();
      for(Line obj : this)
      {
         if (value == obj.getCaption())
         {
            result.add(obj);
         }
      }
      return result;
   }

   public LineSet withCaption(String value) {
      for (Line obj : this)
      {
         obj.setCaption(value);
      }
      return this;
   }
   public LineSet getChildren()
   {
      LineSet result = new LineSet();
      for (Line obj : this)
      {
         result.with(obj.getChildren());
      }
      return result;
   }

   public LineSet filterChildren(Object value)
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
      LineSet answer = new LineSet();
      for (Line obj : this)
      {
         if (! Collections.disjoint(neighbors, obj.getChildren()))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public LineSet withChildren(Task value)
   {
      for (Line obj : this)
      {
         obj.withChildren(value);
      }
      return this;
   }
   public LineSet getOwner()
   {
      LineSet result = new LineSet();
      for (Line obj : this)
      {
         result.with(obj.getOwner());
      }
      return result;
   }

   public LineSet filterOwner(Object value)
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
      LineSet answer = new LineSet();
      for (Line obj : this)
      {
         if (neighbors.contains(obj.getOwner()) || (neighbors.isEmpty() && obj.getOwner() == null))
         {
            answer.add(obj);
         }
      }
      return answer;
   }

   public LineSet withOwner(Board value)
   {
      for (Line obj : this)
      {
         obj.withOwner(value);
      }
      return this;
   }
}