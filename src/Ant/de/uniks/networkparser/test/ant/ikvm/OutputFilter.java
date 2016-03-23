package de.uniks.networkparser.test.ant.ikvm;

import java.util.ArrayList;
import java.util.List;

public class OutputFilter implements Filter {
	private List<Filter> filters = new ArrayList<Filter> ();

	public void addRegex (Regex filter) {
		filters.add(filter);
	}

	public void addWildcard (Wildcard filter) {
		filters.add(filter);
	}

	public void addContains (Contains filter) {
		filters.add(filter);
	}

	public boolean suppress (String output) {
		for (Filter filter : filters)
			if (filter.suppress(output))
				return true;
		return false;
	}
}
