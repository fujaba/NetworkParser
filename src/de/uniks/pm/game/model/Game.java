package de.uniks.pm.game.model;

import de.uniks.pm.game.model.Dice;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.uniks.networkparser.interfaces.SendableEntity;

public class Game implements SendableEntity
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
      setDice(null);
      firePropertyChange("REMOVE_YOU", this, null);
   }

   public static final String PROPERTY_ACTIONPOINTS = "actionPoints";

   private int actionPoints = 0;

   public int getActionPoints()
   {
      return this.actionPoints;
   }

   public void setActionPoints(int value)
   {
      if (this.actionPoints != value)
      {
         int oldValue = this.actionPoints;
         this.actionPoints = value;
         firePropertyChange(PROPERTY_ACTIONPOINTS, oldValue, value);
      }
   }

   public Game withActionPoints(int value)
   {
      setActionPoints(value);
      return this;
   }

   public static final String PROPERTY_DICE = "dice";

   private Dice dice = null;

   public Dice getDice()
   {
      return this.dice;
   }

   public boolean setDice(Dice value)
   {
      boolean changed = false;
      if (this.dice != value) {
         Dice oldValue = this.dice;
         if (this.dice != null) {
            this.dice = null;
            oldValue.setGame(null);
         }
         this.dice = value;
         if (value != null) {
            value.withGame(this);
         }
         firePropertyChange(PROPERTY_DICE, oldValue, value);
         changed = true;
      }
      return changed;
   }

   public Game withDice(Dice value)
   {
      this.setDice(value);
      return this;
   }

   public boolean checkEnd(  )
   {
      return false;
   }

}