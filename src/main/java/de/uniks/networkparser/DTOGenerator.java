package de.uniks.networkparser;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * The Class DTOGenerator.
 *
 * @author Stefan
 */
public class DTOGenerator implements SendableEntityCreator {
  private SimpleKeyValueList<String, String> values = new SimpleKeyValueList<String, String>();
  private IdMap map;
  private SendableEntityCreator original;
  private Object prototype;
  private boolean enable = true;

  /**
   * Instantiates a new DTO generator.
   *
   * @param map the map
   * @param prototype the prototype
   */
  public DTOGenerator(IdMap map, Object prototype) {
    this.map = map;
    this.prototype = prototype;
    this.original = this.map.getCreatorClass(prototype);
    this.map.withCreator(this);
  }

  /**
   * Adds the.
   *
   * @param key the key
   * @param values the values
   * @return the DTO generator
   */
  public DTOGenerator add(String key, String... values) {
    if (key == null || values == null) {
      return this;
    }
    CharacterBuffer sb = new CharacterBuffer();
    if (sb.addValues('.', values)) {
      this.values.add(key, sb.toString());
    }
    return this;
  }

  /**
   * Gets the properties.
   *
   * @return the properties
   */
  @Override
  public String[] getProperties() {
    if (!enable && this.original != null) {
      return this.original.getProperties();
    }
    return this.values.keySet().toArray(new String[this.values.size()]);
  }

  /**
   * Gets the sendable instance.
   *
   * @param prototyp the prototyp
   * @return the sendable instance
   */
  @Override
  public Object getSendableInstance(boolean prototyp) {
    return prototype;
  }

  /**
   * Checks if is enable.
   *
   * @return true, if is enable
   */
  public boolean isEnable() {
    return enable;
  }

  /**
   * Sets the enable.
   *
   * @param enable the new enable
   */
  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  /**
   * Gets the original.
   *
   * @return the original
   */
  public SendableEntityCreator getOriginal() {
    return original;
  }

  /**
   * Gets the value.
   *
   * @param entity the entity
   * @param attribute the attribute
   * @return the value
   */
  @Override
  public Object getValue(Object entity, String attribute) {
    if (!enable && this.original != null) {
      return this.original.getValue(entity, attribute);
    }
    String referenzValue = this.values.get(attribute);
    if (entity.getClass() == this.prototype.getClass()) {
      // Must be original
      if (this.original == null) {
        return null;
      }
      return this.original.getValue(entity, referenzValue);
    }
    if (this.map == null) {
      return null;
    }
    SendableEntityCreator creator = this.map.getCreatorClass(entity);
    if (creator == null) {
      return null;
    }
    return creator.getValue(entity, referenzValue);
  }

  /**
   * Sets the value.
   *
   * @param entity the entity
   * @param attribute the attribute
   * @param value the value
   * @param type the type
   * @return true, if successful
   */
  @Override
  public boolean setValue(Object entity, String attribute, Object value, String type) {
    if (!enable && this.original != null) {
      return this.original.setValue(entity, attribute, value, type);
    }
    String referenzValue = this.values.get(attribute);
    if (entity.getClass() == this.prototype.getClass()) {
      // Must be original
      if (this.original == null) {
        return false;
      }
      return this.original.setValue(entity, referenzValue, value, type);
    }
    if (this.map == null) {
      return false;
    }
    SendableEntityCreator creator = this.map.getCreatorClass(entity);
    if (creator == null) {
      return false;
    }
    return creator.setValue(entity, referenzValue, value, type);
  }

}
