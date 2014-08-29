package de.uniks.networkparser.graph;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
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
   DataType(String value) {
      this.with(value);
   }
   public String getValue()
   {
      return value;
   }
   public String getValue(boolean shortName) {
	   if (!shortName || value==null || value.lastIndexOf(".")<0) {
	       return value;
	   }
	   return value.substring(value.lastIndexOf(".") + 1);
   }
 
   public DataType with(String value) {
      this.value = value;
      return this;
   }
  
   public static DataType ref(String value) {
      return new DataType(value);
   }
   public static DataType ref(Class<?> value) {
      return new DataType(value.getName().replace("$", "."));
   }
   public static DataType ref(GraphNode value) {
      return new DataType(value.getClassName());
   }
  
   @Override
   public String toString()
   {
      return "DataType." + value.toUpperCase();
   }
  
  
   protected final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
}
