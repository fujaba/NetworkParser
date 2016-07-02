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
import java.text.NumberFormat;
import java.text.ParseException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import de.uniks.networkparser.gui.CellEditorElement;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.FieldTyp;

public class NumberTextField extends TextField implements CellEditorElement {
	private NumberFormat nf;
	private boolean init=false;
	private BigDecimal value = new BigDecimal(0);
	private CellEditorElement owner;
	private Column column;

	private void format() {
		try {
			String input = getText();
			if (input == null || input.length() == 0) {
				return;
			}
			Number parsedNumber = nf.parse(input);
			BigDecimal newValue = new BigDecimal(parsedNumber.toString());
			withValue(newValue);
			selectAll();
		} catch (ParseException ex) {
			// If parsing fails keep old number
			setText(nf.format(value));
		}
	}

	public CellEditorElement getOwner() {
		return owner;
	}

	public NumberTextField withOwner(CellEditorElement owner) {
		this.owner = owner;
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
	public void dispose() {
	}

	@Override
	public NumberTextField withColumn(Column column) {
		this.column = column;
		String numberFormat = column.getNumberFormat();
		this.nf = NumberFormat.getInstance();
		int pos=numberFormat.indexOf(".");
		if(pos>0&& pos<numberFormat.length()){
			nf.setMaximumFractionDigits(numberFormat.length() - pos -1);
			nf.setMaximumIntegerDigits(pos);
		}else{
			nf.setMaximumFractionDigits(0);
			nf.setMaximumIntegerDigits(numberFormat.length());
			nf.setGroupingUsed(false);
		}
		nf.setMinimumIntegerDigits(1);
		return this;
	}

	@Override
	public Object getValue(boolean convert) {
		return value;
	}

	@Override
	public NumberTextField withValue(Object value) {
		BigDecimal newValue;
		if(value instanceof BigDecimal){
			newValue = (BigDecimal)value;
		}else{
			newValue = new BigDecimal(""+value);
		}
		if(!init){
			setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					format();
					apply(APPLYACTION.SAVE);
				}
			});

			focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (!newValue.booleanValue()) {
						format();
					}
				}
			});
			addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>()
					{
						@Override
						public void handle(KeyEvent t)
						{
							if(t.getCode()==KeyCode.ENTER){
								apply(APPLYACTION.ENTER);
							}
						}
					});
			this.init = true;
		}
		this.value = newValue;
		if(nf!=null){
			setText(nf.format(newValue));
		}
		return this;
	}

	public FieldTyp getControllForTyp(Object value) {
		return FieldTyp.INTEGER;
	}

	public Column getColumn() {
		return column;
	}

	public NumberTextField withWidth(int value) {
		this.setPrefWidth(value);
		return this;
	}
}
