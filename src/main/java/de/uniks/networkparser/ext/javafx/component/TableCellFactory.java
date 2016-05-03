package de.uniks.networkparser.ext.javafx.component;

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
import de.uniks.networkparser.ext.javafx.controls.EditFieldMap;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.TableCellValue;
import javafx.scene.control.TableCell;

public class TableCellFactory {
	private UpdateItemCell updateListener;
	public TableCell<Object, TableCellValue> create(TableComponent tableComponent, Column column, EditFieldMap editFieldMap) {
		return new TableCellFX().withTableComponent(tableComponent).withColumn(column).withEditFieldMap(editFieldMap).withUpdateListener(updateListener);
	}

	public TableCellFactory withUpdateListener(UpdateItemCell updateListener) {
		this.updateListener = updateListener;
		return this;
	}
}
