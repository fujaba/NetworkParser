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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.javafx.TableList;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.FieldTyp;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

public class ComboEditControl extends EditControl<ComboBox<Object>>{
	private SimpleList<Object> list;

	@Override
	public Object getValue(boolean convert) {
		return this.control.getValue();
	}

	@Override
	public FieldTyp getControllForTyp(Object value) {
		return FieldTyp.COMBOBOX;
	}

	@Override
	public ComboEditControl withValue(Object value) {
		getControl().setValue(value);
		return this;
	}

	@Override
	public ComboBox<Object> createControl(Column column) {
		control = new ComboBox<Object>();
		if(column.getFieldTyp()==FieldTyp.ASSOC){
			SendableEntityCreator creator = map.getCreatorClass(value);

			if(creator!=null){
				this.list = map.getTypList(creator);
			}
		}else if(column.getNumberFormat()!=null && column.getNumberFormat().startsWith("[")){
			CharacterBuffer tokener=new CharacterBuffer();
			tokener.with(column.getNumberFormat());
			tokener.withStartPosition(1);
			tokener.withBufferLength(tokener.length()-1);
			CharacterBuffer sub;
			this.list=new TableList();
			do{
				sub = tokener.nextString(new CharacterBuffer(), true, false, ',');
				if(sub.length()>0){
					list.add(sub);
				}
			}while(sub.length()>0);
		}
		if(list!=null){
//			String[] items=new String[list.size()];
//			int count=0;
//			for(Object item : list){
//				items[count++]=item.toString();
//			}
			control.setItems( FXCollections.observableList(this.list) );
		}
		return control;
	}
	public ComboEditControl addChoiceList(Object value) {
		control.getItems().add(value);
		return this;
	}

}
