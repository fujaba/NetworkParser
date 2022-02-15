package de.uniks.networkparser.ext.sql;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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

/**
 * The Enum SQLCommand.
 *
 * @author Stefan
 */
public enum SQLCommand {
    /** The update. */
    UPDATE("UPDATE", 4),
    /** The insert. */
    INSERT("INSERT INTO", 3),
    /** The delete. */
    DELETE("DELETE FROM", 5),
    /** The createtable. */
    CREATETABLE("CREATE TABLE IF NOT EXISTS", 2),
    /** The droptable. */
    DROPTABLE("DROP TABLE IF EXISTS", 1),
    /** The connection. */
    CONNECTION("", 0),
    /** The select. */
    SELECT("SELECT", 6);

	private String value;

	private int executePriority;

	SQLCommand(String name, int executePriority) {
		this.value = name;
		this.executePriority = executePriority;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Gets the execute priority.
	 *
	 * @return the execute priority
	 */
	public int getExecutePriority() {
		return executePriority;
	}
}
