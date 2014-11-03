package de.uniks.networkparser.gui.controls;

import java.util.Set;

public interface AutoCompletion<T> {
	public Set<T> items(String text, boolean caseSensitive);
}
