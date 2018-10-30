package de.uniks.networkparser.ext.javafx;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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

public class DialogPane implements Runnable {
	private Object opaqueLayer;

	private DialogBox owner;
	private Object parent;
	private Object pane;
	private double initWidth;
	private double initHeight;
	private int initCount = 1;

	public DialogPane(DialogBox owner, Object parent) {
		this.owner = owner;
		this.parent = parent;

		this.pane = ReflectionLoader.newInstance(ReflectionLoader.PANE);
		if (owner != null) {
			this.initHeight = this.owner.prefWidth(-1);
			this.initWidth = this.owner.prefHeight(-1);
			if (owner.isModel() == false) {
				opaqueLayer = ReflectionLoader.newInstance(ReflectionLoader.REGION);
				JavaBridgeFX.setStyle(opaqueLayer, false, "lightweight-dialog-background");
				JavaBridgeFX.addChildren(pane, 0, opaqueLayer);
			}
			JavaBridgeFX.addChildren(pane, -1, owner.getRoot());
		}
		ReflectionLoader.call(pane, "setManaged", boolean.class, true);
	}

	public Object getPane() {
		return pane;
	}

	protected void layoutChildren() {
		double dialogWidth = this.owner.prefWidth(-1);
		double dialogHeight = this.owner.prefHeight(-1);
		if (this.initCount > 999) {
			this.initCount = -1;
		}
		if (dialogHeight == this.initHeight && dialogWidth == this.initWidth) {
			if (this.initCount > 0) {
				this.initCount++;
				JavaAdapter.execute(this);
			}
		} else if (this.initCount > 0) {
			this.initCount = -1;
		}

		final double w = owner.getOverlayWidth();
		final double h = owner.getOverlayHeight();

		final double x = 0;
		final double y = 0;
		if (parent != null) {
			ReflectionLoader.call(parent, "resizeRelocate", double.class, x, double.class, y, double.class, w,
					double.class, h);
		}

		if (opaqueLayer != null) {
			ReflectionLoader.call(opaqueLayer, "resizeRelocate", double.class, x, double.class, y, double.class, w,
					double.class, h);
		}
		Object root = owner.getRoot();

		ReflectionLoader.call(root, "resize", double.class, (int) (dialogWidth), double.class, (int) (dialogHeight));

		// hacky, but we only want to position the dialog the first time
		// it is laid out - after that the only way it should move is if
		// the user moves it.
		if (this.initCount == -1) {
			this.initCount = -2;
			double dialogX = (Double) ReflectionLoader.call(root, "getLayoutX");
			dialogX = dialogX == 0.0 ? w / 2.0 - dialogWidth / 2.0 : dialogX;

			double dialogY = (Double) ReflectionLoader.call(root, "getLayoutY");
			dialogY = dialogY == 0.0 ? h / 2.0 - dialogHeight / 2.0 : dialogY;

			ReflectionLoader.call(root, "relocate", double.class, (int) (dialogX), double.class, (int) (dialogY));
		}
	}

	// These are the actual implementations in Region (the parent of Pane),
	// but just for clarify I reproduce them here
	protected double computeMinHeight(double width) {
		return (Double) ReflectionLoader.call(parent, "minHeight", width);
	}

	protected double computeMinWidth(double height) {
		return (Double) ReflectionLoader.call(parent, "minWidth", height);
	}

	protected double computePrefHeight(double width) {
		return (Double) ReflectionLoader.call(parent, "prefHeight", width);
	}

	protected double computePrefWidth(double height) {
		return (Double) ReflectionLoader.call(parent, "prefWidth", height);
	}

	protected double computeMaxHeight(double width) {
		return (Double) ReflectionLoader.call(parent, "maxHeight", width);
	}

	protected double computeMaxWidth(double height) {
		return (Double) ReflectionLoader.call(parent, "maxWidth", height);
	}

	@Override
	public void run() {
		this.layoutChildren();
	}
}
