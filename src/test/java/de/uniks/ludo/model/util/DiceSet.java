package de.uniks.ludo.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.ludo.model.Dice;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.ludo.model.Ludo;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.SimpleEvent;

public class DiceSet extends SimpleSet<Dice> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		Dice.PROPERTY_GAME,
		Dice.PROPERTY_NUMBER,
	};

	public static final DiceSet EMPTY_SET = new DiceSet().withFlag(DiceSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Dice();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Dice == false) {
			return null;
		}
		Dice element = (Dice)entity;
		if (Dice.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			return element.getGame();
		}

		if (Dice.PROPERTY_NUMBER.equalsIgnoreCase(attribute)) {
			return element.getNumber();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof Dice == false) {
			return false;
		}
		Dice element = (Dice)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (Dice.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			element.setGame((Ludo) value);
			return true;
		}

		if (Dice.PROPERTY_NUMBER.equalsIgnoreCase(attribute)) {
			return element.setNumber((int) value);
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return Dice.class;
	}

	@Override
	public DiceSet getNewList(boolean keyValue) {
		return new DiceSet();
	}


	public NumberList getNumber(int... filter) {
		NumberList result = new NumberList();
		if(listener != null) {
			result.withListener(listener);
			Dice[] children = this.toArray(new Dice[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getNumber(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Dice obj : this) {
				result.add(obj.getNumber());
			}
		} else {
			for (Dice obj : this) {
				int item = obj.getNumber();
				for(int i=0;i<filter.length;i++) {
					if (filter[i] == item) {
						result.add(item);
						break;
					}
				}
			}
		}
		return result;
	}
	public DiceSet filterNumber(int minValue, int maxValue) {
		DiceSet result = new DiceSet();
		for(Dice obj : this) {
			if (	minValue <= obj.getNumber() && maxValue >= obj.getNumber()) {
				result.add(obj);
			}
		}
		return result;
	}

	public DiceSet withNumber(int value) {
		for (Dice obj : this) {
			obj.setNumber(value);
		}
		return this;
	}
	public LudoSet getGame(Ludo... filter) {
		LudoSet result = new LudoSet();
		if(listener != null) {
			result.withListener(listener);
			Dice[] children = this.toArray(new Dice[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getGame(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Dice obj : this) {
				result.add(obj.getGame());
			}
			return result;
		}
		for (Dice obj : this) {
			Ludo item = obj.getGame();
			if(item != null) {
				for(int i=0;i<filter.length;i++) {
					if (item.equals(filter[i])) {
						result.add(item);
						break;
					}
				}
			}
		}
		return result;
	}


	public DiceSet withGame(Ludo value) {
		for (Dice obj : this) {
			obj.withGame(value);
		}
		return this;
	}
}