package de.uniks.networkparser.gui.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import de.uniks.networkparser.gui.grid.TableGridPane;

public class GridTest extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		TableGridPane gridPane = new TableGridPane();
		Label twoRows = new Label("2 Spalten");
		twoRows.setFont(Font.font("Arial", 50));
		Label label1 = new Label("1");
		Label label2 = new Label("2");
		
		
		Label h1 = new Label("zwei Zeilen");
//		Label h2 = new Label("h2");
//		Label h3 = new Label("h3");
		gridPane.add(twoRows, 0, 0);
		gridPane.setSpanColumn(twoRows, 2);
		
//		Label helloB = new Label("sd");
//		gridPane.add(helloB, 0, 1);
//		gridPane.setSpanColumn(helloB, 3);
		
		
		gridPane.add(label1, 0, 1);
		gridPane.add(label2, 1, 1);
		gridPane.add(h1, 2, 0);
		gridPane.setSpanRow(h1, 2 );
		
		
//		gridPane.add(h2, 2, 1);
//		gridPane.add(h3, 2, 2);
//		gridPane.setSpanRow(hh, 4);
		
//		TableRTableCell cell = gridPane.getCell(label2);
//		cell.select("-fx-background-color: #d8f0f3;");
//		cell.deselect();
		
//		gridPane.
		
//		System.out.println(gridPane.getChildren().remove(1));
		
		
//		cell.setStyle(cell.getStyle()+"-fx-background-color: #d8f0f3;");
//		 for (Node n: gridPane.getChildren()) {
//			if (n instanceof Control) {
//				Control control = (Control) n;
//				control.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//				control.setStyle("-fx-border-width: 2px; -fx-border-color: black;");
//			}
//			if (n instanceof Pane) {
//				Pane pane = (Pane) n;
//				pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//				pane.setStyle("-fx-border-width: 2px; -fx-border-color: black;");
//			}
//		}
		 
		//gridPane.setGridLinesVisible(true);
		Scene scene = new Scene(gridPane);
		stage.setScene(scene);
		stage.setMinHeight(100);
		stage.show();
	}
}
