package de.uniks.pm.game.model;

import de.uniks.pm.game.model.ZombieOwner;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.uniks.networkparser.interfaces.SendableEntity;

public class Zombie implements SendableEntity
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
      setZombieOwner(null);
      firePropertyChange("REMOVE_YOU", this, null);
   }

   public static final String PROPERTY_AP = "ap";

   private int ap = 0;

   public int getAp()
   {
      return this.ap;
   }

   public void setAp(int value)
   {
      if (this.ap != value)
      {
         int oldValue = this.ap;
         this.ap = value;
         firePropertyChange(PROPERTY_AP, oldValue, value);
      }
   }

   public Zombie withAp(int value)
   {
      setAp(value);
      return this;
   }

   public static final String PROPERTY_HP = "hp";

   private int hp = 0;

   public int getHp()
   {
      return this.hp;
   }

   public void setHp(int value)
   {
      if (this.hp != value)
      {
         int oldValue = this.hp;
         this.hp = value;
         firePropertyChange(PROPERTY_HP, oldValue, value);
      }
   }

   public Zombie withHp(int value)
   {
      setHp(value);
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

   public Zombie withName(String value)
   {
      setName(value);
      return this;
   }

   public static final String PROPERTY_ZOMBIEOWNER = "zombieOwner";

   private ZombieOwner zombieOwner = null;

   public ZombieOwner getZombieOwner()
   {
      return this.zombieOwner;
   }

   public boolean setZombieOwner(ZombieOwner value)
   {
      boolean changed = false;
      if (this.zombieOwner != value) {
         ZombieOwner oldValue = this.zombieOwner;
         if (this.zombieOwner != null) {
            this.zombieOwner = null;
            oldValue.withoutZombies(this);
         }
         this.zombieOwner = value;
         if (value != null) {
            value.withZombies(this);
         }
         firePropertyChange(PROPERTY_ZOMBIEOWNER, oldValue, value);
         changed = true;
      }
      return changed;
   }

   public Zombie withZombieOwner(ZombieOwner value)
   {
      this.setZombieOwner(value);
      return this;
   }

}