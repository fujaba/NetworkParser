package de.uniks.networkparser.ext.sql;

import java.io.Serializable;
import java.util.Comparator;

public class SQLStatementComparator implements Comparator<Object>, Serializable{
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Object o1, Object o2) {
		SQLStatement s1 = (SQLStatement) o1;
		SQLStatement s2 = (SQLStatement) o2;
		SQLCommand c1 = s1.getCommand();
		SQLCommand c2 = s2.getCommand();
		int executeDiff = c1.getExecutePriority() - c2.getExecutePriority();
		if(executeDiff != 0) {
			return executeDiff;
		}
		return s1.getTable().compareTo(s2.getTable());
	}

}
