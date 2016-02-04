package de.uniks.networkparser.parser;

public class JavaParser {
	private boolean fileBodyHasChanged = false;

	public Object parse(CharSequence fileBody) {
//		new C
		return null;
	}
//	@Override
//	public void parseToEntity(SimpleKeyValueList<?, ?> entity) {
//	}
//
//	@Override
//	public void parseToEntity(AbstractList<?> entityList) {
//	}

	public boolean isFileBodyHasChanged() {
		return fileBodyHasChanged;
	}

	public boolean setFileBodyHasChanged(boolean value) {
		if(value != this.fileBodyHasChanged) {
			this.fileBodyHasChanged = value;
			return true;
		}
		return false;
	}
}
