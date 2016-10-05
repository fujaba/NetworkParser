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
import java.util.concurrent.LinkedBlockingQueue;

import de.uniks.networkparser.interfaces.SendableEntity;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class PointPaneController extends AbstractModelController implements PropertyChangeListener, EventHandler<MouseEvent>, SendableEntity{
	public static final String PROPERTY_CLICK="click";
	public static final String PROPERTY_VALUE="value";
	private Pane pane;
	private ArrayList<Circle> children = new ArrayList<Circle>();
	private String color="BLACK";
	private EventHandler<MouseEvent> mouseHandler;
	private PropertyChangeSupport listeners;
	private int number;
	private Timeline timeline = new Timeline();
	private LinkedBlockingQueue<KeyFrame> animations = new LinkedBlockingQueue<KeyFrame>();
	private boolean animation=true;

	public PointPaneController(Node value) {
		if (value instanceof Pane) {
			this.pane = (Pane) value;
			this.pane.setOnMouseClicked(this);
		}
		this.timeline.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				PointPaneController.this.finishAnimationEvent();
			}
		});
	}
	
	public void addW6Listener() {
		EventHandler<MouseEvent> eventListener = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				PointPaneController that = PointPaneController.this;
				if(that.timeline.getStatus()==Status.STOPPED) {
					int point = that.randInt(1, 6);
					that.setValue(point);
				}
			}
		};
		this.addMouseListener(eventListener);
	}

	public void setValue(int number) {
		if(animation == false) {
			showNumber(number);
			fireEvent(number);
			return;
		}
		Rotate rotationTransform = new Rotate(0, pane.getLayoutX()+pane.getWidth()/2, pane.getLayoutY()+pane.getHeight()/2);
		this.pane.getTransforms().setAll(rotationTransform);

		// Rotate
		KeyFrame animation = new KeyFrame(Duration.seconds(2), new KeyValue(rotationTransform.angleProperty(), 360));
		this.animations.add(animation);

		SimpleIntegerProperty item = new SimpleIntegerProperty(0);
		item.addListener(new ChangeListener<Number>() {
			public void changed(javafx.beans.value.ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				showNumber((int)newValue);
			};
		});
		int count=600;
		this.animations.add(new KeyFrame(Duration.millis(count), new KeyValue(item, 6)));
		this.animations.add(new KeyFrame(Duration.millis(count), new KeyValue(item, 1)));
		this.animations.add(new KeyFrame(Duration.millis(count), new KeyValue(item, 6)));
		this.animations.add(new KeyFrame(Duration.millis(count), new KeyValue(item, 1)));
		this.animations.add(new KeyFrame(Duration.ONE, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				fireEvent(number);				
			}
		}, new KeyValue(item, number)));

		// Run Animation
		finishAnimationEvent();
	}
	
	
	public void finishAnimationEvent() {
		if(animations.size()>0 && timeline.getStatus()==Status.STOPPED) {
			timeline.getKeyFrames().setAll(animations.poll());
			timeline.play();
		}
	}
	
	public PointPaneController withAnimation(boolean value) {
		this.animation = value;
		return this;
	}
	
	public void showNumber(int number) {
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
	}
	
	private void fireEvent(int number) {
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
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt != null){
			int val = 0;
			if(evt.getNewValue() != null) {
				val=(Integer)evt.getNewValue();
			}
			this.setValue(val);
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
