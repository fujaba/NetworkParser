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

public class Transition extends NamedElement 
{

   
   //==========================================================================
   
   @Override
public Object get(String attrName)
   {
      if (PROPERTY_NAME.equalsIgnoreCase(attrName))
      {
         return getName();
      }

      if (PROPERTY_CNET.equalsIgnoreCase(attrName))
      {
         return getCnet();
      }

      if (PROPERTY_PREP.equalsIgnoreCase(attrName))
      {
         return getPrep();
      }

      if (PROPERTY_POSTP.equalsIgnoreCase(attrName))
      {
         return getPostp();
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
      if (PROPERTY_NAME.equalsIgnoreCase(attrName))
      {
         setName((String) value);
         return true;
      }

      if (PROPERTY_CNET.equalsIgnoreCase(attrName))
      {
         setCnet((Net) value);
         return true;
      }

      if (PROPERTY_PREP.equalsIgnoreCase(attrName))
      {
         addToPrep((Place) value);
         return true;
      }
      
      if ((PROPERTY_PREP + JsonIdMap.REMOVE).equalsIgnoreCase(attrName))
      {
         removeFromPrep((Place) value);
         return true;
      }

      if (PROPERTY_POSTP.equalsIgnoreCase(attrName))
      {
         addToPostp((Place) value);
         return true;
      }
      
      if ((PROPERTY_POSTP + JsonIdMap.REMOVE).equalsIgnoreCase(attrName))
      {
         removeFromPostp((Place) value);
         return true;
      }

      if (PROPERTY_OTHER.equalsIgnoreCase(attrName))
      {
         setOther((M2MElement) value);
         return true;
      }

      return false;
   }

   
   @Override
public void removeYou()
   {
      setCnet(null);
      removeAllFromPrep();
      removeAllFromPostp();
      setOther(null);
      getPropertyChangeSupport().firePropertyChange("REMOVE_YOU", this, null);
      super.removeYou();
   }

   @Override
public String toString()
   {
      StringBuilder result = new StringBuilder();
      
      result.append(" ").append(this.getName());
      return result.substring(1);
   }

   /********************************************************************
    * <pre>
    *              many                       one
    * Transition ----------------------------------- Net
    *              transitions                   cnet
    * </pre>
    */
   
   public static final String PROPERTY_CNET = "cnet";
   
   private Net cnet = null;
   
   public Net getCnet()
   {
      return this.cnet;
   }
   
   public boolean setCnet(Net value)
   {
      boolean changed = false;
      
      if (this.cnet != value)
      {
         Net oldValue = this.cnet;
         
         if (this.cnet != null)
         {
            this.cnet = null;
            oldValue.withoutTransitions(this);
         }
         
         this.cnet = value;
         
         if (value != null)
         {
            value.withTransitions(this);
         }
         
         getPropertyChangeSupport().firePropertyChange(PROPERTY_CNET, oldValue, value);
         changed = true;
      }
      
      return changed;
   }
   
   public Transition withCnet(Net value)
   {
      setCnet(value);
      return this;
   } 
   
   public Net createCnet()
   {
      Net value = new Net();
      withCnet(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              many                       many
    * Transition ----------------------------------- Place
    *              postt                   prep
    * </pre>
    */
   
   public static final String PROPERTY_PREP = "prep";
   
   private LinkedHashSet<Place> prep = null;
   
   public LinkedHashSet<Place> getPrep()
   {
      if (this.prep == null)
      {
         return new LinkedHashSet<Place>();
      }
   
      return this.prep;
   }
   
   public boolean addToPrep(Place value)
   {
      boolean changed = false;
      
      if (value != null)
      {
         if (this.prep == null)
         {
            this.prep = new LinkedHashSet<Place>();
         }
         
         changed = this.prep.add (value);
         
         if (changed)
         {
            value.withPostt(this);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_PREP, null, value);
         }
      }
         
      return changed;   
   }
   
   public boolean removeFromPrep(Place value)
   {
      boolean changed = false;
      
      if ((this.prep != null) && (value != null))
      {
         changed = this.prep.remove (value);
         
         if (changed)
         {
            value.withoutPostt(this);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_PREP, value, null);
         }
      }
         
      return changed;   
   }
   
   public Transition withPrep(Place value)
   {
      addToPrep(value);
      return this;
   } 
   
   public Transition withoutPrep(Place value)
   {
      removeFromPrep(value);
      return this;
   } 
   
   public void removeAllFromPrep()
   {
      LinkedHashSet<Place> tmpSet = new LinkedHashSet<Place>(this.getPrep());
   
      for (Place value : tmpSet)
      {
         this.removeFromPrep(value);
      }
   }
   
   public Place createPrep()
   {
      Place value = new Place();
      withPrep(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              many                       many
    * Transition ----------------------------------- Place
    *              pret                   postp
    * </pre>
    */
   
   public static final String PROPERTY_POSTP = "postp";
   
   private LinkedHashSet<Place> postp = null;
   
   public LinkedHashSet<Place> getPostp()
   {
      if (this.postp == null)
      {
         return new LinkedHashSet<Place>();
      }
   
      return this.postp;
   }
   
   public boolean addToPostp(Place value)
   {
      boolean changed = false;
      
      if (value != null)
      {
         if (this.postp == null)
         {
            this.postp = new LinkedHashSet<Place>();
         }
         
         changed = this.postp.add (value);
         
         if (changed)
         {
            value.withPret(this);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_POSTP, null, value);
         }
      }
         
      return changed;   
   }
   
   public boolean removeFromPostp(Place value)
   {
      boolean changed = false;
      
      if ((this.postp != null) && (value != null))
      {
         changed = this.postp.remove (value);
         
         if (changed)
         {
            value.withoutPret(this);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_POSTP, value, null);
         }
      }
         
      return changed;   
   }
   
   public Transition withPostp(Place value)
   {
      addToPostp(value);
      return this;
   } 
   
   public Transition withoutPostp(Place value)
   {
      removeFromPostp(value);
      return this;
   } 
   
   public void removeAllFromPostp()
   {
      LinkedHashSet<Place> tmpSet = new LinkedHashSet<Place>(this.getPostp());
   
      for (Place value : tmpSet)
      {
         this.removeFromPostp(value);
      }
   }
   
   public Place createPostp()
   {
      Place value = new Place();
      withPostp(value);
      return value;
   } 
}

