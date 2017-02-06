package de.uniks.pm.game.model;

import de.uniks.pm.game.model.Ground;
import de.uniks.pm.game.model.Trainer;
import de.uniks.pm.game.model.Trap;
import de.uniks.pm.game.model.Zombie;
import de.uniks.pm.game.model.ZombieOwner;
import de.uniks.pm.game.model.util.GroundSet;
import de.uniks.pm.game.model.util.ZombieSet;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.uniks.networkparser.interfaces.SendableEntity;

public class Trainer implements ZombieOwner, SendableEntity
{

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

   public void removeYou()
   {
      setPrev(null);
      withoutGround(this.getGround().toArray(new Ground[this.getGround().size()]));
      withoutZombies(this.getZombies().toArray(new Zombie[this.getZombies().size()]));
      firePropertyChange("REMOVE_YOU", this, null);
   }

   public static final String PROPERTY_COLOR = "color";

   private String color = null;

   public String getColor()
   {
      return this.color;
   }

   public void setColor(String value)
   {
      if (this.color != value)
      {
         String oldValue = this.color;
         this.color = value;
         firePropertyChange(PROPERTY_COLOR, oldValue, value);
      }
   }

   public Trainer withColor(String value)
   {
      setColor(value);
      return this;
   }

   public static final String PROPERTY_EXPERIENCE = "experience";

   private int experience = 0;

   public int getExperience()
   {
      return this.experience;
   }

   public void setExperience(int value)
   {
      if (this.experience != value)
      {
         int oldValue = this.experience;
         this.experience = value;
         firePropertyChange(PROPERTY_EXPERIENCE, oldValue, value);
      }
   }

   public Trainer withExperience(int value)
   {
      setExperience(value);
      return this;
   }

   public static final String PROPERTY_NAME = "name";

   private String name = null;

   public String getName()
   {
      return this.name;
   }

   public void setName(String value)
   {
      if (this.name != value)
      {
         String oldValue = this.name;
         this.name = value;
         firePropertyChange(PROPERTY_NAME, oldValue, value);
      }
   }

   public Trainer withName(String value)
   {
      setName(value);
      return this;
   }

   public static final String PROPERTY_PREV = "prev";

   private Trainer prev = null;

   public Trainer getPrev()
   {
      return this.prev;
   }

   public boolean setPrev(Trainer value)
   {
      boolean changed = false;
      if (this.prev != value) {
         Trainer oldValue = this.prev;
         if (this.prev != null) {
            this.prev = null;
            oldValue.setNext(null);
         }
         this.prev = value;
         if (value != null) {
            value.withNext(this);
         }
         firePropertyChange(PROPERTY_PREV, oldValue, value);
         changed = true;
      }
      return changed;
   }

   public Trainer withPrev(Trainer value)
   {
      this.setPrev(value);
      return this;
   }

   public static final String PROPERTY_NEXT = "next";

   private Trainer next = null;

   public Trainer getNext()
   {
      return this.next;
   }

   public boolean setNext(Trainer value)
   {
      boolean changed = false;
      if (this.next != value) {
         Trainer oldValue = this.next;
         if (this.next != null) {
            this.next = null;
            oldValue.setPrev(null);
         }
         this.next = value;
         if (value != null) {
            value.withPrev(this);
         }
         firePropertyChange(PROPERTY_NEXT, oldValue, value);
         changed = true;
      }
      return changed;
   }

   public Trainer withNext(Trainer value)
   {
      this.setNext(value);
      return this;
   }

   public static final String PROPERTY_GROUND = "ground";

   private GroundSet ground = null;

   public GroundSet getGround()
   {
      return this.ground;
   }

   public Trainer withGround(Ground... value)
   {
      if (value == null) {
         return this;
      }
      for (Ground item : value) {
         if (item != null) {
            if (this.ground == null) {
               this.ground = new GroundSet();
            }
            boolean changed = this.ground.add(item);
            if (changed)
            {
               item.withTrainers(this);
               firePropertyChange(PROPERTY_GROUND, null, item);
            }
         }
      }
      return this;
   }

   public Trainer withoutGround(Ground... value)
   {
      for (Ground item : value) {
         if (this.ground != null && item != null) {
            if (this.ground.remove(item)) {
               item.withoutTrainers(this);
            }
         }
      }
      return this;
   }

   public Ground createGround()
   {
      Ground value = new Ground();
      withGround(value);
      return value;
   }

   public static final String PROPERTY_ZOMBIES = "zombies";

   private ZombieSet zombies = null;

   public ZombieSet getZombies()
   {
      return this.zombies;
   }

   public ZombieOwner withZombies(Zombie... value)
   {
      if (value == null) {
         return this;
      }
      for (Zombie item : value) {
         if (item != null) {
            if (this.zombies == null) {
               this.zombies = new ZombieSet();
            }
            boolean changed = this.zombies.add(item);
            if (changed)
            {
               item.withZombieOwner(this);
               firePropertyChange(PROPERTY_ZOMBIES, null, item);
            }
         }
      }
      return this;
   }

   public ZombieOwner withoutZombies(Zombie... value)
   {
      for (Zombie item : value) {
         if (this.zombies != null && item != null) {
            if (this.zombies.remove(item)) {
               item.setZombieOwner(null);
            }
         }
      }
      return this;
   }

   public Zombie createZombies()
   {
      Zombie value = new Zombie();
      withZombies(value);
      return value;
   }

   public void attackZombie( Zombie v0, Zombie v1 )
   {
      
   }

   public void catchZombie( Zombie v0, Trap v1 )
   {
      
   }

   public void movePlayer( Ground v0 )
   {
      
   }

}