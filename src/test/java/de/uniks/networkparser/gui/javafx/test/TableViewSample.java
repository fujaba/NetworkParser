package de.uniks.networkparser.gui.javafx.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TableViewSample extends Application {

  @Override
  public void start(Stage primaryStage) {
	final BorderPane root = new BorderPane();
	final TableView<PersonGUI> table = new TableView<>();
	table.getItems().addAll(
		new PersonGUI("Jacob", "Smith", "jacob.smith@example.com"),
		new PersonGUI("Isabella", "Johnson", "isabella.johnson@example.com"),
		new PersonGUI("Ethan", "Williams", "ethan.williams@example.com"),
		new PersonGUI("Emma", "Jones", "emma.jones@example.com"),
		new PersonGUI("Michael", "Brown", "michael.brown@example.com"));
	TableColumn<PersonGUI, String> firstNameCol = new TableColumn<>("First Name");
	TableColumn<PersonGUI, String> lastNameCol = new TableColumn<>("Last Name");
	TableColumn<PersonGUI, String> emailCol = new TableColumn<>("Email");
	firstNameCol.setCellValueFactory(new PropertyValueFactory<PersonGUI, String>("firstName"));
	lastNameCol.setCellValueFactory(new PropertyValueFactory<PersonGUI, String>("lastName"));
	emailCol.setCellValueFactory(new PropertyValueFactory<PersonGUI, String>("email"));

	firstNameCol.setMinWidth(150);
	lastNameCol.setMinWidth(150);
	emailCol.setMinWidth(150);

	table.getColumns().addAll(Arrays.asList(firstNameCol, lastNameCol, emailCol));

	final ContextMenu tableContextMenu = new ContextMenu();
	final MenuItem addMenuItem = new MenuItem("Add...");
	final MenuItem deleteSelectedMenuItem = new MenuItem("Delete selected");
	deleteSelectedMenuItem.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
	deleteSelectedMenuItem.setOnAction(new EventHandler<ActionEvent>() {
	  @Override
	  public void handle(ActionEvent event) {
		final List<PersonGUI> selectedPeople = new ArrayList<>(table.getSelectionModel().getSelectedItems());
		table.getItems().removeAll(selectedPeople);
	  }
	});
	tableContextMenu.getItems().addAll(addMenuItem, deleteSelectedMenuItem);

	table.setContextMenu(tableContextMenu);

	table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

//	table.setRowFactory(new Callback<TableView<Person>, TableRow<Person>>() {
//	  @Override
//	  public TableRow<Person> call(TableView<Person> tableView) {
//		final TableRow<Person> row = new TableRow<>();
//		final ContextMenu rowMenu = new ContextMenu();
//		ContextMenu tableMenu = tableView.getContextMenu();
//		if (tableMenu != null) {
//		  rowMenu.getItems().addAll(tableMenu.getItems());
//		  rowMenu.getItems().add(new SeparatorMenuItem());
//		}
//		MenuItem editItem = new MenuItem("Edit");
//		MenuItem removeItem = new MenuItem("Delete");
//		rowMenu.getItems().addAll(editItem, removeItem);
//		row.contextMenuProperty().bind(
//			Bindings.when(Bindings.isNotNull(row.itemProperty()))
//			.then(rowMenu)
//			.otherwise((ContextMenu) null));
//		return row;
//	  }
//	});

//	emailCol.setCellFactory(new Callback<TableColumn<Person, String>, TableCell<Person, String>>() {
//
//	  @Override
//	  public TableCell<Person, String> call(TableColumn<Person, String> col) {
//		final TableCell<Person, String> cell = new TableCell<>();
//		cell.itemProperty().addListener(new ChangeListener<String>() {
//
//		  @Override
//		  public void changed(ObservableValue<? extends String> obs, String oldValue, String newValue) {
//			if (newValue != null) {
//			  final ContextMenu cellMenu = new ContextMenu();
//			  final TableRow<?> row = cell.getTableRow();
//			  final ContextMenu rowMenu;
//			  if (row != null) {
//				rowMenu = cell.getTableRow().getContextMenu();
//				if (rowMenu != null) {
//				  cellMenu.getItems().addAll(rowMenu.getItems());
//				  cellMenu.getItems().add(new SeparatorMenuItem());
//				} else {
//				  final ContextMenu tableMenu = cell.getTableView().getContextMenu();
//				  if (tableMenu != null) {
//					cellMenu.getItems().addAll(tableMenu.getItems());
//					cellMenu.getItems().add(new SeparatorMenuItem());
//				  }
//				}
//			  }
//			  final MenuItem emailMenuItem = new MenuItem("Email");
//			  emailMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//				@Override
//				public void handle(ActionEvent event) {
//				  System.out.println("Email " + cell.getTableRow().getItem() + " at " + cell.getItem());
//				}
//			  });
//			  cellMenu.getItems().add(emailMenuItem);
//			  cell.setContextMenu(cellMenu);
//			} else {
//			  cell.setContextMenu(null);
//			}
//		  }
//		});
//		cell.textProperty().bind(cell.itemProperty());
//		return cell;
//	  }
//
//	});

	root.setCenter(table);
	Scene scene = new Scene(root, 600, 400);
	primaryStage.setScene(scene);
	primaryStage.show();
  }

  public static void main(String[] args) {
	launch(args);
  }
}
