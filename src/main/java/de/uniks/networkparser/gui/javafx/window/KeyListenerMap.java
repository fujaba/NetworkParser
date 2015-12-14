package de.uniks.networkparser.gui.javafx.window;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
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
