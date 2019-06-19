package de.uniks.networkparser.ext.javafx;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.ext.generic.ReflectionLoader;

public class DialogStage implements Runnable {
	public DialogBox parent;
	private Object stage;
	private Object owner;

	public DialogStage() {
	}

	public DialogStage(DialogBox parent, Object owner) {
		this.parent = parent;
		this.owner = owner;
	}

	public void showAndWait() {
		centerOnScreen();
		ReflectionLoader.call(stage, "showAndWait");
	}

	public void show() {
		ReflectionLoader.call(stage, "show");
	}

	@Override
	public void run() {
		Object transparent = ReflectionLoader.getField(ReflectionLoader.STAGESTYLE, "TRANSPARENT");
		this.stage = ReflectionLoader.newInstance(ReflectionLoader.STAGE, ReflectionLoader.STAGESTYLE, transparent);
		parent.setStage(this.stage);

		Object modality;
		if (parent.modal) {
			if (owner != null) {
				modality = ReflectionLoader.getField(ReflectionLoader.MODALITY, "WINDOW_MODAL");
			} else {
				modality = ReflectionLoader.getField(ReflectionLoader.MODALITY, "APPLICATION_MODAL");
			}
		} else {
			modality = ReflectionLoader.getField(ReflectionLoader.MODALITY, "NONE");
		}
		ReflectionLoader.call(this.stage, "initModality", ReflectionLoader.MODALITY, modality);

		parent.createContent();
		Object scene = ReflectionLoader.newInstance(ReflectionLoader.SCENE, ReflectionLoader.PARENT, parent.getRoot());
		Object color = ReflectionLoader.getField(ReflectionLoader.COLOR, "TRANSPARENT");
		ReflectionLoader.call(scene, "setFill", ReflectionLoader.PAINT, color);
		ReflectionLoader.call(this.stage, "setScene", scene);
		parent.configScene();

		if (parent.modal) {
			this.showAndWait();
			return;
		}
		this.show();
	}

	public void centerOnScreen() {
		Object scene = ReflectionLoader.call(owner, "getScene");
		if (scene != null) {
			/*
			 * scene.getY() seems to represent the y-offset from the top of the titlebar to
			 * the start point of the scene, so it is the titlebar height
			 */
			double sceneX = (Double) ReflectionLoader.call(owner, "getX");
			double sceneY = (Double) ReflectionLoader.call(owner, "getY");

			/*
			 * because Stage does not seem to centre itself over its owner, we do it here.
			 */
			double x, y;

			double dialogWidth = parent.prefWidth(-1);
			double dialogHeight = parent.prefHeight(-1);

			double ownerX = (Double) ReflectionLoader.call(owner, "getX");
			double ownerY = (Double) ReflectionLoader.call(owner, "getY");

			if (ownerX < 0 || ownerY < 0) {
				Object screen = ReflectionLoader.call(ReflectionLoader.SCREEN, "getPrimary");
				double maxW = (Double) ReflectionLoader.callChain(screen, "getVisualBounds", "getWidth");
				double maxH = (Double) ReflectionLoader.callChain(screen, "getVisualBounds", "getHeight");

				x = maxW / 2.0 - dialogWidth / 2.0;
				y = maxH / 2.0 - dialogHeight / 2.0 + sceneY;
			} else {
				x = ownerX + (sceneX / 2.0) - (dialogWidth / 2.0);
				y = ownerY + sceneY + (sceneY / 2.0) - (dialogHeight / 2.0);
			}

			ReflectionLoader.call(stage, "setX", double.class, x);
			ReflectionLoader.call(stage, "setY", double.class, y);
		}
	}
}
