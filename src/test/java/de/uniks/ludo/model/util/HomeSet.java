package de.uniks.ludo.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.ludo.model.Home;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.ludo.model.Player;
import de.uniks.ludo.model.Field;
import de.uniks.ludo.model.Ludo;
import de.uniks.ludo.model.Meeple;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;

public class HomeSet extends SimpleSet<Home> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		Home.PROPERTY_PLAYER,
		Home.PROPERTY_PREV,
		Home.PROPERTY_GAME,
		Home.PROPERTY_MEEPLE,
	};

	public static final HomeSet EMPTY_SET = new HomeSet().withFlag(HomeSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Home();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Home == false) {
			return null;
		}
		Home element = (Home)entity;
		if (Home.PROPERTY_PLAYER.equalsIgnoreCase(attribute)) {
			return element.getPlayer();
		}

		if (Home.PROPERTY_PREV.equalsIgnoreCase(attribute)) {
			return element.getPrev();
		}

		if (Home.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			return element.getGame();
		}

		if (Home.PROPERTY_MEEPLE.equalsIgnoreCase(attribute)) {
			return element.getMeeple();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof Home == false) {
			return false;
		}
		Home element = (Home)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (Home.PROPERTY_PLAYER.equalsIgnoreCase(attribute)) {
			element.setPlayer((Player) value);
			return true;
		}

		if (Home.PROPERTY_PREV.equalsIgnoreCase(attribute)) {
			element.setPrev((Field) value);
			return true;
		}

		if (Home.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			element.setGame((Ludo) value);
			return true;
		}

		if (Home.PROPERTY_MEEPLE.equalsIgnoreCase(attribute)) {
			element.setMeeple((Meeple) value);
			return true;
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return Home.class;
	}

	@Override
	public HomeSet getNewList(boolean keyValue) {
		return new HomeSet();
	}


	public PlayerSet getPlayer(Player... filter) {
		PlayerSet result = new PlayerSet();
		if(listener != null) {
			result.withListener(listener);
			Home[] children = this.toArray(new Home[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getPlayer(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Home obj : this) {
				result.add(obj.getPlayer());
			}
			return result;
		}
		for (Home obj : this) {
			Player item = obj.getPlayer();
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


	public HomeSet withPlayer(Player value) {
		for (Home obj : this) {
			obj.withPlayer(value);
		}
		return this;
	}
	public FieldSet getPrev(Field... filter) {
		FieldSet result = new FieldSet();
		if(listener != null) {
			result.withListener(listener);
			Home[] children = this.toArray(new Home[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getPrev(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Home obj : this) {
				result.add(obj.getPrev());
			}
			return result;
		}
		for (Home obj : this) {
			Field item = obj.getPrev();
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


	public HomeSet withPrev(Field value) {
		for (Home obj : this) {
			obj.withPrev(value);
		}
		return this;
	}
	public FieldSet getNext(Field... filter) {
		FieldSet result = new FieldSet();
		if(listener != null) {
			result.withListener(listener);
			Home[] children = this.toArray(new Home[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getNext(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Home obj : this) {
				result.add(obj.getNext());
			}
			return result;
		}
		for (Home obj : this) {
			Field item = obj.getNext();
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


	public HomeSet withNext(Field value) {
		for (Home obj : this) {
			obj.withNext(value);
		}
		return this;
	}
	public LudoSet getGame(Ludo... filter) {
		LudoSet result = new LudoSet();
		if(listener != null) {
			result.withListener(listener);
			Home[] children = this.toArray(new Home[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getGame(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Home obj : this) {
				result.add(obj.getGame());
			}
			return result;
		}
		for (Home obj : this) {
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


	public HomeSet withGame(Ludo value) {
		for (Home obj : this) {
			obj.withGame(value);
		}
		return this;
	}
	public MeepleSet getMeeple(Meeple... filter) {
		MeepleSet result = new MeepleSet();
		if(listener != null) {
			result.withListener(listener);
			Home[] children = this.toArray(new Home[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getMeeple(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Home obj : this) {
				result.add(obj.getMeeple());
			}
			return result;
		}
		for (Home obj : this) {
			Meeple item = obj.getMeeple();
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


	public HomeSet withMeeple(Meeple value) {
		for (Home obj : this) {
			obj.withMeeple(value);
		}
		return this;
	}
}