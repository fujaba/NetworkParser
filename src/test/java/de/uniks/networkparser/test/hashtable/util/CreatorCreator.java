package de.uniks.networkparser.test.hashtable.util;

import de.uniks.networkparser.json.JsonIdMap;

class CreatorCreator{

   public static JsonIdMap createIdMap(String sessionID)
   {
      JsonIdMap jsonIdMap = (JsonIdMap) new JsonIdMap().withSessionId(sessionID);
      
      jsonIdMap.withCreator(new de.uniks.networkparser.test.hashtable.util.GroupAccountCreator());
      jsonIdMap.withCreator(new de.uniks.networkparser.test.hashtable.util.PersonCreator());
      jsonIdMap.withCreator(new de.uniks.networkparser.test.hashtable.util.ItemCreator());

      return jsonIdMap;
   }
}
