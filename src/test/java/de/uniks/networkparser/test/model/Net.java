/*
   Copyright (c) 2013 zuendorf 
   
   Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
   and associated documentation files (the "Software"), to deal in the Software without restriction, 
   including without limitation the rights to use, copy, modify, merge, publish, distribute, 
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
   furnished to do so, subject to the following conditions: 
   
   The above copyright notice and this permission notice shall be included in all copies or 
   substantial portions of the Software. 
   
   The Software shall be used for Good, not Evil. 
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
   
package de.uniks.networkparser.test.model;

import java.util.LinkedHashSet;

import de.uniks.networkparser.json.JsonIdMap;

public class Net extends M2MElement
{

   
   //==========================================================================
   
   @Override
   public Object get(String attrName)
   {
      if (PROPERTY_PLACES.equalsIgnoreCase(attrName))
      {
         return getPlaces();
      }

      if (PROPERTY_TRANSITIONS.equalsIgnoreCase(attrName))
      {
         return getTransitions();
      }

      if (PROPERTY_OTHER.equalsIgnoreCase(attrName))
      {
         return getOther();
      }

      return null;
   }

   
   //==========================================================================
   
   @Override
   public boolean set(String attrName, Object value)
   {
      if (PROPERTY_PLACES.equalsIgnoreCase(attrName))
      {
         addToPlaces((Place) value);
         return true;
      }
      
      if ((PROPERTY_PLACES + JsonIdMap.REMOVE).equalsIgnoreCase(attrName))
      {
         removeFromPlaces((Place) value);
         return true;
      }

      if (PROPERTY_TRANSITIONS.equalsIgnoreCase(attrName))
      {
         addToTransitions((Transition) value);
         return true;
      }
      
      if ((PROPERTY_TRANSITIONS + JsonIdMap.REMOVE).equalsIgnoreCase(attrName))
      {
         removeFromTransitions((Transition) value);
         return true;
      }

      if (PROPERTY_OTHER.equalsIgnoreCase(attrName))
      {
         setOther((M2MElement) value);
         return true;
      }

      return false;
   }
   
   //==========================================================================
   
   @Override
   public void removeYou()
   {
      removeAllFromPlaces();
      removeAllFromTransitions();
      setOther(null);
      getPropertyChangeSupport().firePropertyChange("REMOVE_YOU", this, null);
      super.removeYou();
   }

   
   /********************************************************************
    * <pre>
    *              one                       many
    * Net ----------------------------------- Place
    *              cnet                   places
    * </pre>
    */
   
   public static final String PROPERTY_PLACES = "places";
   
   private LinkedHashSet<Place> places = null;
   
   public LinkedHashSet<Place> getPlaces()
   {
      if (this.places == null)
      {
         return new LinkedHashSet<Place>();
      }
   
      return this.places;
   }
   
   public boolean addToPlaces(Place value)
   {
      boolean changed = false;
      
      if (value != null)
      {
         if (this.places == null)
         {
            this.places = new LinkedHashSet<Place>();
         }
         
         changed = this.places.add (value);
         
         if (changed)
         {
            value.withCnet(this);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_PLACES, null, value);
         }
      }
         
      return changed;   
   }
   
   public boolean removeFromPlaces(Place value)
   {
      boolean changed = false;
      
      if ((this.places != null) && (value != null))
      {
         changed = this.places.remove (value);
         
         if (changed)
         {
            value.setCnet(null);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_PLACES, value, null);
         }
      }
         
      return changed;   
   }
   
   public Net withPlaces(Place value)
   {
      addToPlaces(value);
      return this;
   } 
   
   public Net withoutPlaces(Place value)
   {
      removeFromPlaces(value);
      return this;
   } 
   
   public void removeAllFromPlaces()
   {
      LinkedHashSet<Place> tmpSet = new LinkedHashSet<Place>(this.getPlaces());
   
      for (Place value : tmpSet)
      {
         this.removeFromPlaces(value);
      }
   }
   
   public Place createPlaces()
   {
      Place value = new Place();
      withPlaces(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              one                       many
    * Net ----------------------------------- Transition
    *              cnet                   transitions
    * </pre>
    */
   
   public static final String PROPERTY_TRANSITIONS = "transitions";
   
   private LinkedHashSet<Transition> transitions = null;
   
   public LinkedHashSet<Transition> getTransitions()
   {
      if (this.transitions == null)
      {
         return new LinkedHashSet<Transition>();
      }
   
      return this.transitions;
   }
   
   public boolean addToTransitions(Transition value)
   {
      boolean changed = false;
      
      if (value != null)
      {
         if (this.transitions == null)
         {
            this.transitions = new LinkedHashSet<Transition>();
         }
         
         changed = this.transitions.add (value);
         
         if (changed)
         {
            value.withCnet(this);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_TRANSITIONS, null, value);
         }
      }
         
      return changed;   
   }
   
   public boolean removeFromTransitions(Transition value)
   {
      boolean changed = false;
      
      if ((this.transitions != null) && (value != null))
      {
         changed = this.transitions.remove (value);
         
         if (changed)
         {
            value.setCnet(null);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_TRANSITIONS, value, null);
         }
      }
         
      return changed;   
   }
   
   public Net withTransitions(Transition value)
   {
      addToTransitions(value);
      return this;
   } 
   
   public Net withoutTransitions(Transition value)
   {
      removeFromTransitions(value);
      return this;
   } 
   
   public void removeAllFromTransitions()
   {
      LinkedHashSet<Transition> tmpSet = new LinkedHashSet<Transition>(this.getTransitions());
   
      for (Transition value : tmpSet)
      {
         this.removeFromTransitions(value);
      }
   }
   
   public Transition createTransitions()
   {
      Transition value = new Transition();
      withTransitions(value);
      return value;
   } 
}

