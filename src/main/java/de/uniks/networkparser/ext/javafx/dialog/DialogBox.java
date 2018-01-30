package de.uniks.networkparser.ext.javafx.dialog;

/*
NetworkParser
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.javafx.JavaBridgeFX;
import de.uniks.networkparser.gui.controls.Button;
import de.uniks.networkparser.gui.controls.Control;
import de.uniks.networkparser.gui.controls.Label;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleSet;

public class DialogBox implements ObjectCondition{
	protected static final int HEADER_HEIGHT = 28;
	protected static final URL DIALOGS_CSS_URL = DialogBox.class.getResource("dialogs.css");
	boolean alwaysOnTop;
	boolean modal = true;

	private SimpleSet<Control> titleElements = new SimpleSet<Control>();
	private SimpleSet<Control> actionElements = new SimpleSet<Control>();

	private double mouseDragDeltaY;
	private double mouseDragDeltaX;

	protected static final Object ACTIVE_PSEUDO_CLASS = ReflectionLoader.call("getPseudoClass", ReflectionLoader.PSEUDOCLASS, "active");
	protected Object dialogTitleBar;

	private Object root;
	private Object stage;
	private Object originalParent;

	Button action;
	private Object owner;
	private Label titleElement = new Label().withType(Label.TITLE);

	
	//Inline Show
	private boolean isInline;

	private boolean iconified;
	private Object center;
	
	

	public DialogBox() {
		titleElements.add(titleElement);
		titleElements.add(new Label().withType(Label.SPACER));
		titleElements.add(new Button().withActionType(Button.CLOSE, this));
//		minButton = new DialogButton().withGrafik(Grafik.minimize).withStage(stage);
//		maxButton = new DialogButton().withGrafik(Grafik.maximize).withStage(stage);
	}

	public DialogBox withTitle(String value) {
		if(value != null) {
			titleElement.setValue(value);
		}
		return this;
	}
	public Button show(Object owner){
		if(titleElement.length() < 1 ) {
			Object title = ReflectionLoader.call("getTitle", owner);
			if(title != null) {
				titleElement.setValue((String) title);
			}
		}
		if(isInline) {
			return showIntern(owner);
		}
		return showExtern(owner);

	}

	@SuppressWarnings("unchecked")
	private Button showIntern(Object parent) {
		Object scene;
		this.stage = parent;
		scene = ReflectionLoader.call("getScene", stage);
		configScene();

		// modify scene root to install opaque layer and the dialog
		originalParent = ReflectionLoader.call("getRoot", scene);

		createContent();

		DialogPane myPane = new DialogPane(this, originalParent);

		
		
		ReflectionLoader.call("pseudoClassStateChanged", root, ReflectionLoader.PSEUDOCLASS, ACTIVE_PSEUDO_CLASS, boolean.class, true);

		// add to originalParent
		ReflectionLoader.call("setRoot", scene, ReflectionLoader.PARENT, myPane.getPane());
		
		ReflectionLoader.call("setVisible", root, boolean.class, true);
		if (originalParent != null) {
			JavaBridgeFX.addChildren(myPane.getPane(), 0, originalParent);
			Map<Object,Object> properties = (Map<Object, Object>) ReflectionLoader.call("getProperties", originalParent);
			
			Map<Object, Object> dialogProperties = (Map<Object, Object>) ReflectionLoader.call("getProperties", myPane.getPane());
			dialogProperties.putAll(properties);
		}
		ReflectionLoader.call("requestFocus", root);
		
		ReflectionLoader.call("runLater", ReflectionLoader.PLATFORM, myPane);
		return null;
	}

	public void hide(Button value) {
		if(this.action == null) {
			this.setAction(value);
		}
	}

	public void setAction(Button value) {
		this.action = value;
		if(isInline) {
			// hide the dialog
			ReflectionLoader.call("setVisible", root, boolean.class, false);
			// reset the scene root
			Object scene = ReflectionLoader.call("getScene", stage);
			Object oldParent = ReflectionLoader.call("getRoot", scene);
			
			JavaBridgeFX.removeChildren(oldParent, originalParent);
			JavaBridgeFX.removeStyle(originalParent, "root");
			ReflectionLoader.call("setRoot", scene, ReflectionLoader.PARENT, originalParent);
			return;
		}
		if (stage != null) {
			ReflectionLoader.call("hide", stage);
		}
	}

	public void minimize() {
		if (stage != null) {
			ReflectionLoader.call("setIconified", stage, this.iconified);
		}
	}

	public void maximize() {
		if(isInline) {
//			if(originalParent instanceof Node) {
//			root.setPrefWidth(originalParent.getWidth());
		}
		if (stage != null) {
			ReflectionLoader.call("setFullScreen", stage, true);
		}
	}

	protected double getOverlayWidth() {
		if (owner != null) {
			return (Double) ReflectionLoader.callChain(owner, "getLayoutBounds", "getWidth");
		} else if (stage != null) {
			return (Double) ReflectionLoader.callChain(stage, "getScene", "getWidth");
		}

		return 0;
	}

	protected double getOverlayHeight() {
		if (owner != null) {
			return (Double) ReflectionLoader.callChain(owner, "getLayoutBounds", "getHeight");
		} else if (stage != null) {
			return (Double) ReflectionLoader.callChain(stage, "getScene", "getHeight");
		}

		return 0;
	}

	private Button showExtern(Object owner) {
		Object toolKit = ReflectionLoader.call("getToolkit", ReflectionLoader.TOOLKITFX);
		Object isFX = ReflectionLoader.call("isFxUserThread", toolKit);
		if(isFX!=null && (Boolean)isFX) {
			new DialogStage(this, owner).run();
			return action;
		}
		ReflectionLoader.call("runLater", ReflectionLoader.PLATFORM, Runnable.class, new DialogStage(this, owner));
		return null;
	}

	public DialogBox withInline(boolean value) {
		this.isInline = value;
		return this;
	}

	public DialogBox withAlwaysOnTop(boolean value) {
		this.alwaysOnTop = value;
		return this;
	}

	public DialogBox withModal(boolean modal) {
		this.modal = modal;
		return this;
	}

	@SuppressWarnings("unchecked")
	void configScene() {
		Object scene = ReflectionLoader.call("getScene", stage);
		String dialogsCssUrl = DIALOGS_CSS_URL.toExternalForm();
		if (scene == null && owner != null) {
			scene = ReflectionLoader.call("getScene", owner);
		}
		if (scene != null) {
			// install CSS
			Object styleSheet = ReflectionLoader.call("getStylesheets", scene);
			if(styleSheet instanceof List<?>) {
				List<String> list = (List<String>) styleSheet;
				if (list.contains(dialogsCssUrl) == false) {
					list.add(dialogsCssUrl);
				}
			}
		}
	}

	public void createContent() {
		root = ReflectionLoader.newInstance(ReflectionLoader.BORDERPANE);
		JavaBridgeFX.setStyle(root, false, "dialog", "decorated-root");

		ObjectCondition condition = new ObjectCondition() {
			@Override
			public boolean update(Object value) {
				boolean active = (Boolean) value;
				 ReflectionLoader.call("pseudoClassStateChanged", root, ReflectionLoader.PSEUDOCLASS, ACTIVE_PSEUDO_CLASS, boolean.class, active);
				return true;
			}
		};
		
		Object property = ReflectionLoader.call("focusedProperty", stage);
		JavaBridgeFX.addListener(property, "addListener", ReflectionLoader.CHANGELISTENER, condition);

		// --- titlebar (only used for cross-platform look)
		dialogTitleBar = ReflectionLoader.newInstance(ReflectionLoader.TOOLBAR);
		JavaBridgeFX.setStyle(dialogTitleBar, false, "window-header");
		ReflectionLoader.call("setPrefHeight", dialogTitleBar, double.class, HEADER_HEIGHT);
		ReflectionLoader.call("setMinHeight", dialogTitleBar, double.class, HEADER_HEIGHT);
		ReflectionLoader.call("setMaxHeight", dialogTitleBar, double.class, HEADER_HEIGHT);
		for(Control element : titleElements) {
			Object guiElement = JavaBridgeFX.convert(element, true);
			JavaBridgeFX.addChildren(dialogTitleBar, -1, guiElement);
		}
		
		condition = new ObjectCondition() {
			@Override
			public boolean update(Object event) {
				mouseDragDeltaX = (Double) ReflectionLoader.call("getSceneX", event);
				mouseDragDeltaY = (Double) ReflectionLoader.call("getSceneY", event);

				return true;
			}
		};
		JavaBridgeFX.addListener(dialogTitleBar, "setOnMousePressed", ReflectionLoader.EVENTHANDLER, condition);

		
		condition = new ObjectCondition() {
			@Override
			public boolean update(Object event) {
				double eventX = (Double) ReflectionLoader.call("getScreenX", event) - mouseDragDeltaX;
				double eventY = (Double) ReflectionLoader.call("getScreenY", event) - mouseDragDeltaY;
				
				if(isInline) {
					double x = (Double) ReflectionLoader.call("getLayoutX", root);
					double y = (Double) ReflectionLoader.call("getLayoutY", root);
					ReflectionLoader.call("setLayoutX", root, double.class, x + eventX);
					ReflectionLoader.call("setLayoutY", root, double.class, y + eventY);
				}else{
					ReflectionLoader.call("setX", stage, double.class, eventX);
					ReflectionLoader.call("setY", stage, double.class, eventY);
				}

				return true;
			}
		};
		JavaBridgeFX.addListener(dialogTitleBar, "setOnMouseDragged", ReflectionLoader.EVENTHANDLER, condition);

		ReflectionLoader.call("setTop", root, ReflectionLoader.NODE, dialogTitleBar);
		ReflectionLoader.call("setCenter", root, ReflectionLoader.NODE, center);
		if(this.actionElements.size() > 0) {
			Object actionToolbar = ReflectionLoader.newInstance(ReflectionLoader.HBOX);
			JavaBridgeFX.setStyle(actionToolbar, false, "window-action");
			for(Control item : this.actionElements) {
				Object guiElement = JavaBridgeFX.convert(item, false);
//				item.withOwner(this);
				JavaBridgeFX.addChildren(actionToolbar, -1, guiElement);
			}
			Object pos = ReflectionLoader.getField("TOP_RIGHT", ReflectionLoader.POS);
			ReflectionLoader.call("setAlignment", actionToolbar, ReflectionLoader.POS, pos);
			ReflectionLoader.call("setBottom", root, ReflectionLoader.NODE, actionToolbar);
		}
	}

	public DialogBox withCenter(Object node) {
		this.center = node;
		return this;
	}

	public DialogBox withTitleButton(int index, Control... value) {
		if(value==null){
			return this;
		}
		ArrayList<Control> items=new ArrayList<Control>();
		for(Control item : value) {
			items.add(item);
		}
		this.titleElements.addAll(index, items);
		return this;

	}

	public DialogBox withTitleButton(Control... value) {
		if(value==null){
			return this;
		}
		for(Control item : value) {
			this.titleElements.add(item);
		}
		return this;
	}

	public DialogBox withActionButton(int index, Control... value) {
		if(value==null){
			return this;
		}
		ArrayList<Control> items=new ArrayList<Control>();
		for(Control item : value) {
			items.add(item);
		}
		this.actionElements.addAll(index, items);
		return this;

	}

	public DialogBox withActionButton(Control... value) {
		if(value==null){
			return this;
		}
		for(Control item : value) {
			this.actionElements.add(item);
		}
		return this;
	}

	public DialogBox withCenterInfo(String value) {
		withCenterText("information.png", value);
		return this;
	}

	public DialogBox withCenterQuestion(String value) {
		withCenterText("confirm.png", value);
		return this;
	}

	public DialogBox withCenterText(String image, String value) {
		Object box =ReflectionLoader.newInstance(ReflectionLoader.HBOX);
		URL resource = DialogBox.class.getResource(image);
		if(resource == null) {
			return this;
		}
		Object imageView = ReflectionLoader.newInstance(ReflectionLoader.IMAGEVIEW, String.class, resource.toString());
		
		Object text = ReflectionLoader.newInstance(ReflectionLoader.LABEL, String.class, value);
		JavaBridgeFX.setStyle(text, false, "labelText");

		Object vBox = ReflectionLoader.newInstance(ReflectionLoader.VBOX);
		Object pos = ReflectionLoader.getField("CENTER", ReflectionLoader.POS);
		ReflectionLoader.call("setAlignment", vBox, ReflectionLoader.POS, pos);
		JavaBridgeFX.addChildren(vBox, -1, text);

		JavaBridgeFX.addChildren(box, -1, imageView, vBox);
		JavaBridgeFX.setStyle(box, false, "centerbox");
		this.center = box;
		return this;
	}

	public static Button showInfo(Object parent, String title, String text, boolean inLine) {
		DialogBox dialogBox = new DialogBox().withTitle(title).withCenterInfo(text).withInline(inLine);
		return dialogBox
			.withActionButton(new Button().withActionType(Button.CLOSE, dialogBox).withValue("OK"))
			.show(parent);
	}

	public static Button showInfo(String title, String text) {
		DialogBox dialogBox = new DialogBox().withTitle(title).withCenterInfo(text);
		return dialogBox
			.withActionButton(new Button().withValue("OK").withActionType(Button.CLOSE, dialogBox))
			.show(null);
	}

	public static Button showQuestion(Object parent, String title, String text) {
		DialogBox dialogBox = new DialogBox().withTitle(title).withCenterInfo(text);
		return dialogBox
			.withActionButton(new Button().withValue("Yes").withActionType(Button.CLOSE, dialogBox), new Button().withValue("No").withActionType(Button.CLOSE, dialogBox))
			.show(parent);
	}
	public static boolean createQuestionCheck(Object parent, String title, String text, String... check) {
		Button action = showQuestion(parent, title, text);
		if(action==null) {
			return false;
		}
		for(String item : check){
			if(item != null) {
				if(item.equalsIgnoreCase(action.getValue())){
					return true;
				}
			}
		}
		return false;
	}

	public Button getAction() {
		return action;
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof Button == false) {
			return false;
		}
		Button btn = (Button) value;
		if(Button.CLOSE.equalsIgnoreCase(btn.getActionType())) {
			this.hide(btn);
		}
		if(Button.MINIMIZE.equalsIgnoreCase(btn.getActionType())) {
			this.minimize();
		}
		if(Button.MAXIMIZE.equalsIgnoreCase(btn.getActionType())) {
			this.maximize();
		}
		return true;
	}
	
	public double prefWidth(double value) {
		return (Double) ReflectionLoader.call("prefWidth", root, double.class, -1);
	}
	public double prefHeight(double value) {
		return (Double) ReflectionLoader.call("prefHeight", root, double.class, -1);
	}

	public void setStage(Object newStage) {
		this.stage = newStage;
	}

	public Object getRoot() {
		return root;
	}

	public boolean isModel() {
		return this.modal;
	}

	public Object getScene() {
		return ReflectionLoader.call("getScene", stage);
	}
}
