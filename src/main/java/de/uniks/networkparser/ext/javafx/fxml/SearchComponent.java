package de.uniks.networkparser.ext.javafx.fxml;

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
import java.net.URL;
import java.util.ResourceBundle;

import de.uniks.networkparser.ext.javafx.component.TableComponent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class SearchComponent implements Initializable{
	@FXML AnchorPane root;
	private TableComponent tableView;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tableView = new TableComponent();

		root.getChildren().add(tableView);
	}

	public TableComponent getTable() {
		return tableView;
	}
}
