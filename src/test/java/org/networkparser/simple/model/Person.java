package org.networkparser.simple.model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.uniks.networkparser.list.SimpleKeyValueList;
import org.networkparser.simple.model.Person;
import org.networkparser.simple.model.Room;


public class Person
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
   public Person withDynamicValue(String key, Object value) {
      this.dynamicValues.put(key, value);
      return this;
   }
   public Object[][] getDynamicValues() {
      return this.dynamicValues.toTable();
   }
   public static final String PROPERTY_AGE = "age";

   private int age;

   public int getAge()
   {
      return this.age;
   }

   public void setAge(int value)
   {
      if (this.age != value)
      {         int oldValue = this.age;
         this.age = value;
         firePropertyChange(PROPERTY_AGE, oldValue, value);
      }
   }

   public Person withAge(int value)
   {
      setAge(value);
      return this;
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

   public Person withName(String value)
   {
      setName(value);
      return this;
   }


   public static final String PROPERTY_ROOM = "room";

   private Room room = null;

   public Room getRoom()
   {
      return this.room;
   }

   public boolean setRoom(Room value)
   {
      boolean changed = false;
      if (this.room != value) {
         Room oldValue = this.room;
         if (this.room != null) {
            this.room = null;
            oldValue.withoutPersons(this);
         }
         this.room = value;
         if (value != null) {
            value.withPersons(this);
         }
         firePropertyChange(PROPERTY_ROOM, oldValue, value);
         changed = true;
      }
      return changed;
   }

   public Person withRoom(Room value)
   {
      this.setRoom(value);
      return this;
   }

   public Room createRoom()
   {
      Room value = new Room();
      withRoom(value);
      return value;
   }
}