package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.json.JsonIdMap;

class CreatorCreator{

   public static JsonIdMap createIdMap(String sessionID)
   {
      JsonIdMap jsonIdMap = (JsonIdMap) new JsonIdMap().withSessionId(sessionID);
      
      jsonIdMap.withCreator(new de.uniks.networkparser.test.model.util.GroupAccountCreator());
      jsonIdMap.withCreator(new de.uniks.networkparser.test.model.util.PersonCreator());
      jsonIdMap.withCreator(new de.uniks.networkparser.test.model.util.ItemCreator());

      return jsonIdMap;
   }
}
