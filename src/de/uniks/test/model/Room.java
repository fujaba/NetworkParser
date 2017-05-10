package de.uniks.test.model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.uniks.test.model.Person;
import de.uniks.test.model.util.PersonSet;


public class Room
{
   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
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

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (listeners == null) {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners == null) {
         listeners.removePropertyChangeListener(listener);
      }
      listeners.removePropertyChangeListener(listener);
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (listeners != null) {
         listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }
   public static final String PROPERTY_PERSONS = "persons";

   private PersonSet persons = null;

   public PersonSet getPersons()
   {
      return this.persons;
   }

   public Room withPersons(Person... value)
   {
      if (value == null) {
         return this;
      }
      for (Person item : value) {
         if (item != null) {
            if (this.persons == null) {
               this.persons = new PersonSet();
            }
            boolean changed = this.persons.add(item);
            if (changed)
            {
               item.withRoom(this);
               firePropertyChange(PROPERTY_PERSONS, null, item);
            }
         }
      }
      return this;
   }

   public Room withoutPersons(Person... value)
   {
      for (Person item : value) {
         if (this.persons != null && item != null) {
            if (this.persons.remove(item)) {
               item.withRoom(null);
            }
         }
      }
      return this;
   }

   public Person createPersons()
   {
      Person value = new Person();
      withPersons(value);
      return value;
   }
}