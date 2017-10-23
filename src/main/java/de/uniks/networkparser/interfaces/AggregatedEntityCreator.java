package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.list.ObjectSet;

public interface AggregatedEntityCreator extends SendableEntityCreator
{
   public void aggregate(ObjectSet graph, Object obj);
}
