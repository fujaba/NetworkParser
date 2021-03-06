/*
   Copyright (c) 2016 zuendorf
   
   Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
   and associated documentation files (the "Software"), to deal in the Software without restriction, 
   including without limitation the rights to use, copy, modify, merge, publish, distribute, 
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
   furnished to do so, subject to the following conditions: 
   
   The above copyright notice and this permission notice shall be included in all copies or 
   substantial portions of the Software. 
   
   The Software shall be used for Good, not Evil. 
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
   
package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashSet;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.interfaces.SendableEntity;
   /**
    * 
    * @see <a href='../../../../../../../../../src/test/java/org/sdmlib/test/examples/studyrightWithAssignments/StudyRightWithAssignmentsModel.java'>StudyRightWithAssignmentsModel.java</a>
 * @see <a href='../../../../../../../../../src/test/java/org/sdmlib/test/examples/studyrightWithAssignments/StudyRightWithAssignmentsStoryboards.java'>StudyRightWithAssignmentsStoryboards.java</a>
 */
   public  class Assignment implements SendableEntity
{

   
   //==========================================================================
   
   protected PropertyChangeSupport listeners = null;
   
   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (listeners != null) {
   		listeners.firePropertyChange(propertyName, oldValue, newValue);
   		return true;
   	}
   	return false;
   }
   
   public boolean addPropertyChangeListener(PropertyChangeListener listener) 
   {
   	if (listeners == null) {
   		listeners = new PropertyChangeSupport(this);
   	}
   	listeners.addPropertyChangeListener(listener);
   	return true;
   }
   
   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
   	if (listeners == null) {
   		listeners = new PropertyChangeSupport(this);
   	}
   	listeners.addPropertyChangeListener(propertyName, listener);
   	return true;
   }
   
   public boolean removePropertyChangeListener(PropertyChangeListener listener) {
   	if (listeners == null) {
   		listeners.removePropertyChangeListener(listener);
   	}
   	listeners.removePropertyChangeListener(listener);
   	return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener) {
   	if (listeners != null) {
   		listeners.removePropertyChangeListener(propertyName, listener);
   	}
   	return true;
   }

   
   //==========================================================================
   
   
   public void removeYou()
   {
      withoutStudents(this.getStudents().toArray(new Student[this.getStudents().size()]));
      firePropertyChange("REMOVE_YOU", this, null);
   }

   
   //==========================================================================
   
   public static final String PROPERTY_CONTENT = "content";
   
   private String content;

   public String getContent()
   {
      return this.content;
   }
   
   public void setContent(String value)
   {
      if ( ! EntityUtil.stringEquals(this.content, value)) {
      
         String oldValue = this.content;
         this.content = value;
         this.firePropertyChange(PROPERTY_CONTENT, oldValue, value);
      }
   }
   
   public Assignment withContent(String value)
   {
      setContent(value);
      return this;
   } 


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();
      
      result.append(" ").append(this.getContent());
      result.append(" ").append(this.getPoints());
      return result.substring(1);
   }


   
   //==========================================================================
   
   public static final String PROPERTY_POINTS = "points";
   
   private int points;

   public int getPoints()
   {
      return this.points;
   }
   
   public void setPoints(int value)
   {
      if (this.points != value) {
      
         int oldValue = this.points;
         this.points = value;
         this.firePropertyChange(PROPERTY_POINTS, oldValue, value);
      }
   }
   
   public Assignment withPoints(int value)
   {
      setPoints(value);
      return this;
   } 

   
   /********************************************************************
    * <pre>
    *              many                       many
    * Assignment ----------------------------------- Student
    *              done                   students
    * </pre>
    */
   
   public static final String PROPERTY_STUDENTS = "students";

   private LinkedHashSet<Student> students = null;
   
   public LinkedHashSet<Student> getStudents()
   {
      if (this.students == null)
      {
         return new LinkedHashSet<Student>();
      }
   
      return this.students;
   }

   public Assignment withStudents(Student... value)
   {
      if(value==null){
         return this;
      }
      for (Student item : value)
      {
         if (item != null)
         {
            if (this.students == null)
            {
               this.students = new LinkedHashSet<Student>();
            }
            
            boolean changed = this.students.add (item);

            if (changed)
            {
               firePropertyChange(PROPERTY_STUDENTS, null, item);
            }
         }
      }
      return this;
   } 

   public Assignment withoutStudents(Student... value)
   {
      for (Student item : value)
      {
         if ((this.students != null) && (item != null))
         {
            if (this.students.remove(item))
            {
               firePropertyChange(PROPERTY_STUDENTS, item, null);
            }
         }
      }
      return this;
   }

   public Student createStudents()
   {
      Student value = new Student();
      withStudents(value);
      return value;
   } 
}
