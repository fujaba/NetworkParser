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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.LinkedHashSet;

import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.creator.ItemSet;
import de.uniks.networkparser.test.model.creator.PersonSet;

public class Person implements SendableEntity
{

   //==========================================================================
   
   public Object get(String attrName)
   {
      if (PROPERTY_NAME.equalsIgnoreCase(attrName))
      {
         return getName();
      }

      if (PROPERTY_BALANCE.equalsIgnoreCase(attrName))
      {
         return getBalance();
      }

      if (PROPERTY_PARENT.equalsIgnoreCase(attrName))
      {
         return getParent();
      }

      if (PROPERTY_ITEMS.equalsIgnoreCase(attrName))
      {
         return getItems();
      }
      if (PROPERTY_ACTIVE.equalsIgnoreCase(attrName))
      {
         return isActive();
      }
      if (PROPERTY_TITLE.equalsIgnoreCase(attrName))
      {
         return getTitle();
      }
      if ((PROPERTY_CREATED).equalsIgnoreCase(attrName))
      {
         return getCreated();
      }

      return null;
   }

   
   //==========================================================================
   
   public boolean set(String attrName, Object value)
   {
      if (PROPERTY_NAME.equalsIgnoreCase(attrName))
      {
         setName((String) value);
         return true;
      }

      if (PROPERTY_BALANCE.equalsIgnoreCase(attrName))
      {
         setBalance(Double.parseDouble(value.toString()));
         return true;
      }

      if (PROPERTY_PARENT.equalsIgnoreCase(attrName))
      {
         setParent((GroupAccount) value);
         return true;
      }

      if (PROPERTY_ITEMS.equalsIgnoreCase(attrName))
      {
         addToItems((Item) value);
         return true;
      }
      
      if ((PROPERTY_ITEMS + JsonIdMap.REMOVE).equalsIgnoreCase(attrName))
      {
         removeFromItems((Item) value);
         return true;
      }
      if ((PROPERTY_CREATED).equalsIgnoreCase(attrName))
      {
    	  setCreated((Date) value);
         return true;
      }
      
      if (PROPERTY_ACTIVE.equalsIgnoreCase(attrName))
      {
    	  setActive((boolean) value);
         return true;
      }
      
      if (PROPERTY_TITLE.equalsIgnoreCase(attrName))
      {
    	  setTitle(""+value);
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
   
   @Override
   public boolean addPropertyChangeListener(PropertyChangeListener listener) 
   {
      getPropertyChangeSupport().addPropertyChangeListener(listener);
      return true;
   }

   
   //==========================================================================
   
   public void removeYou()
   {
      setParent(null);
      removeAllFromItems();
      getPropertyChangeSupport().firePropertyChange("REMOVE_YOU", this, null);
   }

   
   //==========================================================================
   
   public static final String PROPERTY_NAME = "name";
   
   private String name;

   public String getName()
   {
      return this.name;
   }
   
   public void setName(String value)
   {
      if ( ( this.name==null && value!=null) || (this.name!=null && !this.name.equals(value)))
      {
         String oldValue = this.name;
         this.name = value;
         getPropertyChangeSupport().firePropertyChange(PROPERTY_NAME, oldValue, value);
      }
   }
   
   public Person withName(String value)
   {
      setName(value);
      return this;
   } 

   //==========================================================================
   
   public static final String PROPERTY_CREATED= "created";
   
   private Date created;

   public Date getCreated()
   {
      return this.created;
   }
   
   public void setCreated(Date value)
   {
      if ( ( this.created==null && value!=null) || (this.created!=null && !this.created.equals(value)))
      {
         Date oldValue = this.created;
         this.created = value;
         getPropertyChangeSupport().firePropertyChange(PROPERTY_CREATED, oldValue, value);
      }
   }
   
   public Person withCreated(Date value)
   {
      setCreated(value);
      return this;
   } 

   //==========================================================================
   
   public static final String PROPERTY_ACTIVE = "active";
   
   private boolean active;

   public boolean isActive()
   {
      return this.active;
   }
   
   public void setActive(boolean value)
   {
      if (this.active != value )
      {
         boolean oldValue = this.active;
         this.active = value;
         getPropertyChangeSupport().firePropertyChange(PROPERTY_ACTIVE, oldValue, value);
      }
   }
   
   public Person withActive(boolean value)
   {
      setActive(value);
      return this;
   } 

   //==========================================================================
   
   public static final String PROPERTY_TITLE = "title";
   
   private String title;

   public String getTitle()
   {
      return this.title;
   }
   
   public void setTitle(String value)
   {
      if ( ( this.title==null && value!=null) || (this.title!=null && !this.title.equals(value)))
      {
         String oldValue = this.title;
         this.title = value;
         getPropertyChangeSupport().firePropertyChange(PROPERTY_TITLE, oldValue, value);
      }
   }
   
   public Person withTitle(String value)
   {
      setTitle(value);
      return this;
   } 

   
   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();
      
      result.append(" ").append(this.getName());
      result.append(" ").append(this.getBalance());
      return result.substring(1);
   }


   
   //==========================================================================
   
   public static final String PROPERTY_BALANCE = "balance";
   
   private double balance;

   public double getBalance()
   {
      return this.balance;
   }
   
   public void setBalance(double value)
   {
      if (this.balance != value)
      {
         double oldValue = this.balance;
         this.balance = value;
         getPropertyChangeSupport().firePropertyChange(PROPERTY_BALANCE, oldValue, value);
      }
   }
   
   public Person withBalance(double value)
   {
      setBalance(value);
      return this;
   } 

   
   public static final PersonSet EMPTY_SET = new PersonSet();

   
   /********************************************************************
    * <pre>
    *              many                       one
    * Person ----------------------------------- GroupAccount
    *              persons                   parent
    * </pre>
    */
   
   public static final String PROPERTY_PARENT = "parent";
   
   private GroupAccount parent = null;
   
   public GroupAccount getParent()
   {
      return this.parent;
   }
   
   public boolean setParent(GroupAccount value)
   {
      boolean changed = false;
      
      if (this.parent != value)
      {
         GroupAccount oldValue = this.parent;
         
         if (this.parent != null)
         {
            this.parent = null;
            oldValue.withoutPersons(this);
         }
         
         this.parent = value;
         
         if (value != null)
         {
            value.withPersons(this);
         }
         
         getPropertyChangeSupport().firePropertyChange(PROPERTY_PARENT, oldValue, value);
         changed = true;
      }
      
      return changed;
   }
   
   public Person withParent(GroupAccount value)
   {
      setParent(value);
      return this;
   } 
   
   public GroupAccount createParent()
   {
      GroupAccount value = new GroupAccount();
      withParent(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              one                       many
    * Person ----------------------------------- Item
    *              buyer                   items
    * </pre>
    */
   
   public static final String PROPERTY_ITEMS = "items";
   
   private ItemSet items = null;
   
   public ItemSet getItems()
   {
      if (this.items == null)
      {
         return Item.EMPTY_SET;
      }
   
      return this.items;
   }
   
   public boolean addToItems(Item value)
   {
      boolean changed = false;
      
      if (value != null)
      {
         if (this.items == null)
         {
            this.items = new ItemSet();
         }
         
         changed = this.items.add (value);
         
         if (changed)
         {
            value.withBuyer(this);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_ITEMS, null, value);
         }
      }
         
      return changed;   
   }
   
   public boolean removeFromItems(Item value)
   {
      boolean changed = false;
      
      if ((this.items != null) && (value != null))
      {
         changed = this.items.remove (value);
         
         if (changed)
         {
            value.setBuyer(null);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_ITEMS, value, null);
         }
      }
         
      return changed;   
   }
   
   public Person withItems(Item value)
   {
      addToItems(value);
      return this;
   } 
   
   public Person withoutItems(Item value)
   {
      removeFromItems(value);
      return this;
   } 
   
   public void removeAllFromItems()
   {
      LinkedHashSet<Item> tmpSet = new LinkedHashSet<Item>(this.getItems());
   
      for (Item value : tmpSet)
      {
         this.removeFromItems(value);
      }
   }
   
   public Item createItems()
   {
      Item value = new Item();
      withItems(value);
      return value;
   }


	@Override
	public boolean addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);
		return true;
	}


	@Override
	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
		return true;
	} 
}

