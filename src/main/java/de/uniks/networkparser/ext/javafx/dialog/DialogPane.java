package de.uniks.networkparser.ext.javafx.dialog;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.javafx.JavaBridgeFX;

public class DialogPane implements Runnable {
	private Object opaqueLayer;

	private DialogBox owner;
	private Object parent;
	private Object pane;
	private double initWidth;
	private double initHeight;
	private int initCount=1;


	public DialogPane(DialogBox owner, Object parent) {
		this.owner = owner;
		this.parent = parent;

		this.pane = ReflectionLoader.newInstance(ReflectionLoader.PANE);
		if (owner!= null) {
			this.initHeight = this.owner.prefWidth(-1);
			this.initWidth  = this.owner.prefHeight(-1);
			if(owner.isModel() == false) {
				opaqueLayer = ReflectionLoader.newInstance(ReflectionLoader.REGION);
				JavaBridgeFX.setStyle(opaqueLayer, false, "lightweight-dialog-background");
				JavaBridgeFX.addChildren(pane, 0, opaqueLayer);
			}
			JavaBridgeFX.addChildren(pane, -1, owner.getRoot());
		}
		ReflectionLoader.call("setManaged", pane, boolean.class, true);
	}

	public Object getPane() {
		return pane;
	}

	protected void layoutChildren() {
		double dialogWidth = this.owner.prefWidth(-1);
		double dialogHeight = this.owner.prefHeight(-1);
		if(this.initCount>999) {
			this.initCount = -1;
		}
		if(dialogHeight == this.initHeight && dialogWidth == this.initWidth) {
			if(this.initCount>0) {
				this.initCount++;
				ReflectionLoader.call("runLater", ReflectionLoader.PLATFORM, this);
			}
		} else if(this.initCount > 0){
			this.initCount = -1;
		}

		final double w = owner.getOverlayWidth();
		final double h = owner.getOverlayHeight();

		final double x = 0;
		final double y = 0;
		if (parent != null) {
			ReflectionLoader.call("resizeRelocate", parent, double.class, x, double.class, y, double.class, w, double.class, h);
		}

		if (opaqueLayer != null) {
			ReflectionLoader.call("resizeRelocate", opaqueLayer, double.class, x, double.class, y, double.class, w, double.class, h);
		}
		Object root = owner.getRoot();


		ReflectionLoader.call("resize", root, double.class, (int) (dialogWidth), double.class, (int) (dialogHeight));

		// hacky, but we only want to position the dialog the first time
		// it is laid out - after that the only way it should move is if
		// the user moves it.
		if(this.initCount == -1) {
			this.initCount = -2;
			double dialogX = (Double) ReflectionLoader.call("getLayoutX", root);
			dialogX = dialogX == 0.0 ? w / 2.0 - dialogWidth / 2.0 : dialogX;

			double dialogY = (Double) ReflectionLoader.call("getLayoutY", root);
			dialogY = dialogY == 0.0 ? h / 2.0 - dialogHeight / 2.0 : dialogY;

			ReflectionLoader.call("relocate", root, double.class, (int) (dialogX), double.class, (int) (dialogY));
		}
	}

	// These are the actual implementations in Region (the parent of Pane),
	// but just for clarify I reproduce them here
	protected double computeMinHeight(double width) {
		return (Double) ReflectionLoader.call("minHeight", parent, width);
	}

	protected double computeMinWidth(double height) {
		return (Double) ReflectionLoader.call("minWidth", parent, height);
	}

	protected double computePrefHeight(double width) {
		return (Double) ReflectionLoader.call("prefHeight", parent, width);
	}

	protected double computePrefWidth(double height) {
		return (Double) ReflectionLoader.call("prefWidth", parent, height);
	}

	protected double computeMaxHeight(double width) {
		return (Double) ReflectionLoader.call("maxHeight", parent, width);
	}

	protected double computeMaxWidth(double height) {
		return (Double) ReflectionLoader.call("maxWidth", parent, height);
	}

	@Override
	public void run() {
		this.layoutChildren();
	}
}
