package de.uniks.networkparser.ext.javafx.window;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
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

	public FXStageController getController() {
		return this.controller;
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
