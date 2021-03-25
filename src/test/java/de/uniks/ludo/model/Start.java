package de.uniks.ludo.model;

public class Start extends Field {


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
			oldValue.setStart(null);
		}
		this.player = value;
		if (value != null) {
			value.withStart(this);
		}
		firePropertyChange(PROPERTY_PLAYER, oldValue, value);
		return true;
	}

	public Start withPlayer(Player value) {
		this.setPlayer(value);
		return this;
	}

	public Player createPlayer() {
		Player value = new Player();
		withPlayer(value);
		return value;
	}
}