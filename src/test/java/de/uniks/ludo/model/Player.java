package de.uniks.ludo.model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;

import de.uniks.ludo.model.util.HomeSet;
import de.uniks.ludo.model.util.MeepleSet;
import de.uniks.ludo.model.util.TargetSet;


public class Player {
	protected PropertyChangeSupport listeners = null;

	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (listeners != null) {
			listeners.firePropertyChange(propertyName, oldValue, newValue);
			return true;
		}
		return false;
	}

	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(listener);
		return true;
	}

	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(propertyName, listener);
		return true;
	}

	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(listener);
		}
		listeners.removePropertyChangeListener(listener);
		return true;
	}

	public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(propertyName, listener);
		}
		return true;
	}
	@Override
	public String toString() {
		return this.getName();
	}
	public static final String PROPERTY_COLOR = "color";

	private String color;

	public String getColor() {
		return this.color;
	}

	public boolean setColor(String value) {
		if (this.color != value) {
			String oldValue = this.color;
			this.color = value;
			firePropertyChange(PROPERTY_COLOR, oldValue, value);
			return true;
		}
		return false;
	}

	public Player withColor(String value) {
		setColor(value);
		return this;
	}

	public static final String PROPERTY_NAME = "name";

	private String name;

	public String getName() {
		return this.name;
	}

	public boolean setName(String value) {
		if (this.name != value) {
			String oldValue = this.name;
			this.name = value;
			firePropertyChange(PROPERTY_NAME, oldValue, value);
			return true;
		}
		return false;
	}

	public Player withName(String value) {
		setName(value);
		return this;
	}


	public static final String PROPERTY_HOME = "home";

	private HomeSet home = null;

	public HomeSet getHome() {
		if(this.home == null) {
			return HomeSet.EMPTY_SET;
		}
		return this.home;
	}

	public boolean setHome(Home... values) {
		if (values == null) {
			return true;
		}
		boolean result=true;
		if (this.home == null) {
			this.home = new HomeSet();
		}
		for (Home item : values) {
			if (item == null) {
				continue;
			}
			if(item.setPlayer(this)) {
				result = result & this.home.rawAdd(item);
				firePropertyChange(PROPERTY_HOME, null, item);
			}
		}
		return result;
	}

	public Player withHome(Object... values) {
		if (values == null) {
			return this;
		}
		for (Object item : values) {
			if (item == null) {
				continue;
			}
			if (item instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) item;
				setHome(collection.toArray(new Home[collection.size()]));
			} else {
				setHome((Home) item);
			}
		}
		return this;
	}

	public Player withoutHome(Home... value) {
		if(this.home == null) {
			return this;
		}
		for (Home item : value) {
			if (item != null) {
				if (this.home.remove(item)) {
					item.withPlayer(null);
				}
			}
		}
		return this;
	}

	public Home createHome() {
		Home value = new Home();
		withHome(value);
		return value;
	}

	public static final String PROPERTY_CURRENTGAME = "currentGame";

	private Ludo currentGame = null;

	public Ludo getCurrentGame() {
		return this.currentGame;
	}

	public boolean setCurrentGame(Ludo value) {
		if (this.currentGame == value) {
			return false;
		}
		Ludo oldValue = this.currentGame;
		if (this.currentGame != null) {
			this.currentGame = null;
			oldValue.setCurrentPlayer(null);
		}
		this.currentGame = value;
		if (value != null) {
			value.withCurrentPlayer(this);
		}
		firePropertyChange(PROPERTY_CURRENTGAME, oldValue, value);
		return true;
	}

	public Player withCurrentGame(Ludo value) {
		this.setCurrentGame(value);
		return this;
	}

	public Ludo createCurrentGame() {
		Ludo value = new Ludo();
		withCurrentGame(value);
		return value;
	}

	public static final String PROPERTY_GAME = "game";

	private Ludo game = null;

	public Ludo getGame() {
		return this.game;
	}

	public boolean setGame(Ludo value) {
		if (this.game == value) {
			return false;
		}
		Ludo oldValue = this.game;
		if (this.game != null) {
			this.game = null;
			oldValue.withoutPlayers(this);
		}
		this.game = value;
		if (value != null) {
			value.withPlayers(this);
		}
		firePropertyChange(PROPERTY_GAME, oldValue, value);
		return true;
	}

	public Player withGame(Ludo value) {
		this.setGame(value);
		return this;
	}

	public Ludo createGame() {
		Ludo value = new Ludo();
		withGame(value);
		return value;
	}

	public static final String PROPERTY_WONGAME = "wonGame";

	private Ludo wonGame = null;

	public Ludo getWonGame() {
		return this.wonGame;
	}

	public boolean setWonGame(Ludo value) {
		if (this.wonGame == value) {
			return false;
		}
		Ludo oldValue = this.wonGame;
		if (this.wonGame != null) {
			this.wonGame = null;
			oldValue.setWinner(null);
		}
		this.wonGame = value;
		if (value != null) {
			value.withWinner(this);
		}
		firePropertyChange(PROPERTY_WONGAME, oldValue, value);
		return true;
	}

	public Player withWonGame(Ludo value) {
		this.setWonGame(value);
		return this;
	}

	public Ludo createWonGame() {
		Ludo value = new Ludo();
		withWonGame(value);
		return value;
	}

	public static final String PROPERTY_MEEPLE = "meeple";

	private MeepleSet meeple = null;

	public MeepleSet getMeeple() {
		if(this.meeple == null) {
			return MeepleSet.EMPTY_SET;
		}
		return this.meeple;
	}

	public boolean setMeeple(Meeple... values) {
		if (values == null) {
			return true;
		}
		boolean result=true;
		if (this.meeple == null) {
			this.meeple = new MeepleSet();
		}
		for (Meeple item : values) {
			if (item == null) {
				continue;
			}
			if(item.setPlayer(this)) {
				result = result & this.meeple.rawAdd(item);
				firePropertyChange(PROPERTY_MEEPLE, null, item);
			}
		}
		return result;
	}

	public Player withMeeple(Object... values) {
		if (values == null) {
			return this;
		}
		for (Object item : values) {
			if (item == null) {
				continue;
			}
			if (item instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) item;
				setMeeple(collection.toArray(new Meeple[collection.size()]));
			} else {
				setMeeple((Meeple) item);
			}
		}
		return this;
	}

	public Player withoutMeeple(Meeple... value) {
		if(this.meeple == null) {
			return this;
		}
		for (Meeple item : value) {
			if (item != null) {
				if (this.meeple.remove(item)) {
					item.withPlayer(null);
				}
			}
		}
		return this;
	}

	public Meeple createMeeple() {
		Meeple value = new Meeple();
		withMeeple(value);
		return value;
	}

	public static final String PROPERTY_PREV = "prev";

	private Player prev = null;

	public Player getPrev() {
		return this.prev;
	}

	public boolean setPrev(Player value) {
		if (this.prev == value) {
			return false;
		}
		Player oldValue = this.prev;
		if (this.prev != null) {
			this.prev = null;
			oldValue.setNext(null);
		}
		this.prev = value;
		if (value != null) {
			value.withNext(this);
		}
		firePropertyChange(PROPERTY_PREV, oldValue, value);
		return true;
	}

	public Player withPrev(Player value) {
		this.setPrev(value);
		return this;
	}

	public Player createPrev() {
		Player value = new Player();
		withPrev(value);
		return value;
	}

	public static final String PROPERTY_NEXT = "next";

	private Player next = null;

	public Player getNext() {
		return this.next;
	}

	public boolean setNext(Player value) {
		if (this.next == value) {
			return false;
		}
		Player oldValue = this.next;
		if (this.next != null) {
			this.next = null;
			oldValue.setPrev(null);
		}
		this.next = value;
		if (value != null) {
			value.withPrev(this);
		}
		firePropertyChange(PROPERTY_NEXT, oldValue, value);
		return true;
	}

	public Player withNext(Player value) {
		this.setNext(value);
		return this;
	}

	public Player createNext() {
		Player value = new Player();
		withNext(value);
		return value;
	}

	public static final String PROPERTY_START = "start";

	private Start start = null;

	public Start getStart() {
		return this.start;
	}

	public boolean setStart(Start value) {
		if (this.start == value) {
			return false;
		}
		Start oldValue = this.start;
		if (this.start != null) {
			this.start = null;
			oldValue.setPlayer(null);
		}
		this.start = value;
		if (value != null) {
			value.withPlayer(this);
		}
		firePropertyChange(PROPERTY_START, oldValue, value);
		return true;
	}

	public Player withStart(Start value) {
		this.setStart(value);
		return this;
	}

	public Start createStart() {
		Start value = new Start();
		withStart(value);
		return value;
	}

	public static final String PROPERTY_TARGET = "target";

	private TargetSet target = null;

	public TargetSet getTarget() {
		if(this.target == null) {
			return TargetSet.EMPTY_SET;
		}
		return this.target;
	}

	public boolean setTarget(Target... values) {
		if (values == null) {
			return true;
		}
		boolean result=true;
		if (this.target == null) {
			this.target = new TargetSet();
		}
		for (Target item : values) {
			if (item == null) {
				continue;
			}
			if(item.setPlayer(this)) {
				result = result & this.target.rawAdd(item);
				firePropertyChange(PROPERTY_TARGET, null, item);
			}
		}
		return result;
	}

	public Player withTarget(Object... values) {
		if (values == null) {
			return this;
		}
		for (Object item : values) {
			if (item == null) {
				continue;
			}
			if (item instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) item;
				setTarget(collection.toArray(new Target[collection.size()]));
			} else {
				setTarget((Target) item);
			}
		}
		return this;
	}

	public Player withoutTarget(Target... value) {
		if(this.target == null) {
			return this;
		}
		for (Target item : value) {
			if (item != null) {
				if (this.target.remove(item)) {
					item.withPlayer(null);
				}
			}
		}
		return this;
	}

	public Target createTarget() {
		Target value = new Target();
		withTarget(value);
		return value;
	}
}