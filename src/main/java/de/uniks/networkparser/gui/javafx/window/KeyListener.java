package de.uniks.networkparser.gui.javafx.window;

/*
 NetworkParser
 Copyright (c) 2011 - 2014, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import de.uniks.networkparser.gui.javafx.table.TableCellFX;

public class KeyListener {
	private Runnable runnable;
	private KeyCode keyCode;
	private boolean isAlt;
	private boolean isShift;
	private boolean isControl;
	private boolean isMetaKey;
	private boolean isShortcut;
	private boolean isInTableComponent;

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
	
	public KeyListener withInTableComponent(boolean value) {
		this.isInTableComponent = value;
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
		if(event.getTarget() instanceof Parent && isInTableComponent) {
			Parent n = ((Parent) event.getTarget());
			if(isInTableComponent==(n instanceof TableCellFX)){
				return false;
			}
			n = n.getParent();
			if(isInTableComponent==(n instanceof TableCellFX)){
				return false;
			}
		}
		
		return true;
	}
}
