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

package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Item;
import de.uniks.networkparser.test.model.Person;

public class GroupAccountCreator implements SendableEntityCreator
{
   private final String[] properties = new String[]
   {
	  GroupAccount.PROPERTY_NAME,
	  GroupAccount.PROPERTY_PERSONS,
	  GroupAccount.PROPERTY_ITEM

   };

   @Override
   public String[] getProperties()
   {
	  return properties;
   }

   @Override
   public Object getSendableInstance(boolean reference)
   {
	  return new GroupAccount();
   }

   @Override
   public Object getValue(Object target, String attrName)
   {
	  int pos = attrName.indexOf('.');
	  String attribute = attrName;

	  if (pos > 0)
	  {
		 attribute = attrName.substring(0, pos);
	  }

	  if (GroupAccount.PROPERTY_NAME.equalsIgnoreCase(attribute))
	  {
		 return ((GroupAccount) target).getName();
	  }

	  if (GroupAccount.PROPERTY_PERSONS.equalsIgnoreCase(attribute))
	  {
		 return ((GroupAccount) target).getPersons();
	  }

	  if (GroupAccount.PROPERTY_ITEM.equalsIgnoreCase(attribute))
	  {
		 return ((GroupAccount) target).getItem();
	  }

	  return null;
   }

   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
	  if (SendableEntityCreator.REMOVE.equals(type) && value != null)
	  {
		 attrName = attrName + type;
	  }

	  if (GroupAccount.PROPERTY_NAME.equalsIgnoreCase(attrName))
	  {
		 ((GroupAccount) target).setName((String) value);
		 return true;
	  }

	  if (GroupAccount.PROPERTY_PERSONS.equalsIgnoreCase(attrName))
	  {
		 ((GroupAccount) target).withPersons((Person) value);
		 return true;
	  }

	  if ((GroupAccount.PROPERTY_PERSONS + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
	  {
		 ((GroupAccount) target).withoutPersons((Person) value);
		 return true;
	  }

	  if (GroupAccount.PROPERTY_ITEM.equalsIgnoreCase(attrName))
	  {
		 ((GroupAccount) target).withItem((Item) value);
		 return true;
	  }

	  if ((GroupAccount.PROPERTY_ITEM + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
	  {
		 ((GroupAccount) target).withoutItem((Item) value);
		 return true;
	  }

	  return false;
   }
   public static IdMap createIdMap(String sessionID)
   {
		IdMap jsonIdMap = new IdMap().withSession(sessionID);
		jsonIdMap.with(new de.uniks.networkparser.test.model.util.GroupAccountCreator());
		jsonIdMap.with(new de.uniks.networkparser.test.model.util.PersonCreator());
		jsonIdMap.with(new de.uniks.networkparser.test.model.util.ItemCreator());
		return jsonIdMap;
	}

   //==========================================================================

  public void removeObject(Object entity)
   {
	  ((GroupAccount) entity).removeYou();
   }
}
