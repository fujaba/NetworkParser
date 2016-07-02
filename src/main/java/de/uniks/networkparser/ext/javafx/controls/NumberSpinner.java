package de.uniks.networkparser.ext.javafx.controls;

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
import java.math.BigDecimal;
import javafx.beans.binding.NumberBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import de.uniks.networkparser.gui.CellEditorElement;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.FieldTyp;

public class NumberSpinner extends HBox implements CellEditorElement {
	private final static double ARROW_SIZE = 4;
	private BigDecimal stepWitdh = BigDecimal.ONE;

	private NumberTextField numberField;
	private Button incrementButton;
	private Button decrementButton;
	private NumberBinding buttonHeight;
	private NumberBinding spacing;
	private CellEditorElement owner;
	private Column column;

	/**
	 * increment number value by stepWidth
	 */
	private void increment() {
		BigDecimal value = (BigDecimal) numberField.getValue(false);
		value = value.add(getStepWitdh());
		numberField.withValue(value);
	}

	/**
	 * decrement number value by stepWidth
	 */
	private void decrement() {
		BigDecimal value = (BigDecimal) numberField.getValue(false);
		value = value.subtract(getStepWitdh());
		numberField.withValue(value);
	}

	public CellEditorElement getOwner() {
		return owner;
	}

	public NumberSpinner withOwner(CellEditorElement owner) {
		this.owner = owner;
		return this;
	}

	public BigDecimal getStepWitdh() {
		return stepWitdh;
	}

	public NumberSpinner withStepWitdh(BigDecimal stepWitdh) {
		this.stepWitdh = stepWitdh;
		return this;
	}
	public Column getColumn() {
		return column;
	}

	@Override
	public NumberSpinner withColumn(Column column) {
		this.column = column;
		// TextField
		numberField = new NumberTextField().withColumn(column).withOwner(this).withWidth(124);

		// Enable arrow keys for dec/inc
		numberField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.DOWN) {
					decrement();
					keyEvent.consume();
				}
				if (keyEvent.getCode() == KeyCode.UP) {
					increment();
					keyEvent.consume();
				}
			}
		});

		// Painting the up and down arrows
		Path arrowUp = new Path();
		arrowUp.getElements().addAll(new MoveTo(-ARROW_SIZE, 0), new LineTo(ARROW_SIZE, 0),
				new LineTo(0, -ARROW_SIZE), new LineTo(-ARROW_SIZE, 0));
		// mouse clicks should be forwarded to the underlying button
		arrowUp.setMouseTransparent(true);

		Path arrowDown = new Path();
		arrowDown.getElements().addAll(new MoveTo(-ARROW_SIZE, 0), new LineTo(ARROW_SIZE, 0),
				new LineTo(0, ARROW_SIZE), new LineTo(-ARROW_SIZE, 0));
		arrowDown.setMouseTransparent(true);

		// the spinner buttons scale with the textfield size
		// the following approach leads to the desired result, but it is
		// not fully understood why and obviously it is not quite elegant
		buttonHeight = numberField.heightProperty().subtract(3).divide(2);
		// give unused space in the buttons VBox to the incrementBUtton
		spacing = numberField.heightProperty().subtract(2).subtract(buttonHeight.multiply(2));

		// inc/dec buttons
		VBox buttons = new VBox();
		incrementButton = new Button();
		incrementButton.prefWidthProperty().bind(numberField.heightProperty());
		incrementButton.minWidthProperty().bind(numberField.heightProperty());
		incrementButton.maxHeightProperty().bind(buttonHeight.add(spacing));
		incrementButton.prefHeightProperty().bind(buttonHeight.add(spacing));
		incrementButton.minHeightProperty().bind(buttonHeight.add(spacing));
		incrementButton.setFocusTraversable(false);
		incrementButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				increment();
				ae.consume();
			}
		});

		// Paint arrow path on button using a StackPane
		StackPane incPane = new StackPane();
		incPane.getChildren().addAll(incrementButton, arrowUp);
		incPane.setAlignment(Pos.CENTER);

		decrementButton = new Button();
		decrementButton.prefWidthProperty().bind(numberField.heightProperty());
		decrementButton.minWidthProperty().bind(numberField.heightProperty());
		decrementButton.maxHeightProperty().bind(buttonHeight);
		decrementButton.prefHeightProperty().bind(buttonHeight);
		decrementButton.minHeightProperty().bind(buttonHeight);

		decrementButton.setFocusTraversable(false);
		decrementButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent ae) {
				decrement();
				ae.consume();
			}
		});

		StackPane decPane = new StackPane();
		decPane.getChildren().addAll(decrementButton, arrowDown);
		decPane.setAlignment(Pos.CENTER);

		buttons.getChildren().addAll(incPane, decPane);
		this.getChildren().addAll(numberField, buttons);
		return this;
	}

	@Override
	public void cancel() {
		if(owner!=null){
			owner.cancel();
		}
	}

	@Override
	public boolean setFocus(boolean value) {
		if(owner!=null){
			return owner.setFocus(value);
		}
		return false;
	}

	@Override
	public boolean onActive(boolean value) {
		if(owner!=null){
			return owner.onActive(value);
		}
		return false;
	}

	@Override
	public boolean nextFocus() {
		if(owner!=null){
			return owner.nextFocus();
		}
		return false;
	}

	@Override
	public void apply(APPLYACTION action) {
		if(owner!=null){
			owner.apply(action);
		}
	}

	@Override
	public Object getValue(boolean convert) {
		return numberField.getValue(convert);
	}

	@Override
	public NumberSpinner withValue(Object value) {
		 numberField.withValue(new BigDecimal(""+value));
		 return this;
	}

	public FieldTyp getControllForTyp(Object value) {
		return FieldTyp.INTEGER;
	}

	@Override
	public void dispose() {
	}
}
