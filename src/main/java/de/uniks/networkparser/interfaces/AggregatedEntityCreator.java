package de.uniks.networkparser.interfaces;

public interface AggregatedEntityCreator extends SendableEntityCreator
{
   /**
    * Gets the properties that belong to an aggregation where this class models the contained elements.
    *
    * @return the properties
    */
   public String[] getUpProperties();
   
   /**
    * Gets the properties that belong to an aggregation where this class models the contained elements.
    *
    * @return the properties
    */
   public String[] getDownProperties();
   
}
