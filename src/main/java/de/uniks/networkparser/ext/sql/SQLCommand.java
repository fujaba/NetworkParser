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

public enum SQLCommand {
	UPDATE("UPDATE", 4), INSERT("INSERT INTO", 3), DELETE("DELETE FROM", 5),
	CREATETABLE("CREATE TABLE IF NOT EXISTS", 2), DROPTABLE("DROP TABLE IF EXISTS", 1), CONNECTION("", 0),
	SELECT("SELECT", 6);

	private String value;

	private int executePriority;

	SQLCommand(String name, int executePriority) {
		this.value = name;
		this.executePriority = executePriority;
	}

	public String getValue() {
		return this.value;
	}

	public int getExecutePriority() {
		return executePriority;
	}
}
