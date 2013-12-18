package de.uniks.networkparser.json;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or later as soon they
 will be approved by the European Commission - subsequent
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import de.uniks.networkparser.EntityList;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ReferenceObject;
import de.uniks.networkparser.event.MapEntry;
import de.uniks.networkparser.event.creator.DateCreator;
import de.uniks.networkparser.event.creator.MapEntryCreator;
import de.uniks.networkparser.interfaces.BaseEntity;
import de.uniks.networkparser.interfaces.MapUpdateListener;
import de.uniks.networkparser.interfaces.NoIndexCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.creator.JsonArrayCreator;
import de.uniks.networkparser.json.creator.JsonObjectCreator;
import de.uniks.networkparser.logic.Deep;
import de.uniks.networkparser.sort.EntityComparator;
/**
 * The Class JsonIdMap.
 */

public class JsonIdMap extends IdMap {
	
