package de.uniks.networkparser.gui.dialog;


import java.net.URL;
import java.util.ArrayList;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import com.sun.javafx.tk.Toolkit;

import de.uniks.networkparser.gui.dialog.DialogButton.Grafik;
import de.uniks.networkparser.gui.window.KeyListenerMap;

public class DialogBox {
	protected static final PseudoClass ACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("active");
	protected static final int HEADER_HEIGHT = 28;
	protected static final URL DIALOGS_CSS_URL = DialogBox.class.getResource("dialogs.css");
    protected ToolBar dialogTitleBar;
   
	private BorderPane root;
	private Stage stage;
	private Scene scene;
	private Parent owner;
	private TitleText titleElement = new TitleText();
	private ArrayList<DialogElement> titleElements = new ArrayList<DialogElement>();
	private ArrayList<DialogElement> actionElements = new ArrayList<DialogElement>();
	private boolean modal = true;
	private double mouseDragDeltaY;
	private double mouseDragDeltaX;
	private boolean alwaysOnTop;
	
	//Inline Show
	private boolean isInline;
	private Parent originalParent;
	private Pane dialogStack;
	private Region opaqueLayer;
	private boolean iconified;
	private DialogButton action;
	private Node center;
	
	public DialogBox() {
		titleElements.add(titleElement);
		titleElements.add(new TitleSpacer());
		titleElements.add(new DialogButton().withGrafik(Grafik.close));
//        closeButton = new DialogButton().withGrafik(Grafik.close).withStage(stage);
//        minButton = new DialogButton().withGrafik(Grafik.minimize).withStage(stage);
//        maxButton = new DialogButton().withGrafik(Grafik.maximize).withStage(stage);
	}
	
	public DialogBox withTitle(String value) {
		if(value != null) {
			titleElement.setText(value);
		}
		return this;
	}
	public DialogButton show(Window owner){
		if(titleElement.getText().length() < 1 ) {
			if(owner instanceof Stage) {
				titleElement.setText( ((Stage) owner).getTitle() );
			}
		}
		if(isInline) {
			return showIntern(owner);
		}
		return showExtern(owner);
		
	}
	
	private DialogButton showIntern(Window parent) {
		if (parent instanceof Stage) {
			this.stage = (Stage) parent;
			this.scene = stage.getScene();
        }else{
        	return null;
        }
		configScene();
        
        // modify scene root to install opaque layer and the dialog
        originalParent = scene.getRoot();

        createContent();

        buildDialogStack(originalParent);

        root.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, true);
        
        root.setVisible(true);
        

        // add to originalParent
        Parent originalParent = scene.getRoot();
        scene.setRoot(dialogStack);
        if (originalParent != null) {
        	dialogStack.getChildren().add(0, originalParent);
        	dialogStack.getProperties().putAll(originalParent.getProperties());
        }

        root.requestFocus();
        if(!modal) {
        	Toolkit.getToolkit().enterNestedEventLoop(root);
        	return action;
        }
        return null;
	}
	
	public void hide(DialogButton value) {
		if(this.action == null) {
			this.setAction(value);
		}
	}
	
	public void setAction(DialogButton value) {
		this.action = value;
		if(isInline) {
	        // hide the dialog
			root.setVisible(false);
	        // reset the scene root
	        Parent oldParent = scene.getRoot();
	        if(oldParent instanceof Pane) {
	        	((Pane) oldParent).getChildren().remove(originalParent);
	        	originalParent.getStyleClass().remove("root");
		        scene.setRoot(originalParent);
	        }
	        if(!modal) {
	        	Toolkit.getToolkit().exitNestedEventLoop(root, null);
	        }
	        return;
		}
		if (stage != null) {
			stage.hide();
		}
	}

	public void minimize() {
		if (stage != null) {
			stage.setIconified(this.iconified);
		}
	}

	public void maximize() {
		if(isInline) {
//			if(originalParent instanceof Node) {
//			root.setPrefWidth(originalParent.getWidth());
		}
		if (stage != null) {
			stage.setFullScreen(true);
		}
	}
	
	private void buildDialogStack(final Node parent) {
        dialogStack = new Pane() {
            private boolean isFirstRun = true;
            {
            	if(!modal){
	            	opaqueLayer = new Region();
	                opaqueLayer.getStyleClass().add("lightweight-dialog-background");
	                getChildren().add(0, opaqueLayer);
            	}
                getChildren().add(root);
            }
            
            @Override protected void layoutChildren() {
                final double w = getOverlayWidth();
                final double h = getOverlayHeight();
                
                final double x = getOverlayX();
                final double y = getOverlayY();
                
                if (parent != null) {
                    parent.resizeRelocate(x, y, w, h);
                }
                
                if (opaqueLayer != null) {
                    opaqueLayer.resizeRelocate(x, y, w, h);
                }
                
                final double dialogWidth = root.prefWidth(-1);
                final double dialogHeight = root.prefHeight(-1);
                root.resize((int)(dialogWidth), (int)(dialogHeight));
                
                // hacky, but we only want to position the dialog the first time 
                // it is laid out - after that the only way it should move is if
                // the user moves it.
                if (isFirstRun) {
                    isFirstRun = false;
                    
                    double dialogX = root.getLayoutX();
                    dialogX = dialogX == 0.0 ? w / 2.0 - dialogWidth / 2.0 : dialogX;
                    
                    double dialogY = root.getLayoutY();
                    dialogY = dialogY == 0.0 ? h / 2.0 - dialogHeight / 2.0 : dialogY;
                    
                    root.relocate((int)(dialogX), (int)(dialogY));
                }
            }
            
            // These are the actual implementations in Region (the parent of Pane),
            // but just for clarify I reproduce them here
            @Override protected double computeMinHeight(double width) {
                return parent.minHeight(width);
            }
            
            @Override protected double computeMinWidth(double height) {
                return parent.minWidth(height);
            }
            
            @Override protected double computePrefHeight(double width) {
                return parent.prefHeight(width);
            }
            
            @Override protected double computePrefWidth(double height) {
                return parent.prefWidth(height);
            }
            
            @Override protected double computeMaxHeight(double width) {
                return parent.maxHeight(width);
            }
            
            @Override protected double computeMaxWidth(double height) {
                return parent.maxWidth(height);
            }
        };
                
        dialogStack.setManaged(true);
    }
	
    private double getOverlayWidth() {
        if (owner != null) {
            return owner.getLayoutBounds().getWidth();
        } else if (scene != null) {
            return scene.getWidth();
        } 
        
        return 0;
    }
    
    private double getOverlayHeight() {
        if (owner != null) {
            return owner.getLayoutBounds().getHeight();
        } else if (scene != null) {
            return scene.getHeight();
        } 
        
        return 0;
    }
	
    private double getOverlayX() {
        return 0;
    }
    
    private double getOverlayY() {
        return 0;
    }
    
	private DialogButton showExtern(Window owner) {
		stage = new Stage(StageStyle.TRANSPARENT) {
            @Override public void showAndWait() {
                centerOnScreen();
                super.showAndWait();
            }
            
            @Override public void centerOnScreen() {
                Window owner = getOwner();
                if (owner != null) {
                    Scene scene = owner.getScene();
                    
                    // scene.getY() seems to represent the y-offset from the top of the titlebar to the
                    // start point of the scene, so it is the titlebar height
                    final double titleBarHeight = scene.getY();
                    
                    // because Stage does not seem to centre itself over its owner, we
                    // do it here.
                    double x, y;
                    
                    final double dialogWidth = root.prefWidth(-1);
                    final double dialogHeight = root.prefHeight(-1);
                    
                    if (owner.getX() < 0 || owner.getY() < 0) {
                        // Fix for #165
                        Screen screen = Screen.getPrimary(); // todo something more sensible
                        double maxW = screen.getVisualBounds().getWidth();
                        double maxH = screen.getVisualBounds().getHeight();
                        
                        x = maxW / 2.0 - dialogWidth / 2.0;
                        y = maxH / 2.0 - dialogHeight / 2.0 + titleBarHeight;
                    } else {
                        x = owner.getX() + (scene.getWidth() / 2.0) - (dialogWidth / 2.0);
                        y = owner.getY() +  titleBarHeight + (scene.getHeight() / 2.0) - (dialogHeight / 2.0);
                    }
                    
                    setX(x);
                    setY(y);
                }
            }
        };

        if (owner != null) {
            stage.initOwner(owner);
        }
        if (modal) {
            if (owner != null) {
                stage.initModality(Modality.WINDOW_MODAL);
            } else {
                stage.initModality(Modality.APPLICATION_MODAL);
            }
        } else {
            stage.initModality(Modality.NONE);
        }
        
        createContent();
        scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        configScene();
        
        if(modal) {
        	stage.setAlwaysOnTop(alwaysOnTop);
        	stage.showAndWait();
        	return action;
        }
        stage.show();
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

	
	  private void configScene() {
		Scene element = scene;
		String dialogsCssUrl = DIALOGS_CSS_URL.toExternalForm();
		if (scene != null) {
			// install CSS
			if (!scene.getStylesheets().contains(dialogsCssUrl)) {
				scene.getStylesheets().addAll(dialogsCssUrl);
			}
		} else if (owner != null) {
			element = owner.getScene();
			if (element != null) {
				// install CSS
				if (!element.getStylesheets().contains(dialogsCssUrl)) {
					element.getStylesheets().addAll(dialogsCssUrl);
				}
			}
		}
		element.addEventHandler(KeyEvent.ANY, new KeyListenerMap() );
	}
	
	public void createContent(){
        root = new BorderPane();
        root.getStyleClass().addAll("dialog", "decorated-root");
        
        stage.focusedProperty().addListener(new InvalidationListener () {

			@Override
			public void invalidated(Observable valueModel) {
				 boolean active = ((ReadOnlyBooleanProperty)valueModel).get();
		         root.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, active);
			}
        });
        
        // --- titlebar (only used for cross-platform look)
        dialogTitleBar = new ToolBar();
        dialogTitleBar.getStyleClass().add("window-header");
        dialogTitleBar.setPrefHeight(HEADER_HEIGHT);
        dialogTitleBar.setMinHeight(HEADER_HEIGHT);
        dialogTitleBar.setMaxHeight(HEADER_HEIGHT);
        for(DialogElement element : titleElements) {
	        dialogTitleBar.getItems().add((Node) element.withOwner(this));
        }
        dialogTitleBar.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(isInline) {
	        		mouseDragDeltaX = event.getSceneX();
	        		mouseDragDeltaY = event.getSceneY();
	        	}else{
	        		mouseDragDeltaX = event.getSceneX();
	        		mouseDragDeltaY = event.getSceneY();
	        	}				
			}
    	});
        dialogTitleBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
        	@Override
			public void handle(MouseEvent event) {
	        	if(isInline) {
	        		root.setLayoutX(root.getLayoutX() + (event.getSceneX() - mouseDragDeltaX));
	        		root.setLayoutY(root.getLayoutY() + (event.getSceneY() - mouseDragDeltaY));
	        		mouseDragDeltaX = event.getSceneX();
	        		mouseDragDeltaY = event.getSceneY();
	        		
	        	}else{
		            stage.setX(event.getScreenX() - mouseDragDeltaX);
		            stage.setY(event.getScreenY() - mouseDragDeltaY);
	        	}
	        }
        });
        root.setTop(dialogTitleBar);
        root.setCenter(center);
        if(this.actionElements.size() > 0) {
        	HBox actionToolbar = new HBox();
        	actionToolbar.getStyleClass().add("window-action");
        	for(DialogElement item : this.actionElements) {
        		item.withOwner(this);
        		actionToolbar.getChildren().add((Node) item);
        	}
        	actionToolbar.setAlignment(Pos.TOP_RIGHT);
        	root.setBottom(actionToolbar);
        }
	}
	
	public DialogBox withCenter(Node node) {
		this.center = node;
		return this;
	}
	
	public DialogBox withTitleButton(int index, DialogElement... value) {
		if(value==null){
			return this;
		}
		ArrayList<DialogElement> items=new ArrayList<DialogElement>();
		for(DialogElement item : value) {
			items.add(item);
		}
		this.titleElements.addAll(index, items);
		return this;
		
	}
	
	public DialogBox withTitleButton(DialogElement... value) {
		if(value==null){
			return this;
		}
		for(DialogElement item : value) {
			this.titleElements.add(item);
		}
		return this;
	}

	public DialogBox withActionButton(int index, DialogElement... value) {
		if(value==null){
			return this;
		}
		ArrayList<DialogElement> items=new ArrayList<DialogElement>();
		for(DialogElement item : value) {
			items.add(item);
		}
		this.actionElements.addAll(index, items);
		return this;
		
	}
	
	public DialogBox withActionButton(DialogElement... value) {
		if(value==null){
			return this;
		}
		for(DialogElement item : value) {
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
		HBox box = new HBox();
		
		ImageView imageView = new ImageView(DialogBox.class.getResource(image).toString());
		Label text= new Label(value);
		text.getStyleClass().add("labelText");
		
		VBox vBox=new VBox();
		vBox.setAlignment(Pos.CENTER);
		vBox.getChildren().add(text);
	
		box.getChildren().add(imageView);
		box.getChildren().add(vBox);
		box.getStyleClass().add("centerbox");
		this.center = box;
		return this;
	}
	
	public static DialogButton createInfo(Window parent, String title, String text) {
		return new DialogBox()
			.withTitle(title)
			.withCenterInfo(text)
			.withActionButton(new DialogButton().withName("OK").withAction(Grafik.close))
			.show(parent);
	}
	
	public static DialogButton createQuestion(Window parent, String title, String text) {
		return new DialogBox()
			.withTitle(title)
			.withCenterQuestion(text)
			.withActionButton(new DialogButton().withName("Yes").withAction(Grafik.close), new DialogButton().withName("No").withAction(Grafik.close))
			.show(parent);
	}
	public static boolean createQuestionCheck(Window parent, String title, String text, String... check) {
		DialogButton action = createQuestion(parent, title, text);
		if(action==null) {
			return false;
		}
		for(String item : check){
			if(item != null) {
				if(item.equalsIgnoreCase(action.getText())){
					return true;
				}
			}
		}
		return false;
	}

	public DialogButton getAction() {
		return action;
	}
}
