package de.uniks.networkparser.ext.javafx.window;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.ext.javafx.component.TableCellFX;
import javafx.scene.Parent;
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
