package de.uniks.networkparser.graph;

public interface GraphType {
	   public static final GraphDataType VOID = new GraphDataType("void");
	   public static final GraphDataType INT = new GraphDataType("int");
	   public static final GraphDataType LONG = new GraphDataType("long");
	   public static final GraphDataType DOUBLE = new GraphDataType("double");
	   public static final GraphDataType STRING = new GraphDataType("String");
	   public static final GraphDataType BOOLEAN = new GraphDataType("boolean");
	   public static final GraphDataType OBJECT = new GraphDataType("java.lang.Object");
	   public static final GraphDataType CHAR = new GraphDataType("char");
	   public String getName(boolean shortName);
}
