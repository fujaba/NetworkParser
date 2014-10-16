package de.uniks.networkparser.gui.controls;

import java.util.List;

public interface AutoCompletion<T> {
	public List<T> items(String text);
}
