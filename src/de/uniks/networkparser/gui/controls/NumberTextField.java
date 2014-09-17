package de.uniks.networkparser.gui.controls;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or – as soon they
 will be approved by the European Commission - subsequent
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
import de.uniks.networkparser.gui.table.CellEditorElement;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.table.FieldTyp;

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
	public void apply() {
		if(owner!=null){
			owner.apply();
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
                	apply();
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
    	                		apply();
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
}
