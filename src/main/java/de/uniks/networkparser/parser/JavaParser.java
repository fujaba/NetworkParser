package de.uniks.networkparser.parser;

public class JavaParser {
	private boolean fileBodyHasChanged = false;

	public Object parse(CharSequence fileBody) {
		// [packagestat] importlist classlist
//TODO IMPLEMENTS	      if (currentRealTokenEquals(PACKAGE))
//	      {
//	         parsePackageDecl();
//	      }
//
//	      int startPos = currentRealToken.startPos;
//	      
//	      while (currentRealTokenEquals(IMPORT))
//	      {
//	         parseImport();
//	      }
//
//	      endOfImports = previousRealToken.endPos;
//
//	      checkSearchStringFound(IMPORT, startPos);
//
//	      parseClassDecl();
//	   }
		return null;
	}

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
