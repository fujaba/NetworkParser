package de.uniks.networkparser.gui;

import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * Created by Stefan on 29.10.13.
 */
public class ModelListenerNumberProperty extends ModelListenerProperty<Number> {

    public ModelListenerNumberProperty(SendableEntityCreator creator, Object item, String property) {
        super(creator, item, property);
    }
}
