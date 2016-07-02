package de.uniks.networkparser.ext.javafx.component;

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
import javafx.beans.property.SimpleObjectProperty;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.TableCellValue;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class TableCellValueFX extends SimpleObjectProperty<TableCellValue> implements TableCellValue{
	private Column column;
	private SendableEntityCreator creator;
	private Object item;

	public TableCellValueFX withItem(Object item) {
		this.item = item;
		this.set(this);
		return this;
	}

	public TableCellValueFX withColumn(Column column) {
		this.column = column;
		return this;
	}

	public TableCellValueFX withCreator(
			SendableEntityCreator creator) {
		this.creator = creator;
		return this;
	}

	@Override
	public Object getItem(){
		return item;
	}
	@Override
	public Column getColumn() {
		return column;
	}

	@Override
	public SendableEntityCreator getCreator() {
		return creator;
	}
	@Override
	public String toString(){
		if(creator==null){
			return "";
		}
		return ""+this.column.getValue(item, creator);
	}

	@Override
	public Object getSimpleValue() {
		if(creator==null){
			return "";
		}
		return this.column.getValue(item, creator);
	}
}
