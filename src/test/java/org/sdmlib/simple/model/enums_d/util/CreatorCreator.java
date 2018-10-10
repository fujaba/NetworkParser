package org.sdmlib.simple.model.enums_d.util;
import de.uniks.networkparser.IdMap;

class CreatorCreator {

	public static final IdMap createIdMap(String session) {
		IdMap map = new IdMap().withSession(session);
		map.withCreator(new TestEnumSet());

		return map;
	}
}