package de.uniks.networkparser.graph;

public enum Cardinality
{
	
   ONE("1"), MANY("n");

   private String value;
   
   private Cardinality(String value){
	   this.value = value;
   }
   
   @Override
   public String toString()
   {
      return super.toString().toLowerCase();
   }

	public String getValue() {
		return value;
	}
	
	public Cardinality withValue(String value) {
		this.value = value;
		return this;
	}
}
