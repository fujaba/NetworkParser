package de.uniks.networkparser.simple.modelA.util;
import de.uniks.networkparser.IdMap;

class CreatorCreator {

   public static final IdMap createIdMap(String session) {
        IdMap map = new IdMap().withSession(session);
        map.withCreator(new PersonCreator());
        map.withCreator(new RoomCreator());

        return map;
   }
}