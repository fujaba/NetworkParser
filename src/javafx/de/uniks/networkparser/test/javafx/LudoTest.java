package de.uniks.networkparser.test.javafx;

import de.uniks.ludo.model.Ludo;
import de.uniks.ludo.model.Player;
import de.uniks.ludo.model.util.LudoSet;
import de.uniks.ludo.model.util.PlayerSet;
import de.uniks.networkparser.ext.SimpleController;
import de.uniks.networkparser.ext.javafx.ModelListenerProperty;
import de.uniks.networkparser.ext.javafx.DiceController;
import de.uniks.networkparser.interfaces.ObjectCondition;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class LudoTest {

	public static void main(String[] args) {
		SimpleController controller = SimpleController.createFX();
		controller.withMap(LudoSet.createIdMap("42"));
		
		// DICE CONTROLLER
		controller.withMap(new DiceController(), "dice");
		
		Ludo game = new Ludo();
		game.init(new Player().withName("Albert"), new Player().withName("Stefan"));
		PlayerSet players = game.getPlayers();
		
		ModelListenerProperty controllerFactory = new ModelListenerProperty();
		controllerFactory.registerEvent(MouseEvent.MOUSE_CLICKED, new ObjectCondition() {
			@Override
			public boolean update(Object event) {
				MouseEvent evt = (MouseEvent) event;
				if(evt.getClickCount() == 2){
					Label  label = (Label) evt.getSource();
					String id = label.getId();
					label.setVisible(false);
					id+="_textfield";
					
					TextField field = (TextField) label.getScene().lookup("#"+id);
					field.setVisible(true);
				}
				return false;
			}
		});
		
		ModelListenerProperty playerNameControllerFactory = new ModelListenerProperty();
		playerNameControllerFactory.registerEvent(KeyEvent.KEY_PRESSED, new ObjectCondition() {
			@Override
			public boolean update(Object value) {
				KeyEvent evt = (KeyEvent) value;
				if(evt.getCode()==KeyCode.ENTER) {
					TextField  field = (TextField) evt.getSource();
					field.setVisible(false);
					String id = field.getId().substring(0, field.getId().indexOf("_"));
					Label label = (Label) field.getScene().lookup("#"+id);
					label.setVisible(true);
				}
				return false;
			}
		});
		
		for(int p=1;p<=players.size();p++) {
			Player player = players.get(p-1);
			controller.withMap(controllerFactory, "p"+p, player, Player.PROPERTY_NAME);
			controller.withMap(playerNameControllerFactory, "p"+p+"_textfield", player, Player.PROPERTY_NAME);
//			HomeSet homes = player.getHome();
//			for(int h=1;h<=homes.size();h++) {
//				controller.withMap(new FieldController(), "h"+p+"_"+h, homes.get(h-1));
//			}
//			
//			TargetSet targets = player.getTarget();
//			for(int t=1;t<=targets.size();t++) {
//				controller.withMap(new FieldController(), "t"+p+"_"+t);
//			}
		}
		// AND MERGING
		controller.withFXML("LudoGameScreen.fxml", LudoTest.class);
		
		
		controllerFactory.setValue("Albert2");
		if(args != null && args.length>0) {
			controller.withTitle("Player: "+args[0]);
		}
		controller.show();
	}
}
