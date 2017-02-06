package de.uniks.pm.game.model;

import de.uniks.pm.game.model.Trainer;
import de.uniks.pm.game.model.Zombie;
import de.uniks.pm.game.model.ZombieOwner;
import de.uniks.pm.game.model.util.TrainerSet;
import de.uniks.pm.game.model.util.ZombieSet;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.uniks.networkparser.interfaces.SendableEntity;

public class Ground implements ZombieOwner, SendableEntity
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
      withoutTrainers(this.getTrainers().toArray(new Trainer[this.getTrainers().size()]));
      withoutZombies(this.getZombies().toArray(new Zombie[this.getZombies().size()]));
      firePropertyChange("REMOVE_YOU", this, null);
   }

   public static final String PROPERTY_X = "x";

   private int x = 0;

   public int getX()
   {
      return this.x;
   }

   public void setX(int value)
   {
      if (this.x != value)
      {
         int oldValue = this.x;
         this.x = value;
         firePropertyChange(PROPERTY_X, oldValue, value);
      }
   }

   public Ground withX(int value)
   {
      setX(value);
      return this;
   }

   public static final String PROPERTY_Y = "y";

   private int y = 0;

   public int getY()
   {
      return this.y;
   }

   public void setY(int value)
   {
      if (this.y != value)
      {
         int oldValue = this.y;
         this.y = value;
         firePropertyChange(PROPERTY_Y, oldValue, value);
      }
   }

   public Ground withY(int value)
   {
      setY(value);
      return this;
   }

   public static final String PROPERTY_TRAINERS = "trainers";

   private TrainerSet trainers = null;

   public TrainerSet getTrainers()
   {
      return this.trainers;
   }

   public Ground withTrainers(Trainer... value)
   {
      if (value == null) {
         return this;
      }
      for (Trainer item : value) {
         if (item != null) {
            if (this.trainers == null) {
               this.trainers = new TrainerSet();
            }
            boolean changed = this.trainers.add(item);
            if (changed)
            {
               item.withGround(this);
               firePropertyChange(PROPERTY_TRAINERS, null, item);
            }
         }
      }
      return this;
   }

   public Ground withoutTrainers(Trainer... value)
   {
      for (Trainer item : value) {
         if (this.trainers != null && item != null) {
            if (this.trainers.remove(item)) {
               item.withoutGround(this);
            }
         }
      }
      return this;
   }

   public Trainer createTrainers()
   {
      Trainer value = new Trainer();
      withTrainers(value);
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

}