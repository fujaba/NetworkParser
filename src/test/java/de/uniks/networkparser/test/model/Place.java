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

public class Place extends NamedElement
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

      if (PROPERTY_POSTT.equalsIgnoreCase(attrName))
      {
         return getPostt();
      }

      if (PROPERTY_PRET.equalsIgnoreCase(attrName))
      {
         return getPret();
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

      if (PROPERTY_POSTT.equalsIgnoreCase(attrName))
      {
         addToPostt((Transition) value);
         return true;
      }
      
      if ((PROPERTY_POSTT + JsonIdMap.REMOVE).equalsIgnoreCase(attrName))
      {
         removeFromPostt((Transition) value);
         return true;
      }

      if (PROPERTY_PRET.equalsIgnoreCase(attrName))
      {
         addToPret((Transition) value);
         return true;
      }
      
      if ((PROPERTY_PRET + JsonIdMap.REMOVE).equalsIgnoreCase(attrName))
      {
         removeFromPret((Transition) value);
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
      setCnet(null);
      removeAllFromPostt();
      removeAllFromPret();
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
    * Place ----------------------------------- Net
    *              places                   cnet
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
            oldValue.withoutPlaces(this);
         }
         
         this.cnet = value;
         
         if (value != null)
         {
            value.withPlaces(this);
         }
         
         getPropertyChangeSupport().firePropertyChange(PROPERTY_CNET, oldValue, value);
         changed = true;
      }
      
      return changed;
   }
   
   public Place withCnet(Net value)
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
    * Place ----------------------------------- Transition
    *              prep                   postt
    * </pre>
    */
   
   public static final String PROPERTY_POSTT = "postt";
   
   private LinkedHashSet<Transition> postt = null;
   
   public LinkedHashSet<Transition> getPostt()
   {
      if (this.postt == null)
      {
         return new LinkedHashSet<Transition>();
      }
   
      return this.postt;
   }
   
   public boolean addToPostt(Transition value)
   {
      boolean changed = false;
      
      if (value != null)
      {
         if (this.postt == null)
         {
            this.postt = new LinkedHashSet<Transition>();
         }
         
         changed = this.postt.add (value);
         
         if (changed)
         {
            value.withPrep(this);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_POSTT, null, value);
         }
      }
         
      return changed;   
   }
   
   public boolean removeFromPostt(Transition value)
   {
      boolean changed = false;
      
      if ((this.postt != null) && (value != null))
      {
         changed = this.postt.remove (value);
         
         if (changed)
         {
            value.withoutPrep(this);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_POSTT, value, null);
         }
      }
         
      return changed;   
   }
   
   public Place withPostt(Transition value)
   {
      addToPostt(value);
      return this;
   } 
   
   public Place withoutPostt(Transition value)
   {
      removeFromPostt(value);
      return this;
   } 
   
   public void removeAllFromPostt()
   {
      LinkedHashSet<Transition> tmpSet = new LinkedHashSet<Transition>(this.getPostt());
   
      for (Transition value : tmpSet)
      {
         this.removeFromPostt(value);
      }
   }
   
   public Transition createPostt()
   {
      Transition value = new Transition();
      withPostt(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              many                       many
    * Place ----------------------------------- Transition
    *              postp                   pret
    * </pre>
    */
   
   public static final String PROPERTY_PRET = "pret";
   
   private LinkedHashSet<Transition> pret = null;
   
   public LinkedHashSet<Transition> getPret()
   {
      if (this.pret == null)
      {
         return new LinkedHashSet<Transition>();
      }
   
      return this.pret;
   }
   
   public boolean addToPret(Transition value)
   {
      boolean changed = false;
      
      if (value != null)
      {
         if (this.pret == null)
         {
            this.pret = new LinkedHashSet<Transition>();
         }
         
         changed = this.pret.add (value);
         
         if (changed)
         {
            value.withPostp(this);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_PRET, null, value);
         }
      }
         
      return changed;   
   }
   
   public boolean removeFromPret(Transition value)
   {
      boolean changed = false;
      
      if ((this.pret != null) && (value != null))
      {
         changed = this.pret.remove (value);
         
         if (changed)
         {
            value.withoutPostp(this);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_PRET, value, null);
         }
      }
         
      return changed;   
   }
   
   public Place withPret(Transition value)
   {
      addToPret(value);
      return this;
   } 
   
   public Place withoutPret(Transition value)
   {
      removeFromPret(value);
      return this;
   } 
   
   public void removeAllFromPret()
   {
      LinkedHashSet<Transition> tmpSet = new LinkedHashSet<Transition>(this.getPret());
   
      for (Transition value : tmpSet)
      {
         this.removeFromPret(value);
      }
   }
   
   public Transition createPret()
   {
      Transition value = new Transition();
      withPret(value);
      return value;
   } 
}

