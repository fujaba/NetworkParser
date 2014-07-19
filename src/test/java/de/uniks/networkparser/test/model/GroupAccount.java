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
import java.util.LinkedHashSet;

import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.creator.ItemSet;
import de.uniks.networkparser.test.model.creator.PersonSet;

public class GroupAccount implements SendableEntity
{
   //==========================================================================
   
   public Object get(String attrName)
   {
      if (PROPERTY_PERSONS.equalsIgnoreCase(attrName))
      {
         return getPersons();
      }

      if (PROPERTY_ITEMS.equalsIgnoreCase(attrName))
      {
         return getItems();
      }

      return null;
   }

   
   //==========================================================================
   
   public boolean set(String attrName, Object value)
   {
      if (PROPERTY_PERSONS.equalsIgnoreCase(attrName))
      {
         addToPersons((Person) value);
         return true;
      }
      
      if ((PROPERTY_PERSONS + JsonIdMap.REMOVE).equalsIgnoreCase(attrName))
      {
         removeFromPersons((Person) value);
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
      removeAllFromPersons();
      removeAllFromItems();
      getPropertyChangeSupport().firePropertyChange("REMOVE_YOU", this, null);
   }

   
   //==========================================================================
   
   public double initAccounts( double p0, String p1 )
   {
      return 0;
   }

   
   //==========================================================================
   
   
   /********************************************************************
    * <pre>
    *              one                       many
    * GroupAccount ----------------------------------- Person
    *              parent                   persons
    * </pre>
    */
   
   public static final String PROPERTY_PERSONS = "persons";
   
   private PersonSet persons = null;
   
   public PersonSet getPersons()
   {
      if (this.persons == null)
      {
         return Person.EMPTY_SET;
      }
   
      return this.persons;
   }
   
   public boolean addToPersons(Person value)
   {
      boolean changed = false;
      
      if (value != null)
      {
         if (this.persons == null)
         {
            this.persons = new PersonSet();
         }
         
         changed = this.persons.add (value);
         
         if (changed)
         {
            value.withParent(this);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_PERSONS, null, value);
         }
      }
         
      return changed;   
   }
   
   public boolean removeFromPersons(Person value)
   {
      boolean changed = false;
      
      if ((this.persons != null) && (value != null))
      {
         changed = this.persons.remove (value);
         
         if (changed)
         {
            value.setParent(null);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_PERSONS, value, null);
         }
      }
         
      return changed;   
   }
   
   public GroupAccount withPersons(Person value)
   {
      addToPersons(value);
      return this;
   } 
   
   public GroupAccount withoutPersons(Person value)
   {
      removeFromPersons(value);
      return this;
   } 
   
   public void removeAllFromPersons()
   {
      LinkedHashSet<Person> tmpSet = new LinkedHashSet<Person>(this.getPersons());
   
      for (Person value : tmpSet)
      {
         this.removeFromPersons(value);
      }
   }
   
   public Person createPersons()
   {
      Person value = new Person();
      withPersons(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              one                       many
    * GroupAccount ----------------------------------- Item
    *              parent                   items
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
            value.withParent(this);
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
            value.setParent(null);
            getPropertyChangeSupport().firePropertyChange(PROPERTY_ITEMS, value, null);
         }
      }
         
      return changed;   
   }
   
   public GroupAccount withItems(Item value)
   {
      addToItems(value);
      return this;
   } 
   
   public GroupAccount withoutItems(Item value)
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
	   public void updateBalances(  )
	   {
	      double total = this.getItems().getValueSum();
	      
	      double share = total / this.getPersons().size();
	      
	      for (Person person : this.getPersons())
	      {
	         double myCosts = person.getItems().getValueSum();
	         
	         person.setBalance(myCosts - share); 
	      }
	   }

}

