package de.uniks.networkparser.gui.controls;

import java.util.Set;

import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;

import com.sun.javafx.Utils;

public class AutoCompletePopup extends PopupControl {
	public static final String DEFAULT_STYLE_CLASS = "auto-complete-popup";
	private TextField control;
	private AutoCompletePopupSkin skin;

	public AutoCompletePopup() {
		this.setAutoFix(true);
		this.setAutoHide(true);
		this.setHideOnEscape(true);
	}
	
	public AutoCompletePopup showing(TextField control, Set<String> list) {
		this.control = control;
		
		setPosition();
		super.show(this.control, 0, 0);
		this.skin.withSuggestions(list);
		setPosition();
		return this;
	}
	
	public AutoCompletePopup setPosition() {
		Point2D point = Utils.pointRelativeTo(control,
                prefWidth(-1), prefHeight(-1),
                HPos.CENTER, VPos.BOTTOM, 0, 0, true);
		setX(point.getX());
		setY(point.getY());
		setPrefWidth(control.getWidth());
		setWidth(control.getWidth());
		setMaxWidth(control.getWidth());
		setMinWidth(control.getWidth());
		this.skin.withWidth(control.getWidth());
		return this;
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		this.skin = new AutoCompletePopupSkin(this);
		return skin;
	}
	
	public void accept(String value) {
		this.control.setText(value);
		this.hide();
	}
}
