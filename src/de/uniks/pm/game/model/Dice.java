package de.uniks.pm.game.model;

import de.uniks.pm.game.model.Game;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.uniks.networkparser.interfaces.SendableEntity;

public class Dice implements SendableEntity
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
      setGame(null);
      firePropertyChange("REMOVE_YOU", this, null);
   }

   public static final String PROPERTY_VALUE = "value";

   private int value = 0;

   public int getValue()
   {
      return this.value;
   }

   public void setValue(int value)
   {
      if (this.value != value)
      {
         int oldValue = this.value;
         this.value = value;
         firePropertyChange(PROPERTY_VALUE, oldValue, value);
      }
   }

   public Dice withValue(int value)
   {
      setValue(value);
      return this;
   }

   public static final String PROPERTY_GAME = "game";

   private Game game = null;

   public Game getGame()
   {
      return this.game;
   }

   public boolean setGame(Game value)
   {
      boolean changed = false;
      if (this.game != value) {
         Game oldValue = this.game;
         if (this.game != null) {
            this.game = null;
            oldValue.setDice(null);
         }
         this.game = value;
         if (value != null) {
            value.withDice(this);
         }
         firePropertyChange(PROPERTY_GAME, oldValue, value);
         changed = true;
      }
      return changed;
   }

   public Dice withGame(Game value)
   {
      this.setGame(value);
      return this;
   }

}