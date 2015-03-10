package de.uniks.networkparser.gui;

import de.uniks.networkparser.interfaces.SendableEntityCreator;

@FunctionalInterface
public interface CellHandler {
	public CellEditorElement onAction(String typ, Object entity, SendableEntityCreator creator, double x, double y);
}
