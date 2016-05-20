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

public enum SQLCommand {
	UPDATE("UPDATE", 4), INSERT("INSERT INTO", 3), DELETE("DELETE FROM", 5), CREATETABLE("CREATE TABLE IF NOT EXISTS", 2), DROPTABLE("DROP TABLE IF EXISTS", 1), CONNECTION("", 0), SELECT("SELECT", 6);

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
