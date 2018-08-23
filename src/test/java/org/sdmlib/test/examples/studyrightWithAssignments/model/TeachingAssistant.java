package org.sdmlib.test.examples.studyrightWithAssignments.model;
import de.uniks.networkparser.list.SimpleKeyValueList;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Room;


public class TeachingAssistant extends Student
{

   private SimpleKeyValueList<String, Object> dynamicValues=new SimpleKeyValueList<String, Object>();
   public Object getDynamicValue(String key) {
      return this.dynamicValues.getValue(key);
   }
   public TeachingAssistant withDynamicValue(String key, Object value) {
      this.dynamicValues.put(key, value);
      return this;
   }
   public Object[][] getDynamicValues() {
      return this.dynamicValues.toTable();
   }
   public static final String PROPERTY_CERTIFIED = "certified";

   private boolean certified;

   public boolean isCertified()
   {
      return this.certified;
   }

   public void setCertified(boolean value)
   {
      if (this.certified != value)
      {         boolean oldValue = this.certified;
         this.certified = value;
         firePropertyChange(PROPERTY_CERTIFIED, oldValue, value);
      }
   }

   public TeachingAssistant withCertified(boolean value)
   {
      setCertified(value);
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
            oldValue.withoutTas(this);
         }
         this.room = value;
         if (value != null) {
            value.withTas(this);
         }
         firePropertyChange(PROPERTY_ROOM, oldValue, value);
         changed = true;
      }
      return changed;
   }

   public TeachingAssistant withRoom(Room value)
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