package de.uniks.networkparser.gui.table;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import com.sun.javafx.scene.control.skin.TableColumnHeader;

public class TableColumnHeaderFX extends TableColumnHeader{

	public TableColumnHeaderFX(TableView<?> viewer, TableColumn<?, ?> column) {
		super(viewer, column);
	}
	
}
