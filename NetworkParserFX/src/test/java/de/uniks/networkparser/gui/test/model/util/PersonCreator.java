package de.uniks.networkparser.gui.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.gui.test.model.GroupAccount;
import de.uniks.networkparser.gui.test.model.Item;
import de.uniks.networkparser.gui.test.model.Person;

public class PersonCreator implements SendableEntityCreator
{
   private final String[] properties = new String[]
   {
      Person.PROPERTY_NAME,
      Person.PROPERTY_BALANCE,
      Person.PROPERTY_PARENT,
      Person.PROPERTY_ITEM
   };
   
   @Override
   public String[] getProperties()
   {
      return properties;
   }
   
   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new Person();
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
	   
	   if (Person.PROPERTY_NAME.equalsIgnoreCase(attribute))
      {
         return ((Person)target).getName();
      }
	   if (Person.PROPERTY_BALANCE.equalsIgnoreCase(attribute))
	      {
	         return ((Person)target).getBalance();
	      }
	   if (Person.PROPERTY_PARENT.equalsIgnoreCase(attribute))
	      {
		   if(attribute.equals(attrName)) {
	         return ((Person)target).getParent();
		   }else {
			   return ((Person)target).getParent().getName();
		   }
	      }
	   if (Person.PROPERTY_WALLET.equalsIgnoreCase(attribute))
	      {
		   if(attribute.equals(attrName)) {
	         return ((Person)target).getWallet();
		   }else {
			   return ((Person)target).getWallet().getBalance();
		   }
	      }
	   if (Person.PROPERTY_ITEM.equalsIgnoreCase(attribute))
	      {
	         return ((Person)target).getItem();
	      }
	      return null;
	   }
   
   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
	      if (Person.PROPERTY_NAME.equalsIgnoreCase(attrName))
	      {
	         ((Person)target).setName((String) value);
	         return true;
	      }

	      if (Person.PROPERTY_BALANCE.equalsIgnoreCase(attrName))
	      {
	    	  ((Person)target).setBalance(Double.parseDouble(value.toString()));
	         return true;
	      }

	      if (Person.PROPERTY_PARENT.equalsIgnoreCase(attrName))
	      {
	    	  ((Person)target).setParent((GroupAccount) value);
	         return true;
	      }

	      if (Person.PROPERTY_ITEM.equalsIgnoreCase(attrName))
	      {
	    	  ((Person)target).withItem((Item) value);
	         return true;
	      }
	      
	      if ((Person.PROPERTY_ITEM + JsonIdMap.REMOVE).equalsIgnoreCase(attrName))
	      {
	    	  ((Person)target).withoutItem((Item) value);
	         return true;
	      }
      return false;
   }
}

