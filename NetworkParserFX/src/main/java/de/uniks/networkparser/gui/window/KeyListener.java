package de.uniks.networkparser.gui.window;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyListener {
	private Runnable runnable;
	private KeyCode keyCode;
	private boolean isAlt;
	private boolean isShift;
	private boolean isControl;
	private boolean isMetaKey;
	private boolean isShortcut;

	public KeyListener(KeyCode keycode, Runnable runnable){
		this.keyCode = keycode;
		this.runnable = runnable;
	}
	
	public KeyListener(KeyCode keyCode) {
		this.keyCode = keyCode;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public boolean isControl() {
		return isControl;
	}

	public KeyListener withControl(boolean value) {
		this.isControl = value;
		return this;
	}

	public boolean isShift() {
		return isShift;
	}

	public KeyListener withShift(boolean value) {
		this.isShift = value;
		return this;
	}

	public boolean isAlt() {
		return isAlt;
	}

	public KeyListener withAlt(boolean value) {
		this.isAlt = value;
		return this;
	}

	public KeyCode getKeyCode() {
		return keyCode;
	}

	public boolean isMetaKey() {
		return isMetaKey;
	}

	public KeyListener withMetaKey(boolean value) {
		this.isMetaKey = value;
		return this;
	}

	public boolean isShortcut() {
		return isShortcut;
	}

	public KeyListener withShortcut(boolean value) {
		this.isShortcut = value;
		return this;
	}

	public boolean matches(KeyEvent event) {
		if(keyCode!=event.getCode()){
			return false;
		}
		if(isAlt!=event.isAltDown()){
			return false;
		}
		if(isShift!=event.isShiftDown()){
			return false;
		}
		if(isControl!=event.isControlDown()){
			return false;
		}
		if(isMetaKey!=event.isMetaDown()){
			return false;
		}
		if(isShortcut!=event.isShortcutDown()){
			return false;
		}
		return true;
	}
}
