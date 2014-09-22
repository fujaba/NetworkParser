package de.uniks.networkparser.gui.fxml;

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
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import de.uniks.networkparser.gui.table.SearchTableComponent;

public class SearchComponent implements Initializable{
	@FXML AnchorPane root;
	private SearchTableComponent tableView;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
//		  FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
//		  "SearchTableComponent.fxml"));
//		          fxmlLoader.setRoot(this);
//		          fxmlLoader.setController(this);
//
//		          try {
//		              fxmlLoader.load();
//		          } catch (IOException exception) {
//		              throw new RuntimeException(exception);
//		          }
		tableView = new SearchTableComponent();

		
//        JsonIdMap map = new  JsonIdMap();
//        map.withCreator(new TableListCreator());
//        map.withCreator(new PersonCreator());
//        map.withCreator(new GroupAccountCreator());
//        
//        tableView.withMap(map);
//        GroupAccount groupAccount = new GroupAccount();
//        
//        Person albert = groupAccount.createPersons().withName("Albert");
//        
//        groupAccount.createPersons().withName("Nina");
//        
//        tableView.withColumn(new Column().withAttrName(Person.PROPERTY_NAME).withStyle(new Style().withWidth(100)));
//        tableView.withColumn(new Column().withAttrName(Person.PROPERTY_BALANCE).withStyle(new Style().withWidth(100)));
//        tableView.withSearchProperties(Person.PROPERTY_NAME);
//        tableView.withList(groupAccount,  GroupAccount.PROPERTY_PERSONS);
		
		root.getChildren().add(tableView);
	}
	
	public SearchTableComponent getTable() {
		return tableView;
	}

}
