package de.uniks.networkparser;

import java.beans.PropertyChangeEvent;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleSet;

/**
 * Condition for Listener for changes in Element (Datamodel) in IdMap Or AtomarCondition with
 * PropertyChange.
 *
 * @author Stefan Lindel
 */
public class UpdateCondition implements ObjectCondition {
  private Object owner;
  private ObjectCondition condition; /* FOR ATOM OR TRANSACTION */

  /** FOR ACCUMULATE */
  private Tokener tokener;
  private Entity change;
  private SimpleMap map;
  /** Target */
  private Object defaultItem;

  /** Target or StartClass */
  private SendableEntityCreator creator;
  /** May be class<?> or Object */
  private String property;
  private ObjectCondition startCondition;

  private ObjectCondition endCondition;
  /** May be class<?> or Object */
  private String endProperty;
  private Object endClass;
  private SimpleSet<SimpleEvent> changes;
  private SendableEntityCreator endCreator;

  UpdateCondition() {

  }

  /**
   * With contidion.
   *
   * @param condition the condition
   * @return the update condition
   */
  public UpdateCondition withContidion(ObjectCondition condition) {
    this.condition = condition;
    return this;
  }

  /**
   * Creates the atomar condition.
   *
   * @param listener the listener
   * @return the update condition
   */
  public static UpdateCondition createAtomarCondition(ObjectCondition listener) {
    UpdateCondition condition = new UpdateCondition();
    condition.condition = listener;
    return condition;
  }

  /**
   * Creates the update condition.
   *
   * @return the update condition
   */
  public static UpdateCondition createUpdateCondition() {
    return new UpdateCondition();
  }

  /**
   * Creates the acumulate condition.
   *
   * @param tokener the tokener
   * @return the update condition
   */
  public static UpdateCondition createAcumulateCondition(Tokener tokener) {
    UpdateCondition condition = new UpdateCondition();
    condition.tokener = tokener;
    return condition;
  }

  /**
   * Creates the acumulate condition.
   *
   * @param map the map
   * @return the update condition
   */
  public static UpdateCondition createAcumulateCondition(SimpleMap map) {
    UpdateCondition condition = new UpdateCondition();
    if (map != null) {
      condition.tokener = map.getMapListener().getTokener();
    }
    condition.map = map;
    return condition;
  }

  /**
   * Creates the acumulate condition.
   *
   * @param tokener the tokener
   * @param target the target
   * @param creator the creator
   * @param property the property
   * @return the update condition
   */
  public static UpdateCondition createAcumulateCondition(Tokener tokener, Object target,
      SendableEntityCreator creator, String property) {
    UpdateCondition condition = new UpdateCondition();
    condition.tokener = tokener;
    condition.owner = target;
    condition.creator = creator;
    condition.property = property;
    return condition;
  }

  /**
   * Creates the transaction.
   *
   * @param map the map
   * @return the update condition
   */
  public static UpdateCondition createTransaction(SimpleMap map) {
    UpdateCondition condition = new UpdateCondition();
    condition.map = map;
    return condition;
  }

  /**
   * Checks if is atomar.
   *
   * @return true, if is atomar
   */
  public boolean isAtomar() {
    return !isTransaction() && condition != null;
  }

  /**
   * Checks if is transaction.
   *
   * @return true, if is transaction
   */
  public boolean isTransaction() {
    return (endProperty != null || endClass != null);
  }

  /**
   * Checks if is accumulate.
   *
   * @return true, if is accumulate
   */
  public boolean isAccumulate() {
    return tokener != null;
  }

  /**
   * Checks if is change listener.
   *
   * @return true, if is change listener
   */
  public boolean isChangeListener() {
    return condition == null;
  }

  /**
   * With start.
   *
   * @param property the property
   * @param startClass the start class
   * @return the update condition
   */
  public UpdateCondition withStart(String property, Object startClass) {
    this.owner = startClass;
    this.property = property;
    if (!(startClass instanceof Class<?>) && map != null) {
      this.creator = map.getCreatorClass(startClass);
    }
    return this;
  }

  /**
   * With start.
   *
   * @param startClass the start class
   * @return the update condition
   */
  public UpdateCondition withStart(Object startClass) {
    this.owner = startClass;
    if (map != null) {
      if (startClass instanceof Class<?>) {
        Class<?> subClass = (Class<?>) startClass;
        this.creator = map.getCreator(subClass.getName(), true);
      } else {
        this.creator = map.getCreatorClass(startClass);
      }
    }
    return this;
  }

  /**
   * With end.
   *
   * @param property the property
   * @return the update condition
   */
  public UpdateCondition withEnd(String property) {
    this.endProperty = property;
    return this;
  }

  /**
   * With start conition.
   *
   * @param condition the condition
   * @return the update condition
   */
  public UpdateCondition withStartConition(ObjectCondition condition) {
    this.startCondition = condition;
    return this;
  }

  /**
   * With end.
   *
   * @param property the property
   * @param endClass the end class
   * @return the update condition
   */
  public UpdateCondition withEnd(String property, Object endClass) {
    this.endClass = endClass;
    this.endProperty = property;
    if (map != null) {
      if (endClass instanceof Class<?>) {
        Class<?> subClass = (Class<?>) endClass;
        this.creator = map.getCreator(subClass.getName(), true);
      } else {
        this.creator = map.getCreatorClass(endClass);
      }
    }
    return this;
  }

  /**
   * With end conition.
   *
   * @param condition the condition
   * @return the update condition
   */
  public UpdateCondition withEndConition(ObjectCondition condition) {
    this.endCondition = condition;
    return this;
  }

  /**
   * Update.
   *
   * @param evt the evt
   * @return true, if successful
   */
  @Override
  public boolean update(Object evt) {
    if (isAtomar()) {
      if (evt instanceof PropertyChangeEvent) {
        return condition.update(evt);
      }
      return false;
    }
    /* MUST BE A SIMPLEEVENT */
    if (!(evt instanceof SimpleEvent)) {
      return false;
    }

    SimpleEvent event = (SimpleEvent) evt;
    if (isChangeListener()) {
      if (creator != null && property != null) {
        if (event.getNewValue() != null) {
          /* CREATE ONE */
          creator.setValue(event.getNewValue(), property, owner, SendableEntityCreator.NEW);
        } else {
          creator.setValue(event.getOldValue(), property, owner, SendableEntityCreator.REMOVE);
        }
        return false;
      }
      Object source = event.getSource();
      if (source instanceof SimpleMap) {
    	  SimpleMap m = (SimpleMap) source;
        return m.getKey(event.getModelValue()) == null && m.getKey(event.getNewValue()) == null;
      }
      return false;
    }
    if (isTransaction()) {
      if (startCondition != null && !startCondition.update(evt)) {
        return false;
      }
      Object source = event.getSource();
      if (source == null) {
        return false;
      }
      if (changes == null && property != null && property.equalsIgnoreCase(event.getPropertyName())) {
        /* Search for Start Transaction */
        if (owner instanceof Class<?> && source.getClass() == owner) {
          this.changes = new SimpleSet<SimpleEvent>();
          return true;
        }
      } else if (owner != null && creator != null) {
        SendableEntityCreator c = map.getCreatorClass(source);
        if (c != null && c == this.creator) {
          this.changes = new SimpleSet<SimpleEvent>();
          return true;
        }
        c = map.getCreatorClass(event.getNewValue());
        if (c != null && c == this.creator) {
          this.changes = new SimpleSet<SimpleEvent>();
          return true;
        }
      }

      if (this.changes != null) {
        this.changes.add(event);
        /* Check for End */
        if (endCondition != null && !endCondition.update(evt)) {
          return true;
        }
        if (endProperty != null && endProperty.equalsIgnoreCase(event.getPropertyName())) {
          /* Search for Start Transaction */
          if (endClass instanceof Class<?>) {
            if (source.getClass() == endClass) {
              if (this.condition != null) {
                return this.condition.update(this.changes);
              }
              this.changes = null;
              return true;
            }
          } else if (endClass != null && endCreator != null) {
            SendableEntityCreator c = map.getCreatorClass(source);
            if (c != null && c == endCreator) {
              if (this.condition != null) {
                return this.condition.update(this.changes);
              }
              this.changes = null;
              return true;
            }
          } else if (creator != null && owner != null) {
            SendableEntityCreator c = map.getCreatorClass(event.getModelValue());
            if (c != null && this.creator == c) {
              if (this.condition != null) {

                SimpleEvent eventTransaction = new SimpleEvent(this, "transaction", null,
                    mergeChanges());
                return this.condition.update(eventTransaction);
              }
              this.changes = null;
              return true;
            }

          }
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Merge changes.
   *
   * @return the object
   */
  public Object mergeChanges() {
    if (this.changes == null) {
      return null;
    }
    Entity mergeChange = null;
    Entity mergeUpdate = null;

    for (Object change : this.changes) {
      if (!(change instanceof SimpleEvent)) {
        continue;
      }
      SimpleEvent evt = (SimpleEvent) change;
      Entity entity = evt.getEntity();
      if (mergeChange == null) {
        mergeChange = (Entity) entity.getNewList(true);
        /* Copy first One */
        for (int i = 0; i < entity.size(); i++) {
          String key = entity.getKeyByIndex(i);
          Object value = entity.getValueByIndex(i);
          mergeChange.put(key, value);
          if (SendableEntityCreator.UPDATE.equals(key)) {
            mergeUpdate = (Entity) value;
          }
        }
      } else {
        for (int i = 0; i < entity.size(); i++) {
          String key = entity.getKeyByIndex(i);
          if (SendableEntityCreator.UPDATE.equals(key)) {
            Object value = entity.getValueByIndex(i);
            if (value instanceof Entity) {
              Entity valueEntity = (Entity) value;
              for (int c = 0; c < valueEntity.size(); c++) {
                String valueKey = valueEntity.getKeyByIndex(c);
                Object valueValue = valueEntity.getValueByIndex(c);
                if (mergeUpdate != null) {
                  mergeUpdate.put(valueKey, valueValue);
                }
              }
            }
          }
        }
      }
    }
    return mergeChange;
  }

  /**
   * Change item.
   *
   * @param source the source
   * @param target the target
   * @param property the property
   * @return true, if successful
   */
  public boolean changeItem(Object source, Object target, String property) {
    if (map == null) {
      return false;
    }
    SendableEntityCreator creator = map.getCreatorClass(source);
    Object defaultItem = creator.getSendableInstance(true);
    Object oldValue = creator.getValue(source, property);
    Object newValue = creator.getValue(source, property);

    if ((oldValue == null && newValue == null) || (oldValue != null && oldValue.equals(newValue))) {
      return false;
    }

    if (oldValue != creator.getValue(defaultItem, property)) {
      if (change == null) {
        change = tokener.newInstance();
        change.put(SimpleMap.ID, map.getId(source, true));
      }
      Entity child;

      /* OldValue */
      if (change.has(SendableEntityCreator.REMOVE)) {
        child = tokener.newInstance();
      } else {
        child = (Entity) change.getValue(SendableEntityCreator.REMOVE);
        change.put(SendableEntityCreator.REMOVE, child);
      }
      SendableEntityCreator creatorClass = map.getCreatorClass(oldValue);
      if (creatorClass != null) {
        String oldId = map.getId(oldValue, true);
        if (oldId != null) {
          Entity item = tokener.newInstance();
          item.put(SimpleMap.ID, oldId);
          child.put(property, item);
        }
      } else {
        child.put(property, oldValue);
      }

      /* NewValue */
      if (change.has(SendableEntityCreator.UPDATE)) {
        child = tokener.newInstance();
      } else {
        child = (Entity) change.getValue(SendableEntityCreator.UPDATE);
        change.put(SendableEntityCreator.UPDATE, child);
      }

      creatorClass = map.getCreatorClass(newValue);
      if (creatorClass != null) {
        String newId = map.getId(newValue, true);
        if (newId != null) {
          Entity item = tokener.newInstance();
          item.put(SimpleMap.ID, newId);
          child.put(property, item);
        }
      } else {
        child.put(property, newValue);
      }
    }
    return true;
  }

  /**
   * With attribute.
   *
   * @param newValue the new value
   * @param property the property
   * @return the update condition
   */
  public UpdateCondition withAttribute(Object newValue, String property) {
    changeAttribute(newValue, property);
    return this;
  }

  /**
   * With target.
   *
   * @param value the value
   * @return the update condition
   */
  public UpdateCondition withTarget(Object value) {
    this.owner = value;
    if (value != null && map != null) {
      this.creator = map.getCreatorClass(owner);
      if (this.creator != null) {
        this.defaultItem = creator.getSendableInstance(true);
      }
    }
    return this;
  }

  /**
   * With acumulate target.
   *
   * @param value the value
   * @param creator the creator
   * @param property the property
   * @return the update condition
   */
  public UpdateCondition withAcumulateTarget(Object value, SendableEntityCreator creator, String property) {
    this.owner = value;
    this.creator = creator;
    this.property = property;
    return this;
  }

  private void addChange(UpdateListener listener, Object source, SendableEntityCreator creator, String property,
      Object oldValue, Object newValue) {
    if (listener == null) {
      return;
    }
    if (this.change == null) {
      this.change = listener.change(property, source, creator, oldValue, newValue);
    } else {
      listener.change(property, creator, change, oldValue, newValue);
    }
  }

  /**
   * Change attribute.
   *
   * @param listener the listener
   * @param source the source
   * @param creator the creator
   * @param property the property
   * @param oldValue the old value
   * @param newValue the new value
   * @return true, if successful
   */
  public boolean changeAttribute(UpdateListener listener, Object source, SendableEntityCreator creator,
      String property, Object oldValue, Object newValue) {
    if (this.owner == null) {
      addChange(listener, source, creator, property, oldValue, newValue);
      return true;
    } else if (this.property == null) {
      addChange(listener, source, creator, property, oldValue, newValue);
      return true;
    } else if (this.property.equals(property)) {
      addChange(listener, source, creator, property, oldValue, newValue);
      return true;
    }
    return false;
  }

  /**
   * Change attribute.
   *
   * @param newValue the new value
   * @param property the property
   * @return true, if successful
   */
  public boolean changeAttribute(Object newValue, String property) {
    return changeAttribute(owner, newValue, property, creator, defaultItem);
  }

  private boolean changeAttribute(Object target, Object newValue, String property, SendableEntityCreator creator,
      Object defaultItem) {
    if (creator == null) {
      return false;
    }
    Object oldValue = creator.getValue(target, property);

    if ((oldValue == null && newValue == null) || (oldValue != null && oldValue.equals(newValue))) {
      return false;
    }

    if (oldValue != creator.getValue(defaultItem, property)) {
      if (change == null) {
        change = tokener.newInstance();
        change.put(SimpleMap.ID, map.getId(target, true));
      }
      Entity child;

      /* OldValue */
      if (change.has(SendableEntityCreator.REMOVE)) {
        child = (Entity) change.getValue(SendableEntityCreator.REMOVE);
        change.put(SendableEntityCreator.REMOVE, child);
      } else {
        child = tokener.newInstance();
        change.put(SendableEntityCreator.REMOVE, child);
      }
      SendableEntityCreator creatorClass = map.getCreatorClass(oldValue);
      if (creatorClass != null) {
        String oldId = map.getId(oldValue, true);
        if (oldId != null) {
          Entity childItem = tokener.newInstance();
          childItem.put(SimpleMap.ID, oldId);
          child.put(property, childItem);
        }
      } else {
        child.put(property, oldValue);
      }

      /* NewValue */
      if (change.has(SendableEntityCreator.UPDATE)) {
        child = (Entity) change.getValue(SendableEntityCreator.UPDATE);
        change.put(SendableEntityCreator.UPDATE, child);
      } else {
        child = tokener.newInstance();
        change.put(SendableEntityCreator.UPDATE, child);
      }

      creatorClass = map.getCreatorClass(newValue);
      if (creatorClass != null) {
        String newId = map.getId(newValue, true);
        if (newId != null) {
          Entity childItem = tokener.newInstance();
          childItem.put(SimpleMap.ID, newId);
          child.put(property, childItem);
        }
      } else {
        child.put(property, newValue);
      }
    }
    return true;
  }

  /**
   * Gets the change.
   *
   * @return the change
   */
  public Entity getChange() {
    return change;
  }

  /**
   * With change.
   *
   * @param change the change
   * @return the update condition
   */
  public UpdateCondition withChange(Entity change) {
    this.change = change;
    return this;
  }
}
