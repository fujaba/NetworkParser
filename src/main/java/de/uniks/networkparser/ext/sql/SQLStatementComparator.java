package de.uniks.networkparser.ext.sql;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
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
