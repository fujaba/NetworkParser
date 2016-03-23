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
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.AppleTree;

public class AppleTreeCreator extends TreeCreator
{
   private final String[] properties = new String[]
   {
	  AppleTree.PROPERTY_HAS,
   };

   @Override
   public String[] getProperties()
   {
	  return properties;
   }

   @Override
   public Object getSendableInstance(boolean reference)
   {
	  return new AppleTree();
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

	  if (AppleTree.PROPERTY_HAS.equalsIgnoreCase(attribute))
	  {
		 return ((AppleTree) target).getHas();
	  }

	  return null;
   }

   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
	  if (IdMap.REMOVE.equals(type) && value != null)
	  {
		 attrName = attrName + type;
	  }

	  if (AppleTree.PROPERTY_HAS.equalsIgnoreCase(attrName))
	  {
		 ((AppleTree) target).addToHas((Apple) value);
		 return true;
	  }

	  if ((AppleTree.PROPERTY_HAS + IdMap.REMOVE).equalsIgnoreCase(attrName))
	  {
		 ((AppleTree) target).removeFromHas((Apple) value);
		 return true;
	  }

	  return false;
   }
}
