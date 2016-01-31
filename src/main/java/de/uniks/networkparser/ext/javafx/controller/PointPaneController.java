package de.uniks.networkparser.ext.javafx.controller;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Random;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class PointPaneController extends AbstractModelController implements PropertyChangeListener{
	private Pane pane;
	private ArrayList<Circle> children = new ArrayList<Circle>();
	private String color="BLACK";
	private EventHandler<MouseEvent> mouseHandler;

	public PointPaneController(Node value) {
		if (value instanceof Pane) {
			this.pane = (Pane) value;
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
	 * @param max Maximum value.  Must be greater than min.
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
		if(this.pane!=null){
			this.pane.setOnMouseClicked(mouseHandler);
		}
		return this;
	}
}
