package de.uniks.pm.game.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.uniks.networkparser.interfaces.SendableEntity;

public class Trap implements SendableEntity
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
      firePropertyChange("REMOVE_YOU", this, null);
   }

   public static final String PROPERTY_SUCCESSRATE = "successRate";

   private int successRate = 0;

   public int getSuccessRate()
   {
      return this.successRate;
   }

   public void setSuccessRate(int value)
   {
      if (this.successRate != value)
      {
         int oldValue = this.successRate;
         this.successRate = value;
         firePropertyChange(PROPERTY_SUCCESSRATE, oldValue, value);
      }
   }

   public Trap withSuccessRate(int value)
   {
      setSuccessRate(value);
      return this;
   }

}