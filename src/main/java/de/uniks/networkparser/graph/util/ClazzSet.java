package de.uniks.networkparser.graph.util;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class ClazzSet extends SimpleSet<Clazz>
{
   public AttributeSet getAttributes() {
      AttributeSet collection = new AttributeSet();
      for(Clazz item : this) {
         collection.addAll(item.getAttributes());
      }
      return collection;
   }

   public AssociationSet getAssociations() {
      AssociationSet collection = new AssociationSet();
      for(Clazz item : this) {
         collection.addAll(item.getAssociations());
      }
      return collection;
   }

   public MethodSet getMethods() {
      MethodSet collection = new MethodSet();
      for(Clazz item : this) {
         collection.addAll(item.getMethods());
      }
      return collection;
   }

   public AnnotationSet getAnnotations() {
      AnnotationSet collection = new AnnotationSet();
      for(Clazz item : this) {
         collection.add(item.getAnnotation());
      }
      return collection;
   }
   
   public ModifierSet getModifiers() {
      ModifierSet collection = new ModifierSet();
      for(Clazz item : this) {
         collection.add(item.getModifier());
      }
      return collection;
   }

   @Override
   public ClazzSet filter(Condition<Clazz> newValue) {
      ClazzSet collection = new ClazzSet();
      filterItems( collection, newValue);
      return collection;
   }

   public ClazzSet hasName(String otherValue) {
      return filter(Clazz.NAME.equals(otherValue));
   }
}
