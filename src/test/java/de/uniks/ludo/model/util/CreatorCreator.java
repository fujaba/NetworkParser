package de.uniks.ludo.model.util;
import de.uniks.networkparser.IdMap;

class CreatorCreator {

	public static final IdMap createIdMap(String session) {
		IdMap map = new IdMap().withSession(session);
		map.withCreator(new DiceSet());
		map.withCreator(new FieldSet());
		map.withCreator(new HomeSet());
		map.withCreator(new LastFieldSet());
		map.withCreator(new LudoSet());
		map.withCreator(new MeepleSet());
		map.withCreator(new PlayerSet());
		map.withCreator(new StartSet());
		map.withCreator(new TargetSet());

		return map;
	}
}