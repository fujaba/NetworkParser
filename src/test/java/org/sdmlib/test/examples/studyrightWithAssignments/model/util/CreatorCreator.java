package org.sdmlib.test.examples.studyrightWithAssignments.model.util;
import de.uniks.networkparser.IdMap;

class CreatorCreator {

	public static final IdMap createIdMap(String session) {
		IdMap map = new IdMap().withSession(session);
		map.withCreator(new AssignmentSet());
		map.withCreator(new PresidentSet());
		map.withCreator(new RoomSet());
		map.withCreator(new StudentSet());
		map.withCreator(new TeachingAssistantSet());
		map.withCreator(new UniversitySet());

		return map;
	}
}