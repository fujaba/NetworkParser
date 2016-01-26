package de.uniks.networkparser.list;

public class BooleanList extends SimpleList<Boolean> {
   public boolean and()
   {
      for (Boolean value : this)
      {
         if (!value)
         {
            return false;
         }
      }

      return true;
   }

   public boolean or()
   {
      for (Boolean value : this)
      {
         if (value)
         {
            return true;
         }
      }

      return false;
   }
}
