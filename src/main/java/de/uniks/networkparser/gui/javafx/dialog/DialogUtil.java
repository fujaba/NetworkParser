package de.uniks.networkparser.gui.javafx.dialog;

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
