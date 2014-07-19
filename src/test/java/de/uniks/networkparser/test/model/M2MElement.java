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

import java.beans.PropertyChangeSupport;

public class M2MElement
{

   
   //==========================================================================
   
   public Object get(String attrName)
   {
      if (PROPERTY_OTHER.equalsIgnoreCase(attrName))
      {
         return getOther();
      }

      return null;
   }

   
   //==========================================================================
   
   public boolean set(String attrName, Object value)
   {
      if (PROPERTY_OTHER.equalsIgnoreCase(attrName))
      {
         setOther((M2MElement) value);
         return true;
      }

      return false;
   }

   
   //==========================================================================
   
   protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
   
   public PropertyChangeSupport getPropertyChangeSupport()
   {
      return listeners;
   }

   
   //==========================================================================
   
   public void removeYou()
   {
      setOther(null);
      getPropertyChangeSupport().firePropertyChange("REMOVE_YOU", this, null);
   }

   
   /********************************************************************
    * <pre>
    *              one                       one
    * M2MElement ----------------------------------- M2MElement
    *              other                   other
    * </pre>
    */
   
   public static final String PROPERTY_OTHER = "other";
   
   private M2MElement other = null;
   
   public M2MElement getOther()
   {
      return this.other;
   }
   
   public boolean setOther(M2MElement value)
   {
      boolean changed = false;
      
      if (this.other != value)
      {
         M2MElement oldValue = this.other;
         
         if (this.other != null)
         {
            this.other = null;
            oldValue.setOther(null);
         }
         
         this.other = value;
         
         if (value != null)
         {
            value.withOther(this);
         }
         
         getPropertyChangeSupport().firePropertyChange(PROPERTY_OTHER, oldValue, value);
         changed = true;
      }
      
      return changed;
   }
   
   public M2MElement withOther(M2MElement value)
   {
      setOther(value);
      return this;
   } 
   
   public M2MElement createOther()
   {
      M2MElement value = new M2MElement();
      withOther(value);
      return value;
   } 
}

