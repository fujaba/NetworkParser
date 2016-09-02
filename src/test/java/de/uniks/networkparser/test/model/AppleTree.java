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
import java.util.LinkedHashSet;
import de.uniks.networkparser.interfaces.SendableEntity;

public class AppleTree extends Tree implements SendableEntity
{
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
		removeAllFromHas();
	}

   /********************************************************************
	* <pre>
	*			  one					   many
	* AppleTree ----------------------------------- Apple
	*			  owner				   has
	* </pre>
	*/

   public static final String PROPERTY_HAS = "has";

   private LinkedHashSet<Apple> has = null;

   public LinkedHashSet<Apple> getHas()
   {
	  if (this.has == null)
	  {
		 return new LinkedHashSet<Apple>();
	  }

	  return this.has;
   }

   public boolean addToHas(Apple value)
   {
	  boolean changed = false;

	  if (value != null)
	  {
		 if (this.has == null)
		 {
			this.has = new LinkedHashSet<Apple>();
		 }

		 changed = this.has.add (value);

		 if (changed)
		 {
			firePropertyChange(PROPERTY_HAS, null, value);
			value.withOwner(this);
		 }
	  }

	  return changed;
   }

   public boolean removeFromHas(Apple value)
   {
	  boolean changed = false;

	  if ((this.has != null) && (value != null))
	  {
		 changed = this.has.remove(value);

		 if (changed)
		 {
			value.setOwner(null);
		 }
	  }

	  return changed;
   }

   public AppleTree withHas(Apple... value)
   {
	  if(value==null){
		 return this;
	  }
	  for (Apple item : value)
	  {
		 addToHas(item);
	  }
	  return this;
   }

   public AppleTree withoutHas(Apple... value)
   {
	  for (Apple item : value)
	  {
		 removeFromHas(item);
	  }
	  return this;
   }

   public void removeAllFromHas()
   {
	  LinkedHashSet<Apple> tmpSet = new LinkedHashSet<Apple>(this.getHas());

	  for (Apple value : tmpSet)
	  {
		 this.removeFromHas(value);
	  }
   }

   public Apple createApple()
   {
	  Apple value = new Apple();
	  withHas(value);
	  return value;
   }
}
