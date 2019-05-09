package de.uniks.networkparser.graph;

import java.util.Comparator;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.list.SimpleSet;

public class GraphSimpleSet extends SimpleSet<GraphMember> implements Comparator<Object> {
	private boolean comparator = true;
	public static GraphSimpleSet create(boolean comparator) {
		GraphSimpleSet set = new GraphSimpleSet();
		set.comparator = comparator;
		return set;
	}
	
	@Override
	protected boolean checkValue(Object a, Object b) {
		if (!(a instanceof GraphMember)) {
			return a.equals(b);
		}

		String idA = ((GraphMember) a).getFullId();
		if (idA == null) {
			return a.equals(b);
		}
		String idB;
		if (b instanceof String) {
			idB = (String) b;
		} else {
			idB = ((GraphMember) b).getFullId();
		}
		return idA.equalsIgnoreCase(idB);
	}

	@Override
	public Comparator<Object> comparator() {
		return this;
	}

	@Override
	public boolean isComparator() {
		return comparator;
	}
	
	public int compare(Object o1, Object o2) {
		if (o1 instanceof GraphMember == false || o2 instanceof GraphMember == false) {
			return 0;
		}
		String id1 = ((GraphMember) o1).getFullId();
		String id2 = ((GraphMember) o2).getFullId();
		if (id1 == id2) {
			return 0;
		}
		if (id1 == null) {
			return 1;
		}
		if (id2 == null) {
			return -1;
		}
		return id1.compareTo(id2);
	}
}
