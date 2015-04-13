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
   
package de.uniks.networkparser.gui.javafx.test.model.util;

import de.uniks.networkparser.gui.javafx.test.model.GroupAccount;
import de.uniks.networkparser.gui.javafx.test.model.Item;
import de.uniks.networkparser.gui.javafx.test.model.Person;

public class ItemSet extends SDMSetBase<Item>
{
	public String getEntryType()
   {
      return "org.sdmlib.examples.groupAccount.Item";
   }


   public ItemSet with(Item value)
   {
      this.add(value);
      return this;
   }
   
   public ItemSet without(Item value)
   {
      this.remove(value);
      return this;
   }
   
   public ItemSet withDescription(String value)
   {
      for (Item obj : this)
      {
         obj.setDescription(value);
      }
      
      return this;
   }

   public ItemSet withValue(double value)
   {
      for (Item obj : this)
      {
         obj.setValue(value);
      }
      
      return this;
   }
   public doubleList getValue()
   {
	   doubleList result = new doubleList();
      
      for (Item obj : this)
      {
         result.add(obj.getValue());
      }
      
      return result;
   }
   public double getValueSum()
   {
	   double result=0;
      
      for (Item obj : this)
      {
    	  result += obj.getValue();
      }
      
      return result;
   }

   public GroupAccountSet getParent()
   {
      GroupAccountSet result = new GroupAccountSet();
      
      for (Item obj : this)
      {
         result.add(obj.getParent());
      }
      
      return result;
   }

   public ItemSet withParent(GroupAccount value)
   {
      for (Item obj : this)
      {
         obj.withParent(value);
      }
      
      return this;
   }

   public PersonSet getBuyer()
   {
      PersonSet result = new PersonSet();
      
      for (Item obj : this)
      {
         result.add(obj.getBuyer());
      }
      
      return result;
   }

   public ItemSet withBuyer(Person value)
   {
      for (Item obj : this)
      {
         obj.withBuyer(value);
      }
      
      return this;
   }

}

