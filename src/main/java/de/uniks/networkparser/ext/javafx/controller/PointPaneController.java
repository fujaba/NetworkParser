package de.uniks.networkparser.ext.javafx.controller;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.uniks.networkparser.interfaces.SendableEntity;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class PointPaneController extends AbstractModelController implements PropertyChangeListener, EventHandler<MouseEvent>, SendableEntity {
	public static final String PROPERTY_CLICK="click";
	public static final String PROPERTY_VALUE="value";
	private Pane pane;
	private ArrayList<Circle> children = new ArrayList<Circle>();
	private String color="BLACK";
	private EventHandler<MouseEvent> mouseHandler;
	private PropertyChangeSupport listeners;
	private int number;
	private int delay = 0;

	public PointPaneController(Node value) {
		if (value instanceof Pane) {
			this.pane = (Pane) value;
			this.pane.setOnMouseClicked(this);
		}
	}

	public void setValue(int number) {
		this.reset();
		if (number == 1) {
			this.addCircle(2, 2);
		} else if (number == 2) {
			this.addCircle(1, 1, 3, 3);
		} else if (number == 3) {
			this.addCircle(1, 1, 2, 2, 3, 3);
		} else if (number == 4) {
			this.addCircle(1, 1, 1, 3, 3, 1, 3, 3);
		} else if (number == 5) {
			this.addCircle(1, 1, 1, 3, 2, 2, 3, 1, 3, 3);
		} else if (number == 6) {
			this.addCircle(1, 1, 1, 2, 1, 3, 3, 1, 3, 2, 3, 3);
		} else if (number == 7) {
			this.addCircle(1, 1, 1, 2, 1, 3, 2, 2, 3, 1, 3, 2, 3, 3);
		} else if (number == 8) {
			this.addCircle(1, 1, 1, 2, 1, 3, 2, 1, 2, 3, 3, 1, 3, 2, 3, 3);
		} else if (number == 9) {
			this.addCircle(1, 1, 1, 2, 1, 3, 2, 1, 2, 2, 2, 3, 3, 1, 3, 2, 3, 3);
		}
		int oldValue = this.number;
		this.number = number;
		firePropertyChange(PROPERTY_VALUE, oldValue, number);
	}

	public void addCircle(int... values) {
		if(values.length%2>0){
			return;
		}
		for(int i=0;i<values.length;i+=2){
			this.addCircle(getCircle(values[i], values[i+1]));
		}
	}

	private void reset() {
		while (this.children.size() > 0) {
			Circle circle = children.remove(0);
			this.pane.getChildren().remove(circle);
		}
	}

	private void addCircle(Circle value) {
		if (value != null && this.pane != null) {
			this.pane.getChildren().add(value);
			this.children.add(value);
		}
	}

	private Circle getCircle(double x, double y) {
		if (this.pane == null) {
			return null;
		}
		double width = this.pane.getPrefWidth();
		Circle circle = new Circle();
		circle.setFill(Paint.valueOf(getColor()));

		circle.setRadius(width / 10);
		circle.setLayoutX(width / 4 * x);
		circle.setLayoutY(width / 4 * y);
		return circle;
	}

	public String getColor() {
		return color;
	}

	public PointPaneController withColor(String color) {
		this.color = color;
		return this;
	}
	
	public PointPaneController withDelay(int delay) {
		this.delay = delay;
		return this;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt != null){
			int val = 0;
			if(evt.getNewValue() != null) {
				val=(Integer)evt.getNewValue();
			}
			if(this.delay>0) {
				Timer timer = new Timer();
				final int value = val;
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						PointPaneController.this.setValue(value);
					}
				}, delay);
			} else {
				this.setValue(val);
			}
		}
	}

	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value. Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	@Override
	public void initPropertyChange(Object model, Node gui) {
	}

	public PointPaneController addMouseListener(EventHandler<MouseEvent> listener) {
		this.mouseHandler = listener;
		return this;
	}
	
	public Pane getPane() {
		return pane;
	}

	@Override
	public void handle(MouseEvent event) {
		if(this.mouseHandler != null){
			this.mouseHandler.handle(event);
		}
		firePropertyChange(PROPERTY_CLICK, null, number);
	}

	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (listeners != null) {
			listeners.firePropertyChange(propertyName, oldValue, newValue);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners == null) {
	   		listeners = new PropertyChangeSupport(this);
	   	}
		listeners.addPropertyChangeListener(propertyName, listener);
		return true;
	}

	@Override
	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
	   		listeners = new PropertyChangeSupport(this);
	   	}
		listeners.addPropertyChangeListener(listener);
		return true;
	}

	@Override
	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
	   		return true;
	   	}
		listeners.removePropertyChangeListener(listener);
		return true;
	}

	@Override
	public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners == null) {
	   		return true;
	   	}
		listeners.removePropertyChangeListener(propertyName, listener);
		return true;
	}
}
