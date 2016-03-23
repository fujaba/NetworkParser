package de.uniks.networkparser.ext.sql;

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
