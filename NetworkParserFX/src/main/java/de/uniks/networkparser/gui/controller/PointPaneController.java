package de.uniks.networkparser.gui.controller;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class PointPaneController {
	private Pane pane;
	private ArrayList<Circle> children = new ArrayList<Circle>();
	private String color="BLACK";

	public PointPaneController(Node value) {
		if (value instanceof Pane) {
			this.pane = (Pane) value;
		}
	}

	public void setValue(int number) {
		this.reset();
		if (number == 1) {
			this.addCircle(getCircle(2, 2));
		} else if (number == 2) {
			this.addCircle(getCircle(1, 1));
			this.addCircle(getCircle(3, 3));
		} else if (number == 3) {
			this.addCircle(getCircle(1, 1));
			this.addCircle(getCircle(2, 2));
			this.addCircle(getCircle(3, 3));
		} else if (number == 4) {
			this.addCircle(getCircle(1, 1));
			this.addCircle(getCircle(1, 3));
			this.addCircle(getCircle(3, 1));
			this.addCircle(getCircle(3, 3));
		} else if (number == 5) {
			this.addCircle(getCircle(1, 1));
			this.addCircle(getCircle(1, 3));
			this.addCircle(getCircle(2, 2));
			this.addCircle(getCircle(3, 1));
			this.addCircle(getCircle(3, 3));
		} else if (number == 6) {
			this.addCircle(getCircle(1, 1));
			this.addCircle(getCircle(1, 2));
			this.addCircle(getCircle(1, 3));
			this.addCircle(getCircle(3, 1));
			this.addCircle(getCircle(3, 2));
			this.addCircle(getCircle(3, 3));
		} else if (number == 7) {
			this.addCircle(getCircle(1, 1));
			this.addCircle(getCircle(1, 2));
			this.addCircle(getCircle(1, 3));
			this.addCircle(getCircle(2, 2));
			this.addCircle(getCircle(3, 1));
			this.addCircle(getCircle(3, 2));
			this.addCircle(getCircle(3, 3));
		} else if (number == 8) {
			this.addCircle(getCircle(1, 1));
			this.addCircle(getCircle(1, 2));
			this.addCircle(getCircle(1, 3));
			this.addCircle(getCircle(2, 1));
			this.addCircle(getCircle(2, 3));
			this.addCircle(getCircle(3, 1));
			this.addCircle(getCircle(3, 2));
			this.addCircle(getCircle(3, 3));
		} else if (number == 9) {
			this.addCircle(getCircle(1, 1));
			this.addCircle(getCircle(1, 2));
			this.addCircle(getCircle(1, 3));
			this.addCircle(getCircle(2, 1));
			this.addCircle(getCircle(2, 2));
			this.addCircle(getCircle(2, 3));
			this.addCircle(getCircle(3, 1));
			this.addCircle(getCircle(3, 2));
			this.addCircle(getCircle(3, 3));
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

}
