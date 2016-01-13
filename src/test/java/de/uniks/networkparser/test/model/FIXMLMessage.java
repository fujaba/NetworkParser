/*
   Copyright (c) 2014 zuendorf 
   
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class FIXMLMessage 
{

   
   //==========================================================================
   
   public Object get(String attrName)
   {
	  if (PROPERTY_APPLICATIONMESSAGE.equalsIgnoreCase(attrName))
	  {
		 return getApplicationmessage();
	  }

	  return null;
   }

   
   //==========================================================================
   
   public boolean set(String attrName, Object value)
   {
	  if (PROPERTY_APPLICATIONMESSAGE.equalsIgnoreCase(attrName))
	  {
		 setApplicationmessage((ApplicationMessage) value);
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
   
   public void addPropertyChangeListener(PropertyChangeListener listener) 
   {
	  getPropertyChangeSupport().addPropertyChangeListener(listener);
   }

   
   //==========================================================================
   
   public void removeYou()
   {
	  setApplicationmessage(null);
	  getPropertyChangeSupport().firePropertyChange("REMOVE_YOU", this, null);
   }

   
   /********************************************************************
	* <pre>
	*			  one					   one
	* FIXMLMessage ----------------------------------- ApplicationMessage
	*			  fixmlmessage				   applicationmessage
	* </pre>
	*/
   
   public static final String PROPERTY_APPLICATIONMESSAGE = "applicationmessage";

   private ApplicationMessage applicationmessage = null;

   public ApplicationMessage getApplicationmessage()
   {
	  return this.applicationmessage;
   }

   public boolean setApplicationmessage(ApplicationMessage value)
   {
	  boolean changed = false;
	  
	  if (this.applicationmessage != value)
	  {
		 ApplicationMessage oldValue = this.applicationmessage;
		 
		 if (this.applicationmessage != null)
		 {
			this.applicationmessage = null;
			oldValue.setFixmlmessage(null);
		 }
		 
		 this.applicationmessage = value;
		 
		 if (value != null)
		 {
			value.withFixmlmessage(this);
		 }
		 
		 getPropertyChangeSupport().firePropertyChange(PROPERTY_APPLICATIONMESSAGE, oldValue, value);
		 changed = true;
	  }
	  
	  return changed;
   }

   public FIXMLMessage withApplicationmessage(ApplicationMessage value)
   {
	  setApplicationmessage(value);
	  return this;
   } 

   public ApplicationMessage createApplicationmessage()
   {
	  ApplicationMessage value = new ApplicationMessage();
	  withApplicationmessage(value);
	  return value;
   } 
}

