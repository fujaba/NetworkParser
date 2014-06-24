package de.uniks.networkparser.graph;

import de.uniks.networkparser.interfaces.BaseItem;


public abstract class Value implements BaseItem
{
   public static final String PROPERTY_INITIALIZATION = "initialization";
   public static final String PROPERTY_TYPE = "type";
   
   protected DataType type = null;
   protected String name;
   
	
	public String getName() {
		return name;
	}
	public Value with(String value) {
		this.name = value;
		return this;
	}

	public Value with(DataType value)
   {
	   if (( this.type==null && value!=null) || (this.type!=null && this.type!=value))
      {
         this.type = value;
      }
      return this;
   } 
   

   public DataType getType()
   {
      return type;
   }
   
   public String getType(boolean shortName){
	   return type.getValue(shortName);
   }
}
