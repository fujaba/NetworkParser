package de.uniks.networkparser;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class DTOGenerator implements SendableEntityCreator {
  private SimpleKeyValueList<String, String> values = new SimpleKeyValueList<String, String>();
  private IdMap map;
  private SendableEntityCreator original;
  private Object prototype;
  private boolean enable = true;

  public DTOGenerator(IdMap map, Object prototype) {
    this.map = map;
    this.prototype = prototype;
    this.original = this.map.getCreatorClass(prototype);
    this.map.withCreator(this);
  }

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

  @Override
  public String[] getProperties() {
    if (!enable && this.original != null) {
      return this.original.getProperties();
    }
    return this.values.keySet().toArray(new String[this.values.size()]);
  }

  @Override
  public Object getSendableInstance(boolean prototyp) {
    return prototype;
  }

  public boolean isEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public SendableEntityCreator getOriginal() {
    return original;
  }

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
