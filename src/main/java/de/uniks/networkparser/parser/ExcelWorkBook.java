package de.uniks.networkparser.parser;
/*
NetworkParser
Copyright (c) 2011 - 2016, Stefan Lindel
All rights reserved.

Licensed under the EUPL, Version 1.1 or (as soon they
will be approved by the European Commission) subsequent
versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl5

Unless required by applicable law or agreed to in writing, software distributed under the Licence is
distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the Licence for the specific language governing permissions and limitations under the Licence.
*/
import de.uniks.networkparser.list.SimpleList;

public class ExcelWorkBook extends SimpleList<ExcelSheet>{
	public static final String PROPERTY_AUTHOR="author";
	private String author;

	public String getAuthor() {
		return author;
	}

	public boolean setAuthor(String value) {
		if ((this.author == null && value != null) || (this.author != null && this.author.equals(value) == false)) {
			this.author = value;
			return true;
		}
		return false;
	}

	public ExcelWorkBook withAuthor(String value) {
		setAuthor(value);
		return this;
	}
}
