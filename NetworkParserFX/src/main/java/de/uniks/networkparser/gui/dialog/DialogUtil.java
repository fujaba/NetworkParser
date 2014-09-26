package de.uniks.networkparser.gui.dialog;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class DialogUtil {
	public static void injectAsRootPane(Scene scene, Pane injectedParent) {
        Parent originalParent = scene.getRoot();
        scene.setRoot(injectedParent);
        
        if (originalParent != null) {
        	injectedParent.getChildren().add(0, originalParent);
            
            // copy in layout properties, etc, so that the dialogStack displays
            // properly in (hopefully) whatever layout the owner node is in
            injectedParent.getProperties().putAll(originalParent.getProperties());
        }
    }
}
