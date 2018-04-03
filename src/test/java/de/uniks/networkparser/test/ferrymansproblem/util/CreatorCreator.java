package de.uniks.networkparser.test.ferrymansproblem.util;

import de.uniks.networkparser.IdMap;

class CreatorCreator{

   public static IdMap createIdMap(String session)
   {
      IdMap jsonIdMap = new IdMap().withSession(session);
      jsonIdMap.with(new RiverCreator());
      jsonIdMap.with(new BoatCreator());
      jsonIdMap.with(new BankCreator());
      jsonIdMap.with(new CargoCreator());
      jsonIdMap.withTimeStamp(1);
      return jsonIdMap;
   }
}
