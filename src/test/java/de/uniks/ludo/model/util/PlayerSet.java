package de.uniks.ludo.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.ludo.model.Player;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.ludo.model.Home;
import de.uniks.ludo.model.Ludo;
import de.uniks.ludo.model.Meeple;
import de.uniks.ludo.model.Start;
import de.uniks.ludo.model.Target;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.SimpleEvent;

public class PlayerSet extends SimpleSet<Player> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		Player.PROPERTY_HOME,
		Player.PROPERTY_CURRENTGAME,
		Player.PROPERTY_GAME,
		Player.PROPERTY_WONGAME,
		Player.PROPERTY_MEEPLE,
		Player.PROPERTY_PREV,
		Player.PROPERTY_START,
		Player.PROPERTY_TARGET,
		Player.PROPERTY_COLOR,
		Player.PROPERTY_NAME,
	};

	public static final PlayerSet EMPTY_SET = new PlayerSet().withFlag(PlayerSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Player();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Player == false) {
			return null;
		}
		Player element = (Player)entity;
		if (Player.PROPERTY_HOME.equalsIgnoreCase(attribute)) {
			return element.getHome();
		}

		if (Player.PROPERTY_CURRENTGAME.equalsIgnoreCase(attribute)) {
			return element.getCurrentGame();
		}

		if (Player.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			return element.getGame();
		}

		if (Player.PROPERTY_WONGAME.equalsIgnoreCase(attribute)) {
			return element.getWonGame();
		}

		if (Player.PROPERTY_MEEPLE.equalsIgnoreCase(attribute)) {
			return element.getMeeple();
		}

		if (Player.PROPERTY_PREV.equalsIgnoreCase(attribute)) {
			return element.getPrev();
		}

		if (Player.PROPERTY_START.equalsIgnoreCase(attribute)) {
			return element.getStart();
		}

		if (Player.PROPERTY_TARGET.equalsIgnoreCase(attribute)) {
			return element.getTarget();
		}

		if (Player.PROPERTY_COLOR.equalsIgnoreCase(attribute)) {
			return element.getColor();
		}

		if (Player.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return element.getName();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof Player == false) {
			return false;
		}
		Player element = (Player)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (Player.PROPERTY_HOME.equalsIgnoreCase(attribute)) {
			element.withHome((Home) value);
			return true;
		}

		if (Player.PROPERTY_CURRENTGAME.equalsIgnoreCase(attribute)) {
			element.setCurrentGame((Ludo) value);
			return true;
		}

		if (Player.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			element.setGame((Ludo) value);
			return true;
		}

		if (Player.PROPERTY_WONGAME.equalsIgnoreCase(attribute)) {
			element.setWonGame((Ludo) value);
			return true;
		}

		if (Player.PROPERTY_MEEPLE.equalsIgnoreCase(attribute)) {
			element.withMeeple((Meeple) value);
			return true;
		}

		if (Player.PROPERTY_PREV.equalsIgnoreCase(attribute)) {
			element.setPrev((Player) value);
			return true;
		}

		if (Player.PROPERTY_START.equalsIgnoreCase(attribute)) {
			element.setStart((Start) value);
			return true;
		}

		if (Player.PROPERTY_TARGET.equalsIgnoreCase(attribute)) {
			element.withTarget((Target) value);
			return true;
		}

		if (Player.PROPERTY_COLOR.equalsIgnoreCase(attribute)) {
			return element.setColor((String) value);
		}

		if (Player.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return element.setName((String) value);
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return Player.class;
	}

	@Override
	public PlayerSet getNewList(boolean keyValue) {
		return new PlayerSet();
	}


	public StringList getColor(String... filter) {
		StringList result = new StringList();
		if(listener != null) {
			result.withListener(listener);
			Player[] children = this.toArray(new Player[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getColor(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Player obj : this) {
				result.add(obj.getColor());
			}
		} else {
			for (Player obj : this) {
				String item = obj.getColor();
				for(int i=0;i<filter.length;i++) {
					if (filter[i].equals(item)) {
						result.add(item);
						break;
					}
				}
			}
		}
		return result;
	}
	public PlayerSet filterColor(String minValue, String maxValue) {
		PlayerSet result = new PlayerSet();
		for(Player obj : this) {
			if (minValue.compareTo(obj.getColor()) <= 0 && maxValue.compareTo(obj.getColor()) >= 0) {
				result.add(obj);
			}
		}
		return result;
	}

	public PlayerSet withColor(String value) {
		for (Player obj : this) {
			obj.setColor(value);
		}
		return this;
	}
	public StringList getName(String... filter) {
		StringList result = new StringList();
		if(listener != null) {
			result.withListener(listener);
			Player[] children = this.toArray(new Player[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getName(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Player obj : this) {
				result.add(obj.getName());
			}
		} else {
			for (Player obj : this) {
				String item = obj.getName();
				for(int i=0;i<filter.length;i++) {
					if (filter[i].equals(item)) {
						result.add(item);
						break;
					}
				}
			}
		}
		return result;
	}
	public PlayerSet filterName(String minValue, String maxValue) {
		PlayerSet result = new PlayerSet();
		for(Player obj : this) {
			if (minValue.compareTo(obj.getName()) <= 0 && maxValue.compareTo(obj.getName()) >= 0) {
				result.add(obj);
			}
		}
		return result;
	}

	public PlayerSet withName(String value) {
		for (Player obj : this) {
			obj.setName(value);
		}
		return this;
	}
	public HomeSet getHome(Home... filter) {
		HomeSet result = new HomeSet();
		if(listener != null) {
			result.withListener(listener);
			Player[] children = this.toArray(new Player[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getHome(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Player obj : this) {
				result.addAll(obj.getHome());
			}
			return result;
		}
		for (Player obj : this) {
			HomeSet item = obj.getHome();
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


	public PlayerSet withHome(Home value) {
		for (Player obj : this) {
			obj.withHome(value);
		}
		return this;
	}
	public LudoSet getCurrentGame(Ludo... filter) {
		LudoSet result = new LudoSet();
		if(listener != null) {
			result.withListener(listener);
			Player[] children = this.toArray(new Player[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getCurrentGame(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Player obj : this) {
				result.add(obj.getCurrentGame());
			}
			return result;
		}
		for (Player obj : this) {
			Ludo item = obj.getCurrentGame();
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


	public PlayerSet withCurrentGame(Ludo value) {
		for (Player obj : this) {
			obj.withCurrentGame(value);
		}
		return this;
	}
	public LudoSet getGame(Ludo... filter) {
		LudoSet result = new LudoSet();
		if(listener != null) {
			result.withListener(listener);
			Player[] children = this.toArray(new Player[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getGame(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Player obj : this) {
				result.add(obj.getGame());
			}
			return result;
		}
		for (Player obj : this) {
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


	public PlayerSet withGame(Ludo value) {
		for (Player obj : this) {
			obj.withGame(value);
		}
		return this;
	}
	public LudoSet getWonGame(Ludo... filter) {
		LudoSet result = new LudoSet();
		if(listener != null) {
			result.withListener(listener);
			Player[] children = this.toArray(new Player[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getWonGame(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Player obj : this) {
				result.add(obj.getWonGame());
			}
			return result;
		}
		for (Player obj : this) {
			Ludo item = obj.getWonGame();
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


	public PlayerSet withWonGame(Ludo value) {
		for (Player obj : this) {
			obj.withWonGame(value);
		}
		return this;
	}
	public MeepleSet getMeeple(Meeple... filter) {
		MeepleSet result = new MeepleSet();
		if(listener != null) {
			result.withListener(listener);
			Player[] children = this.toArray(new Player[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getMeeple(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Player obj : this) {
				result.addAll(obj.getMeeple());
			}
			return result;
		}
		for (Player obj : this) {
			MeepleSet item = obj.getMeeple();
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


	public PlayerSet withMeeple(Meeple value) {
		for (Player obj : this) {
			obj.withMeeple(value);
		}
		return this;
	}
	public PlayerSet getPrev(Player... filter) {
		PlayerSet result = new PlayerSet();
		if(listener != null) {
			result.withListener(listener);
			Player[] children = this.toArray(new Player[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getPrev(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Player obj : this) {
				result.add(obj.getPrev());
			}
			return result;
		}
		for (Player obj : this) {
			Player item = obj.getPrev();
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


	public PlayerSet withPrev(Player value) {
		for (Player obj : this) {
			obj.withPrev(value);
		}
		return this;
	}
	public PlayerSet getNext(Player... filter) {
		PlayerSet result = new PlayerSet();
		if(listener != null) {
			result.withListener(listener);
			Player[] children = this.toArray(new Player[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getNext(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Player obj : this) {
				result.add(obj.getNext());
			}
			return result;
		}
		for (Player obj : this) {
			Player item = obj.getNext();
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


	public PlayerSet withNext(Player value) {
		for (Player obj : this) {
			obj.withNext(value);
		}
		return this;
	}
	public StartSet getStart(Start... filter) {
		StartSet result = new StartSet();
		if(listener != null) {
			result.withListener(listener);
			Player[] children = this.toArray(new Player[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getStart(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Player obj : this) {
				result.add(obj.getStart());
			}
			return result;
		}
		for (Player obj : this) {
			Start item = obj.getStart();
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


	public PlayerSet withStart(Start value) {
		for (Player obj : this) {
			obj.withStart(value);
		}
		return this;
	}
	public TargetSet getTarget(Target... filter) {
		TargetSet result = new TargetSet();
		if(listener != null) {
			result.withListener(listener);
			Player[] children = this.toArray(new Player[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getTarget(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Player obj : this) {
				result.addAll(obj.getTarget());
			}
			return result;
		}
		for (Player obj : this) {
			TargetSet item = obj.getTarget();
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


	public PlayerSet withTarget(Target value) {
		for (Player obj : this) {
			obj.withTarget(value);
		}
		return this;
	}
}