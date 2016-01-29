package de.uniks.networkparser.parser;

public enum ExcelCellValueType {
	EXTLST("extLst"), FORMULAR("f"), RICHTEXT("is"), VALUE("v");
	private String value;

	ExcelCellValueType(String value) {
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
