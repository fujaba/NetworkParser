package de.uniks.networkparser.ext.javafx;

import java.io.File;
import java.io.PrintStream;
/*
NetworkParser
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.gui.controls.Button;
import de.uniks.networkparser.gui.controls.Control;
import de.uniks.networkparser.gui.controls.Label;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleSet;

public class DialogBox implements ObjectCondition {
	protected static final int HEADER_HEIGHT = 28;
	protected static final URL DIALOGS_CSS_URL = DialogBox.class.getResource("dialogs.css");
	boolean alwaysOnTop;
	boolean modal = true;

	private SimpleSet<Control> titleElements = new SimpleSet<Control>();
	private SimpleSet<Control> actionElements = new SimpleSet<Control>();

	private double mouseDragDeltaY;
	private double mouseDragDeltaX;

	protected static final Object ACTIVE_PSEUDO_CLASS = ReflectionLoader.call(ReflectionLoader.PSEUDOCLASS,
			"getPseudoClass", "active");
	protected Object dialogTitleBar;

	private Object root;
	private Object stage;
	private Object originalParent;

	Button action;
	private Object owner;
	private Label titleElement = new Label().withType(Label.TITLE);

	/* Inline Show */
	private boolean isInline;

	private boolean iconified;
	private Object center;

	public DialogBox() {
		titleElements.add(titleElement);
		titleElements.add(new Label().withType(Label.SPACER));
		titleElements.add(new Button().withActionType(Button.CLOSE, this));
		/*
		 * minButton = new DialogButton().withGrafik(Grafik.minimize).withStage(stage);
		 */
		/*
		 * maxButton = new DialogButton().withGrafik(Grafik.maximize).withStage(stage);
		 */
	}

	public DialogBox withTitle(String value) {
		if (value != null) {
			titleElement.setValue(value);
		}
		return this;
	}

	public Button show(Object owner) {
		if (titleElement.length() < 1) {
			Object title = ReflectionLoader.call(owner, "getTitle");
			if (title != null) {
				titleElement.setValue((String) title);
			}
		}
		if (isInline) {
			return showIntern(owner);
		}
		return showExtern(owner);

	}

	@SuppressWarnings("unchecked")
	private Button showIntern(Object parent) {
		Object scene;
		this.stage = parent;
		scene = ReflectionLoader.call(stage, "getScene");
		configScene();

		/* modify scene root to install opaque layer and the dialog */
		originalParent = ReflectionLoader.call(scene, "getRoot");

		createContent();

		DialogPane myPane = new DialogPane(this, originalParent);

		ReflectionLoader.call(root, "pseudoClassStateChanged", ReflectionLoader.PSEUDOCLASS, ACTIVE_PSEUDO_CLASS,
				boolean.class, true);

		/* add to originalParent */
		ReflectionLoader.call(scene, "setRoot", ReflectionLoader.PARENT, myPane.getPane());

		ReflectionLoader.call(root, "setVisible", boolean.class, true);
		if (originalParent != null) {
			JavaBridgeFX.addChildren(myPane.getPane(), 0, originalParent);
			Map<Object, Object> properties = (Map<Object, Object>) ReflectionLoader.call(originalParent,
					"getProperties");

			Map<Object, Object> dialogProperties = (Map<Object, Object>) ReflectionLoader.call(myPane.getPane(),
					"getProperties");
			dialogProperties.putAll(properties);
		}
		ReflectionLoader.call(root, "requestFocus");

		JavaAdapter.execute(myPane);
		return null;
	}

	public void hide(Button value) {
		if (this.action == null) {
			this.setAction(value);
		}
	}

	public void setAction(Button value) {
		this.action = value;
		if (isInline) {
			/* hide the dialog */
			ReflectionLoader.call(root, "setVisible", boolean.class, false);
			/* reset the scene root */
			Object scene = ReflectionLoader.call(stage, "getScene");
			Object oldParent = ReflectionLoader.call(scene, "getRoot");

			JavaBridgeFX.removeChildren(oldParent, originalParent);
			JavaBridgeFX.removeStyle(originalParent, "root");
			ReflectionLoader.call(scene, "setRoot", ReflectionLoader.PARENT, originalParent);
			return;
		}
		if (stage != null) {
			ReflectionLoader.call(stage, "hide");
		}
	}

	public void minimize() {
		if (stage != null) {
			ReflectionLoader.call(stage, "setIconified", this.iconified);
		}
	}

	public void maximize() {
		if (stage != null) {
			ReflectionLoader.call(stage, "setFullScreen", true);
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
		Object toolKit = ReflectionLoader.call(ReflectionLoader.TOOLKITFX, "getToolkit");
		Object isFX = ReflectionLoader.call(toolKit, "isFxUserThread");
		if (isFX != null && (Boolean) isFX) {
			new DialogStage(this, owner).run();
			return action;
		}
		JavaAdapter.execute(new DialogStage(this, owner));
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
		if (stage == null) {
			return;
		}
		Object scene = ReflectionLoader.call(stage, "getScene");
		if (DIALOGS_CSS_URL == null) {
			return;
		}
		String dialogsCssUrl = DIALOGS_CSS_URL.toExternalForm();
		if (scene == null && owner != null) {
			scene = ReflectionLoader.call(owner, "getScene");
		}
		if (scene != null) {
			/* install CSS */
			Object styleSheet = ReflectionLoader.call(scene, "getStylesheets");
			if (styleSheet instanceof List<?>) {
				List<String> list = (List<String>) styleSheet;
				if (list.contains(dialogsCssUrl) == false) {
					list.add(dialogsCssUrl);
				}
			}
		}
	}

	public DialogBox createContent() {
		if (Os.isReflectionTest()) {
			return this;
		}
		root = ReflectionLoader.newInstance(ReflectionLoader.BORDERPANE);
		JavaBridgeFX.setStyle(root, false, "dialog", "decorated-root");

		Object property = ReflectionLoader.call(stage, "focusedProperty");
		JavaBridgeFX.addListener(property, "addListener", ReflectionLoader.CHANGELISTENER, this);

		/* --- titlebar (only used for cross-platform look) */
		dialogTitleBar = ReflectionLoader.newInstance(ReflectionLoader.TOOLBAR);
		JavaBridgeFX.setStyle(dialogTitleBar, false, "window-header");
		ReflectionLoader.call(dialogTitleBar, "setPrefHeight", double.class, HEADER_HEIGHT);
		ReflectionLoader.call(dialogTitleBar, "setMinHeight", double.class, HEADER_HEIGHT);
		ReflectionLoader.call(dialogTitleBar, "setMaxHeight", double.class, HEADER_HEIGHT);
		for (Control element : titleElements) {
			Object guiElement = JavaBridgeFX.convert(element, true);
			JavaBridgeFX.addChildren(dialogTitleBar, -1, guiElement);
		}

		JavaBridgeFX.addListener(dialogTitleBar, "setOnMousePressed", ReflectionLoader.EVENTHANDLER, this);

		JavaBridgeFX.addListener(dialogTitleBar, "setOnMouseDragged", ReflectionLoader.EVENTHANDLER, this);

		ReflectionLoader.call(root, "setTop", ReflectionLoader.NODE, dialogTitleBar);
		ReflectionLoader.call(root, "setCenter", ReflectionLoader.NODE, center);
		if (this.actionElements.size() > 0) {
			Object actionToolbar = ReflectionLoader.newInstance(ReflectionLoader.HBOX);
			JavaBridgeFX.setStyle(actionToolbar, false, "window-action");
			for (Control item : this.actionElements) {
				Object guiElement = JavaBridgeFX.convert(item, false);
				JavaBridgeFX.addChildren(actionToolbar, -1, guiElement);
			}
			Object pos = ReflectionLoader.getField(ReflectionLoader.POS, "TOP_RIGHT");
			ReflectionLoader.call(actionToolbar, "setAlignment", ReflectionLoader.POS, pos);
			ReflectionLoader.call(root, "setBottom", ReflectionLoader.NODE, actionToolbar);
		}
		return this;
	}

	public DialogBox withCenter(Object node) {
		this.center = node;
		return this;
	}

	public DialogBox withTitleButton(int index, Control... value) {
		if (value == null) {
			return this;
		}
		ArrayList<Control> items = new ArrayList<Control>();
		for (Control item : value) {
			items.add(item);
		}
		this.titleElements.addAll(index, items);
		return this;

	}

	public DialogBox withTitleButton(Control... value) {
		if (value == null) {
			return this;
		}
		for (Control item : value) {
			this.titleElements.add(item);
		}
		return this;
	}

	public DialogBox withActionButton(int index, Control... value) {
		if (value == null) {
			return this;
		}
		ArrayList<Control> items = new ArrayList<Control>();
		for (Control item : value) {
			items.add(item);
		}
		this.actionElements.addAll(index, items);
		return this;

	}

	public DialogBox withActionButton(Control... value) {
		if (value == null) {
			return this;
		}
		for (Control item : value) {
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
		if (value == null) {
			return this;
		}
		if (Os.isReflectionTest()) {
			return this;
		}
		Object box = ReflectionLoader.newInstance(ReflectionLoader.HBOX);
		URL resource = DialogBox.class.getResource(image);
		if (resource == null) {
			return this;
		}
		Object imageView = ReflectionLoader.newInstance(ReflectionLoader.IMAGEVIEW, String.class, resource.toString());

		Object text = ReflectionLoader.newInstance(ReflectionLoader.LABEL, String.class, value);
		JavaBridgeFX.setStyle(text, false, "labelText");

		Object vBox = ReflectionLoader.newInstance(ReflectionLoader.VBOX);
		Object pos = ReflectionLoader.getField(ReflectionLoader.POS, "CENTER");
		ReflectionLoader.call(vBox, "setAlignment", ReflectionLoader.POS, pos);
		JavaBridgeFX.addChildren(vBox, -1, text);

		JavaBridgeFX.addChildren(box, -1, imageView, vBox);
		JavaBridgeFX.setStyle(box, false, "centerbox");
		this.center = box;
		return this;
	}

	public static Button showInfo(Object parent, String title, String text, boolean inLine) {
		DialogBox dialogBox = new DialogBox().withTitle(title).withCenterInfo(text).withInline(inLine);
		return dialogBox.withActionButton(new Button().withActionType(Button.CLOSE, dialogBox).withValue("OK"))
				.show(parent);
	}

	public static Button showInfo(String title, String text) {
		DialogBox dialogBox = new DialogBox().withTitle(title).withCenterInfo(text);
		return dialogBox.withActionButton(new Button().withValue("OK").withActionType(Button.CLOSE, dialogBox))
				.show(null);
	}

	public static Button showQuestion(Object parent, String title, String text) {
		DialogBox dialogBox = new DialogBox().withTitle(title).withCenterInfo(text);
		return dialogBox.withActionButton(new Button().withValue("Yes").withActionType(Button.CLOSE, dialogBox),
				new Button().withValue("No").withActionType(Button.CLOSE, dialogBox)).show(parent);
	}

	public static boolean showQuestionCheck(Object parent, String title, String text, String... check) {
		Button action = showQuestion(parent, title, text);
		if (action == null) {
			return false;
		}
		for (String item : check) {
			if (item != null) {
				if (item.equalsIgnoreCase(action.getValue())) {
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
	public boolean update(Object event) {
		if (event == null) {
			return false;
		}
		if (event.getClass().getName().startsWith("javafx")) {
			double x = (Double) ReflectionLoader.call(event, "getSceneX");
			double y = (Double) ReflectionLoader.call(event, "getSceneY");
			String name = (String) ReflectionLoader.call(event, "getEventType", "getName");
			if (name == null || name.startsWith("MOUSE-DRAG") == false) {
				mouseDragDeltaX = x;
				mouseDragDeltaY = y;
			} else {
				double eventX = x - mouseDragDeltaX;
				double eventY = y - mouseDragDeltaY;
				if (isInline) {
					double xNew = (Double) ReflectionLoader.call(root, "getLayoutX");
					double yNew = (Double) ReflectionLoader.call(root, "getLayoutY");
					ReflectionLoader.call(root, "setLayoutX", double.class, xNew + eventX);
					ReflectionLoader.call(root, "setLayoutY", double.class, yNew + eventY);
				} else {
					ReflectionLoader.call(stage, "setX", double.class, eventX);
					ReflectionLoader.call(stage, "setY", double.class, eventY);
				}

				return true;
			}
			return true;
		}

		if (event instanceof Boolean) {
			boolean active = (Boolean) event;
			ReflectionLoader.call(root, "pseudoClassStateChanged", ReflectionLoader.PSEUDOCLASS, ACTIVE_PSEUDO_CLASS,
					boolean.class, active);
			return true;
		}

		if (event instanceof Button == false) {
			return false;
		}
		Button btn = (Button) event;
		if (Button.CLOSE.equalsIgnoreCase(btn.getActionType())) {
			this.hide(btn);
		}
		if (Button.MINIMIZE.equalsIgnoreCase(btn.getActionType())) {
			this.minimize();
		}
		if (Button.MAXIMIZE.equalsIgnoreCase(btn.getActionType())) {
			this.maximize();
		}
		return true;
	}

	public double prefWidth(double value) {
		if(root != null) {
			return (Double) ReflectionLoader.call(root, "prefWidth", double.class, -1);
		}
		return -1;
	}

	public double prefHeight(double value) {
		if(root != null) {
			return (Double) ReflectionLoader.call(root, "prefHeight", double.class, -1);
		}
		return -1;
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
		return ReflectionLoader.call(stage, "getScene");
	}

	public static String showFileSaveChooser(String caption, String defaultValue, String typeName, String typeExtension,
			Object... parent) {
		return showFileChooser("save", caption, defaultValue, typeName, typeExtension, parent);
	}

	@SuppressWarnings("unchecked")
	public static String showFileChooser(String art, String caption, String defaultValue, String typeName,
			String extensions, Object... parent) {
		Object parentObj = null;
		if (parent != null && parent.length > 0) {
			parentObj = parent[0];
		}
		if (typeName != null) {
			typeName += " (*." + extensions + ")";
		}
		File result;
		if (ReflectionLoader.FILECHOOSERFX != null) {
			/* try JavaFX Dialog */
			Object fileChooser = ReflectionLoader.newInstance(ReflectionLoader.FILECHOOSERFX);
			ReflectionLoader.call(fileChooser, "setTitle", caption);
			ReflectionLoader.call(fileChooser, "setInitialFileName", defaultValue);
			if (typeName != null) {
				Class<?> filterClass = ReflectionLoader.getClass("javafx.stage.FileChooser$ExtensionFilter");
				Object filter = ReflectionLoader.newInstance(filterClass, String.class, typeName, String[].class,
						new String[] { "*." + extensions });
				List<Object> list = (List<Object>) ReflectionLoader.call(fileChooser, "getExtensionFilters");
				list.add(filter);
			}
			Class<?> windowClass = ReflectionLoader.getClass("javafx.stage.Window");

			if ("save".equals(art)) {
				result = (File) ReflectionLoader.call(fileChooser, "showSaveDialog", windowClass, parentObj);
			} else {
				result = (File) ReflectionLoader.call(fileChooser, "showOpenDialog", windowClass, parentObj);
			}
			if (result != null) {
				return result.getAbsolutePath();
			}
		} else {
			/* SWING??? */
			ReflectionLoader.logger = new PrintStream(System.out);
			if (parentObj == null || ReflectionLoader.JFRAME.isAssignableFrom(parentObj.getClass()) == false) {
				parentObj = ReflectionLoader.newInstance(ReflectionLoader.JFRAME);
			}
			Object fileChooser = ReflectionLoader.newInstance(ReflectionLoader.JFILECHOOSER);
			ReflectionLoader.call(fileChooser, "setDialogTitle", caption);
			int userSelection = -1;
			if (defaultValue != null) {
				ReflectionLoader.call(fileChooser, "setSelectedFile", new File(defaultValue));
			}
			if (typeName != null) {
				Class<?> filterClass = ReflectionLoader.getClass("javax.swing.filechooser.FileNameExtensionFilter");
				Class<?> fileFilter = ReflectionLoader.getClass("javax.swing.filechooser.FileFilter");
				Object filter = ReflectionLoader.newInstance(filterClass, String.class, typeName, String[].class,
						new String[] { extensions });
				ReflectionLoader.call(fileChooser, "setFileFilter", fileFilter, filter);
			}
			Class<?> componentClass = ReflectionLoader.getClass("java.awt.Component");
			if ("save".equals(art)) {
				userSelection = (Integer) ReflectionLoader.call(fileChooser, "showSaveDialog", componentClass,
						parentObj);
			} else {
				userSelection = (Integer) ReflectionLoader.call(fileChooser, "showOpenDialog", componentClass,
						parentObj);
			}
			if (userSelection == 0) {
				File fileToSave = (File) ReflectionLoader.call(fileChooser, "getSelectedFile");
				return fileToSave.getAbsolutePath();
			}
		}
		return null;
	}
}
