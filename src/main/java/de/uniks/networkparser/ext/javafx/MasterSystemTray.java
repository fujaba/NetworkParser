package de.uniks.networkparser.ext.javafx;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.net.URL;

public class MasterSystemTray { 
//implements StageEvent{
//	protected FXStageController controller;
	protected PopupMenu popupMenu;
	protected TrayIcon trayIcon;
//	@Override
//	public void stageClosing(WindowEvent event, Stage stage,
//			FXStageController controller) {
//	}

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

//	@Override
//	public void stageShowing(WindowEvent event, Stage stage,
//			FXStageController controller) {
//		this.controller = controller;
//		URL url = getClass().getResource("/de/uniks/networkparser/gui/dialog/JavaCup32.png");
//		Image img = Toolkit.getDefaultToolkit().getImage(url);
//		img.getGraphics();
//		
//		Graphics2D g2 = (Graphics2D) g;
//        int newW = (int) (originalImage.getWidth() * scaleFactor);
//        int newH = (int) (originalImage.getHeight() * scaleFactor);
//        this.setPreferredSize(new Dimension(newW, newH));
//        this.revalidate();
//        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                //RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
//                //RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//        g2.drawImage(originalImage, 0, 0, newW, newH, null);
//		this.trayIcon = new TrayIcon(img);
//		try {
//			SystemTray.getSystemTray().add(trayIcon);
//		} catch (AWTException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public FXStageController getController() {
//		return this.controller;
//	}

	public MasterSystemTray withToolTip(String text) {
		trayIcon.setToolTip(text);
		return this;
	}

	public MasterSystemTray withIcon(URL image) {
		trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(image));
		return this;
	}
}
