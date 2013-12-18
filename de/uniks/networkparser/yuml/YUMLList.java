package de.uniks.networkparser.yuml;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or – as soon they
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import de.uniks.networkparser.interfaces.BaseEntity;
import de.uniks.networkparser.interfaces.BaseEntityList;

public class YUMLList implements BaseEntityList {
	private LinkedHashMap<String, YUMLEntity> children = new LinkedHashMap<String, YUMLEntity>();
	private int typ;
	private ArrayList<Cardinality> cardinalityValues = new ArrayList<Cardinality>();

	@Override
	public BaseEntityList initWithMap(Collection<?> value) {
		for (Iterator<?> i = value.iterator(); i.hasNext();) {
			Object item = i.next();
			if (item instanceof YUMLEntity) {
				YUMLEntity entity = (YUMLEntity) item;
				children.put(entity.getId(), entity);
			}
		}
		return this;
	}

	@Override
	public BaseEntityList put(Object value) {
		if (value instanceof YUMLEntity) {
			YUMLEntity entity = (YUMLEntity) value;
			children.put(entity.getId(), entity);
		}
		return this;
	}

	@Override
	public int size() {
		return children.size();
	}

	@Override
	public boolean add(Object value) {
		if (value instanceof YUMLEntity) {
			YUMLEntity entity = (YUMLEntity) value;
			children.put(entity.getId(), entity);
			return true;
		}
		return false;
	}

	@Override
	public Object get(int z) {
		Iterator<Entry<String, YUMLEntity>> iterator = children.entrySet()
				.iterator();
		while (z > 0 && iterator.hasNext()) {
			iterator.next();
		}
		if (z == 0) {
			return iterator.next().getValue();
		}
		return null;
	}

	public YUMLEntity getById(String id) {
		return children.get(id);
	}

	@Override
	public BaseEntityList getNewArray() {
		return new YUMLList();
	}

	@Override
	public BaseEntity getNewObject() {
		return new YUMLEntity();
	}

	@Override
	public String toString() {
		return toString(0, 0);
	}

	@Override
	public String toString(int indentFactor) {
		return toString(0, 0);
	}

	@Override
	public String toString(int indentFactor, int intent) {
		if (children.size() > 0) {
			StringBuilder sb = new StringBuilder();
			Iterator<YUMLEntity> i = children.values().iterator();

			HashMap<String, HashSet<Cardinality>> links = new HashMap<String, HashSet<Cardinality>>();
			String[] items = new String[2];
			for (Cardinality element : cardinalityValues) {
				if (typ == YUMLIdMap.OBJECT) {
					items[0] = element.getSourceID();
					items[1] = element.getTargetID();
				} else {
					items[0] = element.getSourceClazz();
					items[1] = element.getTargetClazz();
				}
				for (int z = 0; z < 2; z++) {
					if (links.containsKey(items[z])) {
						links.get(items[z]).add(element.reset());
					} else {
						HashSet<Cardinality> hashSet = new HashSet<Cardinality>();
						hashSet.add(element.reset());
						links.put(items[z], hashSet);
					}
				}
			}

			HashSet<YUMLEntity> visitedObj = new HashSet<YUMLEntity>();

			parse(i.next(), sb, visitedObj, links);
			while (i.hasNext()) {
				parse(i.next(), sb, visitedObj, links);
			}
			return sb.toString();
		}
		return null;
	}

	public void parse(YUMLEntity item, StringBuilder sb,
			HashSet<YUMLEntity> visited,
			HashMap<String, HashSet<Cardinality>> links) {
		String key;
		if (typ == YUMLIdMap.OBJECT) {
			key = item.getId();
		} else {
			key = item.getClassName();
		}
		HashSet<Cardinality> showedLinks = links.get(key);
		if (showedLinks == null) {
			sb.append(item.toString(typ, visited.contains(item)));
			visited.add(item);
			return;
		}
		Iterator<Cardinality> iterator = showedLinks.iterator();
		if (iterator.hasNext()) {
			Cardinality entry = iterator.next();
			while (iterator.hasNext() && entry.isShowed()) {
				entry = iterator.next();
			}
			if (!entry.isShowed()) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(item.toString(typ, visited.contains(item)));
				visited.add(item);
				sb.append("-");
				YUMLEntity target = children.get(entry.getTargetID());
				sb.append(target.toString(typ, visited.contains(target)));
				visited.add(target);
				entry.withShowed(true);
				while (iterator.hasNext()) {
					sb.append(",");
					sb.append(item.toString(typ, true));
					sb.append("-");
					visited.add(item);
					sb.append(item.toString(typ, visited.contains(item)));
					entry.withShowed(true);
				}
			}
		}
	}

	@Override
	public BaseEntity withVisible(boolean value) {
		return this;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	public int getTyp() {
		return typ;
	}

	public YUMLList withTyp(int typ) {
		this.typ = typ;
		return this;
	}

	public boolean addCardinality(Cardinality cardinality) {
		return this.cardinalityValues.add(cardinality);
	}
}
