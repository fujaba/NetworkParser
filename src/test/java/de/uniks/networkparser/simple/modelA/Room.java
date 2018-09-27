package de.uniks.networkparser.simple.modelA;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.simple.modelA.util.PersonSet;

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
      if (listeners != null) {
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
   private SimpleKeyValueList<String, Object> dynamicValues=new SimpleKeyValueList<String, Object>();
   public Object getDynamicValue(String key) {
      return this.dynamicValues.getValue(key);
   }
   public Room withDynamicValue(String key, Object value) {
      this.dynamicValues.put(key, value);
      return this;
   }
   public Object[][] getDynamicValues() {
      return this.dynamicValues.toTable();
   }
   public static final String PROPERTY_NAME = "name";

   private String name;

   public String getName()
   {
      return this.name;
   }

   public void setName(String value)
   {
      if (this.name != value)
      {         String oldValue = this.name;
         this.name = value;
         firePropertyChange(PROPERTY_NAME, oldValue, value);
      }
   }

   public Room withName(String value)
   {
      setName(value);
      return this;
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
            this.persons.withVisible(true);
            boolean changed = this.persons.add(item);
            this.persons.withVisible(false);
            if (changed)
            {
               item.setRoom(this);
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
   public void init()    {
      
    }


}