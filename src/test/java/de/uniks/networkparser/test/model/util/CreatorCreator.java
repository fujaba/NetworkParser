package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.IdMap;

class CreatorCreator{

   public static IdMap createIdMap(String sessionID)
   {
	   IdMap jsonIdMap = new IdMap().withSessionId(sessionID);

	  jsonIdMap.with(new de.uniks.networkparser.test.model.util.GroupAccountCreator());
	  jsonIdMap.with(new de.uniks.networkparser.test.model.util.PersonCreator());
	  jsonIdMap.with(new de.uniks.networkparser.test.model.util.ItemCreator());

	  return jsonIdMap;
   }
}
