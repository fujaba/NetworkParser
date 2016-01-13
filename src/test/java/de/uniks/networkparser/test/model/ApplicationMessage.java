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

public class ApplicationMessage 
{

   
   //==========================================================================
   
   public Object get(String attrName)
   {
	  if (PROPERTY_FIXMLMESSAGE.equalsIgnoreCase(attrName))
	  {
		 return getFixmlmessage();
	  }

	  if (PROPERTY_ORDER.equalsIgnoreCase(attrName))
	  {
//		 return getOrder();
	  }

	  return null;
   }

   
   //==========================================================================
   
   public boolean set(String attrName, Object value)
   {
	  if (PROPERTY_FIXMLMESSAGE.equalsIgnoreCase(attrName))
	  {
		 setFixmlmessage((FIXMLMessage) value);
		 return true;
	  }

	  if (PROPERTY_ORDER.equalsIgnoreCase(attrName))
	  {
//		 setOrder((Order) value);
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
	  setFixmlmessage(null);
//	  setOrder(null);
	  getPropertyChangeSupport().firePropertyChange("REMOVE_YOU", this, null);
   }

   
   /********************************************************************
	* <pre>
	*			  one					   one
	* ApplicationMessage ----------------------------------- FIXMLMessage
	*			  applicationmessage				   fixmlmessage
	* </pre>
	*/
   
   public static final String PROPERTY_FIXMLMESSAGE = "fixmlmessage";

   private FIXMLMessage fixmlmessage = null;

   public FIXMLMessage getFixmlmessage()
   {
	  return this.fixmlmessage;
   }

   public boolean setFixmlmessage(FIXMLMessage value)
   {
	  boolean changed = false;
	  
	  if (this.fixmlmessage != value)
	  {
		 FIXMLMessage oldValue = this.fixmlmessage;
		 
		 if (this.fixmlmessage != null)
		 {
			this.fixmlmessage = null;
			oldValue.setApplicationmessage(null);
		 }
		 
		 this.fixmlmessage = value;
		 
		 if (value != null)
		 {
			value.withApplicationmessage(this);
		 }
		 
		 getPropertyChangeSupport().firePropertyChange(PROPERTY_FIXMLMESSAGE, oldValue, value);
		 changed = true;
	  }
	  
	  return changed;
   }

   public ApplicationMessage withFixmlmessage(FIXMLMessage value)
   {
	  setFixmlmessage(value);
	  return this;
   } 

   public FIXMLMessage createFixmlmessage()
   {
	  FIXMLMessage value = new FIXMLMessage();
	  withFixmlmessage(value);
	  return value;
   } 

   
   /********************************************************************
	* <pre>
	*			  one					   one
	* ApplicationMessage ----------------------------------- Order
	*			  applicationmessage				   order
	* </pre>
	*/
   
   public static final String PROPERTY_ORDER = "order";

//   private Order order = null;
//
//   public Order getOrder()
//   {
//	  return this.order;
//   }
//
//   public boolean setOrder(Order value)
//   {
//	  boolean changed = false;
//	  
//	  if (this.order != value)
//	  {
//		 Order oldValue = this.order;
//		 
//		 if (this.order != null)
//		 {
//			this.order = null;
//			oldValue.setApplicationmessage(null);
//		 }
//		 
//		 this.order = value;
//		 
//		 if (value != null)
//		 {
//			value.withApplicationmessage(this);
//		 }
//		 
//		 getPropertyChangeSupport().firePropertyChange(PROPERTY_ORDER, oldValue, value);
//		 changed = true;
//	  }
//	  
//	  return changed;
//   }
//
//   public ApplicationMessage withOrder(Order value)
//   {
//	  setOrder(value);
//	  return this;
//   } 
//
//   public Order createOrder()
//   {
//	  Order value = new Order();
//	  withOrder(value);
//	  return value;
//   } 
}

