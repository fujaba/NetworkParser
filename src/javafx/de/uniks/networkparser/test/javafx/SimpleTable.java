package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.SimpleController;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.controls.TableComponent;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.util.GroupAccountCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;
import javafx.application.Application;
import javafx.stage.Stage;

public class SimpleTable extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		SimpleController controller = SimpleController.create(primaryStage);
		IdMap map = new IdMap();
		map.with(new GroupAccountCreator(), new PersonCreator());
		TableComponent table = new TableComponent();
		table.withMap(map);
		GroupAccount groupAccount = new GroupAccount();
		Person albert = new Person().withName("Albert");
		Person tom = new Person().withName("Tom");
		Person stefan = new Person().withName("Stefan");
		groupAccount.withPersons(albert, tom, stefan);

		table.withSearchProperties(Person.PROPERTY_NAME);
		table.withColumn(new Column().withAttribute(Person.PROPERTY_NAME).withLabel("Name"));

//				TableCellFactory tableCellFactory = new TableCellFactory().withUpdateListener(new UpdateItemCell() {
//					@Override
//					public boolean updateItem(TableCellFX cell, TableCellValue item, boolean empty) {
//						if(empty) {
//							return false;
//						}
//						ImageView imageView = new ImageView(DialogBox.class.getResource("JavaCup32.png").toString());
//						cell.setGraphic(imageView);
//						cell.setText(null);
//						return true;
//					}
//				});

//				table.withColumn(new Column().withAttrName(Person.PROPERTY_NAME).withLabel("Image"), tableCellFactory);
		table.withList(groupAccount, GroupAccount.PROPERTY_PERSONS);
//FIXME		return table;
		controller.show(table);
	}
}

