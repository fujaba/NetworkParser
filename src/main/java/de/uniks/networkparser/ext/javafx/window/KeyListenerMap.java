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
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyListenerMap implements EventHandler<KeyEvent>{
	private WindowListener parent;
	private ArrayList<KeyListener> listener = new ArrayList<KeyListener>();
	private boolean isClosing;

	public KeyListenerMap() {
		withKeyListener(new KeyListener(KeyCode.ESCAPE, new Runnable() {

			@Override
			public void run() {
				if(KeyListenerMap.this.parent != null) {
					if(KeyListenerMap.this.parent.close()) {
						isClosing = true;
					}
				}
			}
		}).withInTableComponent(true));
	}

	public KeyListenerMap(WindowListener value) {
		this();
		this.parent = value;
	}

	@Override
	public void handle(KeyEvent event) {
		for(KeyListener listener : listener) {
			if(listener.matches(event)){
				listener.getRunnable().run();
			}
		}
		if(isClosing) {
			event.consume();
		}
	}

	public KeyListenerMap withKeyListener(KeyCode keyCode, Runnable runnable) {
		this.listener.add(new KeyListener(keyCode, runnable));
		return this;
	}
	public KeyListenerMap withKeyListener(KeyListener listener) {
		this.listener.add(listener);
		return this;
	}
}
