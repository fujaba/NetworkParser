package de.uniks.networkparser.xml;

import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.SimpleGrammar;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;

public class HTMLGrammar extends SimpleGrammar{
	@Override
	public BaseItem encode(Object entity, MapEntity map, Tokener tokener) {
		HTMLEntity rootItem=new HTMLEntity();
		Entity child = map.encode(entity, tokener);
		rootItem.with(child);
		return rootItem;
	}
}
