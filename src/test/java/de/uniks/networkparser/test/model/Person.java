/*
   Copyright (c) 2014 Stefan

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
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.test.model.ludo.StrUtil;
import de.uniks.networkparser.test.model.util.ItemSet;
import de.uniks.networkparser.test.model.util.PersonSet;

public class Person  implements SendableEntity, Comparable<Object> {
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_BALANCE = "balance";
	public static final String PROPERTY_PARENT = "parent";
	public static final String PROPERTY_ITEM = "item";
	public static final String PROPERTY_WALLET = "wallet";

	private ItemSet item = null;
	private GroupAccount parent = null;
	private double balance;
	private Wallet wallet = new Wallet();
	private String name;

	//==========================================================================
	protected PropertyChangeSupport listeners = null;
	   
	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (listeners != null) {
			listeners.firePropertyChange(propertyName, oldValue, newValue);
			return true;
		}
		return false;
	}

	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(listener);
		return true;
	}

	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(propertyName, listener);
		return true;
	}

	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(listener);
		}
		return true;
	}

	public boolean removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(property, listener);
		}
		return true;
	}

	//==========================================================================
	public void removeYou() {
		setParent(null);
		withoutItem(this.getItem().toArray(new Item[this.getItem().size()]));
		firePropertyChange("REMOVE_YOU", this, null);
	}

	//==========================================================================
	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		if ( ! StrUtil.stringEquals(this.name, value)) {
			String oldValue = this.name;
			this.name = value;
			firePropertyChange(PROPERTY_NAME, oldValue, value);
		}
	}

	public Person withName(String value) {
		setName(value);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder r = new StringBuilder();
		r.append(" ").append(this.getName());
		r.append(" ").append(this.getBalance());
		return r.substring(1);
	}

	//==========================================================================
	public double getBalance() {
		return this.balance;
	}

	public void setBalance(double value) {
		if (this.balance != value) {
			double oldValue = this.balance;
			this.balance = value;
			firePropertyChange(PROPERTY_BALANCE, oldValue, value);
		}
	}

	public Person withBalance(double value) {
		setBalance(value);
		return this;
	}

	public static final PersonSet EMPTY_SET = new PersonSet().withFlag(PersonSet.READONLY);

	/********************************************************************
	* <pre>
	*			  many					   one
	* Person ----------------------------------- GroupAccount
	*			  persons				   parent
	* </pre>
	*/

	public GroupAccount getParent() {
		return this.parent;
	}

	public boolean setParent(GroupAccount value) {
		boolean changed = false;
		if (this.parent != value) {
			GroupAccount oldValue = this.parent;
			if (this.parent != null) {
				this.parent = null;
				oldValue.withoutPersons(this);
			}

			this.parent = value;

			if (value != null) {
				value.withPersons(this);
			}

			firePropertyChange(PROPERTY_PARENT, oldValue, value);
			changed = true;
		}

		return changed;
   }

	public boolean setUnidirectionalParent(GroupAccount value) {
		boolean changed = false;
		if (this.parent != value) {
			GroupAccount oldValue = this.parent;
			this.parent = value;
			firePropertyChange(PROPERTY_PARENT, oldValue, value);
			changed = true;
		}
		return changed;
	}

	public Person withParent(GroupAccount value) {
		setParent(value);
		return this;
	}

	public GroupAccount createParent() {
		GroupAccount value = new GroupAccount();
		withParent(value);
		return value;
	}

   /********************************************************************
	* <pre>
	*			  one					   many
	* Person ----------------------------------- Item
	*			  buyer				   item
	* </pre>
	*/

   public ItemSet getItem()
   {
	  if (this.item == null)
	  {
		 return Item.EMPTY_SET;
	  }

	  return this.item;
   }

   public Person withItem(Item... value)
   {
	  if(value==null){
		 return this;
	  }
	  for (Item item : value)
	  {
		 if (item != null)
		 {
			if (this.item == null)
			{
			   this.item = new ItemSet();
			}

			boolean changed = this.item.add (item);

			if (changed)
			{
			   item.withBuyer(this);
			   firePropertyChange(PROPERTY_ITEM, null, item);
			}
		 }
	  }
	  return this;
   }

   public Person withoutItem(Item... value)
   {
	  for (Item item : value)
	  {
		 if ((this.item != null) && (item != null))
		 {
			if (this.item.remove(item))
			{
			   item.setBuyer(null);
			   firePropertyChange(PROPERTY_ITEM, item, null);
			}
		 }

	  }
	  return this;
   }

   public Item createItem()
   {
	  Item value = new Item();
	  withItem(value);
	  return value;
   }
	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}
	public Person withWallet(Wallet wallet) {
		this.wallet = wallet;
		return this;
	}

	@Override
	public int compareTo(Object o) {
		if(o instanceof Person) {
			return this.getName().compareTo(((Person)o).getName());
		}
		return -1;

	}
}

