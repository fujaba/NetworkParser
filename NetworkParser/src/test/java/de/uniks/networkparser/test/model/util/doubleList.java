package de.uniks.networkparser.test.model.util;


public class doubleList extends SDMSetBase<Double>
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
