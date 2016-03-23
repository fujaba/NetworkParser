package de.uniks.networkparser.ext.sql;

import java.util.Comparator;

import de.uniks.networkparser.list.SimpleList;

public class SQLStatementList extends SimpleList<SQLStatement>{
	private final static SQLStatementComparator comparator = new SQLStatementComparator();
	@Override
	public Comparator<Object> comparator() {
		return comparator;
	}
	
	@Override
	public boolean isComparator() {
		return true;
	}

}
