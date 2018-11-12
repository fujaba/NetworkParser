package de.uniks.ludo.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.ludo.model.Ludo;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.ludo.model.Dice;
import de.uniks.ludo.model.Field;
import de.uniks.ludo.model.Player;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;

public class LudoSet extends SimpleSet<Ludo> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		Ludo.PROPERTY_DICE,
		Ludo.PROPERTY_FIELD,
		Ludo.PROPERTY_CURRENTPLAYER,
		Ludo.PROPERTY_PLAYERS,
		Ludo.PROPERTY_WINNER,
	};

	public static final LudoSet EMPTY_SET = new LudoSet().withFlag(LudoSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Ludo();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Ludo == false) {
			return null;
		}
		Ludo element = (Ludo)entity;
		if (Ludo.PROPERTY_DICE.equalsIgnoreCase(attribute)) {
			return element.getDice();
		}

		if (Ludo.PROPERTY_FIELD.equalsIgnoreCase(attribute)) {
			return element.getField();
		}

		if (Ludo.PROPERTY_CURRENTPLAYER.equalsIgnoreCase(attribute)) {
			return element.getCurrentPlayer();
		}

		if (Ludo.PROPERTY_PLAYERS.equalsIgnoreCase(attribute)) {
			return element.getPlayers();
		}

		if (Ludo.PROPERTY_WINNER.equalsIgnoreCase(attribute)) {
			return element.getWinner();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof Ludo == false) {
			return false;
		}
		Ludo element = (Ludo)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (Ludo.PROPERTY_DICE.equalsIgnoreCase(attribute)) {
			element.setDice((Dice) value);
			return true;
		}

		if (Ludo.PROPERTY_FIELD.equalsIgnoreCase(attribute)) {
			element.withField((Field) value);
			return true;
		}

		if (Ludo.PROPERTY_CURRENTPLAYER.equalsIgnoreCase(attribute)) {
			element.setCurrentPlayer((Player) value);
			return true;
		}

		if (Ludo.PROPERTY_PLAYERS.equalsIgnoreCase(attribute)) {
			element.withPlayers((Player) value);
			return true;
		}

		if (Ludo.PROPERTY_WINNER.equalsIgnoreCase(attribute)) {
			element.setWinner((Player) value);
			return true;
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return Ludo.class;
	}

	@Override
	public LudoSet getNewList(boolean keyValue) {
		return new LudoSet();
	}


	public DiceSet getDice(Dice... filter) {
		DiceSet result = new DiceSet();
		if(listener != null) {
			result.withListener(listener);
			Ludo[] children = this.toArray(new Ludo[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getDice(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Ludo obj : this) {
				result.add(obj.getDice());
			}
			return result;
		}
		for (Ludo obj : this) {
			Dice item = obj.getDice();
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


	public LudoSet withDice(Dice value) {
		for (Ludo obj : this) {
			obj.withDice(value);
		}
		return this;
	}
	public FieldSet getField(Field... filter) {
		FieldSet result = new FieldSet();
		if(listener != null) {
			result.withListener(listener);
			Ludo[] children = this.toArray(new Ludo[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getField(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Ludo obj : this) {
				result.addAll(obj.getField());
			}
			return result;
		}
		for (Ludo obj : this) {
			FieldSet item = obj.getField();
			if(item != null) {
				for(int i=0;i<filter.length;i++) {
					if (item.contains(filter[i])) {
						result.add(filter[i]);
						break;
					}
				}
			}
		}
		return result;
	}


	public LudoSet withField(Field value) {
		for (Ludo obj : this) {
			obj.withField(value);
		}
		return this;
	}
	public PlayerSet getCurrentPlayer(Player... filter) {
		PlayerSet result = new PlayerSet();
		if(listener != null) {
			result.withListener(listener);
			Ludo[] children = this.toArray(new Ludo[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getCurrentPlayer(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Ludo obj : this) {
				result.add(obj.getCurrentPlayer());
			}
			return result;
		}
		for (Ludo obj : this) {
			Player item = obj.getCurrentPlayer();
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


	public LudoSet withCurrentPlayer(Player value) {
		for (Ludo obj : this) {
			obj.withCurrentPlayer(value);
		}
		return this;
	}
	public PlayerSet getPlayers(Player... filter) {
		PlayerSet result = new PlayerSet();
		if(listener != null) {
			result.withListener(listener);
			Ludo[] children = this.toArray(new Ludo[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getPlayers(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Ludo obj : this) {
				result.addAll(obj.getPlayers());
			}
			return result;
		}
		for (Ludo obj : this) {
			PlayerSet item = obj.getPlayers();
			if(item != null) {
				for(int i=0;i<filter.length;i++) {
					if (item.contains(filter[i])) {
						result.add(filter[i]);
						break;
					}
				}
			}
		}
		return result;
	}


	public LudoSet withPlayers(Player value) {
		for (Ludo obj : this) {
			obj.withPlayers(value);
		}
		return this;
	}
	public PlayerSet getWinner(Player... filter) {
		PlayerSet result = new PlayerSet();
		if(listener != null) {
			result.withListener(listener);
			Ludo[] children = this.toArray(new Ludo[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getWinner(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Ludo obj : this) {
				result.add(obj.getWinner());
			}
			return result;
		}
		for (Ludo obj : this) {
			Player item = obj.getWinner();
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


	public LudoSet withWinner(Player value) {
		for (Ludo obj : this) {
			obj.withWinner(value);
		}
		return this;
	}
	public LudoSet init(Player... p1) {
		return LudoSet.EMPTY_SET;
	}

}