package i.love.networkparser.util;
import de.uniks.networkparser.IdMap;

class CreatorCreator {

   public static final IdMap createIdMap(String session) {
        IdMap map = new IdMap().withSession(session);
        map.withCreator(new UniversityCreator());

        return map;
   }
}