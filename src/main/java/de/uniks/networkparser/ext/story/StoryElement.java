package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SendableItem;

/**
 * The Class StoryElement.
 *
 * @author Stefan
 */
public abstract class StoryElement extends SendableItem {
	
	/**
	 * Gets the output file.
	 *
	 * @param calculate the calculate
	 * @return the output file
	 */
	public abstract String getOutputFile(boolean calculate);

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public abstract String getLabel();

	/**
	 * Write to file.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 */
	public abstract boolean writeToFile(String... fileName);
}
