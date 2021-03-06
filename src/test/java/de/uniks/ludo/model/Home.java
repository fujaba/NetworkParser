package de.uniks.ludo.model;
import de.uniks.ludo.model.Player;


public class Home extends Field {


	public static final String PROPERTY_PLAYER = "player";

	private Player player = null;

	public Player getPlayer() {
		return this.player;
	}

	public boolean setPlayer(Player value) {
		if (this.player == value) {
			return false;
		}
		Player oldValue = this.player;
		if (this.player != null) {
			this.player = null;
			oldValue.withoutHome(this);
		}
		this.player = value;
		if (value != null) {
			value.withHome(this);
		}
		firePropertyChange(PROPERTY_PLAYER, oldValue, value);
		return true;
	}

	public Home withPlayer(Player value) {
		this.setPlayer(value);
		return this;
	}

	public Player createPlayer() {
		Player value = new Player();
		withPlayer(value);
		return value;
	}
}