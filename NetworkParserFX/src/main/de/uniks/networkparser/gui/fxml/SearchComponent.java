package de.uniks.networkparser.gui.fxml;

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
