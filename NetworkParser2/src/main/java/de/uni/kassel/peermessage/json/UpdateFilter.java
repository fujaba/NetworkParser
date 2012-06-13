package de.uni.kassel.peermessage.json;

import de.uni.kassel.peermessage.IdMap;

public class UpdateFilter extends JsonFilter
{

   @Override
   public boolean isConvertable(IdMap map, Object entity, String property,
         Object value)
   {
      return map.getKey(value) == null;
//      return super.isConvertable(map, entity, property, value);
   }

}
