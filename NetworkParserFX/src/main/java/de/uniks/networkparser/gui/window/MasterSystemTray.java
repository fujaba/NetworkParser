package de.uniks.networkparser.gui.window;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.net.URL;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MasterSystemTray implements StageEvent{
	protected FXStageController controller;
	protected PopupMenu popupMenu;
	protected TrayIcon trayIcon;
	@Override
	public void stageClosing(WindowEvent event, Stage stage,
			FXStageController controller) {
	}

	public MenuItem addMenuItem(String text, ActionListener listener) {
		MenuItem item = new MenuItem(text);
		item.addActionListener(listener);
		getPopUp().add(item);
		return item;
	}

	public void addSeperator() {
		getPopUp().addSeparator();
	}
	
	private Menu getPopUp() {
		if(popupMenu == null) {
			popupMenu = new PopupMenu();
			if(trayIcon != null) {
				trayIcon.setPopupMenu(popupMenu);
			}
		}
		return popupMenu;
	}

	@Override
	public void stageShowing(WindowEvent event, Stage stage,
			FXStageController controller) {
		this.controller = controller;
		URL url = getClass().getResource("/de/uniks/networkparser/gui/dialog/JavaCup32.png");
		Image img = Toolkit.getDefaultToolkit().getImage(url);
		this.trayIcon = new TrayIcon(img);
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public MasterSystemTray withToolTip(String text) {
		trayIcon.setToolTip(text);
		return this;
	}

	public MasterSystemTray withIcon(URL image) {
		trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(image));
		return this;
	}
}