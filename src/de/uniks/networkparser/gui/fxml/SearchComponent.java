package de.uniks.networkparser.gui.fxml;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import de.uniks.networkparser.gui.table.SearchTableComponent;

public class SearchComponent implements Initializable{
	@FXML AnchorPane root;
	private SearchTableComponent table;

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
		table = new SearchTableComponent();
		root.getChildren().add(table);
//		getC
	}
	
	public SearchTableComponent getTable() {
		return table;
	}

}
