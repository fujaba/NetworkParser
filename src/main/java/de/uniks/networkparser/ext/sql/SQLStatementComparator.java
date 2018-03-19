package de.uniks.networkparser.ext.sql;

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
import java.io.Serializable;
import java.util.Comparator;

public class SQLStatementComparator implements Comparator<Object>, Serializable{
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Object o1, Object o2) {
		if(o1 instanceof SQLStatement == false || o2 instanceof SQLStatement) {
			if(o1 != null) {
				if(o1.equals(o2)) {
					return 0;
				}
			}
			return -1;
		}
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
