package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.list.SDMSet;

public class doubleList extends SDMSet<Double>
{

   public double sum()
   {
	  double result = 0;
	  for (double value : this)
	  {
		 result += value;
	  }
	  return result;
   }

}
