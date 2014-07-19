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
   
package de.uniks.networkparser.test.model.creator;

import java.util.LinkedHashSet;

import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Item;
import de.uniks.networkparser.test.model.Person;

public class PersonSet extends LinkedHashSet<Person>
{

	private static final long serialVersionUID = 1L;

public String getEntryType()
   {
      return "org.sdmlib.examples.groupAccount.Person";
   }


   public PersonSet with(Person value)
   {
      this.add(value);
      return this;
   }
   
   public PersonSet without(Person value)
   {
      this.remove(value);
      return this;
   }
 
   public PersonSet withName(String value)
   {
      for (Person obj : this)
      {
         obj.setName(value);
      }
      
      return this;
   }

   public PersonSet withBalance(double value)
   {
      for (Person obj : this)
      {
         obj.setBalance(value);
      }
      
      return this;
   }

   public GroupAccountSet getParent()
   {
      GroupAccountSet result = new GroupAccountSet();
      
      for (Person obj : this)
      {
         result.add(obj.getParent());
      }
      
      return result;
   }

   public PersonSet withParent(GroupAccount value)
   {
      for (Person obj : this)
      {
         obj.withParent(value);
      }
      
      return this;
   }

   public ItemSet getItems()
   {
      ItemSet result = new ItemSet();
      
      for (Person obj : this)
      {
         result.addAll(obj.getItems());
      }
      
      return result;
   }

   public PersonSet withItems(Item value)
   {
      for (Person obj : this)
      {
         obj.withItems(value);
      }
      
      return this;
   }

   public PersonSet withoutItems(Item value)
   {
      for (Person obj : this)
      {
         obj.withoutItems(value);
      }
      
      return this;
   }

}

