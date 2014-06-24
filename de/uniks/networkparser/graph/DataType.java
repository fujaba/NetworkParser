package de.uniks.networkparser.graph;

import java.beans.PropertyChangeSupport;

public class DataType
{
   public static final String PROPERTY_VALUE = "value";

   public static final DataType VOID = new DataType("void");
   public static final DataType INT = new DataType("int");
   public static final DataType LONG = new DataType("long");
   public static final DataType DOUBLE = new DataType("double");
   public static final DataType STRING = new DataType("String");
   public static final DataType BOOLEAN = new DataType("boolean");
   public static final DataType OBJECT = new DataType("Object");
   
   private String value;
   DataType(String value){
      this.with(value);
   }
   public String getValue()
   {
      return value;
   }
   public String getValue(boolean shortName){
	   if(!shortName || value==null || value.lastIndexOf(".")<0){
	       return value;
	   }
	   return value.substring(value.lastIndexOf(".") + 1);
   }
  
   public DataType with(String value){
      this.value = value;
      return this;
   }
   
   public static DataType ref(String value){
      return new DataType(value);
   }
   public static DataType ref(Class<?> value){
      return new DataType(value.getName().replace("$", "."));
   }
   public static DataType ref(GraphNode value){
      return new DataType(value.getClassName());
   }
   
   @Override
   public String toString()
   {
      return "DataType." + value.toUpperCase();
   }
   
   
   protected final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
}
