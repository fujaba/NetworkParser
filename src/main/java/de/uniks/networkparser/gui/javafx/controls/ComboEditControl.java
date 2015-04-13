package de.uniks.networkparser.gui.javafx.controls;

/*
 NetworkParser
 Copyright (c) 2011 - 2014, Stefan Lindel
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
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import de.uniks.networkparser.StringTokener;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.FieldTyp;
import de.uniks.networkparser.gui.TableList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;

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
			StringTokener tokener=new StringTokener();
			tokener.withText(column.getNumberFormat());
			tokener.setIndex(1);
			tokener.setLength(tokener.length()-1);
			String sub;
			this.list=new TableList();
			do{
				sub = tokener.nextString(',', true);
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
