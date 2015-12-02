package de.uniks.networkparser.interfaces;

@FunctionalInterface
public interface CellHandler {
	public boolean onAction(Object entity, SendableEntityCreator creator, double x, double y);
}
