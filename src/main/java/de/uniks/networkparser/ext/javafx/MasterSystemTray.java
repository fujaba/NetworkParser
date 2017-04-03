package de.uniks.networkparser.ext.javafx;

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
