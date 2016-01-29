package de.uniks.networkparser.gui.javafx.test;

import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.TableCellValue;
import de.uniks.networkparser.gui.javafx.dialog.DialogBox;
import de.uniks.networkparser.gui.javafx.table.TableCellFX;
import de.uniks.networkparser.gui.javafx.table.TableCellFactory;
import de.uniks.networkparser.gui.javafx.table.TableComponent;
import de.uniks.networkparser.gui.javafx.table.UpdateItemCell;
import de.uniks.networkparser.gui.javafx.window.FXStageController;
import de.uniks.networkparser.gui.javafx.window.SimpleShell;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.util.GroupAccountCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

public class SimpleTable extends SimpleShell{

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	protected Parent createContents(FXStageController value, Parameters args) {
		JsonIdMap map = new JsonIdMap();
		map.with(new GroupAccountCreator(), new PersonCreator());
		TableComponent table = new TableComponent();
		table.withMap(map);
		GroupAccount groupAccount = new GroupAccount();
		Person albert = new Person().withName("Albert");
		Person tom = new Person().withName("Tom");
		Person stefan = new Person().withName("Stefan");
		groupAccount.withPersons(albert, tom, stefan);

		table.withSearchProperties(Person.PROPERTY_NAME);
		table.withColumn(new Column().withAttrName(Person.PROPERTY_NAME).withLabel("Name"));

		TableCellFactory tableCellFactory = new TableCellFactory().withUpdateListener(new UpdateItemCell() {
			@Override
			public boolean updateItem(TableCellFX cell, TableCellValue item, boolean empty) {
				if(empty) {
					return false;
				}
				ImageView imageView = new ImageView(DialogBox.class.getResource("JavaCup32.png").toString());
				cell.setGraphic(imageView);
				cell.setText(null);
				return true;
			}
		});

		table.withColumn(new Column().withAttrName(Person.PROPERTY_NAME).withLabel("Image"), tableCellFactory);
		table.withList(groupAccount, GroupAccount.PROPERTY_PERSONS);
		return table;
	}
}

