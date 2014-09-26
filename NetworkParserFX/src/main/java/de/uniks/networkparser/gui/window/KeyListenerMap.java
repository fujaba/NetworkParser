package de.uniks.networkparser.gui.window;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyListenerMap implements EventHandler<KeyEvent>{
	private WindowListener parent;
	private ArrayList<KeyListener> listener = new ArrayList<KeyListener>();

	public KeyListenerMap() {
		withKeyListener(new KeyListener(KeyCode.ESCAPE, new Runnable() {
			
			@Override
			public void run() {
				if(KeyListenerMap.this.parent != null) {
					KeyListenerMap.this.parent.close();
				}
			}
		}));
	}
	
	public KeyListenerMap(WindowListener value) {
		this();
		this.parent = value;
	}

	@Override
	public void handle(KeyEvent event) {
		for(KeyListener listener : listener) {
			if(listener.matches(event)){
				listener.getRunnable().run();
			}
		}
	}

	public KeyListenerMap withKeyListener(KeyCode keyCode, Runnable runnable) {
		this.listener.add(new KeyListener(keyCode, runnable));
		return this;
	}
	public KeyListenerMap withKeyListener(KeyListener listener) {
		this.listener.add(listener);
		return this;
	}

	//TODO REMOVE
//		DimoKeyListener.this.closeWindow();

//		}else if(arg0.keyCode==13){
//		DimoKeyListener.this.defaultButton();

		//		if(arg0.stateMask==SWT.CTRL&&arg0.keyCode==SWT.F8){
//		DimoKeyListener.this.openAdminPanel();
//	}else if(arg0.stateMask==SWT.CTRL&&arg0.keyCode==SWT.F12){
//		parent.pressedKey(arg0.stateMask, arg0.keyCode);
//	}else if(arg0.keyCode==SWT.ESC){
//		DimoKeyListener.this.closeWindow();
//		//FIXME STANDARDCONTROLDimoKeyListener.this.closeWindow();
////	}else if(arg0.keyCode==SWT.CTRL){
////		parent.pressedKey(arg0.keyCode);
//	}

	//
//	public DimoKeyListener(ClientGui parent, World world)
//	{
//		this.parent=parent;
//		this.setWorld(world);
//	}
//	@Override
//	public void keyPressed(KeyEvent arg0) {
////		System.out.println(arg0.stateMask+":"+arg0.keyCode);
//		if(arg0.stateMask==SWT.CTRL&&arg0.keyCode==SWT.F8){
//			DimoKeyListener.this.openAdminPanel();
//		}else if(arg0.stateMask==SWT.CTRL&&arg0.keyCode==SWT.F12){
//			parent.pressedKey(arg0.stateMask, arg0.keyCode);
//		}else if(arg0.keyCode==SWT.ESC){
//			DimoKeyListener.this.closeWindow();
//		}else if(arg0.keyCode==13){
//			DimoKeyListener.this.defaultButton();
//			//FIXME STANDARDCONTROLDimoKeyListener.this.closeWindow();
////		}else if(arg0.keyCode==SWT.CTRL){
////			parent.pressedKey(arg0.keyCode);
//		}
//		
//	}
//	
//	@Override
//	public void keyReleased(KeyEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void openAdminPanel()
//	{
//		if(parent instanceof Shell){
////			Display display=Display.getDefault();
//			parent.refreshGUI(World.MSGNOCHAT);
//			TableComponent tableComponent=null;
//			if(parent instanceof RegistrationDeskGUI){
//				tableComponent = ((RegistrationDeskGUI) parent).getTableComponent();
//			}
//			
//			new GUIAdministratorCreator(world, (Shell) parent, tableComponent).runAtDisplayASync();
////			AdministratorGui administratorGui = new AdministratorGui((Shell)parent, 0);
////			administratorGui.open(world);
//		}
//	}
//	public World getWorld() {
//		return world;
//	}
//	public void setWorld(World world) {
//		this.world = world;
//	}
//	public void SaveDebugInfo() {
//		this.world.saveHistory("%DEBUG%", false);
//		this.world.saveNetwork("%DEBUG%");
//	}
//	public void closeWindow(){
//		parent.closeWindow();
//	}
//	public void defaultButton() {
//		parent.defaultButton();
//	}

}
