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
import java.time.LocalDate;
import java.util.GregorianCalendar;
import javafx.event.EventHandler;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.FieldTyp;

public class DateTimeEditControl extends EditControl<DatePicker>{
	@Override
	public Object getValue(boolean convert) {
		LocalDate date = this.control.getValue();
		if(date!=null){
			GregorianCalendar calendar=new GregorianCalendar();
			calendar.set(GregorianCalendar.YEAR, date.getYear());
			calendar.set(GregorianCalendar.MONTH, date.getMonth().getValue());
			calendar.set(GregorianCalendar.DAY_OF_MONTH, date.getDayOfMonth());
			return calendar.getTime();
		}
		return null;
//		return date;
	}

	@Override
	public FieldTyp getControllForTyp(Object value) {
		return FieldTyp.DATE;
	}

	@Override
	public DateTimeEditControl withValue(Object value) {
		if(value instanceof LocalDate){
			getControl().setValue((LocalDate)value);
		}
		return this;
	}

	@Override
	public DatePicker createControl(Column column) {
		return new DatePicker();
	}

	@Override
	protected void registerListener() {
		super.registerListener();

		control.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode().equals(KeyCode.ENTER)){
					apply(APPLYACTION.ENTER);
				}
			}
		});
	}
}
