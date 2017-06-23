package de.uniks.networkparser.ext.javafx.dialog;

import de.uniks.networkparser.ext.generic.ReflectionLoader;

public class DialogStage implements Runnable {
	public DialogBox parent;
	private Object stage;
	private Object owner;

	public DialogStage(DialogBox parent, Object owner) {
		this.parent = parent;
		this.owner = owner;
	}

	public void showAndWait() {
		centerOnScreen();
		ReflectionLoader.call("showAndWait", stage);
	}
	public void show() {
		ReflectionLoader.call("show", stage);
	}

	@Override
	public void run() {
		Object transparent = ReflectionLoader.getField("TRANSPARENT", ReflectionLoader.STAGESTYLE);
		this.stage = ReflectionLoader.newInstance(ReflectionLoader.STAGE, ReflectionLoader.STAGESTYLE, transparent);
		// Stage newStage = new Stage(StageStyle.TRANSPARENT) {
		parent.setStage(this.stage);

		Object modality;
		if (parent.modal) {
			if (owner != null) {
				modality = ReflectionLoader.getField("WINDOW_MODAL", ReflectionLoader.MODALITY);
			} else {
				modality = ReflectionLoader.getField("APPLICATION_MODAL", ReflectionLoader.MODALITY);
			}
		} else {
			modality = ReflectionLoader.getField("NONE", ReflectionLoader.MODALITY);
		}
		ReflectionLoader.call("initModality", this.stage, ReflectionLoader.MODALITY, modality);

		parent.createContent();
		Object scene = ReflectionLoader.newInstance(ReflectionLoader.SCENE, ReflectionLoader.PARENT, parent.getRoot());
		Object color = ReflectionLoader.getField("TRANSPARENT", ReflectionLoader.COLOR);
		ReflectionLoader.call("setFill", scene, ReflectionLoader.PAINT, color);
		ReflectionLoader.call("setScene", this.stage, scene);
		parent.configScene();

		if (parent.modal) {
			this.showAndWait();
			return;
		}
		this.show();
	}

	public void centerOnScreen() {
		Object scene = ReflectionLoader.call("getScene", owner);
		if (scene != null) {
			// scene.getY() seems to represent the y-offset from the top of the titlebar to
			// the
			// start point of the scene, so it is the titlebar height
			double sceneX = (double) ReflectionLoader.call("getX", owner);
			double sceneY = (double) ReflectionLoader.call("getY", owner);

			// because Stage does not seem to centre itself over its owner, we
			// do it here.
			double x, y;

			double dialogWidth = parent.prefWidth(-1);
			double dialogHeight = parent.prefHeight(-1);

			double ownerX = (double) ReflectionLoader.call("getX", owner);
			double ownerY = (double) ReflectionLoader.call("getY", owner);
			
			if (ownerX < 0 || ownerY < 0) {
				// Fix for #165
				Object screen = ReflectionLoader.call("getPrimary", ReflectionLoader.SCREEN);
				double maxW = (double) ReflectionLoader.callChain(screen, "getVisualBounds", "getWidth");
				double maxH = (double) ReflectionLoader.callChain(screen, "getVisualBounds", "getHeight");

				x = maxW / 2.0 - dialogWidth / 2.0;
				y = maxH / 2.0 - dialogHeight / 2.0 + sceneY;
			} else {
				x = ownerX + (sceneX / 2.0) - (dialogWidth / 2.0);
				y = ownerY + sceneY + (sceneY / 2.0) - (dialogHeight / 2.0);
			}

			ReflectionLoader.call("setX", stage, double.class, x);
			ReflectionLoader.call("setY", stage, double.class, y);
		}
	}
}
