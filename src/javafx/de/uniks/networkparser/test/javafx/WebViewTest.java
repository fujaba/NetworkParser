package de.uniks.networkparser.test.javafx;


import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WebViewTest extends Application {


	public static void main(String[] args) {
		launch(args);
	}
	private File captureFile = new File("cap.png");
	private WebView browser;

	@Override
	public void start(Stage stage) {
		AnchorPane root=new AnchorPane();
		Scene scene = new Scene(root, 600, 400);

		//XXX Show Error


		browser = new WebView();
		WebEngine webEngine = browser.getEngine();
//		webEngine.load("http://www.google.de");
		webEngine.load("file://C:/Arbeit/workspace/NetworkParser/neu.html");
		
		

		root.getChildren().add(browser);

		stage.setTitle("WebView");
		stage.setScene(scene);
		stage.show();

		webEngine.getLoadWorker().stateProperty().addListener((ov,oldState,newState)->{
			if(newState==Worker.State.SCHEDULED){
				System.out.println("state: scheduled");
			} else if(newState==Worker.State.RUNNING){
				System.out.println("state: running");
			} else if(newState==Worker.State.SUCCEEDED){
				System.out.println("state: succeeded");
				saveScreenShoot();
			}
		});
		//HACK  NOGEN
		System.out.println("Hallo");
	}

	private void saveScreenShoot() {
		WritableImage image = browser.snapshot(null, null);
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
		try {
			final ImageView imageView = new ImageView();
			ImageIO.write(bufferedImage, "png", captureFile);
			imageView.setImage(new Image(captureFile.toURI().toURL().toExternalForm()));
			System.out.println("Captured WebView to: " + captureFile.getAbsoluteFile());
			//progress.setVisible(false);
			//capture.setDisable(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
